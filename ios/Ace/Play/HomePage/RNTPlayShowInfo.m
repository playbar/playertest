//
//  RNTPlayShowInfo.m
//  Ace
//
//  Created by 于传峰 on 16/3/14.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayShowInfo.h"

@implementation RNTPlayShowInfo

- (instancetype)initWithUser:(RNTUser *)user
{
    if (self = [super init]) {
        self.updateTime = user.updateTime;
        self.uuid = user.uuid;
        self.profile = user.profile;
        self.sex = user.sex;
        self.channel = user.channel;
        self.passWord= user.passWord;
        self.rank = user.rank;
        self.basicScore = user.basicScore;
        self.createTime = user.createTime;
        self.nickName = user.nickName;
        self.signature = user.signature;
        self.userId = user.userId;
        self.status = user.status;
        self.schemaId = user.schemaId;
        self.tokenId = user.tokenId;
        self.subscribeCnt = user.subscribeCnt;
        self.fansCnt = user.fansCnt;
        self.showImg = user.showImg;
        self.postion = user.postion;
    }
    return self;
}

@end
