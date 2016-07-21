//
//  RNTInsetsLabel.h
//  Ace
//
//  Created by 周兵 on 16/5/10.
//  Copyright © 2016年 RNT. All rights reserved.
//  可设置内边距的Label

#import <UIKit/UIKit.h>

@interface RNTInsetsLabel : UILabel
@property(nonatomic) UIEdgeInsets insets;
- (instancetype)initWithFrame:(CGRect)frame andInsets: (UIEdgeInsets) insets;
- (instancetype)initWithInsets: (UIEdgeInsets) insets;
@end
