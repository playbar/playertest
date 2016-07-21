//
//  RNTHomeNetTool.h
//  Ace
//
//  Created by 靳峰 on 16/3/6.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTHomeNetTool : NSObject
/**
 *  获得直播列表
 *
 *  @param page    页数
 *  @param size    一页个数
 *  @param success 成功回调
 *  @param success 失败回调
 */
+ (void)getShowListWithPageNum:(NSInteger)page pageSize:(NSInteger)size  getSuccess:(void (^)(NSArray* showArr,NSArray* userArr))success getFail:(void (^)())failed;
/**
 *  获得关注列表
 *
 *  @param userID  用户ID
 *  @param success 成功回调
 *  @param failed  失败回调
 */
+ (void)getSubscribeListWithUserID:(NSString *)userID  getSuccess:(void (^)(NSArray* userArr,NSArray *showArr,NSString *headTittle))success getFail:(void (^)())failed;
/**
 *  获得首页banner数据
 */
+(void)getBannerDataSuccess:(void (^)(NSArray *bannerArr))success getFail:(void (^)())failed;
/**
 *  获取验证码
 */
+(void)getVerifyCodeWithPhoneNumber:(NSString *)number;

/**
 * 注册
 */
+(void)checkVerifyCodeWithPhoneNumber:(NSString *)number verifyCode:(NSString *)verify passWord:(NSString *)passwd;

/**
 *  搜索
 *
 *  @param text 搜索内容
 *  @param page 页数
 *  @param size 个数
 */
+(void)getSearchListWithText:(NSString *)text   pageNum:(NSInteger)page pageSize:(NSInteger)size getSuccess:(void (^)(NSArray *searchArr))success getFail:(void (^)())failed;

/**
 *  忘记密码
 *
 *  @param number  号码
 *  @param verify  验证码
 *  @param success 成功回调
 *  @param failed  失败回调
 */
+(void)changePassWordWithPhoneNum:(NSString *)number   verifyCode:(NSString *)verify newPWD:(NSString *)passwd checkSuccess:(void (^)()) success getFail:(void (^)(NSString *))failed;
@end
