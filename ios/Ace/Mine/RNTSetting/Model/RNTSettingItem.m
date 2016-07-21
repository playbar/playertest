//
//  RNTSettingItem.m
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@implementation RNTSettingItem

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title
{
    if (self = [super init]) {
        self.icon = icon;
        self.tilte = title;
    }
    return self;
}

@end
