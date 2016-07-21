//
//  RNTUser.h
//  Ace
//
//  Created by 周兵 on 16/3/7.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface RNTUser : NSObject

@property (nonatomic, copy) NSString *updateTime;

@property (nonatomic, copy) NSString *uuid;
//头像
@property (nonatomic, copy) NSString *profile;
//0 女  1 男
@property (nonatomic, copy) NSString *sex;
//渠道: 1微信 2 qq  3 手机登录 4 本地 5 微博
@property (nonatomic, copy) NSString *channel;
//密码
@property (nonatomic, copy) NSString *passWord;
//级别
@property (nonatomic, copy) NSString *rank;

@property (nonatomic, copy) NSString *basicScore;
//播流地址
@property (nonatomic, copy) NSString *downStreanUrl;

@property (nonatomic, copy) NSString *createTime;
//昵称
@property (nonatomic, copy) NSString *nickName;
//个性签名
@property (nonatomic, copy) NSString *signature;
//用户ID
@property (nonatomic, copy) NSString *userId;
//1 正常
@property (nonatomic, copy) NSString *status;

@property (nonatomic, copy) NSString *schemaId;
//三方登录openid
@property (nonatomic, copy) NSString *tokenId;
//关注数
@property (nonatomic, copy) NSString *subscribeCnt;
//粉丝数
@property(nonatomic,copy) NSString *fansCnt;
//封面
@property(nonatomic,copy) NSString *showImg;
//位置信息
@property(nonatomic,copy) NSString *postion;
//1.直播 0.没直播
@property(nonatomic,copy) NSString *isShow;
//房间ID
@property (nonatomic, copy) NSString *showId;

@end

