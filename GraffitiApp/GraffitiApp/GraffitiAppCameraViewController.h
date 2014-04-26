//
//  GraffitiAppCameraViewController.h
//  GraffitiApp
//
//  Created by Aaron Rosen on 4/25/14.
//  Copyright (c) 2014 WesHack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GraffitiAppCameraViewController : UIViewController

@property (strong, nonatomic) IBOutlet UIImageView *imageView;

- (IBAction)takePhoto:(id)sender;

@end
