//
//  RNTSettingRightLabelItem.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingRightLabelItem.h"

@implementation RNTSettingRightLabelItem

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title rightLabelText:(NSString *)labelText
{
    self = [super initWithIcon:icon title:title];
    if (self) {
        self.labelText = labelText;
    }
    return self;
}

@end
