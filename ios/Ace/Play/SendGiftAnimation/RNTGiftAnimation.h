//
//  RNTGiftAnimation.h
//  carAnimation
//
//  Created by 靳峰 on 16/5/4.
//  Copyright © 2016年 靳峰. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, RNTGiftKind) {
    RNTGiftKindCar = 19,//跑车
    RNTGiftKindFlower = 18,//花束
    RNTGiftKindBoat = 20,//船
    RNTGiftKindFireworkes,//烟火
    RNTGiftKindRocket,//火箭
};


@protocol RNTGiftAnimationDelegate <NSObject>

-(void)animationCompelet;

@end

@interface RNTGiftAnimation : UIView

/**
 *  动画效果
 *
 *  @param gift 礼物类型
 *
 *  @return 返回动画视图
 */
+(RNTGiftAnimation *)giftAnimationWithKind:(RNTGiftKind) gift;

/**
 *  动画完成的代理
 */
@property(nonatomic,weak) id<RNTGiftAnimationDelegate>  delegate;

@end
