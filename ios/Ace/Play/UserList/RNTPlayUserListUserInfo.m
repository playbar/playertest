//
//  RNTPlayUserListUserInfo.m
//  Ace
//
//  Created by 于传峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayUserListUserInfo.h"

@implementation RNTPlayUserListUserInfo

+ (instancetype)userInfoWithDict:(NSDictionary *)dict
{
    RNTPlayUserListUserInfo* info = [[self alloc] init];
    
    info.userId = dict[@"userId"];
    info.nickName = dict[@"nickName"];
    info.signature = dict[@"signature"];
    info.profile = dict[@"profile"];
    info.sex = dict[@"sex"];
    
    return info;
}

+ (NSArray *)userInfoWithDictArray:(NSArray *)dictArray
{
    NSMutableArray* infos = [[NSMutableArray alloc] init];
    for (NSDictionary* dict in dictArray) {
        [infos addObject:[self userInfoWithDict:dict]];
    }
    
    return infos;
}

@end
