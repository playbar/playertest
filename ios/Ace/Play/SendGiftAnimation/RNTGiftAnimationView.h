//
//  GiftAnimationView.h
//  15-ace礼物动画
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 于传峰. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTGiftItemView.h"

@class RNTPlaySendGiftInfo;
@interface RNTGiftAnimationView : UIView

- (void)fireItem:(RNTPlaySendGiftInfo *)item;
@property (nonatomic, strong) NSMutableArray *items;

@end
