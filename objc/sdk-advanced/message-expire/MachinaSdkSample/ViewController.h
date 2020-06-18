//
//  ViewController.h
//  MachinaSDKSample
//
//  Copyright © 2020 Ionic Security Inc. All rights reserved.
//  By using this code, I agree to the Terms & Conditions (https://dev.ionic.com/use.html) and the Privacy Policy (https://www.ionic.com/privacy-notice/).
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>

@interface ViewController : UIViewController <MFMailComposeViewControllerDelegate>

- (void) didRecieveProtectedFile:(NSURL*) fileUrl;

@end

