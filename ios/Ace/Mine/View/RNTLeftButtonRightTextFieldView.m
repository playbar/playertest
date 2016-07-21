//
//  RNTLeftButtonRightTextFieldView.m
//  Ace
//
//  Created by 周兵 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTLeftButtonRightTextFieldView.h"
#import "UIImage+RNT.h"
#import "RNTNetTool.h"

#define TextColor RNTColor_16(0x9a9a9a)
#define TextFont [UIFont systemFontOfSize:15]
#define Countdown 120 //获取验证码倒计时时间

@interface RNTLeftButtonRightTextFieldView ()
@property (nonatomic, strong) UIButton *button;

@property (nonatomic,strong)NSTimer *timer;
@property (nonatomic,assign)int btnText;
@end

@implementation RNTLeftButtonRightTextFieldView

+ (instancetype)initWithButtonTitle:(NSString *)title placeholder:(NSString *)placeholder
{
    RNTLeftButtonRightTextFieldView *view = [[RNTLeftButtonRightTextFieldView alloc] init];
    view.textField.placeholder = placeholder;
    [view.button setTitle:title forState:UIControlStateNormal];
    
    return view;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.btnText = Countdown;
        self.backgroundColor = [UIColor whiteColor];
        [self addSubview:self.button];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self.button updateConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.height.equalTo(30);
        make.width.equalTo(88);
        make.centerY.equalTo(self);
    }];
    
    [self.textField updateConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.button.right).offset(21);
        make.top.bottom.right.equalTo(self);
    }];
}

- (void)buttonClick:(UIButton *)btn
{
    btn.enabled = NO;
    WEAK(self);
//    if (self.buttonClickBlock) {
//        _Bool isSuccess = self.buttonClickBlock();
//        if (isSuccess) {
//            [btn setTitle:[NSString stringWithFormat:@"%d秒", Countdown] forState:UIControlStateNormal];
//            [weakself.timer fire];
//        } else {
//            btn.enabled = YES;
//        }
//    }
    
    if (self.btnClickBlock) {
        NSString *phoneNum =  self.btnClickBlock();
        if (phoneNum.length != 11|| ![phoneNum hasPrefix:@"1"]) {
            [MBProgressHUD showError:@"手机格式不正确"];
            btn.enabled = YES;
            return;
        }else{
            [RNTNetTool getVerifyCodeWithPhoneNumber:phoneNum success:^{
                [MBProgressHUD  showSuccess:@"验证码发送成功"];
                [btn setTitle:[NSString stringWithFormat:@"%d秒", Countdown] forState:UIControlStateNormal];
                [weakself.timer fire];
            } failure:^(NSString *error) {
                btn.enabled = YES;
                [MBProgressHUD showError:error];
            }];
        }
    }else{
        btn.enabled = YES;
    }
}

- (void)changeBtnTitle
{
    self.btnText --;
    if (self.btnText == 0) {
        [self.button setTitle:[NSString stringWithFormat:@"获取验证码"] forState:UIControlStateNormal];
        self.btnText = Countdown;
        [self.timer invalidate];
        self.timer = nil;
        self.button.enabled = YES;
        return;
    }
    
    [self.button setTitle:[NSString stringWithFormat:@"%d秒",self.btnText] forState:UIControlStateNormal];
}

- (void)dealloc
{
    [self.timer invalidate];
    self.timer = nil;
}

#pragma mark - 
- (UIButton *)button
{
    if (_button == nil) {
        _button = [[UIButton alloc] init];
        _button.backgroundColor = RNTColor_16(0x0091ff);
        [_button setBackgroundImage:[UIImage imageWithColor:[UIColor lightGrayColor]] forState:UIControlStateDisabled];
        [_button setTitleColor:RNTColor_16(0xcbe2ff) forState:UIControlStateNormal];
        _button.titleLabel.font = [UIFont systemFontOfSize:14];
        _button.layer.cornerRadius = 15;
        _button.layer.masksToBounds = YES;
        [_button addTarget:self action:@selector(buttonClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _button;
}

- (NSTimer *)timer
{
    if (!_timer) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(changeBtnTitle) userInfo:nil repeats:YES];
    }
    return _timer;
}
@end
