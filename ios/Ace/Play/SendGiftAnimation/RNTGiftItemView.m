//
//  GiftItemView.m
//  15-ace礼物动画
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 于传峰. All rights reserved.
//

#import "RNTGiftItemView.h"
#import "RNTPlaySendGiftInfo.h"
#import "RNTGiftCaromCountLabel.h"


@interface RNTGiftItemView()
//@property (nonatomic, assign) NSUInteger giftCount;
@property (nonatomic, strong) NSMutableArray *caromCountArray;

@property (nonatomic, weak) UIImageView *giftIconView;
@property (nonatomic, weak) UILabel *nameLabel;
@property (nonatomic, weak) UILabel *giftNameLabel;

@property (nonatomic, weak) UIView *countView;
@property (nonatomic, weak) UILabel *countLabel;

@property (nonatomic, strong) NSTimer *timer;
@end

@implementation RNTGiftItemView

- (NSMutableArray *)caromCountArray
{
    if (!_caromCountArray) {
        _caromCountArray = [[NSMutableArray alloc] init];
    }
    return _caromCountArray;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = [UIColor clearColor];
        
        UIView* backView = [[UIView alloc] init];
        backView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
        [self addSubview:backView];
        backView.frame = CGRectMake(0, 0, itemWidth - rightMargin, itemHeight);
        UIBezierPath* path = [UIBezierPath bezierPathWithRoundedRect:backView.bounds byRoundingCorners:UIRectCornerTopRight | UIRectCornerBottomRight cornerRadii:CGSizeMake(itemHeight, itemHeight)];
        CAShapeLayer* shapLayer = [CAShapeLayer layer];
        shapLayer.path = path.CGPath;
        backView.layer.mask = shapLayer;
        
        UIImageView* giftIconView = [[UIImageView alloc] init];
        [self addSubview:giftIconView];
        self.giftIconView = giftIconView;
        giftIconView.backgroundColor = [UIColor clearColor];
        [giftIconView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(50, 50));
            make.centerY.equalTo(backView.mas_centerY);
            make.right.equalTo(backView).offset(2);
        }];
        
        UILabel* nameLabel = [[UILabel alloc] init];
        [backView addSubview:nameLabel];
        self.nameLabel = nameLabel;
        nameLabel.textColor = [UIColor whiteColor];
        nameLabel.font = [UIFont systemFontOfSize:14];
        nameLabel.text = @"谈英俊";
        [nameLabel sizeToFit];
        [nameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(backView).offset(10);
            make.bottom.equalTo(backView.mas_centerY).offset(-1.5);
            make.right.equalTo(giftIconView.mas_left).offset(0);
        }];

        UILabel* giftNameLabel = [[UILabel alloc] init];
        [backView addSubview:giftNameLabel];
        self.giftNameLabel = giftNameLabel;
        giftNameLabel.textColor = RNTColor_16(0xfff100);
        giftNameLabel.font = [UIFont systemFontOfSize:12];
        giftNameLabel.text = @"送出：玫瑰花";
        [giftNameLabel sizeToFit];
        [giftNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(nameLabel);
            make.top.equalTo(nameLabel.mas_bottom).offset(4);
            make.right.equalTo(nameLabel);
        }];
        
        UIView* countBackView = [[UIView alloc] init];
        countBackView.hidden = YES;
        countBackView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
        [self addSubview:countBackView];
        self.countView = countBackView;
        CGFloat countWidth = 36;
        CGFloat countHeight = 23;
        countBackView.layer.cornerRadius = countHeight * 0.5;
        countBackView.clipsToBounds = YES;
        [countBackView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(countWidth, countHeight));
            make.top.equalTo(backView);
            make.left.equalTo(backView.mas_right).offset(5);
        }];
        
        UILabel* countLabel = [[RNTGiftCaromCountLabel alloc] init];
        [self addSubview:countLabel];
        self.countLabel = countLabel;
        self.countLabel.hidden = YES;
        countLabel.textColor = RNTColor_16(0xfff100);
        countLabel.font = [UIFont systemFontOfSize:18];
        countLabel.textAlignment = NSTextAlignmentCenter;
        [countLabel sizeToFit];
        [countLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.center.equalTo(countBackView);
        }];
        
    }
    return self;
}

- (void)didMoveToWindow
{
    [super didMoveToWindow];
    
    self.transform = CGAffineTransformMakeTranslation(-itemWidth, 0);
    [UIView animateWithDuration:0.25 animations:^{
        self.transform = CGAffineTransformIdentity;
    } completion:^(BOOL finished) {
        [self fireTimer];
    }];
}

- (void)setInfo:(RNTPlaySendGiftInfo *)info
{
    _info = info;
    self.giftIconView.image = info.giftImage;
    self.nameLabel.text = info.senderName;
    self.giftNameLabel.text = [NSString stringWithFormat:@"送出:%@", info.giftName];
}

- (void)playCountAnimation
{
    if (self.caromCountArray.count == 0) {
        [self fireTimer];
        return;
    }
    [self stopTimer];
    NSNumber* count = [self.caromCountArray firstObject];
    self.countView.hidden = NO;
    self.countLabel.hidden = NO;
    self.countLabel.text = [NSString stringWithFormat:@"X%zd", count.unsignedIntegerValue];
    
    self.countLabel.transform = CGAffineTransformMakeScale(10, 10);
    [UIView animateWithDuration:0.7 delay:0 usingSpringWithDamping:0.8 initialSpringVelocity:10 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        self.countLabel.transform = CGAffineTransformIdentity;
    } completion:^(BOOL finished) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self.caromCountArray removeObjectAtIndex:0];
            [self playCountAnimation];

        });
    }];
}

- (void)addCaromGiftCount:(NSUInteger)count
{
    [self.caromCountArray addObject:@(count)];
    if (self.caromCountArray.count == 1) {
        [self playCountAnimation];
    }
}

#pragma mark - timer
- (void)fireTimer
{
    [self stopTimer];
    
    self.timer = [NSTimer scheduledTimerWithTimeInterval:3.0 target:self selector:@selector(hiddenView) userInfo:nil repeats:NO];
    [[NSRunLoop currentRunLoop] addTimer:self.timer forMode:NSRunLoopCommonModes];
}
- (void)stopTimer
{
    [self.timer invalidate];
    self.timer = nil;
}

- (void)hiddenView
{
    [self stopTimer];
    [UIView animateWithDuration:0.25 animations:^{
        self.alpha = 0.0;
    } completion:^(BOOL finished) {
        [self removeFromSuperview];
        if (self.dismissBlock) {
            self.dismissBlock();
        }
    }];
}

- (void)dealloc
{
//    NSLog(@"-----dealloc--itemView");
}

@end
