//
//  RNTRechargeController.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "UIImageView+WebCaChe.h"

#define IconWidth 70
#define TextFont [UIFont systemFontOfSize:18]
#define NickNameColor RNTColor_16(0x554900)
#define GoldCoinColor1 RNTColor_16(0x000000)
#define GoldCoinColor2 RNTColor_16(0xfe5700)

@interface RNTRechargeHeaderView : UIView
@property (nonatomic, strong) UIImageView *iconView;
@property (nonatomic, strong) UILabel *goldCoinLabel;
@property (nonatomic, strong) UILabel *nickNameLabel;
@end

@implementation RNTRechargeHeaderView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = RNTMainColor;
        [self setupSubviews];
    }
    return self;
}

- (void)setupSubviews
{
    [self addSubview:self.iconView];
    [self addSubview:self.goldCoinLabel];
    [self addSubview:self.nickNameLabel];
    
    [self.iconView makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(IconWidth, IconWidth));
        make.left.equalTo(20);
        make.bottom.equalTo(self.bottom).offset(-15);
    }];
    
    [self.nickNameLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.iconView.right).offset(15);
        make.right.equalTo(self);
        make.bottom.equalTo(self.iconView.centerY).offset(-5);
        make.height.equalTo(18);
    }];
    
    [self.goldCoinLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.nickNameLabel);
        make.top.equalTo(self.iconView.centerY).offset(5);
        make.height.right.equalTo(self.nickNameLabel);
    }];
}

- (void)setGoldAttText:(NSString *)blance
{
    NSMutableAttributedString *att = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"金币余额:%@", blance]];
    [att addAttribute:NSForegroundColorAttributeName value:GoldCoinColor1 range:NSMakeRange(0, 5)];
    [att addAttribute:NSForegroundColorAttributeName value:GoldCoinColor2 range:NSMakeRange(5, blance.length)];
    self.goldCoinLabel.attributedText = att;
}

#pragma mark -
- (UIImageView *)iconView
{
    if (_iconView == nil) {
        _iconView = [[UIImageView alloc] init];
        _iconView.layer.cornerRadius = IconWidth * 0.5;
        _iconView.layer.masksToBounds = YES;
        _iconView.layer.borderWidth = 3.0;
        _iconView.layer.borderColor = [UIColor whiteColor].CGColor;
        [_iconView sd_setImageWithURL:[NSURL URLWithString:[RNTUserManager sharedManager].user.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    }
    return _iconView;
}

- (UILabel *)nickNameLabel
{
    if (_nickNameLabel == nil) {
        _nickNameLabel = [[UILabel alloc] init];
        _nickNameLabel.text = [RNTUserManager sharedManager].user.nickName;
        _nickNameLabel.font = TextFont;
        _nickNameLabel.textColor = NickNameColor;
    }
    return _nickNameLabel;
}

- (UILabel *)goldCoinLabel
{
    if (_goldCoinLabel == nil) {
        _goldCoinLabel = [[UILabel alloc] init];
        NSString *blance = [RNTUserManager sharedManager].blance;
        if (blance.length <= 0) {
            blance = @"0";
        }
        [self setGoldAttText:blance];
        _goldCoinLabel.font = TextFont;
    }
    return _goldCoinLabel;
}
@end

#import "RNTRechargeController.h"
#import <StoreKit/StoreKit.h>
#import "RNTRechargeCell.h"
#import "RNTMineNetTool.h"
#import "RNTWebViewController.h"
#import "YCXMenu.h"
#import "RNTInsetsLabel.h"
#import "RNTDatabaseTool.h"

#define ITMS_SANDBOX_VERIFY_RECEIPT_URL @"https://sandbox.itunes.apple.com/verifyReceipt"
#define ITMS_PRODUCT_VERIFY_RECEIPT_URL @"https://buy.itunes.apple.com/verifyReceipt"

#define ProductArray @[@"Ace.Product.01", @"Ace.Product.02", @"Ace.Product.03", @"Ace.Product.04", @"Ace.Product.05", @"Ace.Product.06"]

#define HeaderHigh 150 - 64
#define SectionHeaderHeight 10

@interface RNTRechargeController ()<UITableViewDataSource, UITableViewDelegate, SKProductsRequestDelegate,SKPaymentTransactionObserver, MBProgressHUDDelegate>

@property (nonatomic, copy)   NSArray *productArray;
@property (nonatomic, strong) RNTRechargeHeaderView *headerView;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray *items;//右上角选项卡
@end

@implementation RNTRechargeController
{
    SKProductsRequest *_productsRequest;
    MBProgressHUD *HUD;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"充值";
    WEAK(self);
    [[RNTUserManager sharedManager] refreshBlanceSuccess:^(NSString *blance) {
        [weakself.headerView setGoldAttText:blance];
    }];
    
    [self setupSubviews];
    
    [[SKPaymentQueue defaultQueue] addTransactionObserver:self];

    _productsRequest = [[SKProductsRequest alloc]initWithProductIdentifiers:[NSSet setWithArray:ProductArray]];
    _productsRequest.delegate = self;
    [_productsRequest start];
    
    [self showHUD];
}

- (void)setupSubviews
{
    UIButton *menuBtn = [[UIButton alloc] init];
    [menuBtn setBackgroundImage:[UIImage imageNamed:@"Recharge_Menu"] forState:UIControlStateNormal];
    [menuBtn sizeToFit];
    [menuBtn addTarget:self action:@selector(menuBtnClick) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:menuBtn];
    
    [self.view addSubview:self.tableView];
    [self.view addSubview:self.headerView];
    
    [self.headerView makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.equalTo(self.view);
        make.height.equalTo(HeaderHigh);
    }];
    
    [self.tableView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.view);
        make.top.equalTo(self.headerView.bottom);
    }];
    
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    self.tableView.backgroundColor = RNTBackgroundColor;
}

- (void)menuBtnClick
{
    WEAK(self);
    
    [YCXMenu setHasShadow:YES];
    [YCXMenu setBackgrounColorEffect:YCXMenuBackgrounColorEffectSolid];
    [YCXMenu setTintColor:[UIColor whiteColor]];
    [YCXMenu setSelectedColor:RNTColor_16(0xe6e6eb)];
    [YCXMenu showMenuInView:self.navigationController.view fromRect:CGRectMake(kScreenWidth - 28, 50, 0, 0) menuItems:self.items selected:^(NSInteger index, YCXMenuItem *item) {
        RNTUserManager *userM = [RNTUserManager sharedManager];
        NSString *url = [RNTWebURLHeader stringByAppendingFormat:@"%@.jsp?userId=%@", item.userInfo[@"webUrlKey"], userM.user.userId];
        RNTWebViewController *webVC = [RNTWebViewController webViewControllerWithTitle:item.title url:url];
        [weakself.navigationController pushViewController:webVC animated:YES];
    }];
}

-(void)dealloc {
    [[SKPaymentQueue defaultQueue] removeTransactionObserver:self];
    [_productsRequest cancel];
    _productsRequest.delegate = nil;
}

#pragma mark - 私有方法
//选择内购商品
- (void)selectAction:(SKProduct *)product {
    SKPayment *payment = [SKPayment paymentWithProduct:product];
    [[SKPaymentQueue defaultQueue] addPayment:payment];
}

//验证交易凭据
- (void)verification:(SKPaymentTransaction *)paymentTransaction {
    [MBProgressHUD showMessage:@"请稍等..."];
    NSString *productIdentifier = paymentTransaction.payment.productIdentifier;
    // 将交易从交易队列中删除
    [[SKPaymentQueue defaultQueue] finishTransaction:paymentTransaction];
    if (productIdentifier.length > 0) {
        NSString *version = [[[NSBundle mainBundle] infoDictionary]objectForKey:@"CFBundleShortVersionString"];
        // 验证凭据，获取到苹果返回的交易凭据
        // appStoreReceiptURL iOS7.0增加的，购买交易完成后，会将凭据存放在该地址
        NSURL *receiptURL = [[NSBundle mainBundle] appStoreReceiptURL];
        // 从沙盒中获取到购买凭据
        NSData *receiptData = [NSData dataWithContentsOfURL:receiptURL];
        
        NSString *receiptStr = [receiptData base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
        
        WEAK(self);
        RNTUser *user = [RNTUserManager sharedManager].user;
        
        //存储购买凭据到数据库
        NSTimeInterval time=[[NSDate date] timeIntervalSince1970]*1000;
        NSString *timestamp = [NSString stringWithFormat:@"%f", time];
        NSDictionary *receiptDict = @{@"receiptData" : receiptData, @"timestamp" : timestamp, @"userID" : user.userId, @"version": version};
        [RNTDatabaseTool saveReceiptDict:receiptDict];
        
        [RNTMineNetTool verificationWithReceipt:receiptStr userId:user.userId version:version success:^(NSDictionary *dict) {
            [MBProgressHUD hideHUD];
            //删除凭据
            [RNTDatabaseTool deleteReceiptWithReceipt:receiptDict];
            
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                [MBProgressHUD showSuccess:@"充值成功"];
                //此处balance为long类型
                [weakself.headerView setGoldAttText:[dict[@"balance"] stringValue]];
            } else {
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            [MBProgressHUD hideHUD];
            [MBProgressHUD showError:@"网络错误"];
            RNTLog(@"%@", error.localizedDescription);
        }];
    }
}

- (void)showFailedError:(SKPaymentTransaction *)paymentTransaction {
    if (paymentTransaction.error.code != SKErrorPaymentCancelled) {
        [MBProgressHUD showError:@"购买失败！"];
    }else {
        [MBProgressHUD showError:@"您已取消交易！"];
    }
}

- (void)showHUD {
    HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [[UIApplication sharedApplication].keyWindow addSubview:HUD];
    [self.view addSubview:HUD];
    HUD.delegate = self;
    HUD.label.text = @"Loading";
    [HUD showAnimated:YES];
}

#pragma mark - UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.productArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RNTRechargeCell *cell = [RNTRechargeCell cellWithTableView:tableView];
    SKProduct *product = self.productArray[indexPath.row];
    cell.product = product;
    
    cell.topSeparatorLine.hidden = indexPath.row;
    
    WEAK(self);
    cell.priceBtnClickBlock = ^{
        [weakself selectAction:product];
    };
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 64;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return SectionHeaderHeight;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *view = [[UIView alloc] init];
    view.backgroundColor = RNTBackgroundColor;
    return view;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 50;
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    RNTInsetsLabel *label = [[RNTInsetsLabel alloc] initWithFrame:CGRectMake(0, 0, 0, 50) andInsets:UIEdgeInsetsMake(0, 5, 0, 5)];
    label.backgroundColor = RNTBackgroundColor;
    label.textColor = RNTColor_16(0x808080);
    label.font = [UIFont systemFontOfSize:12];
    label.text = @"为保证充值顺利，请在网络良好的情况下，并且耐心等待返回结果，如遇充值问题，请联系客服QQ:2583801618";
    label.numberOfLines = 0;
    return label;
}

#pragma mark - SKProductsRequestDelegate
- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response {
    
    self.productArray = response.products;
    
    [self.tableView reloadData];
    [HUD hideAnimated:YES];
}

#pragma mark - SKPaymentTransactionObserver
- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions {
    for (SKPaymentTransaction *paymentTransaction in transactions) {
        switch (paymentTransaction.transactionState) {
            case SKPaymentTransactionStatePurchased: //交易完成
            {
                [self verification:paymentTransaction];
            }
                break;
            case SKPaymentTransactionStateFailed:    //交易失败
            {
                [[SKPaymentQueue defaultQueue] finishTransaction:paymentTransaction];
                [self showFailedError:paymentTransaction];
            }
                break;
            case SKPaymentTransactionStateRestored:  //已经购买过该商品
            {
                [[SKPaymentQueue defaultQueue] finishTransaction:paymentTransaction];
            }
                break;
            case SKPaymentTransactionStatePurchasing://商品添加进列表
                
                break;
                
            default:
                break;
        }
    }
}

#pragma mark - MBProgressHUDDelegate
- (void)hudWasHidden:(MBProgressHUD *)hud {
    [HUD removeFromSuperview];
    HUD = nil;
}

#pragma mark - 
- (RNTRechargeHeaderView *)headerView
{
    if (_headerView == nil) {
        _headerView = [[RNTRechargeHeaderView alloc] init];
    }
    return _headerView;
}

- (UITableView *)tableView
{
    if (_tableView == nil) {
        _tableView = [[UITableView alloc] init];
    }
    return _tableView;
}

//去掉UItableview headerview黏性
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if (scrollView == self.tableView)
    {
        if (scrollView.contentOffset.y <= SectionHeaderHeight && scrollView.contentOffset.y >= 0) {
            scrollView.contentInset = UIEdgeInsetsMake(-scrollView.contentOffset.y, 0, 0, 0);
        } else if (scrollView.contentOffset.y >= SectionHeaderHeight) {
            scrollView.contentInset = UIEdgeInsetsMake(-SectionHeaderHeight, 0, 0, 0);
        }
    }
}

- (NSArray *)items
{
    if (_items == nil) {
        
        YCXMenuItem *chargeRecordItem = [YCXMenuItem menuItem:@"充值记录" image:nil tag:101 userInfo:@{@"webUrlKey":@"recharge"}];
        chargeRecordItem.foreColor = [UIColor blackColor];
        chargeRecordItem.alignment = NSTextAlignmentCenter;
        chargeRecordItem.titleFont = [UIFont systemFontOfSize:15.0f];
        
        YCXMenuItem *consumeRecordItem = [YCXMenuItem menuItem:@"消费记录" image:nil tag:102 userInfo:@{@"webUrlKey":@"consume"}];
        consumeRecordItem.foreColor = [UIColor blackColor];
        consumeRecordItem.alignment = NSTextAlignmentCenter;
        consumeRecordItem.titleFont = [UIFont systemFontOfSize:15.0f];
        
        _items = @[chargeRecordItem, consumeRecordItem];
    }
    return _items;
}
@end
