//
//  RNTPlaySendChatView.h
//  Ace
//
//  Created by 于传峰 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@class RNTPlaySendChatView;
@protocol RNTPlaySendChatViewDelegate <NSObject>

@optional
- (void)playSendChatView:(RNTPlaySendChatView *)chatView sendMessage:(NSString *)message;
- (void)playSendChatViewShowLoginView:(RNTPlaySendChatView *)chatView;

@end

@interface RNTPlaySendChatView : UIView

@property (nonatomic, weak)  id<RNTPlaySendChatViewDelegate> delegate;

- (void)pop;
- (void)dismiss;
@end
