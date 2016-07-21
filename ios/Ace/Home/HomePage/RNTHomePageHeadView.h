//
//  RNTHomePageHeadView.h
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTOrderFansBtn.h"

@interface RNTHomePageHeadView : UIView
//订阅按钮
@property(nonatomic,strong) RNTOrderFansBtn *orderBtn;
//粉丝按钮
@property(nonatomic,strong) RNTOrderFansBtn *fansBtn;
//返回按钮
@property(nonatomic,strong) UIButton *backBtn;
@end
