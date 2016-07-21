//
//  RNTMineController.m
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTMineController.h"
#import "RNTSilverExchangeController.h"
#import "RNTSettingController.h"
#import "RNTMineHeaderView.h"
#import "RNTEditController.h"
#import "RNTFansController.h"
#import "RNTFocusController.h"
//#import "RNTHistoryController.h"
#import "AppDelegate.h"
#import "RNTLoginViewController.h"
#import "RNTRegisterViewController.h"
#import "RNTNavigationController.h"
#import "RNTMineNetTool.h"
#import "MJExtension.h"
#import "RNTRechargeController.h"

#define HeaderHigh 267

@interface RNTMineController ()
@property (nonatomic, strong) RNTMineHeaderView *headerView;
@end

@implementation RNTMineController

- (void)viewDidLoad {
    [super viewDidLoad];
    //禁止侧滑返回
    self.navigationController.interactivePopGestureRecognizer.enabled = NO;
    self.tableView.contentInset = UIEdgeInsetsMake(HeaderHigh, 0, 0, 0);
    
    self.tableView.showsVerticalScrollIndicator = NO;
    
    [self.tableView addSubview:self.headerView];
    
    RNTUserManager *userM = [RNTUserManager sharedManager];

    if (userM.isLogged) {
        [self handleLoggedEvent];
    } else {
        [self handleUnloggedEvent];
    }
    
    //如果没登陆  则用kvo 观察用户是否已经登陆 便于刷新界面
    [userM addObserver:self forKeyPath:@"logged" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (userM.isLogged) {
        
        WEAK(self);
        //刷新金币余额
        [[RNTUserManager sharedManager] refreshBlanceSuccess:^(NSString *blance) {
            RNTSettingRightLabelAndArrowItem *recharge = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_Recharge" title:@"我的金币" rightLabelText:blance destClass:[RNTRechargeController class]];
            RNTSettingGroup *group1 = weakself.options[0];
            [group1.items replaceObjectAtIndex:0 withObject:recharge];
            
            NSIndexPath *indexPath=[NSIndexPath indexPathForRow:0 inSection:0];
            [weakself.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath,nil] withRowAnimation:UITableViewRowAnimationNone];
        }];
        
        if ([RNTUserManager canShowSilverExchange]) {
            //刷新收益
            RNTUserManager *userM = [RNTUserManager sharedManager];
            [RNTMineNetTool getSilverWithUserId:userM.user.userId success:^(NSDictionary *dict) {
                if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                    NSString *silverBalance = dict[@"balance"];
                    RNTSettingRightLabelAndArrowItem *exchange = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_exchange" title:@"我的收益" rightLabelText:silverBalance destClass:[RNTSilverExchangeController class]];
                    RNTSettingGroup *group1 = weakself.options[0];
                    [group1.items replaceObjectAtIndex:1 withObject:exchange];
                    
                    NSIndexPath *indexPath=[NSIndexPath indexPathForRow:1 inSection:0];
                    [weakself.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath,nil] withRowAnimation:UITableViewRowAnimationNone];
                }
            } failure:^(NSError *error) {
                
            }];
        }
        
        //刷新个人资料
        [RNTMineNetTool updatePersonalInfoWithUserId:userM.user.userId success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                RNTUser *user =[RNTUser mj_objectWithKeyValues:dict[@"user"]];
                userM.user = user;
                self.headerView.user = user;
            } else {
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            RNTLog(@"%@", error.localizedDescription);
        }];
    }
    
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    [self.navigationController setNavigationBarHidden:YES animated:delegate.isNavBarAnimated];
    delegate.navBarAnimated = YES;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;;
    if (delegate.isNavBarAnimated) {
        [self.navigationController setNavigationBarHidden:NO animated:YES];
    }
}

- (void)handleLoggedEvent
{
    [self.options removeAllObjects];
    
    self.headerView.isLogged = YES;
    
    RNTSettingGroup *group1 = [[RNTSettingGroup alloc] init];
    group1.headerTitle = @" ";
    
    RNTSettingRightLabelAndArrowItem *recharge = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_Recharge" title:@"我的金币" rightLabelText:@"0" destClass:[RNTRechargeController class]];
    
    [group1.items addObject:recharge];
    
    WEAK(self);
    //刷新金币余额
    [[RNTUserManager sharedManager] refreshBlanceSuccess:^(NSString *blance) {
        RNTSettingRightLabelAndArrowItem *recharge = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_Recharge" title:@"我的金币" rightLabelText:blance destClass:[RNTRechargeController class]];
        RNTSettingGroup *group1 = weakself.options[0];
        [group1.items replaceObjectAtIndex:0 withObject:recharge];
        
        NSIndexPath *indexPath=[NSIndexPath indexPathForRow:0 inSection:0];
        [weakself.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath,nil] withRowAnimation:UITableViewRowAnimationNone];
    }];
    
    RNTSettingRightLabelAndArrowItem *exchange = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_exchange" title:@"我的收益" rightLabelText:@"0" destClass:[RNTSilverExchangeController class]];
    
    if ([RNTUserManager canShowSilverExchange]) {
        [group1.items addObject:exchange];
        //刷新收益
        RNTUserManager *userM = [RNTUserManager sharedManager];
        [RNTMineNetTool getSilverWithUserId:userM.user.userId success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                NSString *silverBalance = dict[@"balance"];
                RNTSettingRightLabelAndArrowItem *exchange = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_exchange" title:@"我的收益" rightLabelText:silverBalance destClass:[RNTSilverExchangeController class]];
                RNTSettingGroup *group1 = weakself.options[0];
                [group1.items replaceObjectAtIndex:1 withObject:exchange];
                
                NSIndexPath *indexPath=[NSIndexPath indexPathForRow:1 inSection:0];
                [weakself.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObjects:indexPath,nil] withRowAnimation:UITableViewRowAnimationNone];
            }
        } failure:^(NSError *error) {
            
        }];
    }
    
    RNTSettingGroup *group2 = [[RNTSettingGroup alloc] init];
    group2.headerTitle = @" ";
    
    RNTSettingArrowItem *setting = [[RNTSettingArrowItem alloc] initWithIcon:@"Mine_setting" title:@"设置" destClass:[RNTSettingController class]];
    
    [group2.items addObject:setting];
    
    [self.options addObject:group1];
    [self.options addObject:group2];
    
    [self.tableView reloadData];
}

- (void)handleUnloggedEvent
{
    [self.options removeAllObjects];
    
    self.headerView.isLogged = NO;
    
    WEAK(self);
    RNTSettingGroup *group1 = [[RNTSettingGroup alloc] init];
    group1.headerTitle = @" ";
    
    RNTSettingRightLabelAndArrowItem *recharge = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_Recharge" title:@"我的金币" rightLabelText:@"0" destClass:nil];
    recharge.action = ^{
        [weakself presentLoginController];
    };
    
    [group1.items addObject:recharge];
    
    RNTSettingRightLabelAndArrowItem *exchange = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:@"Mine_exchange" title:@"我的收益" rightLabelText:@"0" destClass:nil];
    exchange.action = ^{
        [weakself presentLoginController];
    };
    
    if ([RNTUserManager canShowSilverExchange]) {
        [group1.items addObject:exchange];
    }
    
    RNTSettingGroup *group2 = [[RNTSettingGroup alloc] init];
    group2.headerTitle = @" ";
    
    RNTSettingArrowItem *setting = [[RNTSettingArrowItem alloc] initWithIcon:@"Mine_setting" title:@"设置" destClass:[RNTSettingController class]];
    
    [group2.items addObject:setting];
    
    [self.options addObject:group1];
    [self.options addObject:group2];
    
    [self.tableView reloadData];
}

- (void)dealloc
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    [userM removeObserver:self forKeyPath:@"logged"];
}

#pragma mark - 私有方法
- (void)presentLoginController
{
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    delegate.navBarAnimated = NO;
    
    RNTLoginViewController *loginVC = [[RNTLoginViewController alloc] init];
    loginVC.modalPresentationStyle = UIModalPresentationFullScreen;
    RNTNavigationController *loginNV= [[RNTNavigationController alloc] initWithRootViewController:loginVC];
    [self presentViewController:loginNV animated:YES completion:nil];
}

- (void)presentRegisterController
{
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;
    delegate.navBarAnimated = NO;
    
    RNTRegisterViewController *registerVC = [[RNTRegisterViewController alloc] init];
    registerVC.modalPresentationStyle = UIModalPresentationFullScreen;
    RNTNavigationController *registerNV= [[RNTNavigationController alloc] initWithRootViewController:registerVC];
    WEAK(self);
    registerVC.gobackBlock = ^{
        [weakself dismissViewControllerAnimated:YES completion:nil];
    };
    [self presentViewController:registerNV animated:YES completion:nil];
}

- (void)pushEditController
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (userM.isLogged) {
        RNTEditController *EC = [[RNTEditController alloc] init];
        [self.navigationController pushViewController:EC animated:YES];
    } else {
        [self presentLoginController];
    }
}

- (void)handleFans
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (userM.isLogged) {
        RNTFansController *FC = [[RNTFansController alloc] init];
        [self.navigationController pushViewController:FC animated:YES];
    } else {
        [self presentLoginController];
    }
}

- (void)handleFocus
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (userM.isLogged) {
        RNTFocusController *FC = [[RNTFocusController alloc] init];
        [self.navigationController pushViewController:FC animated:YES];
    } else {
        [self presentLoginController];
    }
}

//- (void)handleHistory
//{
//    RNTUserManager *userM = [RNTUserManager sharedManager];
//    if (userM.isLogged) {
//        RNTHistoryController *FC = [[RNTHistoryController alloc] init];
//        [self.navigationController pushViewController:FC animated:YES];
//    } else {
//        [self presentLoginController];
//    }
//}

#pragma mark - KVO
//kvo 观察是否已经登陆  处理不同的事件
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (userM.logged == YES) {
        [self handleLoggedEvent];
    } else {
        [self handleUnloggedEvent];
    }
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (!userM.isLogged) {
        RNTSettingGroup *group = self.options.lastObject;
        RNTSettingItem *item = group.items.firstObject;
        if ([item.tilte isEqualToString:@"设置"]) {
            [super tableView:tableView didSelectRowAtIndexPath:indexPath];
        } else {
            [self presentLoginController];
        }
    } else {
        [super tableView:tableView didSelectRowAtIndexPath:indexPath];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        return 13;
    }
    return 0.1;
}

#pragma mark -
- (RNTMineHeaderView *)headerView
{
    if (_headerView == nil) {
        _headerView = [[RNTMineHeaderView alloc] initWithFrame:CGRectMake(0, -kScreenHeight, kScreenWidth, kScreenHeight)];
        WEAK(self);
        
        _headerView.loginBtnClickBlock = ^{
            [weakself presentLoginController];
        };
        _headerView.registerBtnClickBlock = ^{
            [weakself presentRegisterController];
        };
        _headerView.editBtnClickBlock = ^{
            [weakself pushEditController];
        };
        _headerView.focusBtnClickBlock = ^{
            [weakself handleFocus];
        };
        _headerView.fansBtnClickBlock = ^(NSInteger selectedIndex){
            [weakself handleFans];
        };
//        _headerView.historyBtnClickBlock = ^{
//            [weakself handleHistory];
//        };
    }
    return _headerView;
}

@end
