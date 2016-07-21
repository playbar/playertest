//
//  RNTSilverExchangeController.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSilverExchangeController.h"
#import "RNTAccountManagerButton.h"
#import "RNTWebViewController.h"
#import "RNTBindWechatController.h"
#import "RNTMineNetTool.h"
#import "RNTExchangeOrderController.h"
#import "RegexKitLite.h"

#define TextColor RNTColor_16(0x4d3104)
#define TextFont [UIFont systemFontOfSize:18]
#define BoldTextFont [UIFont boldSystemFontOfSize:30]

@interface RNTSilverExchangeController () <UITextFieldDelegate>
@property (nonatomic, strong) UIView *headerView;
@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) RNTAccountManagerButton *commitBtn;
@property (nonatomic, strong) UIButton *promptDocBtn;   //提示文档按钮
@property (nonatomic, strong) UILabel *totalSilverCoinLabel;
@property (nonatomic, strong) UILabel *totalRMBLabel;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UITextField *exchangeRMBField;
@property (nonatomic, strong) UILabel *exchangeSilverCoinLabel;
@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动

@property (nonatomic, copy) NSString *balance;//总A豆数
@property (nonatomic, copy) NSString *rate;//汇率
@end

@implementation RNTSilverExchangeController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"A豆兑换";
    self.view.backgroundColor = RNTBackgroundColor;
    
    [self setupSubviews];
    
    [self setupNavbar];
    
    [self getSilverBalance];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textFieldEditChanged:) name:UITextFieldTextDidChangeNotification object:nil];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.exchangeSilverCoinLabel.text = nil;
    self.exchangeRMBField.text = nil;
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

-(void)textFieldEditChanged:(NSNotification *)obj
{
    UITextField *textField = (UITextField *)obj.object;
    if (textField.text.length) {
        self.exchangeSilverCoinLabel.text = [NSString stringWithFormat:@"%d", (int)(textField.text.intValue * self.rate.intValue)];
    } else {
        self.exchangeSilverCoinLabel.text = nil;
    }
}

- (void)getSilverBalance
{
    WEAK(self);
    RNTUserManager *userM = [RNTUserManager sharedManager];
    [RNTMineNetTool getSilverWithUserId:userM.user.userId success:^(NSDictionary *dict) {
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            weakself.balance = dict[@"balance"];
            weakself.rate = dict[@"rate"];
            [weakself setDataWithBalance:dict[@"balance"] rate:dict[@"rate"]];
        }
    } failure:^(NSError *error) {
        
    }];
}

- (void)setDataWithBalance:(NSString *)balance rate:(NSString *)rate
{
    NSMutableAttributedString *silverAtt = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"A豆：%@ 个", balance]];
    [silverAtt addAttribute:NSForegroundColorAttributeName value:TextColor range:NSMakeRange(0, silverAtt.length)];
    [silverAtt addAttribute:NSFontAttributeName value:TextFont range:NSMakeRange(0, 3)];
    [silverAtt addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:24] range:NSMakeRange(3, silverAtt.length - 4)];
    [silverAtt addAttribute:NSFontAttributeName value:TextFont range:NSMakeRange(silverAtt.length - 1, 1)];
    self.totalSilverCoinLabel.attributedText = silverAtt;
    
    NSMutableAttributedString *RMBAtt = [[NSMutableAttributedString alloc] initWithString:[@"￥" stringByAppendingFormat:@"%d", (int)balance.intValue / rate.intValue]];
    [RMBAtt addAttribute:NSForegroundColorAttributeName value:TextColor range:NSMakeRange(0, RMBAtt.length)];
    [RMBAtt addAttribute:NSFontAttributeName value:TextFont range:NSMakeRange(0, 1)];
    [RMBAtt addAttribute:NSFontAttributeName value:BoldTextFont range:NSMakeRange(1, RMBAtt.length - 1)];
    self.totalRMBLabel.attributedText = RMBAtt;
}

- (void)setupNavbar
{
    UIButton *exchangeRecordBtn = [[UIButton alloc] init];
    [exchangeRecordBtn setTitle:@"提现记录" forState:UIControlStateNormal];
    [exchangeRecordBtn setTitleColor:RNTColor_16(0x70622f) forState:UIControlStateNormal];
    [exchangeRecordBtn setTitleColor:RNTAlphaColor_16(0x70622f, 0.4) forState:UIControlStateHighlighted];
    exchangeRecordBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    [exchangeRecordBtn sizeToFit];
    [exchangeRecordBtn addTarget:self action:@selector(exchangeRecordClick:) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:exchangeRecordBtn];
}

- (void)setupSubviews
{
    UILabel *exchangeRMBLabel = [[UILabel alloc] init];
    exchangeRMBLabel.text = @"提现金额: ";
    exchangeRMBLabel.font = [UIFont systemFontOfSize:16];
    exchangeRMBLabel.textColor = RNTColor_16(0x666666);
    
    UILabel *exchangeSilverLabel = [[UILabel alloc] init];
    exchangeSilverLabel.text = @"需要A豆: ";
    exchangeSilverLabel.font = [UIFont systemFontOfSize:16];
    exchangeSilverLabel.textColor = RNTColor_16(0x666666);
    
    [self.scrollView addSubview:self.headerView];
    [self.scrollView addSubview:self.imageView];
    [self.scrollView addSubview:self.totalSilverCoinLabel];
    [self.scrollView addSubview:self.totalRMBLabel];
    [self.scrollView addSubview:self.titleLabel];
    [self.scrollView addSubview:self.commitBtn];
    [self.scrollView addSubview:self.promptDocBtn];
    [self.scrollView addSubview:exchangeRMBLabel];
    [self.scrollView addSubview:exchangeSilverLabel];
    [self.scrollView addSubview:self.exchangeRMBField];
    [self.scrollView addSubview:self.exchangeSilverCoinLabel];
    [self.view addSubview:self.scrollView];
    
    [self.headerView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(-200);//处理scrollView滚动时上部显示黄色
        make.left.right.equalTo(self.view);
        make.height.equalTo(408);
    }];
    
    [self.imageView makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.view);
        make.bottom.equalTo(self.headerView);
    }];
    
    [self.totalSilverCoinLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.bottom.equalTo(self.imageView.top).offset(-33.5);
        make.height.equalTo(24);
    }];
    
    [self.totalRMBLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.bottom.equalTo(self.totalSilverCoinLabel.top).offset(-29);
        make.height.equalTo(30);
    }];
    
    [self.titleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.bottom.equalTo(self.totalRMBLabel.top).offset(-10);
        make.height.equalTo(18);
    }];
    
    NSDictionary *att = @{NSFontAttributeName : exchangeRMBLabel.font};
    CGSize exchangeRMBLabelS = [exchangeRMBLabel.text sizeWithAttributes:att];
    [exchangeRMBLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.top.equalTo(233);
        make.width.equalTo(exchangeRMBLabelS);
    }];
    
    [self.exchangeRMBField makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(exchangeRMBLabel.right).offset(10);
        make.centerY.equalTo(exchangeRMBLabel);
        make.right.equalTo(self.view).offset(-10);
        make.height.equalTo(44);
    }];
    
    NSDictionary *attr = @{NSFontAttributeName : exchangeSilverLabel.font};
    CGSize exchangeSilverLabelS = [exchangeSilverLabel.text sizeWithAttributes:attr];
    [exchangeSilverLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(exchangeSilverLabelS);
        make.left.equalTo(exchangeRMBLabel);
        make.top.equalTo(self.exchangeRMBField.bottom).offset(15);
    }];
    
    [self.exchangeSilverCoinLabel makeConstraints:^(MASConstraintMaker *make) {
        make.right.left.equalTo(self.exchangeRMBField);
        make.top.height.equalTo(exchangeSilverLabel);
    }];
    
    [self.commitBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(exchangeRMBLabel);
        make.right.equalTo(self.view).offset(-10);
        make.top.equalTo(exchangeSilverLabel.bottom).offset(52);
        make.height.equalTo(44);
    }];
    
    [self.promptDocBtn makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.commitBtn);
        make.top.equalTo(self.commitBtn.bottom).offset(24);
    }];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 点击事件
- (void)exchangeRecordClick:(UIButton *)btn
{
    NSString *url = [RNTWebURLHeader stringByAppendingFormat:@"cash_record.jsp?userId=%@", [RNTUserManager sharedManager].user.userId];
    RNTWebViewController *webVC = [RNTWebViewController webViewControllerWithTitle:@"提现记录" url:url];
    [self.navigationController pushViewController:webVC animated:YES];
}

- (void)promptDocBtnClick:(UIButton *)btn
{
    NSString *url = [RNTWebURLHeader stringByAppendingString:@"cash_doc.jsp"];
    RNTWebViewController *webVC = [RNTWebViewController webViewControllerWithTitle:@"提现文档" url:url];
    [self.navigationController pushViewController:webVC animated:YES];
}

- (void)commitBtnClick:(UIButton *)btn
{
    NSString *exchangeRMBNum = self.exchangeRMBField.text;
    if (exchangeRMBNum.integerValue % 100) {
        [MBProgressHUD showError:@"请输入100的倍数"];
        return;
    }
    
    if (exchangeRMBNum.length <= 0) {
        [MBProgressHUD showError:@"请输入提现金额"];
        return;
    }
    
    if (exchangeRMBNum.length > 0) {
        WEAK(self);
        RNTUserManager *userM = [RNTUserManager sharedManager];
        [RNTMineNetTool getWithdrawBindInfoWithUserId:userM.user.userId success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                RNTExchangeOrderController *EOVC = [[RNTExchangeOrderController alloc] init];
                EOVC.mobileId = dict[@"bind"][@"mobileId"];
                EOVC.weChatId = dict[@"bind"][@"weChatId"];
                EOVC.RMBCount = weakself.exchangeRMBField.text;
                EOVC.silverCount = weakself.exchangeSilverCoinLabel.text;
                [weakself.navigationController pushViewController:EOVC animated:YES];
            } else if ([dict[@"exeStatus"] isEqualToString:@"0"]) {
                if ([dict[@"errCode"] isEqualToString:@"104"]) {
                    RNTBindWechatController *BWC = [[RNTBindWechatController alloc] init];
                    BWC.RMBCount = weakself.exchangeRMBField.text;
                    BWC.silverCount = weakself.exchangeSilverCoinLabel.text;
                    [weakself.navigationController pushViewController:BWC animated:YES];
                } else {
                    [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
                }
            }
        } failure:^(NSError *error) {
            
        }];
    }
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (string.length > 0) {
        //第一个不让输0
        if ([string isEqualToString:@"0"] && textField.text.length == 0) {
            return NO;
        }

        if ([textField.text stringByAppendingString:string].integerValue > self.balance.integerValue / self.rate.intValue) {
            return NO;
        }
        
        NSString *pattern = @"\\d";
        NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
        return [pred evaluateWithObject:string];
    }
    return YES;
}

#pragma mark - 
- (UIView *)headerView
{
    if (_headerView == nil) {
        _headerView = [[UIView alloc] init];
        _headerView.backgroundColor = RNTMainColor;
    }
    return _headerView;
}

- (RNTAccountManagerButton *)commitBtn
{
    if (_commitBtn == nil) {
        _commitBtn = [[RNTAccountManagerButton alloc] init];
        [_commitBtn setTitle:@"微信提现" forState:UIControlStateNormal];
        [_commitBtn addTarget:self action:@selector(commitBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _commitBtn;
}

- (UIImageView *)imageView
{
    if (_imageView == nil) {
        _imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"SilverExchange"]];
    }
    return _imageView;
}

- (UIButton *)promptDocBtn
{
    if (_promptDocBtn == nil) {
        _promptDocBtn = [[UIButton alloc] init];
        [_promptDocBtn sizeToFit];
        [_promptDocBtn setTitle:@"详细阅读提现文档>>" forState:UIControlStateNormal];
        [_promptDocBtn setTitleColor:RNTColor_16(0x7f7f7f) forState:UIControlStateNormal];
        _promptDocBtn.titleLabel.font = [UIFont systemFontOfSize:14];
        [_promptDocBtn addTarget:self action:@selector(promptDocBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _promptDocBtn;
}

- (UILabel *)totalSilverCoinLabel
{
    if (_totalSilverCoinLabel == nil) {
        _totalSilverCoinLabel = [[UILabel alloc] init];
        _totalSilverCoinLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _totalSilverCoinLabel;
}

- (UILabel *)totalRMBLabel
{
    if (_totalRMBLabel == nil) {
        _totalRMBLabel = [[UILabel alloc] init];
        _totalRMBLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _totalRMBLabel;
}

- (UILabel *)titleLabel
{
    if (_titleLabel == nil) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.text = @"可提现 (元)";
        _titleLabel.font = TextFont;
        _titleLabel.textColor = TextColor;
    }
    return _titleLabel;
}

- (UITextField *)exchangeRMBField
{
    if (_exchangeRMBField == nil) {
        _exchangeRMBField = [[UITextField alloc] init];
        _exchangeRMBField.layer.borderColor = RNTColor_16(0xcccccc).CGColor;
        _exchangeRMBField.layer.borderWidth = 0.5;
        _exchangeRMBField.placeholder = @"请输入100的倍数";
        _exchangeRMBField.textColor = RNTColor_16(0xcccccc);
        _exchangeRMBField.keyboardType = UIKeyboardTypeNumberPad;
        _exchangeRMBField.delegate = self;
        [_exchangeRMBField setValue:RNTColor_16(0xcccccc) forKeyPath:@"_placeholderLabel.textColor"];
        _exchangeRMBField.leftViewMode = UITextFieldViewModeAlways;
        _exchangeRMBField.leftView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 12, 44)];
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 30, 44)];
        label.text = @"元";
        label.textColor = RNTColor_16(0x666666);
        label.font = [UIFont systemFontOfSize:16];
        _exchangeRMBField.rightView = label;
        _exchangeRMBField.rightViewMode = UITextFieldViewModeAlways;
    }
    return _exchangeRMBField;
}

- (UILabel *)exchangeSilverCoinLabel
{
    if (_exchangeSilverCoinLabel == nil) {
        _exchangeSilverCoinLabel = [[UILabel alloc] init];
        _exchangeSilverCoinLabel.font = [UIFont systemFontOfSize:16];
        _exchangeSilverCoinLabel.textColor = RNTColor_16(0xcccccc);
    }
    return _exchangeSilverCoinLabel;
}

- (UIScrollView *)scrollView
{
    if (_scrollView == nil) {
        _scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = YES;
        _scrollView.bounces = YES;
        _scrollView.contentSize = CGSizeMake(kScreenWidth, 510);
        //点击手势
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(endEdit)];
        
        [_scrollView addGestureRecognizer:tap];
    }
    return _scrollView;
}
@end
