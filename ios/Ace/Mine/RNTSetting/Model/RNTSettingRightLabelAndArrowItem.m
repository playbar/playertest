//
//  RNTSettingRightLabelAndArrowItem.m
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingRightLabelAndArrowItem.h"

@implementation RNTSettingRightLabelAndArrowItem
- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title rightLabelText:(NSString *)rightLabelText  destClass:(Class)destVc
{
    self = [super initWithIcon:icon title:title];
    if (self) {
        self.rightLabelText = rightLabelText;
        self.destVC = destVc;
    }
    return self;
}
@end
