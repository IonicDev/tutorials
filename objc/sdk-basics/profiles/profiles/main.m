//
//  main.m
//  profiles
//
//  Copyright © 2020 Ionic Security Inc. All rights reserved.
//  By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the Privacy Policy (https://www.ionic.com/privacy-notice/).
//

#import <Foundation/Foundation.h>

#import <IonicAgentSDK/IonicAgentSDK.h>

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        NSString * persistorPath = [[[[NSFileManager alloc] init] currentDirectoryPath] stringByAppendingPathComponent:@"sample-persistor.pw"];
        
        IonicAgentDeviceProfilePersistorPassword* profilePersistor = [[IonicAgentDeviceProfilePersistorPassword alloc] init];
        profilePersistor.filePath = persistorPath;
        profilePersistor.password = @"ionic123";
        
        
        //NSString* path = [[[NSProcessInfo processInfo]environment]objectForKey:@"PATH"];
        
        NSError * error = nil;
        
        // create an agent and initialize it with the password persistor all defaults
        IonicAgent * agent = [[IonicAgent alloc] initWithDefaults:profilePersistor
                                                            error:&error];
        // check for initialization error
        if (error)
        {
            IonicLogF_Error(@"MyApplicationChannel", @"Failed to initialize agent object, error code = %d.", error.code);
            return 2;
        }
        
        // list all available profiles
        NSArray<IonicAgentDeviceProfile*> *profiles = [agent profiles];
        for (IonicAgentDeviceProfile *eachProfile in profiles) {
            NSLog(@"---");
            NSLog(@"ID       : %@", [eachProfile deviceId]);
            NSLog(@"Name     : %@", [eachProfile name]);
            NSLog(@"Keyspace : %@", [eachProfile keyspace]);
            NSLog(@"ApiUrl   : %@", [eachProfile server]);
        }
        
        // display active profile
        IonicAgentDeviceProfile* activeProfile = [agent activeProfile];
        NSLog(@"ACTIVE PROFILE : %@", [activeProfile deviceId]);
        
        // change active profile
        NSString* newProfileId = @"EfGh.1.54sdf8-sdfj-5802-sd80-248vwqucv9s73";
        NSLog(@"SETTING NEW ACTIVE PROFILE : %@", newProfileId);
        
        BOOL bSuccess = [agent setActiveProfileWithDeviceId:newProfileId];
        if (!bSuccess) {
            NSLog(@"Failed to set active profile");
            return 2;
        }
        
        // display active profile
        IonicAgentDeviceProfile* newActiveProfile = [agent activeProfile];
        NSLog(@"NEW ACTIVE PROFILE : %@", [newActiveProfile deviceId]);
        
    }
    return 0;
}
