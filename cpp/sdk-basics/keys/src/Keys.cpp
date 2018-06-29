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
    agent.setMetadata("ionic-application-name", "ionic-keys-tutorial");
    agent.setMetadata("ionic-application-version", "1.0.0");

    // define fixed attributes
    std::map< std::string, std::vector< std::string > > fixedAttributes;
    std::vector<std::string> dataTypeVal;
    std::vector<std::string> regionVal;
    dataTypeVal.push_back("Finance");
    regionVal.push_back("North America");
    fixedAttributes["data-type"] = dataTypeVal;
    fixedAttributes["region"] = regionVal;

    // define mutable attributes
    std::map< std::string, std::vector< std::string > > mutableAttributes;
    std::vector<std::string> classificationVal;
    std::vector<std::string> designatedOwnerVal;
    classificationVal.push_back("Restricted");
    designatedOwnerVal.push_back("joe@hq.example.com");
    mutableAttributes["classification"] = classificationVal;
    mutableAttributes["designed_owner"] = designatedOwnerVal;

    // create new key with fixed and mutable attributes
    ISAgentCreateKeysRequest request;
    ISAgentCreateKeysRequest::Key requestKey("refid1", 1, fixedAttributes, mutableAttributes);
    request.getKeys().push_back(requestKey);
    ISAgentCreateKeysResponse response;
    nErrorCode = agent.createKeys(request, response);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error creating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    const ISAgentCreateKeysResponse::Key *createdKey = response.findKey("refid1");

    // display new key
    std::cout << "NEW KEY:" << std::endl;
    ISCryptoHexString hexKeyCreated;
    hexKeyCreated.fromBytes(createdKey->getKey());
    std::cout << "KeyId    : " << createdKey->getId() << std::endl;
    std::cout << "KeyBytes : " << hexKeyCreated << std::endl;

    // get key by KeyId
    ISAgentGetKeysResponse getResponse;
    std::string keyId = createdKey->getId();
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

    // display fetched key
    std::cout << "\nFETCHED KEY:" << std::endl;
    ISCryptoHexString hexKeyFetched;
    hexKeyFetched.fromBytes(fetchedKey.getKey());
    std::cout << "KeyId    : " << fetchedKey.getId() << std::endl;
    std::cout << "KeyBytes : " << hexKeyFetched << std::endl;

    // define new mutable attributes
    std::vector<std::string> newClassificationVal;
    newClassificationVal.push_back("Highly Restricted");

    // merge new and existing mutable attributes
    ISAgentUpdateKeysRequest::Key updateKey(fetchedKey);
    updateKey.getMutableAttributes()["classification"] = newClassificationVal;

    // update key
    ISAgentUpdateKeysResponse updateResponse;
    nErrorCode = agent.updateKey(updateKey, updateResponse);
    if (nErrorCode != ISAGENT_OK) {
        std::cerr << "Error updating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << std::endl;
        exit(1);
    }
    ISAgentUpdateKeysResponse::Key updatedKey = updateResponse.getKeys().at(0);

    // display updated key
    std::cout << "\nUPDATED KEY:" << std::endl;
    ISCryptoHexString hexKeyUpdated;
    hexKeyUpdated.fromBytes(updatedKey.getKey());
    std::cout << "KeyId    : " << updatedKey.getId() << std::endl;
    std::cout << "KeyBytes : " << hexKeyUpdated << std::endl;
}
