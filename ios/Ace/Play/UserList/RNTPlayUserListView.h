//
//  RNTPlayUserListView.h
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#define userListIconWH 35
@class RNTPlayUserListView, RNTPlayUserListUserInfo;
@protocol RNTPlayUserListViewDelegate <NSObject>
- (void)userListView:(RNTPlayUserListView *)listView selectedUserWith:(RNTPlayUserListUserInfo *)object;
@optional

@end

@interface RNTPlayUserListView : UIView


@property (nonatomic, copy) NSString *userID;
@property (nonatomic, weak) id<RNTPlayUserListViewDelegate> delegate;

- (void)addUser:(RNTPlayUserListUserInfo *)info;

/**
 *  @author Ranger, 16-04-12 10:04
 *  删除一个user 用于踢出等操作
 *  @param userID userid
 */
- (void)removeUserID:(NSString *)userID;

@end
