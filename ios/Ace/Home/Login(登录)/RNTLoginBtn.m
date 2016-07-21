//
//  RNTLoginBtn.m
//  Ace
//
//  Created by 靳峰 on 16/3/7.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTLoginBtn.h"

@implementation RNTLoginBtn

-(void)layoutSubviews
{
    [super layoutSubviews];
    self.imageView.frame = CGRectMake(0, 0, Width(self.imageView), Height(self.imageView));
    self.imageView.center = CGPointMake(Width(self)*0.5, Height(self.imageView)*0.5);
    
    self.titleLabel.frame = CGRectMake(0, 0, Width(self), Height(self.titleLabel));
    self.titleLabel.center = CGPointMake(Width(self)*0.5, Height(self.imageView)+Height(self.titleLabel)*0.5+2);
    self.titleLabel.textAlignment = NSTextAlignmentCenter;

    self.titleLabel.font = [UIFont systemFontOfSize:13];
    [self setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    
}

@end
