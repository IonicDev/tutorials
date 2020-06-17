//
//  ViewController.h
//  MachinaSDKSample
//
//  Copyright © 2020 Ionic Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>

@interface ViewController : UIViewController <MFMailComposeViewControllerDelegate>

- (void) didRecieveProtectedFile:(NSURL*) fileUrl;

@end

