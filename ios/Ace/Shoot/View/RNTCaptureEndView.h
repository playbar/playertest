//
//  RNTCaptureEndView.h
//  Ace
//
//  Created by Ranger on 16/3/17.
//  Copyright © 2016年 RNT. All rights reserved.
//  直播结束结算页

#import <UIKit/UIKit.h>

@class RNTCaptureEndModel;

typedef void(^RNTCaptureEndViewEnsureClick)();

@interface RNTCaptureEndView : UIView

//模型数据
@property(nonatomic,strong) RNTCaptureEndModel *model;

//按钮点击
@property(nonatomic,copy) RNTCaptureEndViewEnsureClick ensureClickBlock;


/**
 *  @author Ranger, 16-03-18 10:03
 *
 *  显示直播结束结算view
 *
 *  @param view     父view
 *  @param showId   showId
 *  @param btnClick 确认点击
 */
+ (void)showPlayEndViewWithView:(UIView *)view showId:(NSString *)showId EnsureClickBlock:(RNTCaptureEndViewEnsureClick)btnClick;

@end
