//
//  RNTListViewContronller.h
//  Ace
//
//  Created by 靳峰 on 16/4/10.
//  Copyright © 2016年 RNT. All rights reserved.
//

typedef enum {
    //粉丝列表
    FansList,
    //关注列表
    SubscribList
    
} ListViewContronllerType;

#import "RNTViewController.h"

@interface RNTListViewContronller : RNTViewController

//用户ID
@property(nonatomic,copy) NSString *userID;
//模型
@property(nonatomic,strong) NSMutableArray *modelArr;
//列表
@property(nonatomic,strong) UITableView *table;
//点击cell
@property(nonatomic,copy) void(^cellClick)(NSString *userID);

+ (instancetype)listTableViewControllerWithType:(ListViewContronllerType)type;

@end
