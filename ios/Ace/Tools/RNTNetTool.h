//
//  AFNTool.h
//  AFN封装
//
//  Created by 杜 维欣 on 15/6/2.
//  Copyright (c) 2015年 ADo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTNetTool : NSObject

/**
 *  get方法HTTP请求
 *
 *  @param requestKey     如001-001
 *  @param params  参数
 *  @param success 成功
 *  @param failure 失败
 */
+ (void)getHTTPWithURL:(NSString *)requestKey params:(NSDictionary *)params
                success:(void (^)(id responseHTTP))success
                failure:(void (^)(NSError *error))failure;


/**
 *  get方法请求json格式的数据
 *
 *  @param requestKey     如001-001
 *  @param params  参数
 *  @param success 成功
 *  @param failure 失败
 */
+ (void)getJSONWithURL:(NSString *)requestKey params:(NSDictionary *)params
               success:(void (^)(NSDictionary *responseDic))success
               failure:(void (^)(NSError *error))failure;


/**
 *  post方法HTTP请求
 *
 *  @param requestKey     如001-001
 *  @param params  参数
 *  @param success 成功
 *  @param failure 失败
 */
+ (void)postHTTPWithURL:(NSString *)requestKey params:(NSDictionary *)params
                success:(void (^)(id responseHTTP))success
                failure:(void (^)(NSError *error))failure;


/**
 *  post方法请求json格式的数据
 *
 *  @param requestKey     如001-001
 *  @param params  参数
 *  @param success 成功
 *  @param failure 失败
 */
+ (void)postJSONWithURL:(NSString *)requestKey params:(NSDictionary *)params
                success:(void (^)(NSDictionary *responseDic))success
                failure:(void (^)(NSError *error))failure;


/**
 *  上传图片
 *
 *  @param requestKey     如001-001
 *  @param params  参数
 *  @param data    二级制文件
 *  @param success 成功
 *  @param failure 失败
 */
+ (void)postWithURL:(NSString *)requestKey params:(NSDictionary *)params
               data:(NSData *)data dataKey:(NSString *)dataKey
            success:(void (^)(NSDictionary *dict))success
            failure:(void (^)(NSError *error))failure;

/**
 *  获取验证码
 *
 *  @param number  手机号
 *  @param success 成功回调
 *  @param failure 失败回调 参数为错误信息
 */
+(void)getVerifyCodeWithPhoneNumber:(NSString *)number  success:(void (^)())success  failure:(void (^)(NSString *error ))failure;

/**
 *  获取NGB推拉流地址
 *
 *  @param url     原始地址
 *  @param success 成功回调
 *  @param failure 失败回调
 */
+ (void)getNGBWithOriginalURL:(NSString *)originalUrl success:(void (^)(NSString *rtmpString))success failure:(void (^)(NSError *error))failure;
@end
