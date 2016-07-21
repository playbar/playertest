//
//  RNTHomeTableViewCell.h
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTShowListModel.h"
#import "RNTUser.h"

@interface RNTHomeTableViewCell : UITableViewCell

//数据模型
@property(nonatomic,strong) RNTShowListModel *showModel;
//房主模型
@property(nonatomic,strong) RNTUser *userModel;
@end
