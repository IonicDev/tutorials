//
//  ViewController.m
//  MachinaSDKSample
//
//  Copyright © 2020 Ionic Inc. All rights reserved.
//

#import "ViewController.h"

#import <IonicAgentSDK/IonicAgentSDK.h>

@interface ViewController ()
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UILabel *heading;
@property (weak, nonatomic) IBOutlet UITextField *email;
@property (weak, nonatomic) IBOutlet UITextField *message;

@property (strong, nonatomic) IonicAgentDeviceProfilePersistorPassword* profilePersistor;

@end

@implementation ViewController

- (instancetype) initWithCoder:(NSCoder *)aDecoder {
    
    if (self = [super initWithCoder:aDecoder]) {
        
        NSString* persistorPassword = [[[NSProcessInfo processInfo] environment] objectForKey:@"IONIC_PERSISTOR_PASSWORD"];
        if(persistorPassword) {
        
            NSString *libraryDirectory = [NSSearchPathForDirectoriesInDomains (NSLibraryDirectory, NSUserDomainMask, YES) objectAtIndex:0];
            NSString *filePath = [libraryDirectory stringByAppendingPathComponent:@".ionicsecurity/profiles.pw"];
        
            [self createSampleProfile:filePath];
        
            self.profilePersistor = [[IonicAgentDeviceProfilePersistorPassword alloc] init];
            self.profilePersistor.filePath = filePath;
            self.profilePersistor.password = persistorPassword;
        }
    }
    
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    _heading.center = CGPointMake(_heading.center.x-self.view.frame.size.width, _heading.center.y);
    _email.center = CGPointMake(_email.center.x-self.view.frame.size.width, _email.center.y);
    _message.center = CGPointMake(_message.center.x-self.view.frame.size.width, _message.center.y);
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [UIView animateWithDuration:0.5 animations:^{
        _heading.center = CGPointMake(_heading.center.x+self.view.frame.size.width, _heading.center.y);
    }];
    
    [UIView animateWithDuration:0.5 delay:0.3 options:0 animations:^{
        _email.center = CGPointMake(_email.center.x+self.view.frame.size.width, _email.center.y);
    } completion:nil];
    [UIView animateWithDuration:0.5 delay:0.4 options:0 animations:^{
        _message.center = CGPointMake(_message.center.x+self.view.frame.size.width, _message.center.y);
    } completion:nil];
    
    if( ! self.profilePersistor) {
        
        [self showDialogWithTitle:@"Error"
                       andMessage:@"IONIC_PERSISTOR_PASSWORD is not set."];
    }
}

- (IBAction)sendOnClick:(id)sender {
    
    NSString* email = [_email text];
    NSString* mesage = [_message text];
    
    if ([MFMailComposeViewController canSendMail]) {
        
        NSError* error = nil;
        NSString* protectMessage = [self protectMessage:mesage error:&error];
        
        if(error) {
            [self showDialogWithTitle:@"Error" andMessage:error.localizedDescription];
            return;
        }
        
        MFMailComposeViewController *mailVC = [[MFMailComposeViewController alloc] init];
        mailVC.mailComposeDelegate = self;
        [mailVC setToRecipients:@[email]];
        [mailVC setSubject:@"Machina Protected Message"];
        [mailVC addAttachmentData:[protectMessage dataUsingEncoding:NSUTF8StringEncoding]
                         mimeType:@"text/plain"
                         fileName:@"message.mpf"];
        
        [self presentViewController:mailVC animated:YES completion:nil];

    } else {
        
        //This device cannot send email
        [self presentMailRequiredDialog];
    }

}

- (void)mailComposeController:(MFMailComposeViewController *)controller didFinishWithResult:(MFMailComposeResult)result error:(nullable NSError *)error {
    
    [controller dismissViewControllerAnimated:YES completion:^{
        NSString* email = _email.text;
        _email.text = nil;
        _message.text = nil;
        [self showDialogWithTitle:@"Protected Message Sent"
                       andMessage:[NSString stringWithFormat:@"Message Sent to: %@", email]];
    }];
}

- (void) presentMailRequiredDialog {
    
    UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"Mail App Required"
                                                                   message:@"This device does not have the 'Mail' app installed. If running on a simulator try installing this application on a physical device."
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* defaultAction = [UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault
                                                          handler:^(UIAlertAction * action) {}];
    
    [alert addAction:defaultAction];
    [self presentViewController:alert animated:YES completion:nil];
}

- (NSString *) protectMessage:(NSString *) message error:(NSError **) error {
        
    NSError* e = nil;
    // create an agent and initialize it with the password persistor all defaults
    IonicAgent * agent = [[IonicAgent alloc] initWithDefaults:self.profilePersistor
                                                        error:&e];
    // check for initialization error
    if (e)
    {
        IonicLogF_Error(@"MyApplicationChannel", @"Failed to initialize agent object, error code = %d.", e.code);
        *error = e;
        return nil;
    }
    // create a file cipher which will use our agent for secure communication
    // with Ionic API servers
    IonicChunkCryptoCipherAuto * cipher = [[IonicChunkCryptoCipherAuto alloc] initWithAgent:agent];
    
    NSDictionary * d = @{ @"ionic-expiration" : @[[self getExpireDate:1]]};
    IonicChunkCryptoEncryptAttributes* attributes = [[IonicChunkCryptoEncryptAttributes alloc] initWithKeyAttributes:d];
    
    // encrypt a sensitive string
    NSString* encryptedMessage = [cipher encryptText:message withAttributes:attributes error:&e];
    if (e)
    {
        IonicLogF_Error(@"MyApplicationChannel", @"Failed to encrypt message, error code = %d.", e.code);
        *error = e;
        return nil;
    }
    
    return encryptedMessage;
}

- (NSString *) decodeMessage:(NSString *)protectedMessage error:(NSError **) error {
        
    NSError* e = nil;
    
    // create an agent and initialize it using all defaults
    IonicAgent * agent = [[IonicAgent alloc] initWithDefaults:self.profilePersistor error:&e];
    // check for initialization error
    if (e)
    {
        IonicLogF_Error(@"MyApplicationChannel", @"Failed to initialize agent object, error code = %d.", (*error).code);
        *error = e;
        return nil;
    }
    // create a file cipher which will use our agent for secure communication
    // with Ionic API servers
    IonicChunkCryptoCipherAuto * cipher = [[IonicChunkCryptoCipherAuto alloc] initWithAgent:agent];
    
    // encrypt a sensitive file in place
    NSString* plainTextMessage = [cipher decryptText:protectedMessage error:&e];
    if (e)
    {
        IonicLogF_Error(@"MyApplicationChannel", @"Failed to decrypt a string, error code = %d.", (*error).code);
        *error = e;
        return nil;
    }
    return plainTextMessage;
}

// We bundled a profile persistor created on a desktop using the ionic-profiles tool into the application bundle.
// this method will copy it onto the file system if it hasn't already done so during a previous app launch.
- (void) createSampleProfile: (NSString*) destPath {
    
    NSFileManager* fileManager = [NSFileManager defaultManager];
    BOOL alreadyExists = [fileManager fileExistsAtPath:destPath];
    if( ! alreadyExists) {
        
        NSError *error;
        NSString* ionicFolder = [destPath stringByDeletingLastPathComponent];
        if( ! [fileManager createDirectoryAtPath:ionicFolder
                     withIntermediateDirectories:NO
                                      attributes:nil
                                           error:&error]) {
            IonicLogF_Error(@"MyApplicationChannel", @"Failed to create a file, error code = %d.", error.code);
        }
        
        NSString *srcPath = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:@"profiles.pw"];
        if( ! [[NSFileManager defaultManager] copyItemAtPath:srcPath
                                                      toPath:destPath
                                                       error:&error]) {
            
            IonicLogF_Error(@"MyApplicationChannel", @"Failed to create a file, error code = %d.", error.code);
        }
    }
}

- (void) didRecieveProtectedFile:(NSURL*) fileUrl {
    
    NSError* error = nil;
    
    NSString *fileContent = [[NSString alloc]initWithContentsOfFile:fileUrl.path encoding:NSUTF8StringEncoding error:nil];
    NSString* message = [self decodeMessage:fileContent error:&error];

    NSString* title;
    if(error) {
        title = @"Error";
        message = [error localizedDescription];
    } else {
        title = @"Protected Message";
    }
    
    [self showDialogWithTitle:title andMessage:message];
}

- (NSString*) getExpireDate:(int) minFromNow {
    
    NSDate* expireDate = [[NSDate date] dateByAddingTimeInterval:minFromNow*60];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssXXX"];
    
    NSString* formattedDate = [dateFormatter stringFromDate:expireDate];
    
    return formattedDate;
}

- (void) showDialogWithTitle:(NSString*) title andMessage:(NSString*) message {
    
    UIAlertController * alert = [UIAlertController alertControllerWithTitle:title
                                                                    message:message
                                                             preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* okButton = [UIAlertAction
                                actionWithTitle:@"OK"
                                style:UIAlertActionStyleDefault
                                handler:nil];
    [alert addAction:okButton];
    [self presentViewController:alert animated:YES completion:nil];
}

@end
