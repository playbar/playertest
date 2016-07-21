//
//  RNTSettingController.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingController.h"
#import "SDImageCache.h"
#import "RNTAboutController.h"
#import "RNTFeedBackController.h"
#import "RNTAccountManagerController.h"
#import "RNTBlacklistController.h"

//是否显示账号管理
#define CanShowAccountCell ![RNTUserManager sharedManager].isThirdPartLogged && [RNTUserManager sharedManager].isLogged

@interface RNTSettingController ()

@end

@implementation RNTSettingController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"设置";
        
    [self setupSubviews];
}

- (void)setupSubviews
{
    RNTSettingGroup *group1 = [[RNTSettingGroup alloc] init];
    group1.headerTitle = @" ";
    
    if (CanShowAccountCell) {
        RNTSettingArrowItem *account = [[RNTSettingArrowItem alloc] initWithIcon:nil title:@"账号管理" destClass:[RNTAccountManagerController class]];
        [group1.items addObject:account];
    }
    
    RNTSettingArrowItem *blacklist = [[RNTSettingArrowItem alloc] initWithIcon:nil title:@"黑名单" destClass:[RNTBlacklistController class]];
    [group1.items addObject:blacklist];

    NSString *memoryStr = [NSString stringWithFormat:@"%.1f M",([[SDImageCache sharedImageCache] getSize] / 1000 / 1000.0)] ;
    RNTSettingRightLabelItem *cleanMemory = [[RNTSettingRightLabelItem alloc] initWithIcon:nil title:@"清理缓存" rightLabelText:memoryStr];
    WEAK(self);
    cleanMemory.action = ^{
        [weakself cleanMemory];
    };
    
    RNTSettingArrowItem *opoion = [[RNTSettingArrowItem alloc] initWithIcon:nil title:@"意见反馈" destClass:[RNTFeedBackController class]];
    RNTSettingArrowItem *about = [[RNTSettingArrowItem alloc] initWithIcon:nil title:@"关于" destClass:[RNTAboutController class]];
    
    [group1.items addObject:cleanMemory];
    [group1.items addObject:opoion];
    [group1.items addObject:about];
    
    [self.options addObject:group1];
    
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (userM.isLogged) {
        RNTSettingGroup *group2 = [[RNTSettingGroup alloc] init];
        group2.headerTitle = @" ";
        
        RNTSettingItem *exit = [[RNTSettingItem alloc] initWithIcon:nil title:@"退出登录"];
        exit.action = ^{
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"是否确定退出？" message:nil preferredStyle:UIAlertControllerStyleAlert];
            UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
            UIAlertAction *confirm = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                [weakself exit];
            }];
            [alert addAction:cancel];
            [alert addAction:confirm];
            [weakself presentViewController:alert animated:YES completion:nil];
        };
        
        [group2.items addObject:exit];
        [self.options addObject:group2];
    }
}

//清除缓存
- (void)cleanMemory
{
    RNTSettingGroup *group1 = [self.options firstObject];
    
    NSUInteger index;
    if (CanShowAccountCell) {
        index = 2;
    } else {
        index = 1;
    }
    RNTSettingRightLabelItem *clean = [group1.items objectAtIndex:index];
    
    WEAK(self);
    [[SDImageCache sharedImageCache] clearDiskOnCompletion:^{
        [MBProgressHUD showSuccess:@"清理成功"];
        clean.labelText = [NSString stringWithFormat:@"%.1f M",([[SDImageCache sharedImageCache] getSize] / 1000 / 1000.0)] ;
        
        NSUInteger newIndex[] = {0, index};
        NSIndexPath *indexPath = [[NSIndexPath alloc] initWithIndexes:newIndex length:2];
        [weakself.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
    }];
}

//退出登录
- (void)exit
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    [userM logOff];
    [self.navigationController popToRootViewControllerAnimated:YES];
}
@end
