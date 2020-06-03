/*
 * (c) 2018-2020 Ionic Security Inc.
 * By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html)
 * and the Privacy Policy (https://www.ionic.com/privacy-notice/).
 */

#include "ISAgent.h"
#include "ISAgentSDKError.h"
#include <stdio.h>
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include <map>
using namespace std;

#ifdef _WIN32
    #define HOMEVAR "USERPROFILE"
#else 
    #define HOMEVAR "HOME"
#endif

void print_vector(vector <string> const &strings) {
    bool first_time = true;

    cout << "[";
    for (auto const& str: strings) {
        if (first_time) {
            cout << "\"" << str << "\"";
            first_time = false;
        }
        else {
            cout << ",\"" << str << "\"";
        }
    }
    cout << "]";
}

void print_attributes(map<string, vector<string>> const &attributes) {
    bool first_time = true;

    cout << "{";
    for (auto const& pair: attributes) {
        if (first_time) {
            cout << "\"" << pair.first << "\": ";
            first_time = false;
        }
        else {
            cout << ", \"" << pair.first << "\": ";
        }
        print_vector(pair.second);
    }
    cout << "}";
}

void print_fixed_attrs(map<string, vector<string>> const &fixed_attrs) {
    cout << "FixedAttrs   : ";
    print_attributes(fixed_attrs);
    cout << endl;
}

void print_mutable_attrs(map<string, vector<string>> const &mutable_attrs) {
    cout << "MutableAttrs : ";
    print_attributes(mutable_attrs);
    cout << endl;
}

int main(int argc, char* argv[]) {

    int nErrorCode;

    // read persistor password from environment variable
    char* cpersistorPassword = getenv("IONIC_PERSISTOR_PASSWORD");
    if (cpersistorPassword == NULL) {
        cerr << "[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD" << endl;
        exit(1);
    }
    string persistorPassword = string(cpersistorPassword);

    // initialize agent with password persistor
    string persistorPath = string(getenv(HOMEVAR)) + "/.ionicsecurity/profiles.pw";
    ISAgentDeviceProfilePersistorPassword persistor;
    persistor.setFilePath(persistorPath);
    persistor.setPassword(persistorPassword);
    ISAgent agent;
    nErrorCode = agent.initialize(persistor);
    if (nErrorCode != ISAGENT_OK) {
        cerr << "Failed to initialize agent from password persistor (" << persistorPath << ")" << endl;
        cerr << ISAgentSDKError::getErrorCodeString(nErrorCode) << endl;
        exit(1);
    }
    agent.setMetadata("ionic-application-name", "ionic-keys-tutorial");
    agent.setMetadata("ionic-application-version", "1.0.0");

    // define fixed attributes
    map<string, vector<string >> fixedAttributes;
    vector<string> dataTypeVal;
    vector<string> regionVal;
    dataTypeVal.push_back("Finance");
    regionVal.push_back("North America");
    fixedAttributes["data-type"] = dataTypeVal;
    fixedAttributes["region"] = regionVal;

    // define mutable attributes
    map<string, vector<string >> mutableAttributes;
    vector<string> classificationVal;
    vector<string> designatedOwnerVal;
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
        cerr << "Error creating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << endl;
        exit(1);
    }
    const ISAgentCreateKeysResponse::Key *createdKey = response.findKey("refid1");

    // display new key
    cout << "NEW KEY:" << endl;
    ISCryptoHexString hexKeyCreated;
    hexKeyCreated.fromBytes(createdKey->getKey());
    cout << "KeyId        : " << createdKey->getId() << endl;
    cout << "KeyBytes     : " << hexKeyCreated << endl;
    print_fixed_attrs(createdKey->getAttributes());
    print_mutable_attrs(createdKey->getMutableAttributes());
    

    // get key by KeyId
    ISAgentGetKeysResponse getResponse;
    string keyId = createdKey->getId();
    nErrorCode = agent.getKey(keyId, getResponse);
    if (nErrorCode != ISAGENT_OK) {
        cerr << "Error fetching key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << endl;
        exit(1);
    }
    if (response.getKeys().size() == 0) {
        cerr << "No key was returned (key does not exist or access was denied)" << endl;
        exit(1);
    }
    ISAgentGetKeysResponse::Key fetchedKey = getResponse.getKeys().at(0);

    // display fetched key
    cout << "\nFETCHED KEY:" << endl;
    ISCryptoHexString hexKeyFetched;
    hexKeyFetched.fromBytes(fetchedKey.getKey());
    cout << "KeyId    : " << fetchedKey.getId() << endl;
    cout << "KeyBytes : " << hexKeyFetched << endl;
    print_fixed_attrs(fetchedKey.getAttributes());
    print_mutable_attrs(fetchedKey.getMutableAttributes());

    // define new mutable attributes
    vector<string> newClassificationVal;
    newClassificationVal.push_back("Highly Restricted");

    // merge new and existing mutable attributes
    ISAgentUpdateKeysRequest::Key updateKey(fetchedKey);
    updateKey.getMutableAttributes()["classification"] = newClassificationVal;

    // update key
    ISAgentUpdateKeysResponse updateResponse;
    nErrorCode = agent.updateKey(updateKey, updateResponse);
    if (nErrorCode != ISAGENT_OK) {
        cerr << "Error updating key: " << ISAgentSDKError::getErrorCodeString(nErrorCode) << endl;
        exit(1);
    }
    ISAgentUpdateKeysResponse::Key updatedKey = updateResponse.getKeys().at(0);

    // display updated key
    cout << "\nUPDATED KEY:" << endl;
    ISCryptoHexString hexKeyUpdated;
    hexKeyUpdated.fromBytes(updatedKey.getKey());
    cout << "KeyId    : " << updatedKey.getId() << endl;
    cout << "KeyBytes : " << hexKeyUpdated << endl;
    print_fixed_attrs(updatedKey.getAttributes());
    print_mutable_attrs(updatedKey.getMutableAttributes());
}
