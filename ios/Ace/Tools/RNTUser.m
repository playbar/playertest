//
//  RNTUser.m
//  Ace
//
//  Created by 周兵 on 16/3/7.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTUser.h"
#import "MJExtension.h"

@implementation RNTUser
//旧值更换新值
+ (NSDictionary *)mj_replacedKeyFromPropertyName
{
    return @{
             @"fansCnt" : @"extendData.fansCnt",
             @"postion" : @"extendData.postion",
             @"isShow" : @"extendData.isShow",
             @"showId":@"extendData.showId",
             @"downStreanUrl":@"extendData.downStreanUrl",
             @"subscribeCnt":@"extendData.subscribeCnt"
             };
}
//更改类型
- (id)mj_newValueFromOldValue:(id)oldValue property:(MJProperty *)property
{
    if (property.type.typeClass == [NSNumber class]) {
        return [NSString stringWithFormat:@"%@", oldValue];
    }
    return oldValue;
}

- (NSString *)userId
{
    if (!_userId) {
        _userId = @"-1";
    }
    return _userId;
}

@end


