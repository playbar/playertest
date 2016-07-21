//
//  RNTCaptureEndModel.m
//  Ace
//
//  Created by Ranger on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureEndModel.h"
#import "RNTUserManager.h"

@implementation RNTCaptureEndModel

- (RNTUser *)user
{
    if (_user == nil) {
        return [RNTUserManager sharedManager].user;
    }else {
        return _user;
    }
}

- (NSString *)length
{
    if (_length) {

        NSInteger time = _length.integerValue;
        NSInteger seconds = time % 60;
        NSInteger minutes = (time / 60) % 60;
        NSInteger hours = time / 3600;
        NSString *second;
        NSString *minute;
        NSString *hour;
        if (seconds < 10) {
             second = [NSString stringWithFormat:@"0%@", @(seconds)];
        }else {
            second = [NSString stringWithFormat:@"%@", @(seconds)];
        }
        
        if (minutes < 10) {
            minute = [NSString stringWithFormat:@"0%@", @(minutes)];
        }else {
            minute = [NSString stringWithFormat:@"%@", @(minutes)];
        }
        
        if (hours < 10) {
            hour = [NSString stringWithFormat:@"0%@", @(hours)];
        }else {
            hour = [NSString stringWithFormat:@"%@", @(hours)];
        }
        
        _length = [NSString stringWithFormat:@"%@:%@:%@", hour, minute, second];
    }
    return _length;
}

@end
