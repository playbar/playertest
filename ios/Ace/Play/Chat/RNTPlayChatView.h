//
//  RNTPlayChatView.h
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@class RNTPlayChatView, RNTPlayMessageModel;
@protocol RNTPlayChatViewDelegate <NSObject>

@optional
//- (void)playChatView:(RNTPlayChatView *)chatView selectedUser:(NSString *)userID;
//- (void)playChatView:(RNTPlayChatView *)chatView selectedLink:(NSString *)urlStr;
- (void)playChatViewTapAction:(RNTPlayChatView *)chatView;
@end

@interface RNTPlayChatView : UIView

@property (nonatomic, weak)  id<RNTPlayChatViewDelegate> delegate;
@property (nonatomic, assign) BOOL liveType;
- (void)addMessage:(RNTPlayMessageModel *)model;
- (void)addUserEnterTip:(RNTPlayMessageModel *)model;

@end
