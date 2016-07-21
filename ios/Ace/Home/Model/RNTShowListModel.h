//
//  RNTShowListModel.h
//  Ace
//
//  Created by 靳峰 on 16/3/6.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTShowListModel : NSObject


@property (nonatomic, copy) NSString *length;
//位置
@property (nonatomic, copy) NSString *position;
//用户id
@property (nonatomic, copy) NSString *userId;
//房间ID
@property (nonatomic, copy) NSString *showId;

@property (nonatomic, copy) NSString *coinCnt;
//分享数
@property (nonatomic, copy) NSString *shareCnt;
//播流地址
@property (nonatomic, copy) NSString *downStreamUrl;
//标题
@property (nonatomic, copy) NSString *title;
//观众数
@property (nonatomic, copy) NSString *memberCnt;
//开播时间
@property (nonatomic, copy) NSString *startTime;
//关注数
@property (nonatomic, copy) NSString *supportCnt;

@property (nonatomic, copy) NSString *schemaId;

@property (nonatomic, copy) NSString *createTime;

@property (nonatomic, copy) NSString *sortCnt;
//高度
@property(nonatomic,assign) CGFloat cellHeight;

@end

