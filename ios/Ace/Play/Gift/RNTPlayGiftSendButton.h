//
//  RNTPlayGiftSendButton.h
//  Ace
//
//  Created by 于传峰 on 16/3/19.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTPlayGiftSendButton : UIButton

@property (nonatomic, assign) NSUInteger caromCount;
@property (nonatomic, assign) NSUInteger leftTime;

@property (nonatomic, copy) void(^dismissBlock)();

@end
