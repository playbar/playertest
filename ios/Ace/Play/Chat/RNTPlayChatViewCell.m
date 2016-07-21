//
//  RNTPlayChatViewCell.m
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayChatViewCell.h"
#import "RNTPlayMessageModel.h"
#import "RNTPlayChatTextView.h"

static NSString* const ID = @"chatViewCell";

@interface RNTPlayChatViewCell()
//@property (nonatomic, weak) UIView *contentBackView;
@property (nonatomic, weak) RNTPlayChatTextView *contentTextView;
@end

@implementation RNTPlayChatViewCell

+ (instancetype)chatViewCellWithTableView:(UITableView *)tableView
{
    RNTPlayChatViewCell* cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (!cell) {
        cell = [[RNTPlayChatViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ID];
    }
    
    return cell;
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        self.backgroundColor = [UIColor clearColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
//        UIView* contentBackView = [[UIView alloc] init];
//        [self.contentView addSubview:contentBackView];
//        self.contentBackView = contentBackView;
//        contentBackView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.7];
//        [contentBackView makeConstraints:^(MASConstraintMaker *make) {
//            make.left.equalTo(self.contentView);
//            make.top.equalTo(self.contentView).offset(5);
//            make.bottom.equalTo(self.contentView).offset(-5);
//        }];
        
        RNTPlayChatTextView* contentTextView = [[RNTPlayChatTextView alloc] init];
        [self.contentView addSubview:contentTextView];
        contentTextView.backgroundColor = [UIColor clearColor];
        self.contentTextView = contentTextView;
//        [contentLabel sizeToFit];
        [self.contentTextView makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.contentView).insets(UIEdgeInsetsMake(5, cellLeftMargin, 0, 0));
        }];
        
    }
    return self;
}

//- (void)setSelectedUser:(void (^)(NSString *))selectedUser
//{
//    _selectedUser = [selectedUser copy];
//    self.contentTextView.selectedUser = _selectedUser;
//}

- (void)setMessageModel:(RNTPlayMessageModel *)messageModel
{
    _messageModel = messageModel;
    self.contentTextView.attributedText = messageModel.attributedStr;

//    CGSize textSize = messageModel.textSize;
//    textSize.height = self.contentTextView.contentSize.height;
//    messageModel.textSize = textSize;
//    CGFloat contentBackWidth = messageModel.textSize.width + cellRightMargin + cellLeftMargin + 10;
//    [self.contentBackView updateConstraints:^(MASConstraintMaker *make) {
//        make.width.equalTo(contentBackWidth);
//    }];
    
    
//    UIBezierPath* path = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(0, 0, contentBackWidth, messageModel.textSize.height + (cellHeightMargin - 10)) byRoundingCorners:UIRectCornerTopRight | UIRectCornerBottomRight cornerRadii:CGSizeMake(30, 30)];
//    CAShapeLayer* shapLayer = [CAShapeLayer layer];
//    shapLayer.path = path.CGPath;
//    self.contentBackView.layer.mask = shapLayer;
}

//#pragma mrak - RNTPlayChatTextViewDelegate
//- (void)playChatTextView:(RNTPlayChatTextView *)textView selectedUser:(NSString *)userID{
//    if ([self.delegate respondsToSelector:@selector(playChatViewCell:selectedUser:)]) {
//        [self.delegate playChatViewCell:self selectedUser:userID];
//    }
//}
//- (BOOL)textView:(UITextView *)textView shouldInteractWithURL:(NSURL *)URL inRange:(NSRange)characterRange{
//    if ([self.delegate respondsToSelector:@selector(playChatViewCell:selectedLink:)]) {
//        [self.delegate playChatViewCell:self selectedLink:URL.absoluteString];
//    }
//    return NO;
//}


@end
