//
//  RNTPlayHostInfoView.h
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

#define HostInfoViewWidth 170

@class RNTPlayHostInfoView, RNTPlayShowInfo;
@protocol RNTPlayHostInfoViewDelegate <NSObject>
//主播头像被点击
- (void)hostInfoViewClick:(RNTPlayHostInfoView *)hostInfoView withModel:(NSObject *)object;

@optional


@end

@interface RNTPlayHostInfoView : UIView


@property (nonatomic, weak) id<RNTPlayHostInfoViewDelegate>  delegate;

@property (nonatomic, strong) RNTPlayShowInfo *info;
@end
