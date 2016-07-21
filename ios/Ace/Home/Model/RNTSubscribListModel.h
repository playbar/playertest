//
//  RNTSubscribListModel.h
//  Ace
//
//  Created by 靳峰 on 16/3/6.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface RNTSubscribListModel : NSObject

@property (nonatomic, copy) NSString *updateTime;

@property (nonatomic, copy) NSString *uuid;

@property (nonatomic, copy) NSString *profile;
//性别
@property (nonatomic, copy) NSString *sex;

@property (nonatomic, copy) NSString *channel;

@property (nonatomic, copy) NSString *rank;

@property (nonatomic, assign) NSInteger basicScore;

@property (nonatomic, copy) NSString *createTime;
//昵称
@property (nonatomic, copy) NSString *nickName;
//个性签名
@property (nonatomic, copy) NSString *signature;

@property (nonatomic, copy) NSString *userId;
//直播状态
@property (nonatomic, copy) NSString *status;

@property (nonatomic, copy) NSString *schemaId;

@end

