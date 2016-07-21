//
//  RNTPlayShareView.h
//  Ace
//
//  Created by 于传峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@class RNTPlayShareInfo,RNTPlayShareView;

@protocol RNTPlayShareViewDelegate <NSObject>
@optional
- (void)playShareViewShowLoginView:(RNTPlayShareView *)shareView;

@end

@interface RNTPlayShareView : UIView

//+ (instancetype)popShareViewWithInfo:(RNTPlayShareInfo *)info dismiss:(void(^)())dismissBlock;
+ (instancetype)popShareViewWithShowID:(NSString *)showID dismiss:(void(^)())dismissBlock;
+ (void)dismiss;
@property (nonatomic, weak) id<RNTPlayShareViewDelegate> delegate;
@end
