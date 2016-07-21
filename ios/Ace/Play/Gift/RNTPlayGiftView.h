//
//  RNTPlayGiftView.h
//  Ace
//
//  Created by 于传峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@class RNTPlayGiftView;
#define giftViewHeight (scrollViewHeight + giftToolBarHeight)

@protocol RNTPlayGiftViewDelegate <NSObject>

@optional
- (void)playGiftViewShowLoginView:(RNTPlayGiftView *)giftView;
- (void)playGiftViewShowChargeView:(RNTPlayGiftView *)giftView;

@end

@interface RNTPlayGiftView : UIView


@property (nonatomic, weak) id<RNTPlayGiftViewDelegate> delegate;
+ (instancetype)showWithUserID:(NSString *)userID showID:(NSString *)showID dismiss:(void(^)())dismissBlock;
+ (void)dismiss;

@end
