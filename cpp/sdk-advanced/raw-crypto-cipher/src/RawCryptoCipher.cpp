/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "RawCryptoCipher.h"

const std::string RawCryptoCipher::LOGCHANNEL = "RawCrypto";

RawCryptoCipher::RawCryptoCipher(ISAgentKeyServices& keyServices) : m_services(keyServices)
{
}

RawCryptoCipher::~RawCryptoCipher()
{
}

int RawCryptoCipher::encrypt(const ISCryptoBytes & bytesIn, ISCryptoBytes & cipherTextOut, RawCryptoEncryptAttributes & attributesInOut)
{
	// Handle bad input.  This would error out during the encrypt call below, so
	//  we want to error out now before we create a key we won't use.
	if (bytesIn.empty())
	{
		ISLOG_ERROR(LOGCHANNEL.c_str(), "RawCrypto encrypt was given an empty bytesIn");
		return INPUT_ERROR;
	}

	// -- Create a key --
	// Set up the data in and out for the create key call
	// SDK functions often have optional datastructures for attributes, but 
	//  we'll require RawCryptoEncryptAttributes here so we always have a way of 
	//  getting the id (and for simplicity).
	ISAgentCreateKeysResponse createResponse;
	ISMetadataMap createMetadata = attributesInOut.m_metadata;
	ISKeyAttributesMap createAttributes = attributesInOut.m_attributes;
	ISKeyAttributesMap createMutable = attributesInOut.m_mutableAttributes;

	int nErr = m_services.createKey(createAttributes, createMutable, createMetadata, createResponse);
	if (nErr != OK)
	{
		// Take note of the 'F' in ISLOGF, which is for printf-style parameters.
		//  If you don't have any parameters to report, you can use ISLOG.
		ISLOGF_ERROR(LOGCHANNEL.c_str(), "RawCryptoCipher encrypt failed to create a key.  rc = %d", nErr);
		attributesInOut.m_errorResponse = createResponse;
		return nErr;
	}
	// createKey will return an error if no keys are returned, so we know we 
	//  have at least one key.
	attributesInOut.m_keyOut = createResponse.getKeys()[0];

	// -- Encrypt --
	// Now that we have a key, we'll use an AES GCM cipher initialized with our key.
	// We will use the KeyId as the additional authentication data, which can help validate the source of the key
	ISCryptoBytes addAuthData((byte*)attributesInOut.getKeyIdOut().c_str(), attributesInOut.getKeyIdOut().size());
	ISCryptoAesGcmCipher cipher(attributesInOut.m_keyOut.getKey(), addAuthData);
	nErr = cipher.encrypt(bytesIn, cipherTextOut);
	if (nErr != OK)
	{
		ISLOGF_ERROR(LOGCHANNEL.c_str(), "RawCryptoCipher encrypt failed during encryption.  rc = %d", nErr);
		return nErr;
	}

	return OK;
}

int RawCryptoCipher::decrypt(const ISCryptoBytes & cipherTextIn, const std::string& keyIdIn, ISCryptoBytes & bytesOut, RawCryptoDecryptAttributes & attributesInOut)
{
	// Handle bad input.
	if (cipherTextIn.empty())
	{
		ISLOG_ERROR(LOGCHANNEL.c_str(), "RawCryptoCipher decrypt was given an empty bytesIn");
		return INPUT_ERROR;
	}

	// -- Get a key --
	// Set up the data in and out for the get key call
	ISAgentGetKeysResponse getResponse;
	ISMetadataMap getMetadata = attributesInOut.m_metadata;

	int nErr = m_services.getKey(keyIdIn, getMetadata, getResponse);
	if (nErr != OK)
	{
		ISLOGF_ERROR(LOGCHANNEL.c_str(), "RawCryptoCipher decrypt failed to get a key.  rc = %d", nErr);
		attributesInOut.m_errorResponse = getResponse;
		return nErr;
	}

	attributesInOut.m_keyOut = getResponse.getKeys()[0];

	// -- Decrypt --
	// Now that we have a key, we'll use an AES GCM cipher initialized with our key.
	ISCryptoBytes addAuthData((byte*)keyIdIn.c_str(), keyIdIn.size());
	ISCryptoAesGcmCipher cipher(attributesInOut.m_keyOut.getKey(), addAuthData);
	nErr = cipher.decrypt(cipherTextIn, bytesOut);
	if (nErr != OK)
	{
		ISLOGF_ERROR(LOGCHANNEL.c_str(), "RawCryptoCipher decrypt failed during encryption.  rc = %d", nErr);
		return nErr;
	}

	return OK;
}
