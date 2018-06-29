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

    // initialize agent with password persistor
    std::string persistorPath = "../../../sample-data/persistors/sample-persistor.pw";
    std::string persistorPassword = "ionic123";
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

    // verify that there are profiles
    if (!agent.hasAnyProfiles()) {
        std::cerr << "No profiles found in specified profile persistor" << std::endl;
        exit(1);
    }

    // list all available profiles
    std::cout << "ALL PROFILES:" << std::endl;
    const std::vector<ISAgentDeviceProfile> profiles = agent.getAllProfiles();
    for (int i=0; i < profiles.size(); i++) {
        const ISAgentDeviceProfile profile = profiles.at(i);
        std::cout << "---" << std::endl;
        std::cout << "ID       : " << profile.getDeviceId() << std::endl;
        std::cout << "Name     : " << profile.getName() << std::endl;
        std::cout << "Keyspace : " << profile.getKeySpace() << std::endl;
        std::cout << "ApiUrl   : " << profile.getServer() << std::endl;
    }

    // verify there is an active profile
    if (!agent.hasActiveProfile()) {
        std::cerr << "No profile set as active" << std::endl;
        exit(1);
    }
    
    // display active profile
    ISAgentDeviceProfile activeProfile = agent.getActiveProfile();
    std::cout << std::endl << "ACTIVE PROFILE: " << activeProfile.getDeviceId() << std::endl;

    // change active profile
    std::string newProfileId = "EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73";
    std::cout << "\nSETTING NEW ACTIVE PROFILE: " << newProfileId << std::endl;
    bool bSuccess = agent.setActiveProfile(newProfileId);
    if (!bSuccess) {
        std::cerr << "Failed to set active profile" << std::endl;
        exit(2);
    }

    // display active profile
    ISAgentDeviceProfile newActiveProfile = agent.getActiveProfile();
    std::cout << std::endl << "NEW ACTIVE PROFILE: " << newActiveProfile.getDeviceId() << std::endl;

}
