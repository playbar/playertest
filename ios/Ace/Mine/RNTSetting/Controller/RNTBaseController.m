//
//  RNTBaseController.m
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTBaseController.h"

@interface RNTBaseController ()

@end

@implementation RNTBaseController

- (id)init
{
    return [super initWithStyle:UITableViewStyleGrouped];
}

- (id)initWithStyle:(UITableViewStyle)style
{
    return [super initWithStyle:UITableViewStyleGrouped];
}

- (NSMutableArray *)options
{
    if (!_options) {
        self.options = [NSMutableArray array];
    }
    return _options;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.tableView registerClass:[RNTSettingCell class] forCellReuseIdentifier:@"RNTSETTING"];
    self.view.backgroundColor = RNTBackgroundColor;
    [self.tableView  setSeparatorColor:RNTSeparatorColor];
}


#pragma mark - Table view data source
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.options.count;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    RNTSettingGroup *group = self.options[section];
    return group.items.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RNTSettingCell *cell = [tableView dequeueReusableCellWithIdentifier:@"RNTSETTING" forIndexPath:indexPath];
    RNTSettingGroup *group = self.options[indexPath.section];
    RNTSettingItem *item = group.items[indexPath.row];
    
    if ([item isKindOfClass:[RNTSettingRightButtonAndLabelItem class]]) {
        RNTSettingRightButtonAndLabelItem *newItem = (RNTSettingRightButtonAndLabelItem *)item;
        
        WEAK(self);
        newItem.handler = ^(Class className) {
            [weakself.navigationController pushViewController:[[className alloc] init] animated:YES];
        };
    }
    cell.item = item;
    return cell;
}

#pragma mark - UITableViewDelegate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    RNTSettingGroup *group = self.options[indexPath.section];
    RNTSettingItem *item = group.items[indexPath.row];
    //每个item 如有action有值 就执行  没有就不执行  然后根据不同 的需求处理不同的点击事件
    if (item.action != nil) {
        item.action();
    } else if ([item isKindOfClass:[RNTSettingArrowItem class]]) {
        RNTSettingArrowItem *newItem = (RNTSettingArrowItem *)item;
        [self.navigationController pushViewController:[[newItem.destVC alloc]init] animated:YES];
    } else if ([item isKindOfClass:[RNTSettingSinatureItem class]]) {
        RNTSettingSinatureItem *newItem = (RNTSettingSinatureItem *)item;
        [self.navigationController pushViewController:[[newItem.destVC alloc]init] animated:YES];
    } else if ([item isKindOfClass:[RNTSettingRightLabelAndArrowItem class]]) {
        RNTSettingRightLabelAndArrowItem *newItem = (RNTSettingRightLabelAndArrowItem *)item;
        [self.navigationController pushViewController:[[newItem.destVC alloc]init] animated:YES];
    }
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    
    RNTSettingGroup *group = self.options[section];
    return group.headerTitle;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section {
    RNTSettingGroup *group = self.options[section];
    return group.footerTitle;
}

//根据不同的item 返回不同的高度
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTSettingGroup *group = self.options[indexPath.section];
    RNTSettingItem *item = group.items[indexPath.row];

    if ([item isKindOfClass:[RNTSettingLevelItem class]]) {
        return 91;
    }
    if([item isKindOfClass:[RNTSettingPhotoItem class]] )
    {
        return 88;
    }
    if ([item isKindOfClass:[RNTSettingSinatureItem class]]) {
        return [RNTSettingSinatureItem signatureHeight];
    }
    return 51;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 0.1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 13;
}

@end
