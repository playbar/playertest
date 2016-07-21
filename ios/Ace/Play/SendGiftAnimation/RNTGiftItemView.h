//
//  GiftItemView.h
//  15-ace礼物动画
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 于传峰. All rights reserved.
//

#import <UIKit/UIKit.h>

#define itemWidth 190
#define itemHeight 50
#define rightMargin 40

@class RNTPlaySendGiftInfo;
@interface RNTGiftItemView : UIView

@property (nonatomic, copy)  void(^dismissBlock)();

@property (nonatomic, assign, getter=isTopItem) BOOL topItem;
@property (nonatomic, strong) RNTPlaySendGiftInfo *info;
- (void)addCaromGiftCount:(NSUInteger)count;
@end
