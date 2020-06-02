//
//  main.m
//  encryption
//
//  Copyright © 2020 Ionic Security Inc. All rights reserved.
//  By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the Privacy Policy (https://www.ionic.com/privacy-notice/).
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
        [agent setMetadataValue:@"ionic-application-name" forField:@"ionic-encryption-objc-tutorial"];
        [agent setMetadataValue:@"ionic-application-version" forField:@"1.0.0"];
        
#pragma - mark SENDER
        
        NSString* message = @"this is a secret message!";
    
        
        // create new key
        IonicAgentCreateKeysRequestKey* key = [[IonicAgentCreateKeysRequestKey alloc] initWithReferenceId:@"refid1"
                                                                                                 quantity:1];
        IonicAgentCreateKeysRequest* request = [[IonicAgentCreateKeysRequest alloc] init];
        [request addKey:key];
        IonicAgentCreateKeysResponse* response = [agent createKeysUsingRequest:request error:&error];
        
        // check for key creation errors
        if (error) {
            IonicLogF_Error(TAG, @"Error creating key: %@", error);
            return (int) error.code;
        }
        
        // display new key
        IonicAgentCreateKeysResponseKey* createResponseKey = [response findKeyUsingRefId:@"refid1"];
        
        IonicCryptoAesCtrCipher*  senderCipher = [[IonicCryptoAesCtrCipher alloc] init];
        [senderCipher setKey:createResponseKey.key error:&error];
        
        // check for cipher initialization errors
        if (error) {
            IonicLogF_Error(TAG, @"Error initializing cipher: %@", error);
            return (int) error.code;
        }
        
        // encrypt data
        NSData* cipherData = [senderCipher encryptText:message error:&error];
        
        // check for cipher errors
        if (error) {
            IonicLogF_Error(TAG, @"Encryption Error: %@", error);
            return (int) error.code;
        }
        
        // encode the cipher text with a base64 encode.
        NSString* b64CipherText = [IonicCryptoUtils base64EncodeData:cipherData error:&error];
        
        // check for encode errors
        if (error) {
            IonicLogF_Error(TAG, @"Encryption Error: %@", error);
            return (int) error.code;
        }
        
        // put key id and base64 encoded string into a payload.
        NSDictionary * payload = @{
                                   @"key_id": createResponseKey.id,
                                   @"b64_ciphertext": b64CipherText
                                   };
        
        // Display Sender information.
        NSLog(@"CREATED KEYID : %@", createResponseKey.id);
        NSLog(@"CIPHERTEXT    : %@", cipherData);
        NSLog(@"PAYLOAD       : {\"key_id\":\"%@\", \"b64_ciphertext\":\"%@\"}", createResponseKey.id, b64CipherText);
        NSLog(@"");
        
#pragma - mark RECEIVER
        
        NSString* keyId = payload[@"key_id"];
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
        
        IonicAgentGetKeysResponseKey* fetchedKey = [[getKeyResponse keys] firstObject];
        
        IonicCryptoAesCtrCipher*  receiverCipher = [[IonicCryptoAesCtrCipher alloc] init];
        [receiverCipher setKey:fetchedKey.key error:&error];
        
        // check for cipher initialization errors
        if (error) {
            IonicLogF_Error(TAG, @"Error initializing cipher: %@", error);
            return (int) error.code;
        }
        
        // decode received data
        NSString* receivedCipherText = payload[@"b64_ciphertext"];
        NSData* receivedCipherData = [IonicCryptoUtils base64DecodeText:receivedCipherText error:&error];
        
        // check for decode errors
        if (error) {
            IonicLogF_Error(TAG, @"Error decoding payload: %@", error);
            return (int) error.code;
        }
        
        NSString* plaintext = [senderCipher decryptText:receivedCipherData error:&error];
        
        // check for cipher errors
        if (error) {
            IonicLogF_Error(TAG, @"Error: %@", error);
            return (int) error.code;
        }
        
        // Display receiver information
        NSLog(@"FETCHED KEYID : %@", [fetchedKey id]);
        NSLog(@"PLAINTEXT     : %@", plaintext);
    }
        
    return IONIC_AGENT_OK;
}
