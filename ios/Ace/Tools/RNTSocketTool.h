//
//  RNTSocketTool.h
//  Ace
//
//  Created by Ranger on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RNTSocketSummary;

typedef void(^SocketConnectBlock)();

typedef void(^SocketBlock)(NSData *body, RNTSocketSummary *sum);

@protocol RNTSocketToolDelegate <NSObject>

/**
 *  @author Ranger, 16-03-03 16:03
 *  收到消息
 */
- (void)socketDidReceiveChatMessage:(NSData *)data summary:(RNTSocketSummary *)summary;

/**
 *  @author Ranger, 16-03-03 16:03
 *  用户进出直播间 "type":"1,进入房间,2,退出房间"
 */
- (void)socketDidReceiveUserInOutRoom:(NSData *)data summary:(RNTSocketSummary *)summary;

/**
 *  @author Ranger, 16-03-03 16:03
 *  直播间动态数据 人数 赞数
 */
- (void)socketDidReceiveRoomState:(NSData *)data summary:(RNTSocketSummary *)summary;

/**
 *  @author Ranger, 16-03-03 16:03
 *  礼物数据
 */
- (void)socketDidReceiveGift:(NSData *)data summary:(RNTSocketSummary *)summary;


/**
 *  @author Ranger, 16-05-17 10:05
 *  收到广播 type 1礼物 2分享 3点赞 4系统广播 5关注
 */
- (void)socketDidReceiveBroadcast:(NSData *)data summary:(RNTSocketSummary *)summary;

/**
 *  @author Ranger, 16-05-19 10:05
 *  封禁账号
 */
- (void)socketDidReceiveBannedAccount:(NSData *)data summary:(RNTSocketSummary *)summary;


@optional
/**
 *  @author Ranger, 16-03-03 16:03
 *  结束直播
 */
- (void)socketDidReceiveEnd:(NSData *)data summary:(RNTSocketSummary *)summary;

/**
 *  @author Ranger, 16-03-03 16:03
 *  踢人
 */
- (void)socketDidReceiveKickOut:(NSData *)data summary:(RNTSocketSummary *)summary;


/**
 *  @author Ranger, 16-05-19 10:05
 *  下播 针对主播
 */
- (void)socketDidReceiveDownLive:(NSData *)data summary:(RNTSocketSummary *)summary;


/**
 *  @author Ranger, 16-05-21 13:05
 *  暂停 & 恢复暂停
 */
- (void)socketDidReceivePause:(NSData *)data summary:(RNTSocketSummary *)summary;

@end

@interface RNTSocketTool : NSObject

@property (nonatomic, copy) SocketBlock socketResponse;// 发送消息后的回调

@property (nonatomic, weak) id<RNTSocketToolDelegate> delegate;

@property (nonatomic, assign) BOOL isConnected;// 是否连接

/**
 *  @author Ranger, 16-03-03 11:03
 *
 *  创建socket工具
 *
 *  @return socketTool
 */
+ (RNTSocketTool *)shareInstance;


#pragma mark - socket连接
/**
 *  @author Ranger, 16-03-03 14:03
 *
 *  创建socket
 */
- (void)socketSetup;


/**
 *  @author Ranger, 16-03-03 14:03
 *
 *  联接socket
 */
- (void)socketConnectSuccess:(SocketConnectBlock)success fail:(SocketConnectBlock)fail;


/**
 *  @author Ranger, 16-03-03 14:03
 *
 *  断开socket
 */
- (void)socketDisconnect;

#pragma mark - 发数据
/**
 *  @author Ranger, 16-03-03 21:03
 *
 *  发送数据
 *
 *  @param parameter 发送的数据
 *  @param summary   摘要
 *  @param tag       tag
 */
- (void)writeMessage:(NSMutableDictionary *)parameter andSummary:(RNTSocketSummary *)summary withTag:(long)tag;


/**
 *  @author Ranger, 16-03-03 21:03
 *
 *  发送data
 *
 *  @param data    data
 *  @param summary 摘要
 *  @param tag     tag
 */
- (void)writeData:(NSMutableData *)data andSummary:(RNTSocketSummary *)summary withTag:(long)tag;

//
@end
