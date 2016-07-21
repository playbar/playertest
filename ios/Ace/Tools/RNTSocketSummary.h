//
//  RNTSocketSummary.h
//  Ace
//
//  Created by Ranger on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTSocketSummary : NSObject
@property (nonatomic, copy) NSString *requestKey;

@property (nonatomic, copy) NSString *fileName;

@property (nonatomic, copy) NSString *senderSex;
@property (nonatomic, copy) NSString *senderName;
@property (nonatomic, copy) NSString *sendTime;
@property (nonatomic, copy) NSString *duration;// 语音时常

@property (nonatomic, copy) NSString *receiverName;

//@property (nonatomic, copy) NSString *groupId;
//@property (nonatomic, copy) NSString *groupName;

@property (nonatomic, copy) NSString *showId;// 直播间id

@property (nonatomic, copy) NSString *chatMode; // 默认 公聊  私聊0 公聊1
@property (nonatomic, copy) NSString *interactMode;// 默认@"REQUEST"
@property (nonatomic, copy) NSString *msgId;// 不用设置 默认当前时间+随机数
@property (nonatomic, copy) NSString *msgType;// 文件类型 文本 / 文件 1是文本消息 2 媒体消息 3 用户图像 4 群图像 ,...,8群站内分享
@property (nonatomic, copy) NSString *senderId;// 默认用户userId
@property (nonatomic, copy) NSString *receiverId;// 默认 系统用户@"999999"
@end
