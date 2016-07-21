//
//  RNTSettingPhotoItem.m
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingPhotoItem.h"

@implementation RNTSettingPhotoItem
- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title imageUrl:(NSString *)imageUrl
{
    self = [super initWithIcon:icon title:title];
    if (self) {
        self.imageUrl = imageUrl;
    }
    return self;
}
@end
