//
//  RNTPlayChatViewCell.h
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RNTPlayMessageModel, RNTPlayChatViewCell;

//@protocol RNTPlayChatViewCellDelegate <NSObject>
//
//@optional
//- (void)playChatViewCell:(RNTPlayChatViewCell *)cell selectedUser:(NSString *)userID;
//- (void)playChatViewCell:(RNTPlayChatViewCell *)cell selectedLink:(NSString *)urlStr;
//
//@end

@interface RNTPlayChatViewCell : UITableViewCell

+ (instancetype)chatViewCellWithTableView:(UITableView *)tableView;

@property (nonatomic, strong) RNTPlayMessageModel *messageModel;
//@property (nonatomic, weak) id<RNTPlayChatViewCellDelegate> delegate;

@end
