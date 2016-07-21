//
//  RNTPlayToolBarView.h
//  Ace
//
//  Created by 于传峰 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RNTPlayToolBarView;

typedef NS_ENUM(NSUInteger, RNTPlayToolBarOption) {
    RNTPlayToolBarOptionChat,
    RNTPlayToolBarOptionShare,
    RNTPlayToolBarOptionClose,
    RNTPlayToolBarOptionGift,
    RNTPlayToolBarOptionMute,
    RNTPlayToolBarOptionSwitch,
    RNTPlayToolBarOptionFlashLight
};

typedef enum {
    RNTPlayToolBarTypeAudience,//观众
    RNTPlayToolBarTypeAnchor// 主播
} RNTPlayToolBarType;

@protocol RNTPlayToolBarViewDelegate <NSObject>

@optional
- (void)playToolBarView:(RNTPlayToolBarView *)view button:(UIButton *)btn selectedOption:(RNTPlayToolBarOption)option;

@end

@interface RNTPlayToolBarView : UIView

@property (nonatomic, weak)  id<RNTPlayToolBarViewDelegate> delegate;

- (instancetype)initWithType:(RNTPlayToolBarType)type;


@end
