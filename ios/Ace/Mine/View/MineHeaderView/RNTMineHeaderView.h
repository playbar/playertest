//
//  RNTMineHeaderView.h
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RNTUser;

typedef void (^RNTMineHeaderViewBlock)();

@interface RNTMineHeaderView : UIView
@property (nonatomic, strong) RNTUser *user;
@property (nonatomic, assign) BOOL isLogged;

@property (nonatomic, copy) RNTMineHeaderViewBlock focusBtnClickBlock;
@property (nonatomic, copy) RNTMineHeaderViewBlock fansBtnClickBlock;
//@property (nonatomic, copy) RNTMineHeaderViewBlock historyBtnClickBlock;
@property (nonatomic, copy) RNTMineHeaderViewBlock editBtnClickBlock;
@property (nonatomic, copy) RNTMineHeaderViewBlock loginBtnClickBlock;
@property (nonatomic, copy) RNTMineHeaderViewBlock registerBtnClickBlock;
@end
