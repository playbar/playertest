//
//  RNTLeftImageRightTextfieldView.h
//  Ace
//
//  Created by 周兵 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//  修改账号或密码的输入框

#import "RNTRightTextFieldView.h"

@interface RNTLeftImageRightTextfieldView : RNTRightTextFieldView
+ (instancetype)initWithImageName:(NSString *)imageName placeholder:(NSString *)placeholder;
@end
