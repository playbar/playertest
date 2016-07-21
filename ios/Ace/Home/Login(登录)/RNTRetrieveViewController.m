//
//  RNTRetrieveViewController.m
//  Ace
//
//  Created by 靳峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTRetrieveViewController.h"
#import "RNTLeftImageRightTextfieldView.h"
#import "RNTAccountManagerButton.h"
#import "RNTChangePWDController.h"
#import "RNTLeftButtonRightTextFieldView.h"
#import "RNTHomeNetTool.h"
#import "NSString+Extension.h"
#import "RNTUserManager.h"
#import "RNTNetTool.h"

#define HintTextColor RNTColor_16(0xff0a18)
#define HintTextFont [UIFont systemFontOfSize:14]
//#define kAlphaNum  @"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

@interface RNTRetrieveViewController ()<UITextFieldDelegate>
@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@property(nonatomic,weak) RNTLeftImageRightTextfieldView *phoneView;
@property(nonatomic,weak) RNTLeftButtonRightTextFieldView *checkTextFiled;
@property(nonatomic,weak) RNTLeftImageRightTextfieldView *NewPWD;
@property (nonatomic, strong) UILabel *hintLabel1;
@property (nonatomic, strong) UILabel *hintLabel2;
@property (nonatomic, strong) UILabel *hintLabel3;
@end

@implementation RNTRetrieveViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.navigationController.navigationBar.translucent = NO;
    self.title = @"找回密码";
    [self setSubviews];
    
    //注册通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(successLogin:) name:LOGIN_RESULT_NOTIFICATION object:nil];
}

#pragma mark - 通知事件
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

//登录结果
-(void)successLogin:(NSNotification *)noti{
    [MBProgressHUD hideHUDForView:self.view];
    if ([noti.userInfo[@"msg"] isEqualToString:@"success"]) {
        [MBProgressHUD showSuccess:@"登录成功"];
        [self.navigationController.topViewController dismissViewControllerAnimated:YES completion:nil];
    }else{
        [MBProgressHUD showError:noti.userInfo[@"error"]];
    }
}

// 字数限制
- (void)textFieldDidChange:(UITextField *)textField
{
    if (textField == self.phoneView.textField) {
        
        if (textField.text.length > 11) {
            textField.text = [textField.text substringToIndex:11];
            [MBProgressHUD showError:@"手机号超过字数限制"];
        }
    }else if(textField == self.NewPWD.textField){
        if (textField.text.length > 18) {
            textField.text = [textField.text substringToIndex:18];
            [MBProgressHUD showError:@"密码超过字数限制"];
        }
    }else{
        if (textField.text.length > 6) {
            textField.text = [textField.text substringToIndex:6];
            [MBProgressHUD showError:@"验证码超过字数限制"];
        }
    }
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


#pragma mark - 按钮点击
//进入修改密码
-(void)changePassWord:(UIButton *)btn;
{
    
    self.hintLabel1.text = nil;
    self.hintLabel2.text = nil;
    self.hintLabel3.text = nil;
    
    btn.userInteractionEnabled = NO;
    
    NSString *phoneNum = self.phoneView.textField.text ;
    NSString *checkNum =   self.checkTextFiled.textField.text;
    NSString *pwd = self.NewPWD.textField.text;
    
    if (![phoneNum hasPrefix:@"1"] ||phoneNum.length !=11) {
        [MBProgressHUD showError:@"号码格式错误"];
        btn.userInteractionEnabled = YES;
        return;
    }
    
    if (checkNum.length != 6 || checkNum == nil) {
        [MBProgressHUD showError:@"验证码格式错误"];
        btn.userInteractionEnabled = YES;

        return;
    }
    
    if (pwd.length>18 || pwd.length<6 || pwd.length == 0 || pwd == nil) {
        [MBProgressHUD showError:@"密码格式错误"];
        btn.userInteractionEnabled = YES;

        return;
    }
    
    [RNTHomeNetTool changePassWordWithPhoneNum:phoneNum verifyCode:checkNum newPWD:pwd checkSuccess:^{
        [MBProgressHUD showSuccess:@"成功找回,请登录"];
        btn.userInteractionEnabled = YES;
        [self.view endEditing:YES];
        [self.navigationController popViewControllerAnimated:YES];
    } getFail:^(NSString *errStr){
        btn.userInteractionEnabled = YES;
        [MBProgressHUD showError:errStr];
    }];
    
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    
    [self.view addSubview:self.scrollView];
    
    UIView *contantView = [[UIView alloc] init];
    [self.scrollView addSubview:contantView];
    [contantView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(self.scrollView);
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, 260));
    }];
    
    //请输入手机号View
    RNTLeftImageRightTextfieldView *phoneView = [RNTLeftImageRightTextfieldView initWithImageName:@"login_acount" placeholder:@"请输入手机号"];
    [self.scrollView addSubview:phoneView];
    [phoneView mas_makeConstraints:^(MASConstraintMaker *make) {
                make.size.mas_equalTo(CGSizeMake(kScreenWidth, 44));
                make.left.right.mas_equalTo(contantView);
                make.top.mas_equalTo(contantView).offset(12);
    }];
    phoneView.textField.keyboardType = UIKeyboardTypeNumberPad;
    self.phoneView = phoneView;
    self.phoneView.textField.delegate = self;
    [self.phoneView.textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    
    //验证码
    WEAK(self)
    RNTLeftButtonRightTextFieldView *checkTextFiled = [RNTLeftButtonRightTextFieldView initWithButtonTitle:@"获取验证码" placeholder:@"请输入验证码"];
    checkTextFiled.btnClickBlock = ^NSString *(){
        NSString *phoneNum = weakself.phoneView.textField.text ;
        return phoneNum;
    };
    [self.scrollView addSubview:checkTextFiled];
    [checkTextFiled mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, 44));
        make.left.right.mas_equalTo(contantView);
        make.top.mas_equalTo(phoneView.mas_bottom);
    }];
    

    self.checkTextFiled = checkTextFiled;
    self.checkTextFiled.textField.delegate = self;
    self.checkTextFiled.textField.keyboardType = UIKeyboardTypeNumberPad;
    [self.checkTextFiled.textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];

    //新密码
    RNTLeftImageRightTextfieldView *newPWD = [RNTLeftImageRightTextfieldView initWithImageName:@"other_password" placeholder:@"请输入新密码（6-18位字符）"];
    [self.scrollView addSubview:newPWD];
    [newPWD mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(checkTextFiled.bottom);
        make.left.right.mas_equalTo(contantView);
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, 44));
    }];
    self.NewPWD =newPWD;
    self.NewPWD.textField.delegate = self;
    self.NewPWD.textField.keyboardType = UIKeyboardTypeASCIICapable;
    [self.NewPWD.textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];

    
    //完成
    RNTAccountManagerButton *nextBtn = [[RNTAccountManagerButton alloc] init];
    [self.scrollView addSubview:nextBtn];
    [nextBtn setTitle:@"完成" forState:UIControlStateNormal];
    [nextBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(contantView).offset(10);
        make.right.mas_equalTo(contantView).offset(-10);
        make.top.mas_equalTo(checkTextFiled.bottom).offset(120);
        make.height.mas_equalTo(45);
    }];
    [nextBtn addTarget:self action:@selector(changePassWord:) forControlEvents:UIControlEventTouchUpInside];
}

#pragma mark - UITextFiledDelegate
- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    if (textField == self.phoneView.textField) {
        self.hintLabel1.text = nil;
    } else if (textField == self.checkTextFiled.textField) {
        self.hintLabel2.text = nil;
    } else if (textField == self.NewPWD.textField) {
        self.hintLabel3.text = nil;
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == self.NewPWD.textField) {
        if ([string isEqualToString:@" "]) {
            return NO;
        }
    }
    
    return YES;
}




#pragma mark - 懒加载
- (UIScrollView *)scrollView
{
    if (_scrollView == nil) {
        _scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = YES;
        _scrollView.bounces = YES;
        _scrollView.contentSize = CGSizeMake(kScreenWidth, 320);
        //点击手势
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(endEdit)];
        
        [_scrollView addGestureRecognizer:tap];
    }
    return _scrollView;
}

- (UILabel *)hintLabel1
{
    if (_hintLabel1 == nil) {
        _hintLabel1 = [[UILabel alloc] init];
        _hintLabel1.textColor = HintTextColor;
        _hintLabel1.font = HintTextFont;
        _hintLabel1.textAlignment = NSTextAlignmentRight;
        [self.scrollView addSubview:_hintLabel1];
    }
    return _hintLabel1;
}

- (UILabel *)hintLabel2
{
    if (_hintLabel2 == nil) {
        _hintLabel2 = [[UILabel alloc] init];
        _hintLabel2.textColor = HintTextColor;
        _hintLabel2.font = HintTextFont;
        _hintLabel2.textAlignment = NSTextAlignmentRight;
        [self.scrollView addSubview:_hintLabel2];

    }
    return _hintLabel2;
}

- (UILabel *)hintLabel3
{
    if (_hintLabel3 == nil) {
        _hintLabel3 = [[UILabel alloc] init];
        _hintLabel3.textColor = HintTextColor;
        _hintLabel3.font = HintTextFont;
        _hintLabel3.textAlignment = NSTextAlignmentRight;
        [self.scrollView addSubview:_hintLabel3];

    }
    return _hintLabel3;
}


@end
