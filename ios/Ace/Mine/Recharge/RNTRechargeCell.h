//
//  RNTRechargeCell.h
//  Ace
//
//  Created by 周兵 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>
typedef void(^RechargeCellBlock)();

@interface RNTRechargeCell : UITableViewCell
@property (nonatomic, strong)  SKProduct *product;
@property (nonatomic, strong) UIView *topSeparatorLine;
@property (nonatomic, copy) RechargeCellBlock priceBtnClickBlock;
+ (instancetype)cellWithTableView:(UITableView *)tableView;
@end
