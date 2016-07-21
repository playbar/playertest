//
//  RNTInsetsLabel.m
//  Ace
//
//  Created by 周兵 on 16/5/10.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTInsetsLabel.h"

@implementation RNTInsetsLabel

@synthesize insets=_insets;
- (id)initWithFrame:(CGRect)frame andInsets:(UIEdgeInsets)insets {
    self = [super initWithFrame:frame];
    if(self){
        self.insets = insets;
    }
    return self;
}

- (instancetype)initWithInsets:(UIEdgeInsets)insets {
    self = [super init];
    if(self){
        self.insets = insets;
    }
    return self;
}

- (void)drawTextInRect:(CGRect)rect {
    return [super drawTextInRect:UIEdgeInsetsInsetRect(rect, self.insets)];
}

@end
