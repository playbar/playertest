//
//  RNTBindWechatController.m
//  Ace
//
//  Created by 周兵 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTBindWechatController.h"
#import "RNTExchangeOrderController.h"
#import "RNTLeftImageRightTextfieldView.h"
#import "RNTAccountManagerButton.h"
#import "RNTLeftButtonRightTextFieldView.h"
#import "RNTMineNetTool.h"
#import "RegexKitLite.h"

@interface RNTBindWechatController () <UITextFieldDelegate>
@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *wechatView;
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *confirmWechatView;
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *phoneView;
@property (nonatomic, strong) RNTLeftButtonRightTextFieldView *verificationCodeView;
@property (nonatomic, strong) RNTAccountManagerButton *bindBtn;
@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@end

@implementation RNTBindWechatController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.title = @"绑定微信";
    
    [self setupSubviews];
    
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

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setupSubviews
{
    [self.scrollView addSubview:self.imageView];
    [self.scrollView addSubview:self.titleLabel];
    [self.scrollView addSubview:self.wechatView];
    [self.scrollView addSubview:self.confirmWechatView];
    [self.scrollView addSubview:self.phoneView];
    [self.scrollView addSubview:self.verificationCodeView];
    [self.scrollView addSubview:self.bindBtn];
    [self.view addSubview:self.scrollView];
    
    [self.imageView makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.top.equalTo(16);
    }];
    
    [self.titleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.bottom.equalTo(107);
        make.height.equalTo(15);
    }];

    [self.wechatView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.titleLabel);
        make.top.equalTo(132);
        make.height.equalTo(44);
    }];

    [self.confirmWechatView makeConstraints:^(MASConstraintMaker *make) {
        make.height.left.right.equalTo(self.wechatView);
        make.top.equalTo(self.wechatView.bottom).offset(-0.5);
    }];
    
    [self.phoneView makeConstraints:^(MASConstraintMaker *make) {
        make.height.left.right.equalTo(self.wechatView);
        make.top.equalTo(self.confirmWechatView.bottom).offset(12);
    }];
    
    [self.verificationCodeView makeConstraints:^(MASConstraintMaker *make) {
        make.height.left.right.equalTo(self.wechatView);
        make.top.equalTo(self.phoneView.bottom).offset(-0.5);
    }];
    
    [self.bindBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.right.equalTo(self.view.right).offset(-10);
        make.top.equalTo(self.verificationCodeView.bottom).offset(52);
        make.height.equalTo(44);
    }];
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

#pragma mark - 绑定微信
- (void)bindBtnClick
{
    WEAK(self);
    NSString *wechatId = self.wechatView.textField.text;
    NSString *confirmWechat = self.confirmWechatView.textField.text;
    NSString *verifyCode = self.verificationCodeView.textField.text;
    NSString *mobileId = self.phoneView.textField.text;
    
    if (wechatId.length <= 0 || confirmWechat.length <= 0) {
        [MBProgressHUD showError:@"请输入微信账号"];
        return;
    }
    
    if (![wechatId isEqualToString:confirmWechat]) {
        [MBProgressHUD showError:@"微信账号不统一"];
        return;
    }
    
    if (mobileId.length != 11 || ![mobileId hasPrefix:@"1"]) {
        [MBProgressHUD showError:@"手机号错误"];
        return;
    }
    
    NSString *pattern = @"[0-9]{6}";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
    if (![pred evaluateWithObject:verifyCode]) {
        [MBProgressHUD showError:@"验证码错误"];
        return;
    }

    [RNTMineNetTool bindWechatWithUserId:[RNTUserManager sharedManager].user.userId weChatId:wechatId verifyCode:verifyCode mobileId:mobileId success:^(NSDictionary *dict) {
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            RNTExchangeOrderController *EOVC = [[RNTExchangeOrderController alloc] init];
            EOVC.RMBCount = weakself.RMBCount;
            EOVC.silverCount = weakself.silverCount;
            EOVC.weChatId = wechatId;
            EOVC.mobileId = mobileId;
            [weakself.navigationController pushViewController:EOVC animated:YES];
        } else {
            [MBProgressHUD showError:@"绑定失败"];
        }
    } failure:^(NSError *error) {
        [MBProgressHUD showError:@"网络连接失败"];
    }];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    int maxLength = 0;
    if (textField == self.phoneView.textField) {
        maxLength = 11;
    } else if (textField == self.verificationCodeView.textField) {
        maxLength = 6;
    }
    
    if (string.length > 0 && textField.text.length >= maxLength) {
        return NO;
    }
    return YES;
}

#pragma mark -
- (UIImageView *)imageView
{
    if (_imageView == nil) {
        _imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Mine_wechat"]];
    }
    return _imageView;
}

- (UILabel *)titleLabel
{
    if (_titleLabel == nil) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.text = @"绑定微信才可提现";
        _titleLabel.font = [UIFont systemFontOfSize:15];
        _titleLabel.textColor = RNTColor_16(0x19191a);
    }
    return _titleLabel;
}

- (RNTLeftImageRightTextfieldView *)wechatView
{
    if (_wechatView == nil) {
        _wechatView = [RNTLeftImageRightTextfieldView initWithImageName:nil placeholder:@"请输入微信号"];
        _wechatView.textField.keyboardType = UIKeyboardTypeASCIICapable;
        _wechatView.textField.secureTextEntry = YES;
    }
    return _wechatView;
}

- (RNTLeftImageRightTextfieldView *)confirmWechatView
{
    if (_confirmWechatView == nil) {
        _confirmWechatView = [RNTLeftImageRightTextfieldView initWithImageName:nil placeholder:@"请确认微信号"];
        _confirmWechatView.textField.keyboardType = UIKeyboardTypeASCIICapable;
        _confirmWechatView.textField.secureTextEntry = YES;
    }
    return _confirmWechatView;
}

- (RNTLeftImageRightTextfieldView *)phoneView
{
    if (_phoneView == nil) {
        _phoneView = [RNTLeftImageRightTextfieldView initWithImageName:nil placeholder:@"请输入需要绑定的手机号"];
        _phoneView.textField.keyboardType = UIKeyboardTypeNumberPad;
        _phoneView.textField.delegate = self;
    }
    return _phoneView;
}

- (RNTLeftButtonRightTextFieldView *)verificationCodeView
{
    if (_verificationCodeView == nil) {
        _verificationCodeView = [RNTLeftButtonRightTextFieldView initWithButtonTitle:@"获取验证码" placeholder:@"请输入验证码"];
        _verificationCodeView.textField.keyboardType = UIKeyboardTypeNumberPad;
        _verificationCodeView.textField.delegate = self;
        WEAK(self);
        _verificationCodeView.btnClickBlock = ^NSString *(){
             return weakself.phoneView.textField.text;
        };
    }
    return _verificationCodeView;
}

- (RNTAccountManagerButton *)bindBtn
{
    if (_bindBtn == nil) {
        _bindBtn = [[RNTAccountManagerButton alloc] init];
        [_bindBtn setTitle:@"绑定" forState:UIControlStateNormal];
        [_bindBtn addTarget:self action:@selector(bindBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _bindBtn;
}

- (UIScrollView *)scrollView
{
    if (_scrollView == nil) {
        _scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = YES;
        _scrollView.bounces = YES;
        _scrollView.contentSize = CGSizeMake(kScreenWidth, 500);
        //点击手势
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(endEdit)];
        
        [_scrollView addGestureRecognizer:tap];
    }
    return _scrollView;
}
@end
