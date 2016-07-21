//
//  RNTSettingRightButtonAndLabelItem.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingRightButtonAndLabelItem.h"

@implementation RNTSettingRightButtonAndLabelItem

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title rightBtnTitle:(NSString *)btnTitle labelText:(NSString *)labelText destClass:(Class)destVc
{
    if (self = [super initWithIcon:icon title:title]) {
        self.destVC = destVc;
        self.rightBtnTitle = btnTitle;
        self.labelText = labelText;
    }
    return self;
}

@end
