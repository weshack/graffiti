//
//  GraffitiAppDrawingViewController.h
//  GraffitiApp
//
//  Created by Aaron Rosen on 4/25/14.
//  Copyright (c) 2014 WesHack. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface GraffitiAppDrawingViewController : UIViewController{
    CGPoint location;
}

@property (strong, nonatomic) IBOutlet UIImageView *drawingView;
@property CGPoint location;

@end
