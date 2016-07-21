//
//  RNTCaptureAuthorizedView.h
//  Ace
//
//  Created by Ranger on 16/5/9.
//  Copyright © 2016年 RNT. All rights reserved.
//  开播权限检测view

#import <UIKit/UIKit.h>



@interface RNTCaptureAuthorizedView : UIControl

/**
 *  @author Ranger, 16-05-09 17:05
 *
 *  显示权限提示view
 *
 *  @return yes = 已授权 不显示 no = 未授权 显示
 */
+ (BOOL)showAuthView;

@end
