//
//  RNTCaptureStartView.m
//  Ace
//
//  Created by Ranger on 16/3/5.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureStartView.h"
#import "Masonry.h"
#import "UIButton+WebCache.h"
#import "RNTCaptureShareView.h"

#define GrayColor RNTAlphaColor_16(0xffffff, 0.5)

@interface RNTCaptureStartView ()<UITextFieldDelegate>

@property (nonatomic, weak) UIButton *closeBtn;

@property (nonatomic, weak) UIButton *switchBtn;

@property (nonatomic, weak) UIButton *imageBtn;

@property (nonatomic, weak) UITextField *nameTxetF;

@property (nonatomic, weak) UIButton *startBtn;

@end

@implementation RNTCaptureStartView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {

        self.image = [UIImage imageNamed:@"play_Gaussian_Blur_image"];
        self.userInteractionEnabled = YES;
        
        [self setupSubviews];
    }
    return self;
}


- (void)setupSubviews
{
    // 关闭
    UIButton *closeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [closeBtn setImage:[UIImage imageNamed:@"statLive_close"] forState:UIControlStateNormal];
    [closeBtn addTarget:self action:@selector(closeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:closeBtn];
    self.closeBtn = closeBtn;
    
//    // 切换摄像头
//    UIButton *switchBtn = [UIButton buttonWithType:UIButtonTypeCustom];
//    [switchBtn setImage:[UIImage imageNamed:@"statLive_rotate"] forState:UIControlStateNormal];
//    [switchBtn addTarget:self action:@selector(switchBtnClick) forControlEvents:UIControlEventTouchUpInside];
//    [self addSubview:switchBtn];
//    self.switchBtn = switchBtn;
    
    // 封面
//    UIView *borderView = [[UIView alloc] init];
//    borderView.backgroundColor = RNTAlphaColor_16(0xffffff, 0.5);
//    [self addSubview:borderView];
    
    UIButton *imageBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    imageBtn.layer.masksToBounds = YES;
    [imageBtn setBackgroundImage:[UIImage imageNamed:@"startLive_showImage"] forState:UIControlStateNormal];
    [imageBtn addTarget:self action:@selector(imageBtnClick) forControlEvents:UIControlEventTouchUpInside];

    
    [self addSubview:imageBtn];
    self.imageBtn = imageBtn;
    
    // 封面提示
    UILabel *imageLab = [[UILabel alloc] init];
    imageLab.font = [UIFont systemFontOfSize:15];
    imageLab.textColor = GrayColor;
    imageLab.text = @"调整封面";
    imageLab.textAlignment = NSTextAlignmentCenter;
    imageLab.backgroundColor = RNTAlphaColor_16(0x000000, 0.5);
    [self addSubview:imageLab];
    

    //直播标题
    UITextField *nameTxetF = [[UITextField alloc] init];
    nameTxetF.textColor = GrayColor;
    nameTxetF.placeholder = @"输入作品名称";
    nameTxetF.textAlignment = NSTextAlignmentCenter;
    [nameTxetF setValue: GrayColor forKeyPath:@"_placeholderLabel.textColor"];
    nameTxetF.font = [UIFont systemFontOfSize:18];
    [nameTxetF addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    nameTxetF.delegate = self;
    [self addSubview:nameTxetF];
    self.nameTxetF = nameTxetF;
    [nameTxetF becomeFirstResponder];
    
    // 黄线
    UIView *sepLine = [[UIView alloc] init];
    sepLine.backgroundColor = GrayColor;
    [self addSubview:sepLine];
    
    // 分享
    RNTCaptureShareView *shareView = [[RNTCaptureShareView alloc] init];
    [self addSubview:shareView];
    
    shareView.hidden = ![RNTUserManager canShare];
    
    // 开播按钮
    UIButton *startBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [startBtn setTitle:@"开始直播" forState:UIControlStateNormal];
    [startBtn setTitle:@"直播创建中" forState:UIControlStateDisabled];
    startBtn.titleLabel.font = [UIFont systemFontOfSize:18];
//    startBtn.titleLabel.textColor = RNTColor_16(0x191919);
    [startBtn setTitleColor:RNTColor_16(0x19191a) forState:UIControlStateNormal];
    [startBtn setBackgroundColor:RNTMainColor];
    startBtn.layer.cornerRadius = 22;
    startBtn.clipsToBounds = YES;
    [startBtn addTarget:self action:@selector(startBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:startBtn];
    self.startBtn = startBtn;
    
    
    // 布局
    [closeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.top).offset(30);
        make.right.equalTo(self.right).offset(-12);
        
    }];
    
//    [switchBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.right.mas_equalTo(closeBtn.left).offset(-20);
//        make.top.mas_equalTo(self);
//
//    }];
    
    
    
    if (iPhone4 || iPhone5) {

        [imageBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self).offset(60);
            make.centerX.equalTo(self);
            make.size.mas_equalTo(CGSizeMake(100, 100));
        }];
        
        [imageLab mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(imageBtn);
            make.height.mas_equalTo(28);
            make.left.equalTo(imageBtn);
            make.right.equalTo(imageBtn);
        }];
        
//        [borderView mas_makeConstraints:^(MASConstraintMaker *make) {
//            make.center.equalTo(imageBtn);
//            make.size.mas_equalTo(CGSizeMake(106, 106));
//        }];
        
        [nameTxetF mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(imageBtn.bottom).offset(10);
            //        make.centerX.equalTo(self);
            make.left.equalTo(self.left).offset(10);
            make.right.equalTo(self.right).offset(-10);
        }];
        
        
        [sepLine mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(nameTxetF.bottom).offset(10);
            make.left.equalTo(self.left).offset(10);
            make.right.equalTo(self.right).offset(-10);
            make.height.mas_equalTo(0.5);
        }];
        
        [shareView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(sepLine.bottom).offset(12);
            make.centerX.equalTo(sepLine);
            make.size.mas_equalTo(CGSizeMake((ImageSize.width * 5 + Margin * 4), ImageSize.height));
        }];
        
        [startBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(shareView.bottom).offset(10);
            make.left.equalTo(self.left).offset(10);
            make.right.equalTo(self.right).offset(-10);
            make.height.mas_equalTo(44);
        }];

    }else {
        
        [imageBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self).offset(80);
            make.centerX.equalTo(self);
            make.size.mas_equalTo(CGSizeMake(120, 120));
        }];
        
        [imageLab mas_makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(imageBtn);
            make.height.mas_equalTo(28);
            make.left.equalTo(imageBtn);
            make.right.equalTo(imageBtn);
        }];
        
//        [borderView mas_makeConstraints:^(MASConstraintMaker *make) {
//            make.center.equalTo(imageBtn);
//            make.size.mas_equalTo(CGSizeMake(126, 126));
//        }];
        
        [nameTxetF mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(imageBtn.bottom).offset(15);
            //        make.centerX.equalTo(self);
            make.left.equalTo(self.left).offset(10);
            make.right.equalTo(self.right).offset(-10);
        }];
        
        
        [sepLine mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(nameTxetF.bottom).offset(15);
            make.left.equalTo(self.left).offset(10);
            make.right.equalTo(self.right).offset(-10);
            make.height.mas_equalTo(0.5);
        }];
        
        [shareView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(sepLine.bottom).offset(12);
            make.centerX.equalTo(sepLine);
            make.size.mas_equalTo(CGSizeMake((ImageSize.width * 5 + Margin * 4), ImageSize.height));
        }];
        
        [startBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(shareView.bottom).offset(15);
            make.left.equalTo(self.left).offset(10);
            make.right.equalTo(self.right).offset(-10);
            make.height.mas_equalTo(44);
        }];
    }
}

- (void)remove
{
    for (UIView *subview in self.subviews) {
        [subview removeFromSuperview];
    }
    [self removeFromSuperview];
}

#pragma mark - 数据
- (void)setCoverImage:(UIImage *)image imageUrl:(NSString *)url
{
    if (image) {
        [self.imageBtn setBackgroundImage:image forState:UIControlStateNormal];
    }
    
    if (url) {
        [self.imageBtn sd_setBackgroundImageWithURL:[NSURL URLWithString:url] forState:UIControlStateNormal placeholderImage:[UIImage imageNamed:@"startLive_showImage"]];
    }
}

#pragma mark - 按钮点击
- (void)startBtnClick:(UIButton *)btn
{
    RNTLog(@"开播点击");
    [self.nameTxetF endEditing:YES];
    
    if ([self.delegate respondsToSelector:@selector(captureViewDidClickStartBtn:showTitle:)]) {
        [self.delegate captureViewDidClickStartBtn:btn showTitle:self.nameTxetF.text];
    }
    
}

- (void)closeBtnClick
{
    RNTLog(@"关闭点击");
    [self.nameTxetF endEditing:YES];
    if ([self.delegate respondsToSelector:@selector(captureViewDidClickCloseBtn)]) {
        [self.delegate captureViewDidClickCloseBtn];
    }
}

//- (void)switchBtnClick
//{
//    RNTLog(@"切换摄像头");
//    if ([self.delegate respondsToSelector:@selector(captureViewDidClickSwitchBtn)]) {
//        [self.delegate captureViewDidClickSwitchBtn];
//    }
//}

- (void)imageBtnClick
{
    RNTLog(@"切换封面");
    [self.nameTxetF endEditing:YES];
    if ([self.delegate respondsToSelector:@selector(captureViewDidClickImageBtn)]) {
        [self.delegate captureViewDidClickImageBtn];
    }
}

#pragma mark - UITextFieldDelegate
// 字数限制
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == self.nameTxetF) {
        
        NSInteger existedLength = textField.text.length;
        NSInteger selectedLength = range.length;
        NSInteger replaceLength = string.length;
        if (existedLength - selectedLength + replaceLength > 10) {
            return NO;
        }
    }
    return YES;
}

// 字数限制
- (void)textFieldDidChange:(UITextField *)textField
{
    if (textField == self.nameTxetF) {
        if (textField.text.length > 10) {
            textField.text = [textField.text substringToIndex:10];
        }
    }
}

@end
