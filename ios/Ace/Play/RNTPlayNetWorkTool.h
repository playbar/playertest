//
//  RNTPlayNetWorkTool.h
//  Ace
//
//  Created by 于传峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTPlayNetWorkTool : NSObject
/*
 showID: 直播ID
 userID： 用户ID
 */
#pragma mark - 观众列表
+ (void)getLivingUserListWithPageNum:(NSInteger)page pageSize:(NSInteger)size showID:(NSString *)showID getSuccess:(void (^)(NSDictionary* dict))success;
#pragma mark - 用户余额
+ (void)getUserBlanceGetSuccess:(void (^)(NSUInteger count))success;

#pragma mark - 用户资料
+ (void)getUserInfoWithUserID:(NSString *)userID getSuccess:(void (^)(NSDictionary* dict, BOOL attentionState))success;

#pragma mark - 关注
+ (void)followUserWithCurrentUserID:(NSString *)userID followedUserID:(NSString *)followedID getSuccess:(void (^)(BOOL status))success;
#pragma mark - 取消关注
+ (void)cancelFollowUserWithCurrentUserID:(NSString *)userID followedUserID:(NSString *)followedID getSuccess:(void (^)(BOOL status))success;

#pragma mark - 禁言
+(void)silenceWithShowID:(NSString *)showID silenceID:(NSString *)userID;
#pragma mark - 举报
+ (void)reportUserWithCurrentUserID:(NSString *)userID reportedUserID:(NSString *)reportedID getSuccess:(void (^)(BOOL status))success;
#pragma mark - 分享
+ (void)shareUserWithCurrentUserID:(NSString *)userID sharedShowID:(NSString *)showID shareChannle:(NSString *)channle getSuccess:(void (^)(BOOL status))success;

#pragma mark - 关注列表
+ (void)getSubscribeListWithPageNum:(NSInteger)page pageSize:(NSInteger)size userID:(NSString *)userID  getSuccess:(void (^)(NSDictionary* dict))success;
#pragma mark - 粉丝列表
+ (void)getFansListWithPageNum:(NSInteger)page pageSize:(NSInteger)size userID:(NSString *)userID  getSuccess:(void (^)(NSDictionary* dict))success;
#pragma mark - 礼物列表
+ (void)getGiftListGetSuccess:(void (^)(NSDictionary* dict))success;

#pragma mark - 分享内容
+ (void)getShareInfoWithShowID:(NSString *)showID type:(int)type getSuccess:(void (^)(NSDictionary* dict))success;

#pragma mark - socket接口 -
#pragma mark - 赠送礼物
+ (void)sendGiftWithGiftID:(NSString *)giftID senderID:(NSString *)senderID receiverID:(NSString *)receiverID showID:(NSString *)showID giftCount:(NSUInteger)count sendSuccess:(void (^)(NSDictionary*))success;
#pragma mrak - 发送聊天
+ (void)sendChatWithReceiverID:(NSString *)recID showID:(NSString *)showID message:(NSString *)message sendSuccess:(void (^)(NSDictionary *))success;

#pragma mark - 点赞
+ (void)sendLikeWithSenderID:(NSString*)senderID showID:(NSString *)showID sendSuccess:(void(^)(NSDictionary* dict))success;

#pragma mark - 连接socket
+ (void)connetSocketWithShowID:(NSString *)showID sendSuccess:(void(^)(NSDictionary* dict))success;


#pragma mark - 下载图片
+ (void)reDownloadGiftImages;
+ (void)downLoadGiftSources:(NSDictionary *)dict;

+ (UIImage *)giftImageWithGiftID:(NSString *)giftID;
+(void)downLoadGiftAnimationSources:(NSString *)giftID;

@end
