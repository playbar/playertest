//
//  RNTBindPhoneController.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTBindPhoneController.h"
#import "RNTLeftImageRightTextfieldView.h"
#import "RNTLeftButtonRightTextFieldView.h"
#import "RNTAccountManagerButton.h"

#define HintTextColor RNTColor_16(0xff0a18)
#define HintTextFont [UIFont systemFontOfSize:14]

@interface RNTBindPhoneController ()
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *phoneView;
@property (nonatomic, strong) RNTLeftButtonRightTextFieldView *verificationCodeView;
@property (nonatomic, strong) UILabel *hintLabel;
@property (nonatomic, strong) RNTAccountManagerButton *finishBtn;

@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@end

@implementation RNTBindPhoneController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"手机绑定";
    
    [self setupSubview];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)keyboardWillShow:(NSNotification *)notification
{
    NSDictionary *userInfo = [notification userInfo];
    
    NSValue* aValue = [userInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
    
    CGRect keyboardRect = [aValue CGRectValue];
    self.scrollView.mj_h = keyboardRect.origin.y;
}

- (void)keyboardWillHide:(NSNotification *)notification
{
    self.scrollView.mj_h = kScreenHeight;
}

- (void)setupSubview
{
    [self.scrollView addSubview:self.phoneView];
    [self.scrollView addSubview:self.verificationCodeView];
    [self.scrollView addSubview:self.hintLabel];
    [self.scrollView addSubview:self.finishBtn];
    [self.view addSubview:self.scrollView];
    
    [self.phoneView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.height.equalTo(51);
        make.top.equalTo(12);
    }];
    
    [self.verificationCodeView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(self.phoneView);
        make.top.equalTo(self.phoneView.bottom).offset(-0.5);
    }];
    
    [self.hintLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.verificationCodeView);
        make.top.equalTo(self.verificationCodeView.bottom);
        make.height.equalTo(32);
        make.right.equalTo(self.verificationCodeView).offset(-10);
    }];
    
    [self.finishBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.right.equalTo(self.view).offset(-10);
        make.top.equalTo(self.verificationCodeView.bottom).offset(69);
        make.height.equalTo(44);
    }];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

#pragma mark -
- (RNTLeftImageRightTextfieldView *)phoneView
{
    if (_phoneView == nil) {
        _phoneView = [RNTLeftImageRightTextfieldView initWithImageName:@"other_acount" placeholder:@"请输入手机号"];
        _phoneView.textField.keyboardType = UIKeyboardTypeNumberPad;
    }
    return _phoneView;
}

- (RNTLeftButtonRightTextFieldView *)verificationCodeView
{
    if (_verificationCodeView == nil) {
        _verificationCodeView = [RNTLeftButtonRightTextFieldView initWithButtonTitle:@"获取验证码" placeholder:@"请输入验证码"];
        _verificationCodeView.textField.keyboardType = UIKeyboardTypeNumberPad;
        WEAK(self)
        _verificationCodeView.btnClickBlock = ^NSString *(){
            return weakself.phoneView.textField.text;
        };
    }
    return _verificationCodeView;
}

- (UILabel *)hintLabel
{
    if (_hintLabel == nil) {
        _hintLabel = [[UILabel alloc] init];
        _hintLabel.textColor = HintTextColor;
        _hintLabel.font = HintTextFont;
        _hintLabel.textAlignment = NSTextAlignmentRight;
        _hintLabel.text = @"xxx";
    }
    return _hintLabel;
}

- (RNTAccountManagerButton *)finishBtn
{
    if (_finishBtn == nil) {
        _finishBtn = [[RNTAccountManagerButton alloc] init];
        [_finishBtn setTitle:@"完成" forState:UIControlStateNormal];
    }
    return _finishBtn;
}

- (UIScrollView *)scrollView
{
    if (_scrollView == nil) {
        _scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = YES;
        _scrollView.bounces = YES;
        _scrollView.contentSize = CGSizeMake(kScreenWidth, 300);
        //点击手势
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(endEdit)];
        
        [_scrollView addGestureRecognizer:tap];
    }
    return _scrollView;
}
@end
