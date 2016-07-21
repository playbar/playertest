//
//  RNTCaptureNetTool.h
//  Ace
//
//  Created by Ranger on 16/3/11.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RNTCaptureStartModel, RNTCaptureEndModel;

@interface RNTCaptureNetTool : NSObject


/**
 *  @author Ranger, 16-03-12 20:03
 *  发送开播请求
 *  @param model   model
 */
+ (void)sendStartWithModel:(RNTCaptureStartModel *)model success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSDictionary *dict))failure;

/**
 *  @author Ranger, 16-03-15 19:03
 *  结束直播结算数据
 *  @param showId  showId
 *  @param success 结算模型
 */
+ (void)sendEndWithShowId:(NSString *)showId success:(void (^)(RNTCaptureEndModel *endModel))success;

/**
 *  @author Ranger, 16-05-16 14:05
 *  获取用户认证状态
 *  @param userId userId
 */
+ (void)getUserIdentificationState:(NSString *)userId success:(void (^)(NSDictionary *dict))success;


@end
