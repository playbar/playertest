//
//  RNTDeviceAuthStateTool.m
//  Ace
//
//  Created by Ranger on 16/5/9.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTDeviceAuthStateTool.h"
#import <AVFoundation/AVFoundation.h>
#import <Photos/Photos.h>

@implementation RNTDeviceAuthStateTool
// 相机
+ (DeviceAuthStatus)checkCameraAuth
{
    AVAuthorizationStatus videoAuthStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (videoAuthStatus == AVAuthorizationStatusNotDetermined) {
        return DeviceAuthStatusNotDetermined;
    }else if(videoAuthStatus == AVAuthorizationStatusRestricted || videoAuthStatus == AVAuthorizationStatusDenied) {
        return DeviceAuthStatusNotAuthorized;
    }else{
        return DeviceAuthStatusAuthorized;
    }
}

// 麦克风
+ (DeviceAuthStatus)checkMicrophoneAuth
{
    AVAuthorizationStatus audioAuthStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio];
    if (audioAuthStatus == AVAuthorizationStatusNotDetermined) {
        return DeviceAuthStatusNotDetermined;
    }else if(audioAuthStatus == AVAuthorizationStatusRestricted || audioAuthStatus == AVAuthorizationStatusDenied) {
        return DeviceAuthStatusNotAuthorized;
    }else{
        return DeviceAuthStatusAuthorized;
    }
}

// 相册
+ (DeviceAuthStatus)checkPhotoAuth
{
    PHAuthorizationStatus photoAuthStatus = [PHPhotoLibrary authorizationStatus];
    if (photoAuthStatus == PHAuthorizationStatusNotDetermined) {
        return DeviceAuthStatusNotDetermined;
    }else if(photoAuthStatus == PHAuthorizationStatusRestricted || photoAuthStatus == PHAuthorizationStatusDenied) {
        return DeviceAuthStatusNotAuthorized;
    }else{
        return DeviceAuthStatusAuthorized;
    }
}

// 跳转到授权页
+ (void)openAuthSetting
{
    //跳到权限设置页
    if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]]){
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
    }
}

@end
