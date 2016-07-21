//
//  RNTPlayShowInfo.h
//  Ace
//
//  Created by 于传峰 on 16/3/14.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTUser.h"
@interface RNTPlayShowInfo : RNTUser

@property (nonatomic, copy) NSString *memberCount;
@property (nonatomic, copy) NSString *addLikeCount;
@property (nonatomic, copy) NSString *totalLikeCount;


- (instancetype)initWithUser:(RNTUser *)user;

@end
