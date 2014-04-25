//
//  GrafittApp_POST.m
//  Graffiti App
//
//  Created by Kevin on 4/25/14.
//  Copyright (c) 2014 WesHack. All rights reserved.
//

#import "GrafittApp_POST.h"

@implementation GrafittApp_POST

//passes data via the line [request setHTTPBody:body] which arrives in the HTTP body
- (NSString *) getDataFrom:(NSString *)url withBody:(NSData *)body{
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:body];
    [request setValue:[NSString stringWithFormat:@"%d", [body length]] forHTTPHeaderField:@"Content-Length"];
    [request setURL:[NSURL URLWithString:url]];
    
    NSError *error = [[NSError alloc] init];
    NSHTTPURLResponse *responseCode = nil;
    
    NSData *oResponseData = [NSURLConnection sendSynchronousRequest:request returningResponse:&responseCode error:&error];
    
    if([responseCode statusCode] != 200){
        NSLog(@"Error getting %@, HTTP status code %i", url, [responseCode statusCode]);
        return nil;
    }
    
    return [[NSString alloc] initWithData:oResponseData encoding:NSUTF8StringEncoding];
}

]@end

