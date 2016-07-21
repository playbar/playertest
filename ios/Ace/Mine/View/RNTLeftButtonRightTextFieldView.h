//
//  RNTLeftButtonRightTextFieldView.h
//  Ace
//
//  Created by 周兵 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//  获取并输入验证码控件

#import "RNTRightTextFieldView.h"
//typedef BOOL(^ButtonClickBlock)();
typedef NSString *(^btnClickBlock)();

@interface RNTLeftButtonRightTextFieldView : RNTRightTextFieldView
+ (instancetype)initWithButtonTitle:(NSString *)title placeholder:(NSString *)placeholder;
//@property (nonatomic, copy) ButtonClickBlock buttonClickBlock;
@property(nonatomic,copy) btnClickBlock btnClickBlock;
@end
