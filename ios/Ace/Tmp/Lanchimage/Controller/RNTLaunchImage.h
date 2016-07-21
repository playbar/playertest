//
//  RNTLaunchImage.h
//  Weibo
//
//  Created by Ranger on 15/12/7.
//  Copyright © 2015年 Rednovo. All rights reserved.
//  启动图主类

#import <Foundation/Foundation.h>

@class RNTBootPicModel;

@interface RNTLaunchImage : UIViewController

/**
 *  @author Ranger, 15-12-07 11:12
 *
 *  显示网络启动图
 */
+ (void)show;

/**
 *  显示网络启动图
 *
 *  @param url
 */
+(void)showPic:(RNTBootPicModel *)bootPic;

/**
 *  @author Ranger, 15-12-07 16:12
 *
 *  隐藏启动图  并清除，带动画
 *
 *  @param view 显示图片的view或父view
 */
+ (void)hiddenWithView;


@end
