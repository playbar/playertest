//
//  RNTAccountManagerController.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTAccountManagerController.h"
#import "RNTBindPhoneController.h"
#import "RNTChangePWDController.h"

@interface RNTAccountManagerController ()

@end

@implementation RNTAccountManagerController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"账号管理";
    
    RNTSettingGroup *group = [[RNTSettingGroup alloc] init];
    RNTSettingArrowItem *changePWD = [[RNTSettingArrowItem alloc] initWithIcon:nil title:@"修改密码" destClass:[RNTChangePWDController class]];
    
    [group.items addObject:changePWD];
    
    [self.options addObject:group];
}

@end
