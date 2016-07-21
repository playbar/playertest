//
//  RNTSysConfigModel.h
//  Ace
//
//  Created by 靳峰 on 16/5/19.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@class RNTBootPicModel;

@interface RNTSysConfigModel : NSObject

//是否有广告
@property(nonatomic,copy) NSString *ad;
//动态启动页
@property(nonatomic,strong)RNTBootPicModel  *bootPic;
//礼物版本
@property(nonatomic,copy) NSString *giftVer;
//iOS版本
@property(nonatomic,copy) NSString *iosVer;
//是否认证
@property(nonatomic,copy) NSString *isVerify;
//商品版本
@property(nonatomic,copy) NSString *goodVer;
//进入房间提示
@property(nonatomic,copy) NSString *showTips;

//推送标题
@property(nonatomic,copy) NSString *liveShowPushTitle;
//分享标题
@property(nonatomic,copy) NSString *shareTitle;
//分享摘要
@property(nonatomic,copy) NSString *shareSummary;

/**
 *  存储总控模型
 *
 *  @param model 总控模型
 *
 *  @return 是否存储成功
 */
+ (BOOL)saveSysConfigModel:(RNTSysConfigModel *)model;

/**
 *  取出总控模型
 *
 *  @return  总控模型
 */
+ (RNTSysConfigModel *)getSysConfigModel;

@end
