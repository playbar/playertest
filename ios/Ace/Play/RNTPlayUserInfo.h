//
//  RNTPlayUserInfo.h
//  Ace
//
//  Created by 于传峰 on 16/3/10.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTUser.h"
typedef NS_ENUM(NSUInteger, RNTUserRole) {
    RNTUserRoleUser = 0,
    RNTUserRoleHost,
};
@interface RNTPlayUserInfo : RNTUser

@property (nonatomic, copy) NSString *location;
@property (nonatomic, copy) NSString *fansCount;

@property (nonatomic, assign) RNTUserRole role;

@property (nonatomic, assign, getter=isAttentioned) BOOL attentionStated;

+ (instancetype)playUserInfoWithDict:(NSDictionary *)dict;
@end
