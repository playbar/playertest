//
//  AppDelegate.h
//  Ace
//
//  Created by 周兵 on 16/2/23.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
/**
 *  全局变量用来标记Mine控制器navbar隐藏是否使用动画，点击tabbar时修改为NO，Mine控制器显示时改为YES
 */
@property (nonatomic, assign, getter=isNavBarAnimated) BOOL navBarAnimated;

/**
 *  @author Ranger, 16-03-13 13:03
 *  是否有网 
 */
@property (nonatomic, assign) BOOL hasNetwork;

/**
 *  用来保存推送消息
 */
@property (strong, nonatomic) NSDictionary* userInfo;

@end

