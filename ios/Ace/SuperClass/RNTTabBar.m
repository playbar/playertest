//
//  RNTTabBar.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTTabBar.h"

#define liveBtnW 57
#define liveBtnH 30
#define hallBtnW  (kScreenWidth - liveBtnW)*0.5

@interface RNTTabBar ()

@end

@interface RNTTabBarButton :UIButton

@end

@implementation RNTTabBar

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.translucent = YES;
        [self setSubviews];
    }
    return self;
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    
    //移除系统自带控件
    for (UIView* child in self.subviews) {
        // 取出tabbar中的所有UITabBarButton 设置其frame
        if([child isKindOfClass:NSClassFromString(@"UITabBarButton")]
           || [child isKindOfClass:NSClassFromString(@"_UITabBarBackgroundView")]
           || [child isKindOfClass:NSClassFromString(@"UIImageView")]
           ){
            [child removeFromSuperview];
        }
    }
}


-(void) setSubviews
{
    UIView *blackView = [[UIView alloc] initWithFrame:CGRectMake(0, 5, kScreenWidth, 44)];
    blackView.backgroundColor = [UIColor whiteColor];
    blackView.backgroundColor = RNTColor_16(0xffffff);
    [self addSubview:blackView];
    
    //大厅按钮
    RNTTabBarButton *hallButton = [[RNTTabBarButton alloc] initWithFrame:CGRectMake(0, 0, hallBtnW, 44)];
    hallButton.titleLabel.font = [UIFont systemFontOfSize:15];
    [hallButton setImage:[UIImage imageNamed:@"home_hot_selected"] forState:UIControlStateNormal];
    [hallButton setImage:[UIImage imageNamed:@"home_hot_normal"] forState:UIControlStateHighlighted];
    [hallButton setImage:[UIImage imageNamed:@"home_hot_normal"] forState:UIControlStateSelected];
    [hallButton setTitleColor:RNTColor_16(0xffffff) forState:UIControlStateNormal];
    [hallButton setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateSelected];
    hallButton.tag = 0;
    self.hallButton = hallButton;
    self.hallButton.selected = YES;
    self.selectedBtn = self.hallButton;
    [blackView addSubview:hallButton];
    
    //我的按钮
    RNTTabBarButton *mineButton = [[RNTTabBarButton alloc] initWithFrame:CGRectMake(hallBtnW+liveBtnW, 0, hallBtnW, 44)];
    mineButton.titleLabel.font = [UIFont systemFontOfSize:15];
    [mineButton setImage:[UIImage imageNamed:@"home_order_selected"] forState:UIControlStateNormal];
    [mineButton setImage:[UIImage imageNamed:@"home_order_normal"] forState:UIControlStateHighlighted];
    [mineButton setImage:[UIImage imageNamed:@"home_order_normal"] forState:UIControlStateSelected];
    [mineButton setTitleColor:RNTColor_16(0xffffff) forState:UIControlStateNormal];
    [mineButton setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateSelected];
    mineButton.tag =1;
    self.mineButton = mineButton;
    [blackView addSubview:mineButton];
    
    //设置直播按钮
    UIButton *liveBtn = [[UIButton alloc] initWithFrame:CGRectMake(hallBtnW, -20, 64 , 64)];
    [liveBtn setImage:[UIImage imageNamed:@"home_live_normal"] forState:UIControlStateNormal];
    [liveBtn setImage:[UIImage imageNamed:@"home_live_highlithted"] forState:UIControlStateHighlighted];
    [blackView addSubview:liveBtn];
    self.liveBtn = liveBtn;
}

@end

@implementation RNTTabBarButton

-(void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    
    [UIView animateWithDuration:0.25 animations:^{
        self.imageView.transform = CGAffineTransformScale(self.imageView.transform , 1.2, 1.2);
    }];
}
@end
