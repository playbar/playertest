//
//  RNTRegisterViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

//#define kAlphaNum  @"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

#import "RNTRegisterViewController.h"
#import "RNTAccountManagerButton.h"
#import "RNTLeftImageRightTextfieldView.h"
#import "RNTLeftButtonRightTextFieldView.h"
#import "RNTUserManager.h"
#import "NSString+Extension.h"
#import "MJExtension.h"
#import "RNTHomeNetTool.h"

@interface RNTRegisterViewController ()<UITextFieldDelegate>
@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@property(nonatomic,strong) RNTLeftImageRightTextfieldView *phoneView;//手机号
@property(nonatomic,strong) RNTLeftImageRightTextfieldView *passWord;//密码
@property(nonatomic,strong) RNTLeftButtonRightTextFieldView *checkTextFiled;//验证码
@end

@implementation RNTRegisterViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"注册";
    self.navigationController.navigationBar.translucent = NO;
    self.view.backgroundColor = RNTBackgroundColor;
    
    //注册通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(successLogin:) name:LOGIN_RESULT_NOTIFICATION object:nil];
    
    [self setSubviews];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [self.view endEditing:YES];
    [super viewWillDisappear:animated];
}


#pragma mark - 点击注册
-(void)registerBtnClick
{
    //不是11位   不是1开头的手机号
    if (!(self.phoneView.textField.text.length == 11 && [self.phoneView.textField.text hasPrefix:@"1"])){
    
        [MBProgressHUD showError:@"用户名格式错误"];
        return;
    }
    
    if(self.passWord.textField.text.length<6 || self.passWord.textField.text.length>18)
    {
        [MBProgressHUD showError:@"密码字数不符合要求"];
        return;
    }
    
    if (self.checkTextFiled.textField.text.length == 6) {
        [RNTHomeNetTool checkVerifyCodeWithPhoneNumber:self.phoneView.textField.text verifyCode:self.checkTextFiled.textField.text passWord:self.passWord.textField.text];
    }else{
         [MBProgressHUD showError:@"验证码格式错误"];
        return;
    }
    
    [MBProgressHUD showMessage:@"正在为您注册靓号" toView:self.view];



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

// 字数限制
- (void)textFieldDidChange:(UITextField *)textField
{
    if (textField == self.phoneView.textField) {
        
        if (textField.text.length > 11) {
            textField.text = [textField.text substringToIndex:11];
            [MBProgressHUD showError:@"手机号超过字数限制"];
        }
    }else if(textField == self.passWord.textField){
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

- (void)dealloc
{
    RNTLog(@"dealloc-");
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == self.passWord.textField) {
        
        if (textField == self.passWord.textField) {
            if ([string isEqualToString:@" "]) {
                return NO;
            }
        }
    }
    
    return YES;
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
    
    //直接modal出来时，添加返回键
    if (self.navigationController.viewControllers.count == 1) {
        UIButton* backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [backBtn setImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateNormal];
        [backBtn setTitle:@" " forState:UIControlStateNormal];// 纯占位用
        [backBtn sizeToFit];
        [backBtn addTarget:self action:@selector(backBtnClick) forControlEvents:UIControlEventTouchUpInside];
        self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    }
    
    //背景1
    UIView *whiteBG = [[UIView alloc] init];
    whiteBG.backgroundColor = [UIColor whiteColor];
    [self.scrollView addSubview:whiteBG];
    [whiteBG mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, 44));
        make.left.right.mas_equalTo(contantView);
        make.top.mas_equalTo(contantView).offset(12);
    }];
    
    //请输入手机号View
    RNTLeftImageRightTextfieldView *phoneView = [RNTLeftImageRightTextfieldView initWithImageName:@"login_acount" placeholder:@"请输入手机号"];
    [whiteBG addSubview:phoneView];
    [phoneView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.top.bottom.mas_equalTo(whiteBG);
    }];
    self.phoneView = phoneView;
    self.phoneView.textField.keyboardType = UIKeyboardTypeNumberPad;
    [self.phoneView.textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    
    
    //背景2
    UIView *whiteBG2 = [[UIView alloc] init];
    whiteBG2.backgroundColor = [UIColor whiteColor];
    whiteBG2.layer.borderColor = RNTSeparatorColor.CGColor;
    whiteBG2.layer.borderWidth = 0.5;
    [self.scrollView addSubview:whiteBG2];
    [whiteBG2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, 44));
        make.left.right.mas_equalTo(contantView);
        make.top.mas_equalTo(whiteBG.mas_bottom);
    }];
    
    //验证码
    RNTLeftButtonRightTextFieldView *checkTextFiled = [RNTLeftButtonRightTextFieldView initWithButtonTitle:@"获取验证码" placeholder:@"请输入验证码"];
    WEAK(self)
    checkTextFiled.btnClickBlock = ^NSString *(){
        return weakself.phoneView.textField.text;
    };
    [whiteBG2 addSubview:checkTextFiled];
    [checkTextFiled mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.bottom.mas_equalTo(whiteBG2);
    }];
    self.checkTextFiled = checkTextFiled;
    self.checkTextFiled.textField.keyboardType = UIKeyboardTypeNumberPad;
    [self.checkTextFiled.textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    
    //背景3
    UIView *whiteBG3 = [[UIView alloc] init];
    whiteBG3.backgroundColor = [UIColor whiteColor];
    whiteBG3.layer.borderColor = RNTSeparatorColor.CGColor;
    whiteBG3.layer.borderWidth = 0.5;
    [self.scrollView addSubview:whiteBG3];
    [whiteBG3 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, 44));
        make.left.right.mas_equalTo(contantView);
        make.top.mas_equalTo(whiteBG2.mas_bottom).offset(12);
    }];
    
    
    //密码
    RNTLeftImageRightTextfieldView *passWord = [RNTLeftImageRightTextfieldView initWithImageName:@"login_password" placeholder:@"输入密码 (6~18位字符)"];
    [whiteBG3 addSubview:passWord];
    [passWord mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.bottom.mas_equalTo(whiteBG3);
    }];
    self.passWord = passWord;
    [self.passWord.textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    self.passWord.textField.delegate = self;
    self.passWord.textField.keyboardType = UIKeyboardTypeASCIICapable;
    
    //完成按钮
    RNTAccountManagerButton *finishBtn = [[RNTAccountManagerButton alloc] init];
    [finishBtn setTitle:@"完成" forState:UIControlStateNormal];
    [self.scrollView addSubview:finishBtn];
    [finishBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(contantView).offset(10);
        make.right.mas_equalTo(contantView).offset(-10);
        make.top.mas_equalTo(whiteBG3.bottom).offset(52);
        make.height.mas_equalTo(45);
    }];
    [finishBtn addTarget:self action:@selector(registerBtnClick) forControlEvents:UIControlEventTouchUpInside];

}

//返回键
- (void)backBtnClick
{
    if (self.gobackBlock) {
        self.gobackBlock();
    }
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
@end
