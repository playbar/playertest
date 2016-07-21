//
//  Foundation+Log.m
//  Beibaoke
//
//  Created by 张磊 on 14-10-30.
//  Copyright (c) 2014年 Make_ZL. All rights reserved.
//

#import <Foundation/Foundation.h>

@implementation NSDictionary (Log)

- (NSString *)descriptionWithLocale:(id)locale{
    NSMutableString *str = [NSMutableString string];
    
    [str appendString:@"{\n"];
    
    [self enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
        [str appendFormat:@"\t%@ : %@,\n", key, obj];
    }];
    
    [str appendString:@"}"];
    
    NSRange strRange = [str rangeOfString:@"," options:NSBackwardsSearch];
    if (strRange.length != 0) {
        [str deleteCharactersInRange:strRange];
    }
    
    return str;
}

@end

@implementation NSArray (Log)

- (NSString *)descriptionWithLocale:(id)locale{
    NSMutableString *str = [NSMutableString string];
    
    [str appendString:@"[\n"];
    
    [self enumerateObjectsUsingBlock:^(id obj, NSUInteger idx, BOOL *stop) {
        [str appendFormat:@"%@,\n",obj];
    }];
    
    [str appendString:@"]"];
    
    NSRange strRange = [str rangeOfString:@"," options:NSBackwardsSearch];
    if (strRange.length != 0) {
        [str deleteCharactersInRange:strRange];
    }
    
    return str;
}

@end