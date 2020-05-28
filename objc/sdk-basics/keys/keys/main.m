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
        
        // define fixed attributes
        NSDictionary * fixedArrributes = @{
                                      @"data-type": @[@"Finance"],
                                      @"region": @[@"North America"]
                                      };
        // define mutable attributes
        NSDictionary * mutableArrributes = @{
                                           @"classification": @[@"Restricted"],
                                           @"designed_owner": @[@"joe@hq.example.com"]
                                           };
        
        // create new key with fixed and mutable attributes
        IonicAgentCreateKeysRequestKey* key = [[IonicAgentCreateKeysRequestKey alloc] initWithReferenceId:@"refid1"
                                                                                                 quantity:1
                                                                                               attributes:fixedArrributes
                                                                                        mutableAttributes:mutableArrributes];
        IonicAgentCreateKeysRequest* request = [[IonicAgentCreateKeysRequest alloc] init];
        [request addKey:key];
        IonicAgentCreateKeysResponse* response = [agent createKeysUsingRequest:request error:&error];
        
        // check for key creation errors
        if (error) {
            IonicLogF_Error(TAG, @"Error creating key: %@", error);
            return (int) error.code;
        }
        
        // display new key
        IonicAgentCreateKeysResponseKey* createKeyResponse = [response findKeyUsingRefId:@"refid1"];
        NSLog(@"NEW KEY :");
        NSLog(@"");
        NSLog(@"KeyId    : %@", [createKeyResponse id]);
        NSLog(@"KeyBytes : %@", [createKeyResponse key]);
        NSLog(@"");
        
        // get key by KeyId
        NSString* keyId = [createKeyResponse id];
        IonicAgentGetKeysResponse* getKeyResponse = [agent getKeyWithId:keyId error:&error];
        
        // check for key fetch errors
        if (error) {
            IonicLogF_Error(TAG, @"Error creating key: %@", error);
            return (int) error.code;
        }
        if([[getKeyResponse keys] count] == 0) {
            IonicLog_Error(TAG, @"No key was returned (key does not exist or access was denied)");
            return IONIC_AGENT_MISSINGVALUE;
        }
        
        // display fetched key
        IonicAgentGetKeysResponseKey* fetchedKey = [[getKeyResponse keys] firstObject];
        NSLog(@"FETCHED KEY :");
        NSLog(@"");
        NSLog(@"KeyId    : %@", [fetchedKey id]);
        NSLog(@"KeyBytes : %@", [fetchedKey key]);
        NSLog(@"MUTABLE ATTRIBUTES : ");
        for (NSObject* each in [[fetchedKey mutableAttributes] allKeys])
            NSLog(@"%@ : %@", each, [[[fetchedKey mutableAttributes] valueForKey:each] firstObject]);
        NSLog(@"");
        
        // define new mutable attributes
        NSMutableDictionary* updatedAttributes = [mutableArrributes mutableCopy];
        [updatedAttributes setObject:@[@"Highly Restricted"] forKey:@"classification"];
        [fetchedKey setMutableAttributes:updatedAttributes];
        
        IonicAgentUpdateKeysRequestKey* updatedKey = [[IonicAgentUpdateKeysRequestKey alloc] initWithKey:fetchedKey
                                                                                            ForceUpdate:YES];
        
        IonicAgentUpdateKeysResponse* updateResponse = [agent updateKey:updatedKey error:&error];
        
        // check for key update errors
        if (error) {
            IonicLogF_Error(TAG, @"Error updating key: %@", error);
            return (int) error.code;
        }
        
        IonicAgentUpdateKeysResponseKey* updatedResponseKey = [[updateResponse keys] firstObject];
        
        // display updated key
        NSLog(@"UPDATED KEY :");
        NSLog(@"");
        NSLog(@"KeyId    : %@", [updatedResponseKey id]);
        NSLog(@"KeyBytes : %@", [updatedResponseKey key]);
        NSLog(@"MUTABLE ATTRIBUTES : ");
        for (NSObject* each in [[updatedResponseKey mutableAttributes] allKeys])
            NSLog(@"%@ : %@", each, [[[updatedResponseKey mutableAttributes] valueForKey:each] firstObject]);
        NSLog(@"");
    }
        
    return IONIC_AGENT_OK;
}
