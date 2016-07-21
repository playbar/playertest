//
//  RNTAccountManagerButton.m
//  Ace
//
//  Created by 周兵 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTAccountManagerButton.h"
#import "UIImage+RNT.h"

@implementation RNTAccountManagerButton

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.titleLabel.font = [UIFont systemFontOfSize:18];
        [self setTitleColor:RNTColor_16(0x19191a) forState:UIControlStateNormal];
        [self setBackgroundImage:[UIImage imageWithColor:RNTMainHighlightColor] forState:UIControlStateHighlighted];
        [self setBackgroundImage:[UIImage imageWithColor:RNTMainColor] forState:UIControlStateNormal];
        self.layer.cornerRadius = 3.0;
        self.layer.masksToBounds = YES;
    }
    return self;
}

@end
