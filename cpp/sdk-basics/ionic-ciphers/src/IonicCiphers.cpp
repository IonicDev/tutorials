/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <ISChunkCrypto.h>
#include <stdio.h>
#include <cstdlib>
#include <iostream>

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
    agent.setMetadata("ionic-application-name", "ionic-ciphers-tutorial");
    agent.setMetadata("ionic-application-version", "1.0.0");

    /******************************************************************************
     * SENDER
     ******************************************************************************/

    std::string message = "this is a secret message!";

    // initialize aes cipher object
    ISChunkCryptoCipherAuto  senderCipher(agent);

    // encrypt data
    std::string ciphertext;
    nErrorCode = senderCipher.encrypt(message, ciphertext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    std::cout << "CIPHERTEXT    : " << ciphertext << std::endl;

    /******************************************************************************
     * RECEIVER
     ******************************************************************************/

    // initialize chunk cipher object
    ISChunkCryptoCipherAuto  receiverCipher(agent);

    // decrypt data
    std::string plaintext;
    receiverCipher.decrypt(ciphertext, plaintext);
    if (nErrorCode != ISCRYPTO_OK) {
        std::cerr << "Error: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }

    std::cout << "\nPLAINTEXT     : " << plaintext << std::endl;
}
