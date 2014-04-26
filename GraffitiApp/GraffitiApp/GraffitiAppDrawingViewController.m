//
//  GraffitiAppDrawingViewController.m
//  GraffitiApp
//
//  Created by Aaron Rosen on 4/25/14.
//  Copyright (c) 2014 WesHack. All rights reserved.
//

#import "GraffitiAppDrawingViewController.h"

@interface GraffitiAppDrawingViewController ()

@end

@implementation GraffitiAppDrawingViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    self.location = [touch locationInView:_drawingView];
}

-(void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint currentLocation = [touch locationInView:_drawingView];
    
    UIGraphicsBeginImageContext(_drawingView.frame.size);
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    [_drawingView.image drawInRect:CGRectMake(0, 0, _drawingView.frame.size.width, _drawingView.frame.size.height)];
    CGContextSetLineCap(ctx, kCGLineCapRound);
    CGContextSetLineWidth(ctx, 5.0);
    CGContextSetRGBStrokeColor(ctx, 1.0, 0.0, 0.0, 1.0);
    CGContextBeginPath(ctx);
    CGContextMoveToPoint(ctx, location.x, location.y);
    CGContextAddLineToPoint(ctx, currentLocation.x, currentLocation.y);
    CGContextStrokePath(ctx);
    _drawingView.image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    location = currentLocation;
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    CGPoint currentLocation = [touch locationInView:_drawingView];
    
    UIGraphicsBeginImageContext(_drawingView.frame.size);
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    [_drawingView.image drawInRect:CGRectMake(0, 0, _drawingView.frame.size.width, _drawingView.frame.size.height)];
    CGContextSetLineCap(ctx, kCGLineCapRound);
    CGContextSetLineWidth(ctx, 5.0);
    CGContextSetRGBStrokeColor(ctx, 1.0, 0.0, 0.0, 1.0);
    CGContextBeginPath(ctx);
    CGContextMoveToPoint(ctx, location.x, location.y);
    CGContextAddLineToPoint(ctx, currentLocation.x, currentLocation.y);
    CGContextStrokePath(ctx);
    _drawingView.image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    location = currentLocation;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
