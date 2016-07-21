//
//  RNTSMSVerificationController.m
//  Ace
//
//  Created by 周兵 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTExchangeOrderController.h"
#import "RNTLeftImageRightTextfieldView.h"
#import "RNTLeftButtonRightTextFieldView.h"
#import "RNTAccountManagerButton.h"
#import "RNTMineNetTool.h"
#import "RegexKitLite.h"

@interface RNTExchangeOrderController () <UITextFieldDelegate>
@property (nonatomic, strong) UILabel *RMBLabel;
@property (nonatomic, strong) UILabel *silverLabel;
@property (nonatomic, strong) UILabel *userIdLabel;
@property (nonatomic, strong) UILabel *wechatLabel;
@property (nonatomic, strong) RNTLeftImageRightTextfieldView *phoneView;
@property (nonatomic, strong) RNTLeftButtonRightTextFieldView *verificationCodeView;
@property (nonatomic, strong) RNTAccountManagerButton *nextBtn;
@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@end

@implementation RNTExchangeOrderController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    self.title = @"提现信息";
    [self setupNavBar];
    
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

- (void)setupNavBar
{
    UIButton* backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateNormal];
    [backBtn setBackgroundImage:[UIImage imageNamed:@"navigation_back_BG"] forState:UIControlStateHighlighted];
    backBtn.frame = CGRectMake(0, 0, 30, 30);
    [backBtn addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
}

- (void)setupSubviews
{
    CGFloat orderInfoViewW = kScreenWidth - 20;
    UIView *orderInfoView = [[UIView alloc] init];
    orderInfoView.layer.borderColor = RNTSeparatorColor.CGColor;
    orderInfoView.layer.borderWidth = 0.5;
    
    UIView *dashLine = [[UIView alloc] initWithFrame:CGRectMake(17, 58, orderInfoViewW - 34, 0.5)];
    [self drawDashLine:dashLine lineLength:2 lineSpacing:2 lineColor:RNTColor_16(0xcccccc)];
    
    UILabel *RMBTitleLabel = [[UILabel alloc] init];
    RMBTitleLabel.font = [UIFont systemFontOfSize:15];
    RMBTitleLabel.text = @"提现人民币：";
    RMBTitleLabel.textColor = RNTColor_16(0x9a9a9a);
    
    UILabel *silverTitleLabel = [[UILabel alloc] init];
    silverTitleLabel.font = [UIFont systemFontOfSize:15];
    silverTitleLabel.text = @"提现A豆数：";
    silverTitleLabel.textColor = RNTColor_16(0x9a9a9a);
    
    UILabel *userIdTitleLabel = [[UILabel alloc] init];
    userIdTitleLabel.font = [UIFont systemFontOfSize:15];
    userIdTitleLabel.text = @"提现账号：";
    userIdTitleLabel.textColor = RNTColor_16(0x9a9a9a);
    
    UILabel *wechatTitleLabel = [[UILabel alloc] init];
    wechatTitleLabel.font = [UIFont systemFontOfSize:15];
    wechatTitleLabel.text = @"提微信号：";
    wechatTitleLabel.textColor = RNTColor_16(0x9a9a9a);
    
    [orderInfoView addSubview:dashLine];
    [orderInfoView addSubview:self.RMBLabel];
    [orderInfoView addSubview:self.silverLabel];
    [orderInfoView addSubview:self.userIdLabel];
    [orderInfoView addSubview:self.wechatLabel];
    [orderInfoView addSubview:RMBTitleLabel];
    [orderInfoView addSubview:silverTitleLabel];
    [orderInfoView addSubview:userIdTitleLabel];
    [orderInfoView addSubview:wechatTitleLabel];
    [self.scrollView addSubview:orderInfoView];
    [self.scrollView addSubview:self.phoneView];
    [self.scrollView addSubview:self.verificationCodeView];
    [self.scrollView addSubview:self.nextBtn];
    [self.view addSubview:self.scrollView];
    
    [orderInfoView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(12);
        make.left.equalTo(10);
        make.width.equalTo(orderInfoViewW);
        make.height.equalTo(159);
    }];
    
    [RMBTitleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(dashLine);
        make.top.equalTo(orderInfoView);
        make.bottom.equalTo(dashLine.top);
    }];
    
    [self.RMBLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.centerX.centerY.equalTo(RMBTitleLabel);
    }];
    
    [userIdTitleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(dashLine);
        make.height.equalTo(15);
        make.centerY.equalTo(orderInfoView).offset(dashLine.mj_y * 0.5);
    }];
    
    [self.userIdLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.centerX.centerY.equalTo(userIdTitleLabel);
    }];
    
    [silverTitleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(userIdTitleLabel);
        make.centerX.equalTo(orderInfoView);
        make.bottom.equalTo(userIdTitleLabel.top).offset(-9);
    }];
    
    [self.silverLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.centerX.centerY.equalTo(silverTitleLabel);
    }];
    
    [wechatTitleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(silverTitleLabel);
        make.centerX.equalTo(silverTitleLabel);
        make.top.equalTo(userIdTitleLabel.bottom).offset(9);
    }];
    
    [self.wechatLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.centerX.centerY.equalTo(wechatTitleLabel);
    }];
    
    [self.phoneView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.top.equalTo(orderInfoView.bottom).offset(12);
        make.height.equalTo(44);
    }];
    
    [self.verificationCodeView makeConstraints:^(MASConstraintMaker *make) {
        make.height.left.right.equalTo(self.phoneView);
        make.top.equalTo(self.phoneView.bottom).offset(-0.5);
    }];
    
    [self.nextBtn makeConstraints:^(MASConstraintMaker *make) {
        make.right.left.equalTo(orderInfoView);
        make.top.equalTo(self.verificationCodeView.bottom).offset(52);
        make.height.equalTo(44);
    }];
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

#pragma makr -
- (void)nextBtnClick
{
    WEAK(self);
    NSString *verifyCode = self.verificationCodeView.textField.text;
    
    NSString *pattern = @"[0-9]{6}";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
    if (![pred evaluateWithObject:verifyCode]) {
        [MBProgressHUD showError:@"验证码错误"];
        return;
    }
    
    RNTUserManager *userM = [RNTUserManager sharedManager];
    [RNTMineNetTool withdrawWithUserId:userM.user.userId coinAmount:self.silverCount rmbAmount:self.RMBCount verifyCode:verifyCode success:^(NSDictionary *dict) {
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            [MBProgressHUD showSuccess:@"提交申请成功"];
            [weakself.navigationController popToViewController:[weakself.navigationController.viewControllers objectAtIndex:0] animated:YES];
        } else {
            [MBProgressHUD showError:@"提交申请失败"];
        }
    } failure:^(NSError *error) {
        [MBProgressHUD showError:@"网络连接错误"];
    }];
}

- (void)back
{
    [self.navigationController popToViewController:[self.navigationController.viewControllers objectAtIndex:1] animated:YES];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (string.length > 0 && textField.text.length >= 6) {
        return NO;
    }
    return YES;
}
#pragma mark -
- (UILabel *)RMBLabel
{
    if (_RMBLabel == nil) {
        _RMBLabel = [[UILabel alloc] init];
        _RMBLabel.font = [UIFont systemFontOfSize:24];
        _RMBLabel.textAlignment = NSTextAlignmentRight;
        _RMBLabel.textColor = RNTColor_16(0x383838);
        _RMBLabel.text = [self.RMBCount stringByAppendingString:@" 元"];
    }
    return _RMBLabel;
}

- (UILabel *)silverLabel
{
    if (_silverLabel == nil) {
        _silverLabel = [[UILabel alloc] init];
        _silverLabel.font = [UIFont systemFontOfSize:15];
        _silverLabel.textAlignment = NSTextAlignmentRight;
        _silverLabel.textColor = RNTColor_16(0x383838);
        _silverLabel.text = [self.silverCount stringByAppendingString:@"个"];
    }
    return _silverLabel;
}

- (UILabel *)userIdLabel
{
    if (_userIdLabel == nil) {
        _userIdLabel = [[UILabel alloc] init];
        _userIdLabel.font = [UIFont systemFontOfSize:15];
        _userIdLabel.textAlignment = NSTextAlignmentRight;
        _userIdLabel.textColor = RNTColor_16(0x383838);
        _userIdLabel.text = [RNTUserManager sharedManager].user.userId;
    }
    return _userIdLabel;
}

- (UILabel *)wechatLabel
{
    if (_wechatLabel == nil) {
        _wechatLabel = [[UILabel alloc] init];
        _wechatLabel.font = [UIFont systemFontOfSize:15];
        _wechatLabel.textAlignment = NSTextAlignmentRight;
        _wechatLabel.textColor = RNTColor_16(0x383838);
        _wechatLabel.text = self.weChatId;
    }
    return _wechatLabel;
}

- (RNTLeftImageRightTextfieldView *)phoneView
{
    if (_phoneView == nil) {
        _phoneView = [RNTLeftImageRightTextfieldView initWithImageName:@"other_acount" placeholder:[self.mobileId  stringByReplacingCharactersInRange:NSMakeRange(3, 4) withString:@"****"]];
        _phoneView.textField.keyboardType = UIKeyboardTypeNumberPad;
        _phoneView.textField.enabled = NO;
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
            // 获取验证码。。。
            return weakself.mobileId;
        };
    }
    return _verificationCodeView;
}

- (RNTAccountManagerButton *)nextBtn
{
    if (_nextBtn == nil) {
        _nextBtn = [[RNTAccountManagerButton alloc] init];
        [_nextBtn setTitle:@"提交" forState:UIControlStateNormal];
        [_nextBtn addTarget:self action:@selector(nextBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _nextBtn;
}

- (UIScrollView *)scrollView
{
    if (_scrollView == nil) {
        _scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = YES;
        _scrollView.bounces = YES;
        _scrollView.contentSize = CGSizeMake(kScreenWidth, 450);
        //点击手势
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(endEdit)];
        
        [_scrollView addGestureRecognizer:tap];
    }
    return _scrollView;
}

#pragma mark - 画虚线
/**
 ** lineView:   需要绘制成虚线的view
 ** lineLength: 虚线的宽度
 ** lineSpacing: 虚线的间距
 ** lineColor:   虚线的颜色
 **/
- (void)drawDashLine:(UIView *)lineView lineLength:(int)lineLength lineSpacing:(int)lineSpacing lineColor:(UIColor *)lineColor
{
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    [shapeLayer setBounds:lineView.bounds];
    [shapeLayer setPosition:CGPointMake(CGRectGetWidth(lineView.frame) / 2, CGRectGetHeight(lineView.frame))];
    [shapeLayer setFillColor:[UIColor clearColor].CGColor];
    //  设置虚线颜色
    [shapeLayer setStrokeColor:lineColor.CGColor];
    //  设置虚线宽度
    [shapeLayer setLineWidth:CGRectGetHeight(lineView.frame)];
    [shapeLayer setLineJoin:kCALineJoinRound];
    //  设置线宽，线间距
    [shapeLayer setLineDashPattern:[NSArray arrayWithObjects:[NSNumber numberWithInt:lineLength], [NSNumber numberWithInt:lineSpacing], nil]];
    //  设置路径
    CGMutablePathRef path = CGPathCreateMutable();
    CGPathMoveToPoint(path, NULL, 0, 0);
    CGPathAddLineToPoint(path, NULL, CGRectGetWidth(lineView.frame), 0);
    [shapeLayer setPath:path];
    CGPathRelease(path);
    //  把绘制好的虚线添加上来
    [lineView.layer addSublayer:shapeLayer];
}
@end
