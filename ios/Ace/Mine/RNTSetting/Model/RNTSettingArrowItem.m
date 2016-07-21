//
//  RNTSettingArrowItem.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingArrowItem.h"

@implementation RNTSettingArrowItem

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title destClass:(Class)destVc
{
    if (self = [super initWithIcon:icon title:title]) {
        self.destVC = destVc;
    }
    return self;
}

@end
