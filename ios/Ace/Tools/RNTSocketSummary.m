//
//  RNTSocketSummary.m
//  Ace
//
//  Created by Ranger on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSocketSummary.h"
#import "RNTUserManager.h"

@implementation RNTSocketSummary
- (NSString *)msgId
{
    NSDate *currentDate = [NSDate date];
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyyMMddHHmmssSSS";
    formatter.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"zh"];
    
    NSString *dateStr = [formatter stringFromDate:currentDate];
    
    NSNumber *random =  @(arc4random() % 100000 + 100000);
    NSString *msgId = [NSString stringWithFormat:@"IOS%@%@",dateStr,random];
    
    return msgId;
}

- (NSString *)chatMode
{
    if (_chatMode == nil) {
        return @"1";
    }else {
        return _chatMode;
    }
}

- (NSString *)interactMode
{
    if (_interactMode == nil) {
        return @"0";
    }else {
        return _interactMode;
    }
}

- (NSString *)senderId
{
    if (_senderId == nil) {
        
        RNTUserManager * manger = [RNTUserManager sharedManager];
        if (manger.isLogged) {
            return manger.user.userId;
        }else {
            return @"-1";
        }
        
    }else {
        return _senderId;
    }
}

- (NSString *)msgType
{
    if (_msgType == nil) {
        return @"0";
    }else {
        return _msgType;
    }
}

- (NSString *)receiverId
{
    if (_receiverId == nil) {
        return @"999999";
    }else {
        return _receiverId;
    }
}


- (NSString *)senderName
{
    if (_senderId == nil) {
        return [RNTUserManager sharedManager].user.nickName;
    }else {
        return _senderName;
    }
}

- (NSString *)senderSex
{
    if (_senderSex == nil) {
        return [RNTUserManager sharedManager].user.sex;
    }else {
        return _senderSex;
    }
}

@end
