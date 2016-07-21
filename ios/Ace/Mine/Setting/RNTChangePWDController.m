//
//  RNTChangePWDController.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTChangePWDController.h"
#import "RNTLeftImageRightTextfieldView.h"
#import "RNTAccountManagerButton.h"
#import "RNTMineNetTool.h"

#define HintTextColor RNTColor_16(0xff0a18)
#define HintTextFont [UIFont systemFontOfSize:14]

@interface RNTChangePWDController ()<UITextFieldDelegate>
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *oldPWDView;
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *NewPWDView;
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *verifyPWDView;
@property (nonatomic, strong) RNTAccountManagerButton *finishBtn;

@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@end

@implementation RNTChangePWDController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"修改密码";
    
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

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setupSubview
{
    [self.scrollView addSubview:self.oldPWDView];
    [self.scrollView addSubview:self.NewPWDView];
    [self.scrollView addSubview:self.verifyPWDView];
    [self.scrollView addSubview:self.finishBtn];
    [self.view addSubview:self.scrollView];
    
    [self.oldPWDView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.top.equalTo(12);
        make.height.equalTo(51);
    }];
    
    [self.NewPWDView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(self.oldPWDView);
        make.top.equalTo(self.oldPWDView.bottom).offset(-0.5);
    }];
    
    [self.verifyPWDView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(self.oldPWDView);
        make.top.equalTo(self.NewPWDView.bottom).offset(-0.5);
    }];
    
    [self.finishBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.right.equalTo(self.view).offset(-10);
        make.top.equalTo(self.verifyPWDView.bottom).offset(69);
        make.height.equalTo(44);
    }];
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

- (void)finishBtnClick
{
    NSString *oldPWD = self.oldPWDView.textField.text;
    NSString *newPWD = self.NewPWDView.textField.text;
    NSString *verifyPWD = self.verifyPWDView.textField.text;
    
    if (oldPWD.length < 6 || oldPWD.length > 18) {
        [MBProgressHUD showError:@"旧密码格式不正确"];
        return;
    }
    
    if (newPWD.length < 6 || newPWD.length > 18) {
        [MBProgressHUD showError:@"新密码格式不正确"];
        return;
    }
    
    if (verifyPWD.length < 6 || verifyPWD.length > 18) {
        [MBProgressHUD showError:@"确认密码格式不正确"];
        return;
    }
    
    if ([self.NewPWDView.textField.text isEqualToString:self.verifyPWDView.textField.text]) {
        RNTUserManager *userM = [RNTUserManager sharedManager];
        
        NSString *oldPasswd = self.oldPWDView.textField.text;
        NSString *newPasswd = self.NewPWDView.textField.text;
        
        WEAK(self);
        [RNTMineNetTool changePWDWithUserId:userM.user.userId oldPasswd:oldPasswd newPasswd:newPasswd success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                [MBProgressHUD showSuccess:@"修改成功"];
                [[NSUserDefaults standardUserDefaults] setObject:newPasswd forKey:USER_PASSWORD];
                [[NSUserDefaults standardUserDefaults] synchronize];
                userM.logged = NO;
                [weakself.navigationController popToRootViewControllerAnimated:YES];
            } else {
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            RNTLog(@"%@", error.localizedDescription);
        }];
    } else {
        [MBProgressHUD showError:@"新密码输入不一致！！"];
    }
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if ((textField.text.length >= 18 && string.length > 0) || [string isEqualToString:@" "]) {
        return NO;
    }
    return YES;
}

#pragma mark -
- (RNTLeftImageRightTextfieldView *)oldPWDView
{
    if (_oldPWDView == nil) {
        _oldPWDView = [RNTLeftImageRightTextfieldView initWithImageName:@"other_password" placeholder:@"请输入原密码（6-18位字符）"];
        _oldPWDView.textField.keyboardType = UIKeyboardTypeASCIICapable;
        _oldPWDView.textField.secureTextEntry = YES;
        _oldPWDView.textField.delegate = self;
    }
    return _oldPWDView;
}

- (RNTLeftImageRightTextfieldView *)NewPWDView
{
    if (_NewPWDView == nil) {
        _NewPWDView = [RNTLeftImageRightTextfieldView initWithImageName:@"other_password" placeholder:@"请输入新密码（6-18位字符）"];
        _NewPWDView.textField.keyboardType = UIKeyboardTypeASCIICapable;
        _NewPWDView.textField.secureTextEntry = YES;
        _NewPWDView.textField.delegate = self;
    }
    return _NewPWDView;
}

- (RNTLeftImageRightTextfieldView *)verifyPWDView
{
    if (_verifyPWDView == nil) {
        _verifyPWDView = [RNTLeftImageRightTextfieldView initWithImageName:@"other_password" placeholder:@"请再输入一次密码"];
        _verifyPWDView.textField.keyboardType = UIKeyboardTypeASCIICapable;
        _verifyPWDView.textField.secureTextEntry = YES;
        _verifyPWDView.textField.delegate = self;
    }
    return _verifyPWDView;
}

- (RNTAccountManagerButton *)finishBtn
{
    if (_finishBtn == nil) {
        _finishBtn = [[RNTAccountManagerButton alloc] init];
        [_finishBtn setTitle:@"完成" forState:UIControlStateNormal];
        [_finishBtn addTarget:self action:@selector(finishBtnClick) forControlEvents:UIControlEventTouchUpInside];
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
