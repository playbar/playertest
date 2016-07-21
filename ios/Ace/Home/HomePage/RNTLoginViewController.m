//
//  RNTLoginViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTLoginViewController.h"
#import "RNTRegisterViewController.h"
#import "UMSocial.h"
#import "RNTNavigationController.h"
#import "RNTRetrieveViewController.h"

@interface RNTLoginViewController ()<UITextFieldDelegate>
//密码输入框
@property(nonatomic,strong) UITextField *passWordFiled;
//账号输入框
@property(nonatomic,strong) UITextField *acountFiled;
@end

@implementation RNTLoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationController.navigationBar.translucent = YES;
    self.view.backgroundColor = RNTMainColor;
    [self setSubviews];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}

#pragma mark - 按钮点击
//返回上个控制器
-(void)backVC
{
    [self dismissViewControllerAnimated:YES completion:^{
        
    }];
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
    //无账号密码
    if (self.passWordFiled.text.length == 0 || self.acountFiled.text == 0) {
        [MBProgressHUD showError:@"密码和账号不能为空" toView:self.view];
    }
    
    [self dismissViewControllerAnimated:YES completion:^{
        
    }];
}

//三方登录
-(void)UMLogin:(UIButton *)btn
{
    //判断由谁登录
    NSString *loginType;
    switch (btn.tag) {
        case 0:
            loginType = UMShareToQQ;
            break;
        case 1:
            loginType = UMShareToWechatSession;
            break;
        case 2:
            loginType = UMShareToSina;
            break;
            
        default:
            break;
    }
        //QQ\微信\新浪登录
        UMSocialSnsPlatform *snsPlatform = [UMSocialSnsPlatformManager getSocialPlatformWithName:loginType];
        
        snsPlatform.loginClickHandler(self,[UMSocialControllerService defaultControllerService],YES,^(UMSocialResponseEntity *response){
            //登录成功
            if (response.responseCode == UMSResponseCodeSuccess) {
                
                UMSocialAccountEntity *snsAccount = [[UMSocialAccountManager socialAccountDictionary] valueForKey:UMShareToQQ];
                
                [MBProgressHUD showError:@"登录成功" toView:self.view];

                RNTLog(@"username is %@, uid is %@, token is %@ url is %@",snsAccount.userName,snsAccount.usid,snsAccount.accessToken,snsAccount.iconURL);
                
            }else if (response.responseCode == UMSResponseCodeNetworkError){
                
                [MBProgressHUD showError:@"网络错误" toView:self.view];
                
            }else if(response.responseCode == UMSResponseCodeCancel){
            
                [MBProgressHUD showMessage:@"您已取消登录" toView:self.view];
                
            }
        });
}

#pragma mark - UITextFieldDelegate


#pragma mark - 布局子控件
-(void)setSubviews
{
    //背景图片
    UIImageView *BGImg = [[UIImageView alloc] initWithFrame:self.view.bounds];
    BGImg.image = [UIImage imageNamed:@"login_bg"];
    BGImg.userInteractionEnabled = YES;
    BGImg.backgroundColor = RNTMainColor;
    [self.view addSubview:BGImg];
    
    //返回键
    UIButton *backBtn = [[UIButton alloc] init];
    [backBtn setBackgroundImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateNormal];
    [backBtn sizeToFit];
    [self.view addSubview:backBtn];
    [backBtn addTarget:self action:@selector(backVC) forControlEvents:UIControlEventTouchUpInside];
    [backBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo (self.view).offset(40);
        make.left.equalTo(25);
    }];
    
    
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
    
    
    //QQ登录
    UIButton *qqBtn = [[UIButton alloc] init];
    [qqBtn setTitle:@"腾讯账号登录" forState:UIControlStateNormal];
    qqBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    qqBtn.layer.cornerRadius = 16;
    qqBtn.clipsToBounds = YES;
    [qqBtn setImage:[UIImage imageNamed:@"login_QQ"] forState:UIControlStateNormal];
    qqBtn.layer.borderColor = [UIColor blackColor].CGColor;
    qqBtn.layer.borderWidth = 1;
    [self.view addSubview:qqBtn];
    [qqBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.view).offset(-kScreenWidth*0.25);
        make.size.mas_equalTo(CGSizeMake(120, 32));
        make.bottom.mas_equalTo(self.view).offset(-44*kScreenWidth/375);
    }];
    qqBtn.tag = 0;
    [qqBtn addTarget:self action:@selector(UMLogin:) forControlEvents:UIControlEventTouchUpInside];
    
    //微信登录
    UIButton *weChatBtn = [[UIButton alloc] init];
    [weChatBtn setTitle:@"微信账号登录" forState:UIControlStateNormal];
    weChatBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    weChatBtn.layer.cornerRadius = 16;
    weChatBtn.clipsToBounds = YES;
    [weChatBtn setImage:[UIImage imageNamed:@"login_Wechat"] forState:UIControlStateNormal];
    weChatBtn.layer.borderColor = [UIColor blackColor].CGColor;
    weChatBtn.layer.borderWidth = 1;
    [self.view addSubview:weChatBtn];
    [weChatBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.view).offset(kScreenWidth*0.25);
        make.size.mas_equalTo(CGSizeMake(120, 32));
        make.bottom.mas_equalTo(self.view).offset(-44*kScreenWidth/375);
    }];
    weChatBtn.tag = 1;
    [weChatBtn addTarget:self action:@selector(UMLogin:) forControlEvents:UIControlEventTouchUpInside];
    
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
        make.size.mas_equalTo(CGSizeMake(300, 40));
        if (iPhone4) {
            make.bottom.mas_equalTo(qqBtn.top).offset(-54);

        }else{
        
            make.bottom.mas_equalTo(qqBtn.top).offset(-84*kScreenWidth/375);
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
        make.size.mas_equalTo(CGSizeMake(300, 40));
        make.bottom.mas_equalTo(registBtn.top).offset(-10);
        make.centerX.mas_equalTo(self.view);
    }];
    
    //点此找回
    UIButton *foundBtn = [[UIButton alloc] init];
    [foundBtn setTitle:@"点此找回" forState:UIControlStateNormal];
    foundBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [foundBtn setTitleColor:RNTColor_16(0x000000) forState:UIControlStateNormal];
    [foundBtn sizeToFit];
    [self.view addSubview:foundBtn];
    [foundBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.view).offset(-40);
        if (iPhone4) {
            make.bottom.mas_equalTo(loginBtn.top).offset(-32);

        }else{
            make.bottom.mas_equalTo(loginBtn.top).offset(-72*kScreenWidth/375);
        
        }
    }];
    [foundBtn addTarget:self action:@selector(intoRetrieveVC) forControlEvents:UIControlEventTouchUpInside];
    
    //忘记密码
    UIButton *forgetBtn = [[UIButton alloc] init];
    [forgetBtn setTitle:@"忘记密码?" forState:UIControlStateNormal];
    forgetBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [forgetBtn setTitleColor:RNTColor_16(0x8d7300) forState:UIControlStateNormal];
    [forgetBtn sizeToFit];
    [self.view addSubview:forgetBtn];
    [forgetBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(foundBtn.left).offset(-5);
        if (iPhone4) {
            make.bottom.mas_equalTo(loginBtn.top).offset(-32);
            
        }else{
            make.bottom.mas_equalTo(loginBtn.top).offset(-72*kScreenWidth/375);
            
        }
    }];
    [forgetBtn addTarget:self action:@selector(intoRetrieveVC) forControlEvents:UIControlEventTouchUpInside];
    
    //密码横线
    UIView *passLine = [[UIView alloc] init];
    passLine.backgroundColor = [UIColor blackColor];
    [self.view addSubview:passLine];
    [passLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(forgetBtn.top).offset(-14);
        make.right.mas_equalTo(self.view).offset(-40);
        make.size.mas_equalTo(CGSizeMake(265*kScreenWidth/375, 0.5));
    }];
    
    //密码输入框
    UITextField *passWordFiled = [[UITextField alloc] init];
    passWordFiled.placeholder = @"输入密码";
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
    acountFiled.placeholder = @"用户名";
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
    
    //账号图标
    UIImageView *acountImg = [[UIImageView alloc] init];
    acountImg.image = [UIImage imageNamed:@"login_acount"];
    [acountImg sizeToFit];
    [self.view addSubview:acountImg];
    [acountImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(acountLine.left).offset(-10);
        make.centerY.mas_equalTo(acountFiled);
    }];

}
@end
