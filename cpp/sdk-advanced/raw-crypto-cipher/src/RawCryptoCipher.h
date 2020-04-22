#pragma once
#include <ISAgentKeyServices.h>
#include <ISCrypto.h>
#include <ISLog.h>
#include <string>


// Attributes data helper classes should use getters and setters
//  They're left out here only to keep it short and simple
class RawCryptoEncryptAttributes {
public:
	ISMetadataMap m_metadata;
	ISKeyAttributesMap m_attributes;
	ISKeyAttributesMap m_mutableAttributes;
	ISAgentCreateKeysResponse::Key m_keyOut;
	ISAgentResponseBase m_errorResponse;

	const std::string & getKeyIdOut() const {
		return m_keyOut.getId();
	}
};

class RawCryptoDecryptAttributes {
public:
	ISMetadataMap m_metadata;
	ISAgentGetKeysResponse::Key m_keyOut;
	ISAgentResponseBase m_errorResponse;
};

class RawCryptoCipher {
public:
	RawCryptoCipher(ISAgentKeyServices& keyServices);
	~RawCryptoCipher();

	// The ISCryptoBytes type is derived from std::vector<byte> with a secure deleter to ensure that sensitive data is not left in memory.
	int encrypt(const ISCryptoBytes & bytesIn, ISCryptoBytes & cipherTextOut, RawCryptoEncryptAttributes & attributesInOut);

	int decrypt(const ISCryptoBytes & cipherTextIn, const std::string& keyIdIn, ISCryptoBytes & bytesOut, RawCryptoDecryptAttributes & attributesInOut);

	static const std::string LOGCHANNEL;
	enum ErrorCode {
		OK = 0,
		INPUT_ERROR = 1001
	};

private:
	ISAgentKeyServices & m_services;
};

