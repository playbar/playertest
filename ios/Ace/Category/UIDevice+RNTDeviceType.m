//
//  UIDevice+RNTDeviceType.m
//  Ace
//
//  Created by 周兵 on 16/3/21.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "UIDevice+RNTDeviceType.h"
#include <sys/types.h>
#include <sys/sysctl.h>

@implementation UIDevice (RNTDeviceType)
+ (NSString*)getDeviceVersion
{
    size_t size;
    
    sysctlbyname("hw.machine",NULL, &size, NULL,0);
    
    char *machine = (char*)malloc(size);
    
    sysctlbyname("hw.machine", machine, &size,NULL, 0);
    
    NSString *platform = [NSString stringWithUTF8String:machine];
    
    free(machine);
    
    return platform;
}

+ (NSString *)getDevicePlatform
{
    NSString *platform = [self getDeviceVersion];
    
    //iPhone
    if ([platform isEqualToString:@"iPhone4,1"]) return @"iPhone 4s";
    
    if ([platform isEqualToString:@"iPhone5,1"]) return @"iPhone 5";
    if ([platform isEqualToString:@"iPhone5,2"]) return @"iPhone 5";
    
    if ([platform isEqualToString:@"iPhone5,3"]) return @"iPhone 5C";
    if ([platform isEqualToString:@"iPhone5,4"]) return @"iPhone 5C";
    
    if ([platform isEqualToString:@"iPhone6,1"]) return @"iPhone 5S";
    if ([platform isEqualToString:@"iPhone6,2"]) return @"iPhone 5S";
    
    if ([platform isEqualToString:@"iPhone7,1"]) return @"iPhone 6 Plus";
    if ([platform isEqualToString:@"iPhone7,2"]) return @"iPhone 6";
    
    if ([platform isEqualToString:@"iPhone8,1"])   return @"iPhone 6S";
    if ([platform isEqualToString:@"iPhone8,2"])   return @"iPhone 6S Plus";
    
    //iPod Touch
    if ([platform isEqualToString:@"iPod5,1"]) return @"iPod Touch 5";
    
    //iPad
    if ([platform isEqualToString:@"iPad2,1"]) return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,2"]) return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,3"]) return @"iPad 2";
    if ([platform isEqualToString:@"iPad2,4"]) return @"iPad 2";
    
    if ([platform isEqualToString:@"iPad2,5"]) return @"iPad Mini 1";
    if ([platform isEqualToString:@"iPad2,6"]) return @"iPad Mini 1";
    if ([platform isEqualToString:@"iPad2,7"]) return @"iPad Mini 1";
    
    if ([platform isEqualToString:@"iPad3,1"]) return @"iPad 3";
    if ([platform isEqualToString:@"iPad3,2"]) return @"iPad 3";
    if ([platform isEqualToString:@"iPad3,3"]) return @"iPad 3";
    
    if ([platform isEqualToString:@"iPad3,4"]) return @"iPad 4";
    if ([platform isEqualToString:@"iPad3,5"]) return @"iPad 4";
    if ([platform isEqualToString:@"iPad3,6"]) return @"iPad 4";
    
    if ([platform isEqualToString:@"iPad4,1"]) return @"iPad air";
    if ([platform isEqualToString:@"iPad4,2"]) return @"iPad air";
    if ([platform isEqualToString:@"iPad4,3"]) return @"iPad air";
    
    if ([platform isEqualToString:@"iPad4,4"]) return @"iPad mini 2";
    if ([platform isEqualToString:@"iPad4,5"]) return @"iPad mini 2";
    if ([platform isEqualToString:@"iPad4,6"]) return @"iPad mini 2";
    
    if ([platform isEqualToString:@"iPad4,7"]) return @"iPad mini 3";
    if ([platform isEqualToString:@"iPad4,8"]) return @"iPad mini 3";
    if ([platform isEqualToString:@"iPad4,9"]) return @"iPad mini 3";
    
    if ([platform isEqualToString:@"iPad5,3"]) return @"iPad air 2";
    if ([platform isEqualToString:@"iPad5,4"]) return @"iPad air 2";
    
    //模拟器
    if ([platform isEqualToString:@"iPhone Simulator"] || [platform isEqualToString:@"x86_64"]) return @"iPhone Simulator";
    
    return @"iPhone";
}
@end
