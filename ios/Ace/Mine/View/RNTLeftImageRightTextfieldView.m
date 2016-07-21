//
//  RNTLeftImageRightTextfieldView.m
//  Ace
//
//  Created by 周兵 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTLeftImageRightTextfieldView.h"

//#define TextColor RNTColor_16(0x9a9a9a)
//#define TextFont [UIFont systemFontOfSize:15]

@interface RNTLeftImageRightTextfieldView ()
@property (nonatomic, strong) UIImageView *imageView;
@end

@implementation RNTLeftImageRightTextfieldView

+ (instancetype)initWithImageName:(NSString *)imageName placeholder:(NSString *)placeholder
{
    RNTLeftImageRightTextfieldView *view = [[RNTLeftImageRightTextfieldView alloc] init];
    view.textField.placeholder = placeholder;
    view.imageView.image = [UIImage imageNamed:imageName];
    return view;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.imageView];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self.imageView updateConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.centerY.equalTo(self);
        make.size.equalTo(self.imageView.image.size);
    }];
    
    [self.textField updateConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.imageView.right).offset(13);
        make.top.bottom.right.equalTo(self);
    }];
}

#pragma mark - 
- (UIImageView *)imageView
{
    if (_imageView == nil) {
        _imageView = [[UIImageView alloc] init];
    }
    return _imageView;
}

@end
