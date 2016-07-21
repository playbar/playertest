//
//  RNTCaptureStartView.h
//  Ace
//
//  Created by Ranger on 16/3/5.
//  Copyright © 2016年 RNT. All rights reserved.
//  准备直播页

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@protocol RNTCaptureStartViewDelegate <NSObject>

- (void)captureViewDidClickCloseBtn;

//- (void)captureViewDidClickSwitchBtn;

- (void)captureViewDidClickImageBtn;

- (void)captureViewDidClickStartBtn:(UIButton *)btn showTitle:(NSString *)title;


@end

@interface RNTCaptureStartView : UIImageView

@property (nonatomic, weak) id<RNTCaptureStartViewDelegate> delegate;

/**
 *  @author Ranger, 16-03-08 10:03
 *  设置封面图片 只传一个
 *  @param image 图片
 *  @param image 网络图片地址
 */
- (void)setCoverImage:(UIImage *)image imageUrl:(NSString *)url;

/**
 *  @author Ranger, 16-03-11 15:03
 *
 *  移除自身
 */
- (void)remove;

@end
