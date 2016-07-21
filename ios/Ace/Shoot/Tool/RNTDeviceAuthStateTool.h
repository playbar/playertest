//
//  RNTDeviceAuthStateTool.h
//  Ace
//
//  Created by Ranger on 16/5/9.
//  Copyright © 2016年 RNT. All rights reserved.
//  判断权限

#import <Foundation/Foundation.h>

typedef enum {
    DeviceAuthStatusNotDetermined = 0,// 未选择
    //    CaptureAuthStatusRestricted,// 未授权 家长控制等
    //    CaptureAuthStatusDenied,// 未授权 用户拒绝
    DeviceAuthStatusNotAuthorized,// 未授权
    DeviceAuthStatusAuthorized// 已授权
}DeviceAuthStatus;

@interface RNTDeviceAuthStateTool : NSObject

// 相机
+ (DeviceAuthStatus)checkCameraAuth;

// 麦克风
+ (DeviceAuthStatus)checkMicrophoneAuth;

// 相册
+ (DeviceAuthStatus)checkPhotoAuth;

// 跳转到授权页
+ (void)openAuthSetting;

@end
