/*
 * (c) 2018 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <stdio.h>
#include <cstdlib>
#include <iostream>
#include <map>

#ifdef _WIN32
    #define HOMEVAR "USERPROFILE"
#else 
    #define HOMEVAR "HOME"
#endif

int main(int argc, char* argv[]) {

	int nErrorCode;

	// read persistor password from environment variable
	char* cpersistorPassword = std::getenv("IONIC_PERSISTOR_PASSWORD");
	if (cpersistorPassword == NULL) {
		std::cerr << "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD" << std::endl;
		exit(1);
	}
	std::string persistorPassword = std::string(cpersistorPassword);

	// initialize agent with password persistor
	std::string persistorPath = std::string(std::getenv(HOMEVAR)) + "/.ionicsecurity/profiles.pw";
	ISAgentDeviceProfilePersistorPassword persistor;
	persistor.setFilePath(persistorPath);
	persistor.setPassword(persistorPassword);
	ISAgent agent;
	nErrorCode = agent.initialize(persistor);
	if (nErrorCode != ISAGENT_OK) {
		std::cerr << "Failed to initialize agent from password persistor (" << persistorPath << ")" << std::endl;
		std::cerr << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		exit(1);
	}
	agent.setMetadata("ionic-application-name", "ionic-encryption-tutorial");
	agent.setMetadata("ionic-application-version", "1.0.0");

	/******************************************************************************
	 * SENDER
	 ******************************************************************************/

	std::string message = "this is a secret message!";

	// create single key
	ISAgentCreateKeysRequest request;
	ISAgentCreateKeysRequest::Key requestKey("refid1", 1);
	request.getKeys().push_back(requestKey);
	ISAgentCreateKeysResponse response;
	nErrorCode = agent.createKeys(request, response);
	if (nErrorCode != ISAGENT_OK) {
		std::cerr << "Error creating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		exit(1);
	}
	const ISAgentCreateKeysResponse::Key *createdKey = response.findKey("refid1");

	// initialize aes cipher object
	ISCryptoAesCtrCipher senderCipher(createdKey->getKey());

	// encrypt data
	ISCryptoBytes ciphertext;
	nErrorCode = senderCipher.encrypt(message, ciphertext);
	if (nErrorCode != ISCRYPTO_OK) {
		std::cerr << "Encryption Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		exit(1);
	}

	// Encode the cipher text with a base64 encode.
    std::string b64Ciphertext;
	nErrorCode = ISCryptoUtils::binToBase64(ciphertext, b64Ciphertext, false, ciphertext.size(), true);
	if (nErrorCode != ISCRYPTO_OK) {
		std::cerr << "Bin to base64 Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
		exit(1);
	}

	// Put key ID and base64 encoded string into a payload.
	std::map<std::string, std::string> payload;
	payload["key_id"] = createdKey->getId();
	payload["b64_ciphertext"] = b64Ciphertext;

	// Encode into hex bytes.
	ISCryptoHexString hexCiphertext;
	hexCiphertext.fromBytes(ciphertext);

	// Display Sender information.
	std::cout << "CREATED KEYID : " << createdKey->getId() << std::endl;
	std::cout << "CIPHERTEXT    : " << hexCiphertext << std::endl;

	std::cout << std::endl << "PAYLOAD: ";
	std::cout << "\"key_id\":" << "\"" << payload["key_id"] << "\", ";
	std::cout << "\"b64_ciphertext\":" << "\"" << payload["b64_ciphertext"] << "\"" << std::endl;


    /******************************************************************************
     * RECEIVER
     ******************************************************************************/

    // extract ciphertext and key id
    std::string keyId = createdKey->getId();

    // get key
    ISAgentGetKeysResponse getResponse;
    nErrorCode = agent.getKey(keyId, getResponse);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error fetching key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    if (response.getKeys().size() == 0) {
        std::cerr << "No key was returned (key does not exist or access was denied)" << std::endl;
        exit(1);
    }
    ISAgentGetKeysResponse::Key fetchedKey = getResponse.getKeys().at(0);

    // initialize aes cipher object
    ISCryptoAesCtrCipher receiverCipher(fetchedKey.getKey());

    // decrypt data
    ISCryptoBytes plaintext;
    receiverCipher.decrypt(ciphertext, plaintext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    ISCryptoHexString hexPlaintext;
    hexPlaintext.fromBytes(plaintext);
    std::cout << "\nFETCHED KEYID : " << fetchedKey.getId() << std::endl;
    std::cout << "PLAINTEXT     : " << hexPlaintext << std::endl;
}
