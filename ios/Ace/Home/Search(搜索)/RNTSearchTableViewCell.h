//
//  RNTSearchTableViewCell.h
//  Ace
//
//  Created by 靳峰 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RNTUser;

@interface RNTSearchTableViewCell : UITableViewCell

+ (instancetype)cellWithTableView:(UITableView *)tableView;

//数据模型
@property(nonatomic, strong) RNTUser *user;
@end
