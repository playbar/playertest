//
//  RNTDatabaseTool.h
//  Ace
//
//  Created by 周兵 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>
@class RNTUser;

@interface RNTDatabaseTool : NSObject
/**
 *  获取数据库中缓存的首页主播列表
 *
 *  @return 首页字典
 */
+ (NSDictionary *)getHomepageList;

/**
 *  更新首页主播列表
 *
 *  @param anchorList 主播字典数组
 */
+ (void)updataHomepageWithShowList:(NSArray *)anchorList userList:(NSArray *)userList;

/**
 *  存储浏览记录
 *
 *  @param anchor 主播对象
 */
+ (void)saveAnchor:(RNTUser *)anchor;
/**
 *  获取浏览记录
 *
 *  @return 主播对象数组
 */
+ (NSArray *)getAnchorHistory;

/**
 *  是否为黑名单用户
 *
 */
+ (BOOL)isBlackList:(RNTUser *)anchor;
/**
 *  从黑名单删除
 *
 */
+ (void)deleteFromBlackList:(RNTUser *)anchor;
/**
 *  加入黑名单
 *
 */
+ (void)saveBlackList:(RNTUser *)anchor;
/**
 *  获取黑名单
 *
 *  @return 用户数组
 */
+ (NSArray *)getBlackList;
/**
 *  存储购买凭据
 *
 *  @param receiptDict 购买凭据字典  包括凭据(NSData)  版本号  userId 时间戳
 */
+ (void)saveReceiptDict:(NSDictionary *)receiptDict;
/**
 *  获取未验证凭据
 *
 *  @return 凭据数组
 */
+ (NSArray *)getReceiptArray;
/**
 *  删除购买凭据
 *
 *  @param receiptDict 
 */
+ (void)deleteReceiptWithReceipt:(NSDictionary *)receiptDict;
///**
// *  存储JPushRegistrationID
// *
// *  @param registrationID JPushRegistrationID
// *  @param userId         userID
// */
//+ (void)saveJPushRegistrationID:(NSString *)registrationID userID:(NSString *)userID;
///**
// *  删除userID相对应的registrationID
// *
// *  @param userID userID
// */
//+ (void)deleteRegistrationIDWithUserID:(NSString *)userID;
///**
// *  获取userID相对应的registrationID
// *
// *  @param userID userID
// *
// *  @return registrationID数组
// */
//+ (NSArray *)getRegistrationIDWithUserID:(NSString *)userID;
@end
