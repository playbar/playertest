//
//  RNTSettingLevelItem.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingLevelItem.h"

@implementation RNTSettingLevelItem
- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title level:(NSString *)level EXP:(NSString *)EXP
{
    self = [super initWithIcon:icon title:title];
    if (self) {
        self.level = level;
        self.EXP = EXP;
    }
    return self;
}
@end
