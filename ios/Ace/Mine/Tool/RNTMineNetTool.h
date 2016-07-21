//
//  RNTMineNetTool.h
//  Ace
//
//  Created by 周兵 on 16/3/9.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTMineNetTool : NSObject
/**
 *  更新个人资料
 *
 */
+ (void)updatePersonalInfoWithUserId:(NSString *)userId success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure;

/**
 *  修改签名
 *
 */
+ (void)modifySignatureWithUserId:(NSString *)userId signature:(NSString *)signature success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure;

/**
 *  修改签名
 *
 */
+ (void)modifyNickNameWithUserId:(NSString *)userId nickName:(NSString *)nickName success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure;

/**
 *  修改头像
 *
 */
+ (void)modifyIconWithUserId:(NSString *)userId iconData:(NSData *)iconData success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure;

/**
 *  意见反馈
 *
 *  @param userId      用户id
 *  @param contactInfo 联系方式
 *  @param contact     建议
 *  @param success     成功
 *  @param failure     失败
 */
+ (void)feedbackWithUserId:(NSString *)userId contactInfo:(NSString *)contactInfo contact:(NSString *)contact success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure;

/**
 *  获取粉丝列表
 *
 *  @param userId   用户id
 *  @param page     页数
 *  @param pageSize 每页条数
 *  @param success
 *  @param failure
 */
+ (void)getFansListWithUserId:(NSString *)userId page:(NSString *)page pageSize:(NSString *)pageSize success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  获取关注列表
 *
 *  @param userId   用户id
 *  @param page     页数
 *  @param pageSize 每页条数
 *  @param success
 *  @param failure
 */
+ (void)getSubscribeListWithUserId:(NSString *)userId page:(NSString *)page pageSize:(NSString *)pageSize success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  修改密码
 *
 *  @param userId
 *  @param oldPasswd
 *  @param newPasswd
 *  @param success
 *  @param failure   
 */
+ (void)changePWDWithUserId:(NSString *)userId oldPasswd:(NSString *)oldPasswd newPasswd:(NSString *)newPasswd success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  内购票据验证
 *
 *  @param receipt BASE64编码后的票据
 *  @param userId
 *  @param version
 *  @param success
 *  @param failure
 */
+ (void)verificationWithReceipt:(NSString *)receipt userId:(NSString *)userId version:(NSString *)version success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  修改性别
 *
 *  @param userId
 *  @param sex
 *  @param success
 *  @param failure
 */
+ (void)changeGenderWithUserId:(NSString *)userId sex:(NSString *)sex success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  获取兑点余额
 *
 *  @param userId
 *  @param success
 *  @param failure
 */
+ (void)getSilverWithUserId:(NSString *)userId success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  获取提现绑定信息
 *
 *  @param userId
 *  @param success
 *  @param failure 
 */
+ (void)getWithdrawBindInfoWithUserId:(NSString *)userId success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  绑定提现微信
 *
 *  @param userId
 *  @param weChatId
 *  @param verifyCode
 *  @param mobileId
 *  @param success
 *  @param failure
 */
+ (void)bindWechatWithUserId:(NSString *)userId weChatId:(NSString *)weChatId verifyCode:(NSString *)verifyCode mobileId:(NSString *)mobileId success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;

/**
 *  提现申请
 *
 *  @param userId
 *  @param coinAmount
 *  @param rmbAmount
 *  @param verifyCode
 *  @param success
 *  @param failure
 */
+ (void)withdrawWithUserId:(NSString *)userId coinAmount:(NSString *)coinAmount rmbAmount:(NSString *)rmbAmount verifyCode:(NSString *)verifyCode success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure;
@end
