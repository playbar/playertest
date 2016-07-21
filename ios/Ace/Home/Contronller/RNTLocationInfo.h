//
//  RNTLocationInfo.h
//  Ace
//
//  Created by 靳峰 on 16/3/12.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Singleton.h"

@interface RNTLocationInfo : NSObject
//获取地理位置信息
+(void)getLoacationInfo:(void(^)(NSString *location)) localInfo;
SingletonH(Local)
@end
