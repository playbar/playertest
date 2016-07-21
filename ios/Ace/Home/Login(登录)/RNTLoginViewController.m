//
//  RNTLoginViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

//#define kAlphaNum  @"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

#import "RNTLoginViewController.h"
#import "RNTRegisterViewController.h"
#import "UMSocial.h"
#import "RNTNavigationController.h"
#import "RNTRetrieveViewController.h"
#import "RNTLoginBtn.h"
#import "RNTUserManager.h"
#import "NSString+Extension.h"
#import "RNTWebViewController.h"
#import "AppDelegate.h"
#import "RNTTabBarContronller.h"


@interface RNTLoginViewController ()<UITextFieldDelegate>
//密码输入框
@property(nonatomic,strong) UITextField *passWordFiled;
//账号输入框
@property(nonatomic,strong) UITextField *acountFiled;
//关闭
@property(nonatomic,strong) UIButton *closeBtn;
//看看
@property(nonatomic,strong) UIButton *seeBtn;
@end

@implementation RNTLoginViewController

- (instancetype)init
{
    self = [super init];
    if (self) {
        [self setSubviews];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationController.navigationBar.translucent = YES;
    self.view.backgroundColor = RNTMainColor;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(successLogin:) name:LOGIN_RESULT_NOTIFICATION object:nil];
    
//    [self setSubviews];
    
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.view endEditing:YES];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

#pragma mark - 按钮点击
//跳转协议
-(void)intoProtocolWeb
{
//    RNTWebViewController *webVC = [RNTWebViewController webViewControllerWithTitle:@"Ace用户隐私政策" url:@"http://api.17ace.cn/app/slas.html"];
//    [self.navigationController pushViewController:webVC animated:YES];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://api.17ace.cn/app/slas.html" ]];
}
//返回上个控制器
-(void)backVC
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

//随便看看
-(void)seeVC
{
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    UIWindow *keyWindow = delegate.window;
    keyWindow.rootViewController = [[RNTTabBarContronller alloc]init];
}

//跳转找回密码
-(void)intoRetrieveVC
{
    [self.navigationController pushViewController:[[RNTRetrieveViewController alloc] init] animated:YES];
}
//跳转注册
-(void)pushIntoRegiterVC
{
    
    //跳转注册控制器
    [self.navigationController  pushViewController:[[RNTRegisterViewController alloc] init] animated:YES];
}

//账号密码登录
-(void)login
{
    [self.view endEditing:YES];
    
    if (self.acountFiled.text.length < 4) {

        [MBProgressHUD showError:@"用户名格式错误"];
        return;
    }
    
    if (self.passWordFiled.text.length == 0 || self.passWordFiled.text == nil) {
        [MBProgressHUD showError:@"密码不能为空"];
        return;
    }
    
    if(self.passWordFiled.text.length<5){
        [MBProgressHUD showError:@"密码过短,请重新输入"];
        return;
    }
    [MBProgressHUD showMessage:@"正在登录" toView:self.view];
    [[RNTUserManager sharedManager] loginWithName:self.acountFiled.text password:self.passWordFiled.text];
}

//三方登录
-(void)UMLogin:(UIButton *)btn
{

    [[RNTUserManager sharedManager] loginByThirdPart:btn.tag loginVC:self];

}

#pragma mark - set方法
-(void)setIsSeeLogin:(BOOL)isSeeLogin
{
    _isSeeLogin = isSeeLogin;
    self.closeBtn.hidden = YES;
    self.seeBtn.hidden = NO;
}

#pragma mark - 通知事件
-(void)successLogin:(NSNotification *)noti{
     [MBProgressHUD hideHUDForView:self.view];
    
    if ([noti.userInfo[@"msg"] isEqualToString:@"success"]) {
        
        [MBProgressHUD hideHUDForView:self.view];
        [MBProgressHUD showSuccess:@"登录成功"];
        [self.passWordFiled endEditing:YES];
        [self.acountFiled endEditing:YES];
        
        if (self.isSeeLogin) {
            AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
            UIWindow *keyWindow = delegate.window;
            keyWindow.rootViewController = [[RNTTabBarContronller alloc]init];
        }else{
            [self dismissViewControllerAnimated:YES completion:nil];
        }
    }else{
        
        NSString *errorStr = noti.userInfo[@"error"];

        if ([errorStr isEqualToString:@"用户已经冻结"]) {
            UIAlertView *freezeAlert = [[UIAlertView alloc] initWithTitle:@"账号封禁" message:@"因为您违反Ace平台规则或国家相关法律法规,账号已被封禁24小时。\n(如有疑问请联系QQ:2583801618)" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"我知道了", nil];
            [freezeAlert show];
        }else{
            [MBProgressHUD showError:noti.userInfo[@"error"]];
        }
    }
}

-(void)dealloc
{
     RNTLog(@"delloc-");
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField == self.passWordFiled) {

        if ([string isEqualToString:@" "]) {
            return NO;
        }
    }
    
    return YES;
}

// 字数限制
- (void)textFieldDidChange:(UITextField *)textField
{
    if (textField == self.acountFiled) {
       
        if (textField.text.length > 11) {
            textField.text = [textField.text substringToIndex:11];
             [MBProgressHUD showError:@"手机号超过字数限制"];
        }
    }else if(textField == self.passWordFiled){
        if (textField.text.length > 18) {
            textField.text = [textField.text substringToIndex:18];
            [MBProgressHUD showError:@"密码超过字数限制"];
        }
    }
}
#pragma mark - 布局子控件
-(void)setSubviews
{
    //背景图片
    UIImageView *BGImg = [[UIImageView alloc] initWithFrame:self.view.bounds];
    BGImg.image = [UIImage imageNamed:@"login_bg"];
    BGImg.userInteractionEnabled = YES;
    BGImg.backgroundColor = RNTMainColor;
    [self.view addSubview:BGImg];
    
    
    //关闭
    UIButton *closeBtn = [[UIButton alloc] init];
    [closeBtn setBackgroundImage:[UIImage imageNamed:@"login_close_normal"] forState:UIControlStateNormal];
    [closeBtn setBackgroundImage:[UIImage imageNamed:@"login_close_highlighted"] forState:UIControlStateHighlighted];
    [closeBtn sizeToFit];
    [self.view addSubview:closeBtn];
    [closeBtn addTarget:self action:@selector(backVC) forControlEvents:UIControlEventTouchUpInside];
    [closeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        if (iPhone5) {
            make.top.equalTo (self.view).offset(25);
            make.right.equalTo(self.view).offset(-23);
        }else{
            make.top.equalTo (self.view).offset(40);
            make.right.equalTo(self.view).offset(-25);
        }
    }];
    self.closeBtn = closeBtn;
    self.closeBtn.hidden = NO;
    
    
    //ACE
    UIImageView *aceImg = [[UIImageView alloc] init];
    aceImg.image = [UIImage imageNamed:@"logo"];
    aceImg.clipsToBounds = YES;
    [self.view addSubview:aceImg];
    [aceImg mas_makeConstraints:^(MASConstraintMaker *make) {
        if (iPhone4) {
            make.top.mas_equalTo(self.view).offset(48);
        }else{
            
            make.top.mas_equalTo(self.view).offset(88*kScreenWidth/375);
        }
        make.centerX.mas_equalTo(self.view);
        make.size.mas_equalTo(CGSizeMake(150, 50));
    }];
    
    
    //微信登录
    RNTLoginBtn *weChatBtn = [[RNTLoginBtn alloc] init];
    [weChatBtn setTitle:@"微信" forState:UIControlStateNormal];
    [weChatBtn setImage:[UIImage imageNamed:@"login_Wechat"] forState:UIControlStateNormal];
    [weChatBtn setImage:[UIImage imageNamed:@"login_Wechat_highlighted"] forState:UIControlStateHighlighted];
    [weChatBtn setTitleColor:[UIColor colorWithWhite:0.0 alpha:0.4] forState:UIControlStateHighlighted];
    [self.view addSubview:weChatBtn];
    [weChatBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.view);
        make.size.mas_equalTo(CGSizeMake(32, 49));
        make.bottom.mas_equalTo(self.view).offset(-70);
    }];
    weChatBtn.tag = 1;
    [weChatBtn addTarget:self action:@selector(UMLogin:) forControlEvents:UIControlEventTouchUpInside];
    
    //QQ登录
    RNTLoginBtn *qqBtn = [[RNTLoginBtn alloc] init];
    [qqBtn setTitle:@"腾讯" forState:UIControlStateNormal];
    [qqBtn setImage:[UIImage imageNamed:@"login_QQ"] forState:UIControlStateNormal];
    [qqBtn setImage:[UIImage imageNamed:@"login_QQ_highlighted"] forState:UIControlStateHighlighted];
    [qqBtn setTitleColor:[UIColor colorWithWhite:0.0 alpha:0.4] forState:UIControlStateHighlighted];
    [self.view addSubview:qqBtn];
    [qqBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(weChatBtn.left).offset(-65);
        make.size.mas_equalTo(CGSizeMake(32, 49));
        make.bottom.mas_equalTo(self.view).offset(-70);
    }];
    qqBtn.tag = 0;
    [qqBtn addTarget:self action:@selector(UMLogin:) forControlEvents:UIControlEventTouchUpInside];
    
    
    
    //新浪登录
    RNTLoginBtn *SinaBtn = [[RNTLoginBtn alloc] init];
    [SinaBtn setTitle:@"新浪" forState:UIControlStateNormal];
    [SinaBtn setImage:[UIImage imageNamed:@"login_Sina"] forState:UIControlStateNormal];
    [SinaBtn setImage:[UIImage imageNamed:@"login_Sina_highlighted"] forState:UIControlStateHighlighted];
    [SinaBtn setTitleColor:[UIColor colorWithWhite:0.0 alpha:0.4] forState:UIControlStateHighlighted];
    [self.view addSubview:SinaBtn];
    [SinaBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(weChatBtn.right).offset(65);
        make.size.mas_equalTo(CGSizeMake(32, 49));
        make.bottom.mas_equalTo(self.view).offset(-70);
    }];
    SinaBtn.tag = 2;
    [SinaBtn addTarget:self action:@selector(UMLogin:) forControlEvents:UIControlEventTouchUpInside];
    
    
    //注册
    UIButton *registBtn = [[UIButton alloc] init];
    [registBtn setTitle:@"注册" forState:UIControlStateNormal];
    registBtn.layer.cornerRadius = 3;
    registBtn.clipsToBounds = YES;
    registBtn.layer.borderWidth =1;
    registBtn.layer.borderColor = [UIColor blackColor].CGColor;
    registBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [registBtn setTitleColor:RNTColor_16(0x000000) forState:UIControlStateNormal];
    [self.view addSubview:registBtn];
    [registBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        if (iPhone5) {
             make.size.mas_equalTo(CGSizeMake(250, 40));
        }else{
        
            make.size.mas_equalTo(CGSizeMake(300, 40));
        }
        if (iPhone4) {
            make.bottom.mas_equalTo(qqBtn.top).offset(-20);
            
        }else{
            
            make.bottom.mas_equalTo(qqBtn.top).offset(-32*kScreenWidth/375);
        }
        make.centerX.mas_equalTo(self.view);
    }];
    [registBtn addTarget:self action:@selector(pushIntoRegiterVC) forControlEvents:UIControlEventTouchUpInside];
    
    //登录
    UIButton *loginBtn = [[UIButton alloc] init];
    [loginBtn setTitle:@"登录" forState:UIControlStateNormal];
    [loginBtn addTarget:self action:@selector(login) forControlEvents:UIControlEventTouchUpInside];
    loginBtn.backgroundColor = [UIColor blackColor];
    loginBtn.layer.cornerRadius = 3;
    loginBtn.clipsToBounds = YES;
    loginBtn.layer.borderWidth =1;
    loginBtn.layer.borderColor = [UIColor blackColor].CGColor;
    loginBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    [loginBtn setTitleColor:RNTColor_16(0xffffff) forState:UIControlStateNormal];
    [self.view addSubview:loginBtn];
    [loginBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        if (iPhone5) {
            make.size.mas_equalTo(CGSizeMake(250, 40));
        }else{
            
            make.size.mas_equalTo(CGSizeMake(300, 40));
        }
        make.bottom.mas_equalTo(registBtn.top).offset(-10);
        make.centerX.mas_equalTo(self.view);
    }];
    
    
    //找回密码
    NSMutableAttributedString *attriTitle = [[NSMutableAttributedString alloc] initWithString:@"忘记密码?点此找回"];
    [attriTitle addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:14] range:(NSRange){0,9}];
    [attriTitle addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x8d7300) range:(NSRange){0,5}];
    
    NSMutableAttributedString *attriTitleHigh = [[NSMutableAttributedString alloc] initWithString:@"忘记密码?点此找回"];
    [attriTitleHigh addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:14] range:(NSRange){0,9}];
    [attriTitleHigh addAttribute:NSForegroundColorAttributeName value:[UIColor grayColor] range:(NSRange){0,9}];
    
    UIButton *foundBtn = [[UIButton alloc] init];
    [foundBtn setAttributedTitle:attriTitle forState:UIControlStateNormal];
    [foundBtn setAttributedTitle:attriTitleHigh forState:UIControlStateHighlighted];
    [foundBtn addTarget:self action:@selector(intoRetrieveVC) forControlEvents:UIControlEventTouchUpInside];
    [foundBtn sizeToFit];
    [self.view addSubview:foundBtn];
    [foundBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.view).offset(-40);
        if (iPhone4) {
            make.bottom.mas_equalTo(loginBtn.top).offset(-32);
        }else if(iPhone5){
            make.bottom.mas_equalTo(loginBtn.top).offset(-37);
        }else{
            make.bottom.mas_equalTo(loginBtn.top).offset(-72*kScreenWidth/375);
        }
    }];

    //密码横线
    UIView *passLine = [[UIView alloc] init];
    passLine.backgroundColor = [UIColor blackColor];
    [self.view addSubview:passLine];
    [passLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(foundBtn.top).offset(-14);
        make.right.mas_equalTo(self.view).offset(-40);
        make.size.mas_equalTo(CGSizeMake(265*kScreenWidth/375, 1));
    }];
    
    //密码输入框
    UITextField *passWordFiled = [[UITextField alloc] init];
    passWordFiled.placeholder = @"请输入密码";
    passWordFiled.textAlignment = NSTextAlignmentLeft;
    passWordFiled.font = [UIFont systemFontOfSize:16];
    [passWordFiled setValue: RNTColor_16(0xb29300) forKeyPath:@"_placeholderLabel.textColor"];
    [self.view addSubview:passWordFiled];
    [passWordFiled mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(passLine.top).offset(-1);
        make.size.mas_equalTo(CGSizeMake(265*kScreenWidth/375, 18));
        make.left.mas_equalTo(passLine);
    }];
    self.passWordFiled = passWordFiled;
    self.passWordFiled.secureTextEntry = YES;
    self.passWordFiled.delegate = self;
    self.passWordFiled.clearButtonMode = UITextFieldViewModeWhileEditing;
    self.passWordFiled.keyboardType = UIKeyboardTypeASCIICapable;
    [self.passWordFiled addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    
    //密码图标
    UIImageView *passWordImg = [[UIImageView alloc] init];
    passWordImg.image = [UIImage imageNamed:@"login_password"];
    [passWordImg sizeToFit];
    [self.view addSubview:passWordImg];
    [passWordImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(passLine.left).offset(-10);
        make.centerY.mas_equalTo(passWordFiled);
    }];
    
    //账号横线
    UIView *acountLine = [[UIView alloc] init];
    acountLine.backgroundColor = [UIColor blackColor];
    [self.view addSubview:acountLine];
    [acountLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(passWordFiled.top).offset(-27);
        make.right.mas_equalTo(self.view).offset(-40);
        make.size.mas_equalTo(CGSizeMake(265*kScreenWidth/375, 1));
    }];
    
    //账号输入框
    UITextField *acountFiled = [[UITextField alloc] init];
    acountFiled.placeholder = @"请输入用户ID或手机号";
    acountFiled.textAlignment = NSTextAlignmentLeft;
    acountFiled.font = [UIFont systemFontOfSize:16];
    [acountFiled setValue: RNTColor_16(0xb29300) forKeyPath:@"_placeholderLabel.textColor"];
    [self.view addSubview:acountFiled];
    [acountFiled mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(acountLine.top).offset(-1);
        make.size.mas_equalTo(CGSizeMake(265*kScreenWidth/375, 18));
        make.left.mas_equalTo(acountLine);
    }];
    self.acountFiled = acountFiled;
    self.acountFiled.keyboardType = UIKeyboardTypeNumberPad;
    self.acountFiled.delegate = self;
    self.acountFiled.clearButtonMode =  UITextFieldViewModeWhileEditing;
    [self.acountFiled addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    
    //账号图标
    UIImageView *acountImg = [[UIImageView alloc] init];
    acountImg.image = [UIImage imageNamed:@"login_acount"];
    [acountImg sizeToFit];
    [self.view addSubview:acountImg];
    [acountImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(acountLine.left).offset(-10);
        make.centerY.mas_equalTo(acountFiled);
    }];
    
    //协议label
    UILabel *protocol = [[UILabel alloc] init];
    protocol.text = @"登录即代表你同意";
    protocol.textColor = RNTColor_16(0x6e5500);
    protocol.font = [UIFont systemFontOfSize:13];
    [protocol sizeToFit];
    [self.view addSubview:protocol];
    [protocol mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(self.view).offset(-37);
        make.right.mas_equalTo(self.view.mas_centerX);
    }];
    
    //协议btn
    UIButton *proBtn = [[UIButton alloc] init];
    [self.view addSubview:proBtn];
    [proBtn sizeToFit];
    
    NSMutableAttributedString *title = [[NSMutableAttributedString alloc] initWithString:@"用户许可及服务协议"];
    NSRange titleRange = {0,[title length]};
    [title addAttribute:NSUnderlineStyleAttributeName value:[NSNumber numberWithInteger:NSUnderlineStyleSingle] range:titleRange];
    [title addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0xf46404) range:titleRange];
    
    NSMutableAttributedString *title2 = [[NSMutableAttributedString alloc] initWithString:@"用户许可及服务协议"];
    [title2 addAttribute:NSUnderlineStyleAttributeName value:[NSNumber numberWithInteger:NSUnderlineStyleSingle] range:titleRange];
    [title2 addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x6e5500) range:titleRange];
    
    [proBtn setAttributedTitle:title forState:UIControlStateNormal];
    [proBtn setAttributedTitle:title2 forState:UIControlStateHighlighted];
    proBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    [proBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(protocol.mas_right);
        make.centerY.mas_equalTo(protocol);
    }];
    [proBtn addTarget:self action:@selector(intoProtocolWeb) forControlEvents:UIControlEventTouchUpInside];
    
    //看看
    UIButton *seeBtn = [[UIButton alloc] init];
    
    NSMutableAttributedString *seeStr = [[NSMutableAttributedString alloc] initWithString:@"随便看看"];
    NSRange rangge = {0,[seeStr length]};
    [seeStr addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:13] range:rangge];
    [seeStr addAttribute:NSUnderlineStyleAttributeName value:@1 range:rangge];
     [seeStr addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x6e5500)  range:rangge];
    
    NSMutableAttributedString *seeStr2= [[NSMutableAttributedString alloc] initWithString:@"随便看看"];
    [seeStr2 addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:13] range:rangge];
    [seeStr2 addAttribute:NSUnderlineStyleAttributeName value:@1 range:rangge];
    [seeStr2 addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x8d7300) range:rangge];
    
    [seeBtn setAttributedTitle:seeStr forState:UIControlStateNormal];
    [seeBtn setAttributedTitle:seeStr2 forState:UIControlStateHighlighted];
    
    [self.view addSubview:seeBtn];
    
    [seeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(self.view).offset(-6);
        make.centerX.mas_equalTo(self.view);
    }];
    self.seeBtn = seeBtn;
    self.seeBtn.hidden = YES;
    [self.seeBtn addTarget:self action:@selector(seeVC) forControlEvents:UIControlEventTouchUpInside];
}
@end
