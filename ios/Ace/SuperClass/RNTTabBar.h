//
//  RNTTabBar.h
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTTabBar : UITabBar

@property(nonatomic,strong) UIButton *selectedBtn;
@property(nonatomic,strong) UIButton *hallButton;//大厅
@property(nonatomic,strong) UIButton *mineButton;//我的
@property(nonatomic,strong) UIButton *liveBtn;//直播

@end
