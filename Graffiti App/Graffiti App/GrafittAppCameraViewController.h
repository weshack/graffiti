//
//  GrafittAppCameraViewController.h
//  Graffiti App
//
//  Created by Aaron Rosen on 4/25/14.
//  Copyright (c) 2014 WesHack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GrafittAppCameraViewController : UIViewController <UIImagePickerControllerDelegate, UINavigationControllerDelegate>

@property (strong, nonatomic) IBOutlet UIImageView *cameraImageView;

- (IBAction)takePhotoButton:(id)sender;

@end
