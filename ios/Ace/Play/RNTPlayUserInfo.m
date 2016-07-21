//
//  RNTPlayUserInfo.m
//  Ace
//
//  Created by 于传峰 on 16/3/10.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayUserInfo.h"

@implementation RNTPlayUserInfo

+ (instancetype)playUserInfoWithDict:(NSDictionary *)dict
{
    RNTPlayUserInfo* info = [[self alloc] init];
    RNTLog(@"%@",dict);
    NSDictionary* extendData = dict[@"extendData"];
    info.location = extendData[@"postion"] ? extendData[@"postion"] : @"地球的背面";
    info.fansCount = extendData[@"fansCnt"];
    info.attentionStated = [extendData[@"relatoin"] boolValue];
    
    info.nickName = dict[@"nickName"];
    info.profile = dict[@"profile"];
    info.sex = dict[@"sex"];
    info.signature = dict[@"signature"];
    info.userId = dict[@"userId"];
    
    return info;
}

@end
