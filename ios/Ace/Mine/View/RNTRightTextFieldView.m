//
//  RNTRightTextFieldView.m
//  Ace
//
//  Created by 周兵 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTRightTextFieldView.h"

#define TextColor RNTColor_16(0x9a9a9a)
#define TextFont [UIFont systemFontOfSize:15]

@interface RNTRightTextFieldView ()
@property (nonatomic, strong) UIView *topSeparator;
@property (nonatomic, strong) UIView *bottomSeparator;
@end

@implementation RNTRightTextFieldView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        [self addSubview:self.textField];
        [self addSubview:self.topSeparator];
        [self addSubview:self.bottomSeparator];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self.topSeparator updateConstraints:^(MASConstraintMaker *make) {
        make.left.right.top.equalTo(self);
        make.height.equalTo(0.5);
    }];
    
    [self.bottomSeparator updateConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(self.topSeparator);
        make.bottom.equalTo(self);
    }];
}

#pragma mark - 
- (UITextField *)textField
{
    if (_textField == nil) {
        _textField = [[UITextField alloc] init];
        _textField.font = TextFont;
        _textField.textColor = TextColor;
        _textField.clearButtonMode = UITextFieldViewModeWhileEditing;
  
    }
    return _textField;
}

- (UIView *)topSeparator
{
    if (_topSeparator == nil) {
        _topSeparator = [[UIView alloc] init];
        _topSeparator.backgroundColor = RNTSeparatorColor;
    }
    return _topSeparator;
}

- (UIView *)bottomSeparator
{
    if (_bottomSeparator == nil) {
        _bottomSeparator = [[UIView alloc] init];
        _bottomSeparator.backgroundColor = RNTSeparatorColor;
    }
    return _bottomSeparator;
}
@end
