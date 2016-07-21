//
//  RNTPlayChatView.m
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayChatView.h"
#import "Masonry.h"
#import "RNTPlayChatViewCell.h"
#import "RNTPlayMessageModel.h"

@interface RNTPlayChatView()<UITableViewDataSource, UITableViewDelegate>
@property (nonatomic, strong) NSMutableArray *messages;
@property (nonatomic, weak) UITableView *tableView;
@property (nonatomic, weak) UILabel *tipLabel;
@property (nonatomic, assign) BOOL scrolling;
@property (nonatomic, strong) NSMutableArray *tipModels;
@property (nonatomic, strong) NSTimer *timer;
@end
@implementation RNTPlayChatView

- (NSMutableArray *)messages
{
    if (!_messages) {
        _messages = [[NSMutableArray alloc] init];
    }
    return _messages;
}
- (NSMutableArray *)tipModels
{
    if (!_tipModels) {
        _tipModels = [[NSMutableArray alloc] init];
    }
    return _tipModels;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        
        UILabel* tipLabel = [[UILabel alloc] init];
        [self addSubview:tipLabel];
        tipLabel.hidden = YES;
        self.tipLabel = tipLabel;
        tipLabel.backgroundColor = [UIColor clearColor];
        [tipLabel makeConstraints:^(MASConstraintMaker *make) {
            make.top.right.equalTo(self);
            make.left.equalTo(self).offset(10);
            make.height.equalTo(20);
        }];
        
        UITableView* tableView = [[UITableView alloc] init];
        tableView.showsVerticalScrollIndicator = NO;
        tableView.showsHorizontalScrollIndicator = NO;
        tableView.backgroundColor = [UIColor clearColor];
        tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        tableView.allowsSelection = NO;
        tableView.dataSource = self;
        tableView.delegate = self;

        [self addSubview:tableView];
        self.tableView = tableView;
        [tableView makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.bottom.equalTo(self);
            make.top.equalTo(tipLabel.bottom);
        }];
        
        [tableView reloadData];
        
        [self setupGesture];
        
    }
    return self;
}

- (void)didMoveToSuperview{
    [super didMoveToSuperview];
    if (self.superview) {
        RNTPlayMessageModel* model = [RNTPlayMessageModel playTipsMessageModelNeedLink:!self.liveType];
        [self addMessage:model];
    }
}

- (void)setupGesture
{
    UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapAction:)];
//    tap.delaysTouchesBegan = YES;
    [self.tableView addGestureRecognizer:tap];
}

- (void)tapAction:(UITapGestureRecognizer*)tap
{
    if ([self.delegate respondsToSelector:@selector(playChatViewTapAction:)]) {
        [self.delegate playChatViewTapAction:self];
    }
//    CGPoint location = [tap locationInView:tap.view];
    RNTLog(@"getsture - action");
}

#pragma mark - UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.messages.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTPlayChatViewCell* cell = [RNTPlayChatViewCell chatViewCellWithTableView:tableView];
    RNTPlayMessageModel* model = self.messages[indexPath.row];
    cell.messageModel = model;
    return cell;
}

//#pragma mark - RNTPlayChatViewCellDelegate
//- (void)playChatViewCell:(RNTPlayChatViewCell *)cell selectedUser:(NSString *)userID{
//    if (userID) {
//        if ([self.delegate respondsToSelector:@selector(playChatView:selectedUser:)]) {
//            [self.delegate playChatView:self selectedUser:userID];
//        }
//    }else{
//        if ([self.delegate respondsToSelector:@selector(playChatViewTapAction:)]) {
//            [self.delegate playChatViewTapAction:self];
//        }
//    }
//}
//
//- (void)playChatViewCell:(RNTPlayChatViewCell *)cell selectedLink:(NSString *)urlStr{
//    if ([self.delegate respondsToSelector:@selector(playChatView:selectedLink:)]) {
//        [self.delegate playChatView:self selectedLink:urlStr];
//    }
//}

#pragma mark - UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTPlayMessageModel* model = self.messages[indexPath.row];
//    RNTLog(@"height - %f", model.textSize.height);
    return model.textSize.height + cellHeightMargin;
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
//    RNTLog(@"==========");
    [self.tableView reloadData];
}

- (void)addMessage:(id)model
{
    if (!model) {
        return;
    }
    [self.tableView numberOfRowsInSection:0];
    
    if (self.messages.count > 100) {
        [self.messages removeObjectAtIndex:0];
    }
    
    [self.messages addObject:model];
    if(self.tableView.isDecelerating || self.tableView.isDragging) return;
    [self.tableView reloadData];
    
    [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:self.messages.count - 1 inSection:0] atScrollPosition:UITableViewScrollPositionNone animated:YES];
    
}

- (void)addUserEnterTip:(RNTPlayMessageModel *)model
{
//    if (self.isHidden) {
//        self.hidden = NO;
//    }
    self.tipLabel.hidden = NO;
    [self invalidateTimer];
    [self.tipModels addObject:model];
    if (self.tipModels.count == 1) {
        [self playEnterTip];
    }
}
- (void)playEnterTip
{
    if (self.tipModels.count == 0) {
        [self fireTimer];
        return;
    }
    RNTPlayMessageModel* model = self.tipModels[0];
    self.tipLabel.attributedText = model.attributedStr;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        RNTPlayMessageModel* model = self.tipModels[0];
        [self.tipModels removeObject:model];
        [self playEnterTip];
    });
}
#pragma mark - timer
- (void)fireTimer
{
    [self invalidateTimer];
    
    NSTimer* timer = [NSTimer scheduledTimerWithTimeInterval:5.0 target:self selector:@selector(timerFired) userInfo:nil repeats:NO];
    [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    self.timer = timer;
}
- (void)invalidateTimer
{
    [self.timer invalidate];
    self.timer = nil;
}

- (void)timerFired
{
    [self invalidateTimer];
    self.tipLabel.hidden = YES;
}

- (void)dealloc
{
    [self invalidateTimer];
    RNTLog(@"--");
}

@end
