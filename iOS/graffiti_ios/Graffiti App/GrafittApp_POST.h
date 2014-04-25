//
//  GrafittApp_POST.h
//  Graffiti App
//
//  Created by Kevin on 4/25/14.
//  Copyright (c) 2014 WesHack. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface GrafittApp_POST : NSObject


- (NSString *) getDataFrom:(NSString *)url withBody:(NSData *)body;

@end
