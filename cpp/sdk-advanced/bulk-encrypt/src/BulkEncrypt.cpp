/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

// This file implements a class that can be used as part of the command line
// tool defined in Main.cpp, or included in another program
#include "BulkEncrypt.h"
#include <ISFileCrypto.h>
#include <ISCrossPlatformFileUtil.h>
#include <ISLog.h>
#include <ISAgentSDKError.h>
#include <cctype>
#include <fstream>

const std::string BulkEncrypt::LOGCHANNEL = "BulkEncrypt";

// This implementation of ISAgentKeyServices takes an existing key services
// object and pre-caches created keys in a single call.  This speeds up the
// process of encrypting large numbers of files by consolidating many network
// calls into one.  All other key services operations are passed through to the 
// provided key services object (such as an ISAgent).
class BulkKeyServices : public ISAgentKeyServices
{
public:
	BulkKeyServices(ISAgentKeyServices & unownedKeySource) : m_keySourceAgent(unownedKeySource) {}
	virtual ~BulkKeyServices() {}

	// accessors
	virtual const ISAgentDeviceProfile & getActiveProfile() const
	{
		return m_keySourceAgent.getActiveProfile();
	}

	virtual bool hasActiveProfile() const
	{
		return m_keySourceAgent.hasActiveProfile();
	}

	// network APIs
	virtual int createKeys(const ISAgentCreateKeysRequest & keysRequest, ISAgentCreateKeysResponse & keysResponse)
	{	// If needed we could fake the refIds and compare attributes to take bulk key requests from our cache, but 
		// since this is only intended for internal bulk file encryption use, we shouldn't need to handle this case
		return m_keySourceAgent.createKeys(keysRequest, keysResponse);
	}

	virtual int createKey(const ISKeyAttributesMap & mapKeyAttributes, const ISKeyAttributesMap & mapMutableKeyAttributes, const ISMetadataMap & mapMetadata, ISAgentCreateKeysResponse & keysResponse)
	{
		if (!m_lastKeyFetchResponse.getKeys().empty())
		{	// While we have keys in the cache, use them.  Attributes will be from the FillBulkKeyCache call
			// For a general use key cache, we could check the attributes and only use from a cache correct ones
			keysResponse.getKeys().push_back(m_lastKeyFetchResponse.getKeys().back());
			m_lastKeyFetchResponse.getKeys().pop_back();
			keysResponse.setConversationId(m_lastKeyFetchResponse.getConversationId());
			keysResponse.setHttpResponseCode(m_lastKeyFetchResponse.getHttpResponseCode());
			keysResponse.setServerErrorCode(m_lastKeyFetchResponse.getServerErrorCode());
			keysResponse.setServerErrorDataJson(m_lastKeyFetchResponse.getServerErrorDataJson());
			keysResponse.setServerErrorMessage(m_lastKeyFetchResponse.getServerErrorMessage());
			return ISAGENT_OK;
		}
		ISAgentCreateKeysRequest localKeyRequest;
		localKeyRequest.setMetadata(mapMetadata);
		localKeyRequest.getKeys().push_back(ISAgentCreateKeysRequest::Key("ref", 1, mapKeyAttributes, mapMutableKeyAttributes));
		int nErr = createKeys(localKeyRequest, keysResponse);
		if (nErr)
		{
			return nErr;
		}

		// check to be sure we got a key back.  if not, then return a key denied error.
		if (keysResponse.getKeys().empty())
		{
			return ISAGENT_KEY_DENIED;
		}
		return ISAGENT_OK;
	}

	virtual int getKeys(const ISAgentGetKeysRequest & keysRequest, ISAgentGetKeysResponse & keysResponse)
	{
		return m_keySourceAgent.getKeys(keysRequest, keysResponse);
	}

	virtual int getKey(const std::string& sKeyId, const ISMetadataMap & mapMetadata, ISAgentGetKeysResponse & keysResponse)
	{
		return m_keySourceAgent.getKey(sKeyId, mapMetadata, keysResponse); 
	}

	virtual int updateKeys(const ISAgentUpdateKeysRequest & keysRequest, ISAgentUpdateKeysResponse & keysResponse)
	{
		return updateKeys(keysRequest, keysResponse); 
	}

	virtual int updateKey(const ISAgentUpdateKeysRequest::Key & keyRequest, const ISMetadataMap & mapMetadataIn, ISAgentUpdateKeysResponse & keysResponseOut)
	{
		return updateKey(keyRequest, mapMetadataIn, keysResponseOut);
	}

	// Our own function to allocate a certain number of keys in the cache
	int FillBulkKeyCache(int keyCount, const ISKeyAttributesMap & mapKeyAttributes, const ISKeyAttributesMap & mapMutableKeyAttributes, const ISMetadataMap & mapMetadata)
	{
		ISAgentCreateKeysRequest localKeyRequest;
		localKeyRequest.setMetadata(mapMetadata);
		localKeyRequest.getKeys().push_back(ISAgentCreateKeysRequest::Key("ref", keyCount, mapKeyAttributes, mapMutableKeyAttributes));
		int nErr = createKeys(localKeyRequest, m_lastKeyFetchResponse);
		if (nErr)
		{
			return nErr;
		}
		return ISAGENT_OK;
	}

private:
	ISAgentKeyServices &m_keySourceAgent;
	ISAgentCreateKeysResponse m_lastKeyFetchResponse;
};


BulkEncrypt::BulkEncrypt(ISAgentKeyServices& keyServices) : m_services(keyServices) {}
BulkEncrypt::~BulkEncrypt() {}

void BulkEncrypt::setAttributes(const ISKeyAttributesMap & mapKeyAttributes) { m_mapKeyAttributes = mapKeyAttributes; }
void BulkEncrypt::setMutableAttributes(const ISKeyAttributesMap & mapMutableKeyAttributes) { m_mapMutableKeyAttributes = mapMutableKeyAttributes; }
void BulkEncrypt::setMetadata(const ISMetadataMap & mapMetadata) { m_mapMetadata = mapMetadata; }

int BulkEncrypt::encrypt(std::vector<std::string> filenames, std::string outputPath, bool haltOnError)
{
	int returnStatus = OK;
	BulkKeyServices keyServe(m_services);
	// We have a list of files, so fetch that number of keys at once and fill the cache of keys for use by the file cipher
	keyServe.FillBulkKeyCache((int)filenames.size(), m_mapKeyAttributes, m_mapMutableKeyAttributes, m_mapMetadata);
	std::shared_ptr<ISFileCryptoCipherBase> fileCipher;

	for (std::vector<std::string>::iterator filenameIter = filenames.begin(); filenameIter != filenames.end(); filenameIter++)
	{
		std::string filename = *filenameIter;
		std::string filenameOut = ISFileUtil::joinPaths(outputPath, ISFileUtil::getFileName(filename));

		if (!ISFileUtil::fileExists(filename))
		{
			continue;
		}

		size_t filenameExtPos = filename.find_last_of('.');
		std::string ext = filename.substr(filenameExtPos+1,filename.size() - (filenameExtPos+1));
		// lowercase the extension
		for (size_t i = 0; i < ext.length(); ++i)
		{
			ext[i] = std::tolower(ext[i]);
		}

		if (ext.compare("pdf") == 0)
		{
			fileCipher.reset(new ISFileCryptoCipherPdf(keyServe));
		}
		else if (ext.compare("csv") == 0)
		{
			fileCipher.reset(new ISFileCryptoCipherCsv(keyServe));
		}
		else if (ext.compare("docx") == 0 || ext.compare("pptx") == 0 || ext.compare("xlsx") == 0)
		{
			fileCipher.reset(new ISFileCryptoCipherOpenXml(keyServe));
		}
		else
		{
			fileCipher.reset(new ISFileCryptoCipherGeneric(keyServe));
		}

		ISFileCryptoEncryptAttributes fileAttr(m_mapKeyAttributes, m_mapMutableKeyAttributes);
		fileAttr.setMetadata(m_mapMetadata);
		int err = fileCipher->encrypt(filename, filenameOut, &fileAttr);
		if (err != ISFILECRYPTO_OK)
		{
			returnStatus = err;
			ISLOGF_ERROR(LOGCHANNEL.c_str(), "Error encrypting file %s, err=%d", filename.c_str(), err);
			if (haltOnError)
			{
				return err;				
			}
		}
	}
	return returnStatus;
}

int BulkEncrypt::decrypt(std::vector<std::string> filenames, std::string outputPath, bool haltOnError)
{
	int returnStatus = OK;
	BulkKeyServices keyServe(m_services);
	ISFileCryptoCipherAuto fileCipher(keyServe);

	for (std::vector<std::string>::iterator filenameIter = filenames.begin(); filenameIter != filenames.end(); filenameIter++)
	{
		std::string filename = *filenameIter;
		std::string filenameOut = ISFileUtil::joinPaths(outputPath, ISFileUtil::getFileName(filename));

		if (!ISFileUtil::fileExists(filename))
		{
			continue;
		}

		ISFileCryptoDecryptAttributes fileAttr;
		fileAttr.setMetadata(m_mapMetadata);
		// The file contains the key ID that is retrieved from the server if policy allows
		int err = fileCipher.decrypt(filename, filenameOut, &fileAttr);
		if (err != ISFILECRYPTO_OK)
		{
			returnStatus = err;
			ISLOGF_ERROR(LOGCHANNEL.c_str(), "Error decrypting file %s, err=%d", filename.c_str(), err);
			if (haltOnError)
			{
				return err;
			}
		}
	}
	return returnStatus;
}
