//
//  RNTCaptureEndModel.h
//  Ace
//
//  Created by Ranger on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RNTUser;

@interface RNTCaptureEndModel : NSObject

@property (nonatomic, copy) NSString *coins;

@property (nonatomic, copy) NSString *fans;

@property (nonatomic, copy) NSString *length;

@property (nonatomic, copy) NSString *memberCnt;

@property (nonatomic, copy) NSString *support;

@property (nonatomic, strong) RNTUser *user;

@end
