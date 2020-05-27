//
//  main.m
//  profiles
//
//  Created by Nicolas Vautier on 5/22/20.
//  Copyright © 2020 Nicolas Vautier. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <IonicAgentSDK/IonicAgentSDK.h>
#import <IonicAgentSDK/IonicAgentDeviceProfilePersistor.h>


int main(int argc, const char * argv[]) {
    @autoreleasepool {
        
        NSString* TAG = @"SampleApplicationChannel";
        
        NSString* persistorPassword = [[[NSProcessInfo processInfo] environment] objectForKey:@"IONIC_PERSISTOR_PASSWORD"];
        if( ! persistorPassword) {
            IonicLog_Error(TAG, @"[!] Please provide the persistor password as env variable: IONIC_PERSISTOR_PASSWORD");
            return 1;
        }
        
        NSString * persistorPath = [NSHomeDirectory() stringByAppendingPathComponent:@".ionicsecurity/profiles.pw"];
        NSFileManager *fileManager = [NSFileManager defaultManager];
        if ( ! [fileManager fileExistsAtPath:persistorPath]){
            IonicLogF_Error(TAG, @"[!] '%@' does not exist", persistorPath);
            return 2;
        }
        
        // initialize agent with password persistor
        IonicAgentDeviceProfilePersistorPassword* persistor = [[IonicAgentDeviceProfilePersistorPassword alloc] init];
        persistor.filePath = persistorPath;
        persistor.password = persistorPassword;
        
        NSError * error = nil;
        // create an agent and initialize it with the password persistor all defaults
        IonicAgent * agent = [[IonicAgent alloc] initWithDefaults:persistor
                                                            error:&error];
        // check for initialization error
        if (error) {
            IonicLogF_Error(TAG, @"Failed to initialize agent object, error code = %d.", error.code);
            return (int) error.code;
        }
        
        // set app metadata
        [agent setMetadataValue:@"ionic-application-name" forField:@"ionic-agents-tutorial"];
        [agent setMetadataValue:@"ionic-application-version" forField:@"1.0.0"];
        
        // create single key
        IonicAgentCreateKeysRequestKey* key = [[IonicAgentCreateKeysRequestKey alloc] initWithReferenceId:@"refid1" quantity:1];
        IonicAgentCreateKeysRequest* request = [[IonicAgentCreateKeysRequest alloc] init];
        [request addKey:key];
        IonicAgentCreateKeysResponse* response = [agent createKeysUsingRequest:request error:&error];
        
        // check for errors
        if (error) {
            IonicLogF_Error(TAG, @"Error creating key: %@", error);
            return (int) error.code;
        }
        
        IonicAgentCreateKeysResponseKey* responseKey = [response findKeyUsingRefId:@"refid1"];
        NSLog(@"CREATED NEW KEY  : %@", [responseKey refId]);
    }
        
    return 0;
}
