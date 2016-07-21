//
//  RNTPlayGiftView.m
//  Ace
//
//  Created by 于传峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayGiftView.h"
#import "RNTPlayGiftCell.h"
#import "MJExtension.h"
#import "RNTPlayGiftModel.h"
#import "RNTPlayNetWorkTool.h"
#import "RNTSocketSummary.h"
#import "RNTSocketTool.h"
#import "UIImage+RNT.h"
#import "RNTPlayGiftSendButton.h"

#define giftToolBarHeight  48

#define leftMargin  8
#define topMargin 8
#define perLineCount  4
#define rowCount  2

#define cellWidth (kScreenWidth - leftMargin*(perLineCount + 1)) / (perLineCount * 1.0)
#define cellHeight (cellWidth * 1.3)


#define scrollViewHeight ((cellHeight + topMargin) * rowCount)


@interface RNTPlayGiftView()<UIScrollViewDelegate>
@property (nonatomic, weak) UIScrollView *scrollView;
@property (nonatomic, weak) RNTPlayGiftCell *selectedCell;
@property (nonatomic, weak) UILabel *coinLabel;
@property (nonatomic, copy) NSString *userID;
@property (nonatomic, copy) NSString *showID;
@property (nonatomic, weak) UIButton *sendButton;
@property (nonatomic, weak) UIPageControl *pageControl;
@property (nonatomic, weak) RNTPlayGiftSendButton *caromSendView;

@property (nonatomic, strong) NSTimer *timer;
@property (nonatomic, assign) int index;
@end

static __weak UIButton* _coverButton;
static void(^_dismissBlock)();

@implementation RNTPlayGiftView

- (instancetype)initWithFrame:(CGRect)frame
{
    
    
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.7];
        [self setupSubViews];
    }
    return self;
}

- (void)setupSubViews
{
    [self setupGiftsView];
    
    [self setupToolBarView];
    
    [self setupCaromSendView];
    
    NSMutableArray* infos = [[NSMutableArray alloc] init];
    NSArray* dictsArray = [NSArray arrayWithContentsOfFile:GiftDictsPath];
    for (NSDictionary* giftDict in dictsArray) {
        [infos addObject:[RNTPlayGiftModel mj_objectWithKeyValues:giftDict.allValues[0]]];
    }
    [self addGiftViewsWithGiftInfos:infos];
    
    
    [self getUserBlance];
}

#pragma mark - timer
- (void)fireTimer
{
    self.sendButton.hidden = YES;
    [self invalidateTimer];
//    self.index = 5;
    self.caromSendView.leftTime = 5;
    NSTimer* timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(timerFired) userInfo:nil repeats:YES];
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
    self.caromSendView.leftTime -= 1;
//    self.index --;
    if (self.caromSendView.leftTime <= 0) {
        [self invalidateTimer];
        self.sendButton.hidden = NO;
        self.caromSendView.hidden = YES;
    }
}

- (void)setIndex:(int)index
{
    _index = index;

}

- (void)sendGiftWithModel:(RNTPlayGiftModel*)model
{
    RNTUserManager* manager = [RNTUserManager sharedManager];
//    WEAK(self)
    [RNTPlayNetWorkTool sendGiftWithGiftID:model.ID senderID:manager.user.userId receiverID:self.userID showID:self.showID giftCount:1 sendSuccess:^(NSDictionary* result) {
        RNTLog(@"%@", result);
//        [weakself getUserBlance];
        if ([result[@"exeStatus"] intValue] == 1) {
            self.coinLabel.text = result[@"balance"];
            manager.blance = self.coinLabel.text;
        }else{
            [MBProgressHUD showError:result[@"errMsg"]];
        }
    }];
}

- (void)getUserBlance
{
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        return;
    }
    
    [manager refreshBlanceSuccess:^(NSString *blance) {
        self.coinLabel.text = blance;
    }];
}

- (void)setupGiftsView
{
    UIScrollView* scrollView = [[UIScrollView alloc] init];
    [self addSubview:scrollView];
    self.scrollView = scrollView;
    scrollView.pagingEnabled = YES;
    scrollView.delegate = self;
    scrollView.showsHorizontalScrollIndicator = NO;
    scrollView.showsVerticalScrollIndicator = NO;
    scrollView.backgroundColor = [UIColor clearColor];
    [scrollView makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.equalTo(self);
        make.height.equalTo(scrollViewHeight);
    }];
    
}

- (void)setupToolBarView
{
    UIView* toolBarView = [[UIView alloc] init];
    [self addSubview:toolBarView];
    toolBarView.backgroundColor = [UIColor clearColor];
    [toolBarView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self);
        make.top.equalTo(self.scrollView.bottom);
    }];
    
    // coinLabel
    UILabel* coinLabel = [[UILabel alloc] init];
    [toolBarView addSubview:coinLabel];
    self.coinLabel = coinLabel;
    coinLabel.font = [UIFont systemFontOfSize:14];
    coinLabel.textColor = RNTColor_16(0xfff100);
    coinLabel.text = @"0";
    [coinLabel sizeToFit];
    [coinLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(toolBarView).offset(14);
        make.centerY.equalTo(toolBarView);
    }];
    
    // chargeButton
    UIButton* chargeButton = [[UIButton alloc] init];
    [toolBarView addSubview:chargeButton];
    chargeButton.backgroundColor = [UIColor clearColor];
//    [chargeButton setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0xfff100)] forState:UIControlStateNormal];
    chargeButton.layer.cornerRadius = 3;
    chargeButton.clipsToBounds = YES;
    [chargeButton addTarget:self action:@selector(chargeButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    chargeButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [chargeButton setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateNormal];
    [chargeButton setTitle:@"充值 >" forState:UIControlStateNormal];
    [chargeButton makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(60, 25));
        make.centerY.equalTo(coinLabel);
        make.left.equalTo(coinLabel.right).offset(6);
    }];
    
    // sendButton
    UIButton* sendButton = [[UIButton alloc] init];
    [toolBarView addSubview:sendButton];
    [sendButton setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0x0090ff)] forState:UIControlStateNormal];
    sendButton.layer.cornerRadius = 3;
    sendButton.clipsToBounds = YES;
    sendButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [sendButton setTitleColor:RNTColor_16(0x19191a) forState:UIControlStateNormal];
    [sendButton setTitle:@"发送" forState:UIControlStateNormal];
    self.sendButton = sendButton;
    [sendButton addTarget:self action:@selector(sendButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [sendButton makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(chargeButton);
        make.centerY.equalTo(coinLabel);
        make.right.equalTo(toolBarView).offset(-14);
    }];
    
    // carom
//    UIImageView* caromImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_caromSend_5"]];
//    [toolBarView addSubview:caromImageView];
//    caromImageView.hidden = YES;
//    self.caromImageView = caromImageView;
//    [caromImageView makeConstraints:^(MASConstraintMaker *make) {
//        make.centerY.equalTo(sendButton);
//        make.right.equalTo(sendButton.left).offset(-15);
//    }];
    
    UIPageControl* pageControl = [[UIPageControl alloc] init];
    pageControl.hidesForSinglePage = YES;
    pageControl.currentPageIndicatorTintColor = RNTMainColor;
    pageControl.pageIndicatorTintColor = [UIColor whiteColor];
    [toolBarView addSubview:pageControl];
    self.pageControl = pageControl;
    [pageControl makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(toolBarView);
        make.centerX.equalTo(toolBarView);
    }];
}

- (void)setupCaromSendView{
    RNTPlayGiftSendButton* caromSendView = [[RNTPlayGiftSendButton alloc] init];
    [self addSubview:caromSendView];
    self.caromSendView = caromSendView;
    [caromSendView addTarget:self action:@selector(sendButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [caromSendView makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self).offset(-20);
        make.bottom.equalTo(self).offset(-20);
        make.size.equalTo(CGSizeMake(90, 90));
    }];
}

- (void)addGiftViewsWithGiftInfos:(NSArray *)infos
{
    NSInteger giftCount = infos.count;
    
    NSInteger countPerPage = perLineCount * rowCount;
    NSInteger pageCount = giftCount % countPerPage == 0 ? giftCount / countPerPage : giftCount / countPerPage + 1;
    self.pageControl.numberOfPages = pageCount;
    self.scrollView.contentSize = CGSizeMake(kScreenWidth * pageCount, 0);
    
    NSInteger column;
    NSInteger line;
    int count = 0;
    for (int i = 0; i<pageCount; i++) {
        
        for (int j = 0; j<countPerPage; j++) {
            if (count >= giftCount) {
                break;
            }
            
            column = j / perLineCount;
            line = j % perLineCount;
            RNTPlayGiftCell* cell = [[RNTPlayGiftCell alloc] init];
            cell.model = infos[count];
            cell.tag = count;
            [cell addTarget:self action:@selector(cellSelected:)];
            [self.scrollView addSubview:cell];
            [cell makeConstraints:^(MASConstraintMaker *make) {
                make.size.equalTo(CGSizeMake(cellWidth, cellHeight));
                make.left.equalTo(self.scrollView).offset(leftMargin + i * kScreenWidth + line * (leftMargin+cellWidth));
                make.top.equalTo(self.scrollView).offset(topMargin + column * (topMargin + cellHeight));
            }];
            
            count ++;
        }
    }
    
}



- (void)chargeButtonClicked:(UIButton *)button
{
    [[self class] coverButtonClicked:_coverButton];
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        if ([self.delegate respondsToSelector:@selector(playGiftViewShowLoginView:)]) {
            [self.delegate playGiftViewShowLoginView:self];
        }
        return;
    }
    if ([self.delegate respondsToSelector:@selector(playGiftViewShowChargeView:)]) {
        [self.delegate playGiftViewShowChargeView:self];
    }
}
- (void)sendButtonClicked:(UIButton *)button
{
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        [[self class] coverButtonClicked:_coverButton];
        if ([self.delegate respondsToSelector:@selector(playGiftViewShowLoginView:)]) {
            [self.delegate playGiftViewShowLoginView:self];
        }
        return;
    }
    
    if (self.selectedCell) {
        NSInteger coinCount = self.coinLabel.text.integerValue;
        if (coinCount < self.selectedCell.model.transformPrice.integerValue) {
            [MBProgressHUD showError:@"金币不足，请充值！"];
            return;
        }
    }else{
        [MBProgressHUD showError:@"请选择礼物"];
        return;
    }
    
    RNTPlayGiftModel* model = self.selectedCell.model;
    
    [self sendGiftWithModel:model];
    
    if (model.type) { // 超级礼物

//        [RNTPlayGiftView dismiss];
    }else{
        
        [self fireTimer];
//        self.coinLabel.text = [NSString stringWithFormat:@"%zd", self.coinLabel.text.integerValue - model.sendPrice.integerValue];
    }
    
    
    
    
}

- (void)cellSelected:(RNTPlayGiftCell *)cell
{
    if (self.selectedCell) {
        self.selectedCell.selected = NO;
    }
    if (self.selectedCell.model.ID.intValue != cell.model.ID.intValue) {
        [self invalidateTimer];
        self.sendButton.hidden = NO;
        self.caromSendView.hidden = YES;
    }
    cell.selected = YES;
    self.selectedCell = cell;
}

#pragma mark - scrollViewDelegate
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGFloat contentOffsetX = scrollView.contentOffset.x;
    NSInteger currentPage = contentOffsetX / scrollView.bounds.size.width + 0.5;
    self.pageControl.currentPage = currentPage;
}

+ (instancetype)showWithUserID:(NSString *)userID showID:(NSString *)showID dismiss:(void (^)())dismissBlock
{
    UIButton* coverButton = [[UIButton alloc] init];
    [coverButton addTarget:self action:@selector(coverButtonClicked:) forControlEvents:UIControlEventTouchDown];
    UIWindow* keyWindow = [UIApplication sharedApplication].keyWindow;
    [keyWindow addSubview:coverButton];
    _coverButton = coverButton;
    [coverButton makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(keyWindow);
        make.bottom.equalTo(keyWindow).offset(giftViewHeight);
    }];
    
    RNTPlayGiftView* giftView = [[RNTPlayGiftView alloc] init];
    giftView.userID = userID;
    giftView.showID = showID;
    _dismissBlock = [dismissBlock copy];
    [coverButton addSubview:giftView];
    [giftView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(coverButton);
        make.height.equalTo(giftViewHeight);
    }];
    
    [coverButton layoutIfNeeded];
    
    [coverButton updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(keyWindow);
    }];
    [UIView animateWithDuration:0.25 animations:^{
        [coverButton layoutIfNeeded];
    }];

    
    return giftView;
}

+ (void)dismiss
{
    [self coverButtonClicked:_coverButton];
}

+ (void)coverButtonClicked:(UIButton *)button
{
    button.enabled = NO;
    if (button && button.superview) {
        [button updateConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(button.superview).offset(giftViewHeight);
        }];
    }
    
    if (_dismissBlock) {
        _dismissBlock();
    }
    _dismissBlock = nil;
    
    [UIView animateWithDuration:0.25 animations:^{
        [button layoutIfNeeded];
    } completion:^(BOOL finished) {
        [button.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
        [button removeFromSuperview];
    }];
}

- (void)dealloc
{
//    if (self.dismissBlock) {
//        self.dismissBlock();
//    }
    RNTLog(@"--");
}

@end
