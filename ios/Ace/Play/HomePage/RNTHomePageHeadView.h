//
//  RNTHomePageHeadView.h
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTOrderFansBtn.h"
#import "RNTUser.h"
#import "RNTHomePageLiveBtn.h"
#import "RNTViewController.h"


@interface RNTHomePageHeadView : RNTViewController

//是否从直播间进入
@property(nonatomic,assign) BOOL isShowInto;
//用户ID
@property(nonatomic,copy) NSString *userID;

//关注粉丝列表按钮点击
@property(nonatomic,copy) void(^orderFansBtnClick)(CGPoint point);
//关注按钮点击
@property(nonatomic,copy) void(^attentionBtnClick)(BOOL isAdd);
//关注按钮是否选中
@property(nonatomic,assign) BOOL  isOrderSelected;

+(instancetype)homePageHeadViewWithFrame:(CGRect)frame;
@end
