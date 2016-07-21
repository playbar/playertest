//
//  RNTViewController.h
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTViewController : UIViewController

/**
 *  添加下拉刷新
 *
 *  @param contentView        要下拉刷新的view: 只能是scrollView\collectionView\tableView\webView
 *  @param selelctor          下拉时的回调方法
 */
- (void)addPullDownRefreshWithContentView:(UIView *)contentView refreshingAction:(SEL)selelctor;

/**
 *  添加上拉加载
 *
 *  @param contentView        要上拉刷新的view: 只能是scrollView\collectionView\tableView\webView
 *  @param selelctor          上拉时的回调方法
 */
- (void)addPullUpRefreshWithContentView:(UIView *)contentView refreshingAction:(SEL)selelctor;

/**
 *  刷新完成后结束刷新
 */
- (void)endRefreshing;

/**
 *  提示
 */
- (void)showMBProgressHUDWithMessage:(NSString *)message;
@end
