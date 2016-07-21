//
//  RNTCaptureEndView.m
//  Ace
//
//  Created by Ranger on 16/3/17.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureEndView.h"
#import "RNTCaptureEndModel.h"
#import "UIImageView+WebCache.h"
#import "RNTCaptureNetTool.h"
#import "RNTSocketTool.h"
#import "RNTSocketSummary.h"
#import "RNTUserManager.h"
#import "MJExtension.h"

@interface RNTCaptureEndView ()


//头像
@property(nonatomic,weak) UIImageView *iconView;
//昵称
@property(nonatomic,weak) UILabel *nameView;
//A豆数
@property(nonatomic,weak) UILabel *coinCountView;
//直播时间
@property(nonatomic,weak) UILabel *timeView;
//点赞数
@property(nonatomic,weak) UILabel *zanCountView;
//粉丝数
@property(nonatomic,weak) UILabel *fansCountView;
//观众数
@property(nonatomic,weak) UILabel *audienceCountView;

@end

@implementation RNTCaptureEndView

+ (void)showPlayEndViewWithView:(UIView *)view showId:(NSString *)showId EnsureClickBlock:(RNTCaptureEndViewEnsureClick)btnClick
{
    RNTCaptureEndView *endView = [[RNTCaptureEndView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    
    endView.ensureClickBlock = btnClick;
    endView.alpha = 0.3;

//    [RNTCaptureNetTool sendEndWithShowId:showId success:^(RNTCaptureEndModel *endModel) {
//        endView.model = endModel;
//    }];
    
    RNTSocketTool *socket = [RNTSocketTool shareInstance];
    // 退出房间
    RNTSocketSummary *summary = [[RNTSocketSummary alloc] init];
    summary.requestKey= @"002-006";
    
    NSMutableDictionary *para= [NSMutableDictionary dictionary];
    para[@"userId"] = [RNTUserManager sharedManager].user.userId;
    para[@"showId"] = showId;
    [socket writeMessage:para andSummary:summary withTag:302];
    
    socket.socketResponse = ^(NSData *data, RNTSocketSummary *sum) {
        
        if ([sum.requestKey isEqualToString:summary.requestKey]) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
            RNTCaptureEndModel *endModel = [RNTCaptureEndModel mj_objectWithKeyValues:dict];
            endView.model = endModel;
            RNTLog(@"*********关闭直播*********");
        }
    };
    
    [view addSubview:endView];
    
    [UIView animateWithDuration:0.3 animations:^{
        endView.alpha = 1;
    }];
}


- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor whiteColor];
        [self setupSubviews];
    }
    return self;
}

- (void)setupSubviews
{
    // 上部分
    UIView *containerView = [[UIView alloc] init];
//    containerView.backgroundColor = kRandomColor;
    [self addSubview:containerView];
    if (iPhone4 || iPhone5) {
        [containerView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.equalTo(self);
            make.top.mas_equalTo(30);
            make.height.mas_equalTo(450);
        }];
    }else {
        [containerView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.equalTo(self);
            make.top.mas_equalTo(70);
            make.height.mas_equalTo(450);
        }];
    }
    
    
    // 头像
    UIImageView *iconView = [[UIImageView alloc] init];
    iconView.image = [UIImage imageNamed:@"PlaceholderIcon"];
    iconView.layer.cornerRadius = 40;
    iconView.layer.masksToBounds = YES;
    [containerView addSubview:iconView];
    self.iconView = iconView;
    [iconView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(containerView);
        make.centerX.mas_equalTo(containerView);
        make.size.mas_equalTo(CGSizeMake(80, 80));
    }];
    
    //昵称
    UILabel *nameView = [[UILabel alloc] init];
    nameView.font = [UIFont systemFontOfSize:16];
    nameView.lineBreakMode = NSLineBreakByTruncatingTail;
    nameView.textAlignment = NSTextAlignmentCenter;
    [nameView setFont:[UIFont fontWithName:@"Helvetica-Bold" size:16]];
    nameView.textColor = RNTColor_16(0x4a3c17);
    [nameView sizeToFit];
    [containerView addSubview:nameView];
    self.nameView = nameView;
    [nameView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(iconView.bottom).offset(10);
        make.left.right.equalTo(containerView);
//        make.centerX.mas_equalTo(iconView);
//        make.size.mas_equalTo(CGSizeMake(146, 20));
    }];
    
    //A豆view
    UIView *coinView = [[UIView alloc] init];
//    coinView.backgroundColor = RNTColor_16(0xffd200);
    coinView.backgroundColor = [UIColor clearColor];
    coinView.layer.cornerRadius = 72;
    coinView.clipsToBounds = YES;
    [containerView addSubview:coinView];
    [coinView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(144, 144));
        make.center.equalTo(containerView);
//        make.top.equalTo(nameView.bottom).offset(35);
    }];
    
    //A豆label
    UILabel *coinLab = [[UILabel alloc] init];
    coinLab.text = @"A豆";
    coinLab.font = [UIFont systemFontOfSize:16];
//    [coinLab sizeToFit];
//        coinLab.backgroundColor = kRandomColor;
    [coinView addSubview:coinLab];
    [coinLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(coinView);
        make.top.equalTo(coinView.top).offset(40);
    }];
    
    //A豆数
    UILabel *coinCountView= [[UILabel alloc] init];
    coinCountView.font = [UIFont systemFontOfSize:36];
    coinCountView.adjustsFontSizeToFitWidth = YES;
    coinCountView.textAlignment = NSTextAlignmentCenter;
//    [coinCountView sizeToFit];
    [coinView addSubview:coinCountView];
    self.coinCountView = coinCountView;
    [coinCountView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(coinView);
        make.top.mas_equalTo(coinLab.mas_bottom);
        make.width.mas_equalTo(130);
    }];
    
    
    //粉丝数
    UILabel *fansLabel = [[UILabel alloc] init];
    fansLabel.font = [UIFont systemFontOfSize:16];
    fansLabel.textColor = RNTColor_16(0x7f7f7f);
    fansLabel.text = @"粉丝";
    fansLabel.textAlignment = NSTextAlignmentCenter;
//    [fansLabel sizeToFit];
    [containerView addSubview:fansLabel];
    [fansLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(coinView.bottom).offset(40);
        make.left.equalTo(containerView.left);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];

    UILabel *fansCountView = [[UILabel alloc] init];
    fansCountView.font = [UIFont systemFontOfSize:24];
//        fansCountView.text = @"6969";
    fansCountView.textAlignment = NSTextAlignmentCenter;
//    [fansCountView sizeToFit];
    [containerView addSubview:fansCountView];
    self.fansCountView = fansCountView;
    [fansCountView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(fansLabel.bottom);
        make.left.equalTo(containerView);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];
    
    
    //观众数
    UILabel *audienceLabel = [[UILabel alloc] init];
    audienceLabel.font = [UIFont systemFontOfSize:16];
    audienceLabel.textColor = RNTColor_16(0x7f7f7f);
    audienceLabel.text = @"观众";
    audienceLabel.textAlignment = NSTextAlignmentCenter;
//    [audienceLabel sizeToFit];
    [self addSubview:audienceLabel];
    [audienceLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fansLabel.top);
        make.right.equalTo(containerView.right);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];
    
    
    UILabel *audienceCountView = [[UILabel alloc] init];
    audienceCountView.font = [UIFont systemFontOfSize:24];
    //    self.audienceCount.text = @"659";
    audienceCountView.textAlignment = NSTextAlignmentCenter;
//    [audienceCountView sizeToFit];
    [containerView addSubview:audienceCountView];
    self.audienceCountView = audienceCountView;
    [audienceCountView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(audienceLabel.bottom);
        make.right.equalTo(containerView.right);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];
    
    
    //直播时间
    UILabel *timeLabel = [[UILabel alloc] init];
    timeLabel.font = [UIFont systemFontOfSize:16];
    timeLabel.textColor = RNTColor_16(0x7f7f7f);
    timeLabel.text = @"直播时间";
    timeLabel.textAlignment = NSTextAlignmentCenter;
//    [timeLabel sizeToFit];
    [self addSubview:timeLabel];
    [timeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(fansCountView.bottom).offset(30);
        make.left.equalTo(containerView.left);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];

    UILabel *timeView = [[UILabel alloc] init];
    //    self.timeLab.backgroundColor = kRandomColor;
    timeView.textAlignment = NSTextAlignmentCenter;
    timeView.font = [UIFont systemFontOfSize:24];
    //    self.timeLab.text  = @"01:00:33";
//    [timeView sizeToFit];
    [containerView addSubview:timeView];
    self.timeView = timeView;
    [timeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(timeLabel.bottom);
        make.left.equalTo(containerView.left);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];
    
    //点赞数
    UILabel *zanLabel = [[UILabel alloc] init];
    zanLabel.font = [UIFont systemFontOfSize:16];
    zanLabel.textColor = RNTColor_16(0x7f7f7f);
    zanLabel.text = @"点赞";
//    [zanLabel sizeToFit];
    zanLabel.textAlignment = NSTextAlignmentCenter;
    [containerView addSubview:zanLabel];
    [zanLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(audienceCountView.bottom).offset(30);
        make.right.equalTo(containerView.right);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];
    
    
    UILabel *zanCountView = [[UILabel alloc] init];
    zanCountView.font = [UIFont systemFontOfSize:24];
    //    self.zanCount.text = @"685784";
    zanCountView.textAlignment = NSTextAlignmentCenter;
//    [zanCountViewsizeToFit];
    [containerView addSubview:zanCountView];
    self.zanCountView = zanCountView;
    [zanCountView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(zanLabel.bottom);
        make.right.equalTo(containerView.right);
        make.width.mas_equalTo(kScreenWidth * 0.5);
    }];
    
    
    //确定按钮
    UIButton *certainBtn = [[UIButton alloc] init];
    [certainBtn setTitle:@"确定" forState:UIControlStateNormal];
    certainBtn.backgroundColor = RNTColor_16(0xffd200);
    [certainBtn setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    certainBtn.titleLabel.font = [UIFont systemFontOfSize:18];
    certainBtn.layer.cornerRadius = 22;
    certainBtn.clipsToBounds = YES;
    [certainBtn addTarget:self action:@selector(certainClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:certainBtn];
    [certainBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(self).offset(-20);
        make.left.mas_equalTo(self).offset(8);
        make.right.mas_equalTo(self).offset(-8);
        make.height.mas_equalTo(44);
    }];
}



//赋值
- (void)setModel:(RNTCaptureEndModel *)model
{
    _model = model;
    NSString *time = model.length;
//    RNTLog(@"model time1 = %@", time);

    if ([time isEqualToString:@"00:00:00"]) {
//        RNTLog(@"model time2 = %@", tim   e);
        return;
    }
    
    
    if (model.user.profile.length) {
        [self.iconView sd_setImageWithURL:[NSURL URLWithString:model.user.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    }
    
    self.nameView.text = model.user.nickName;
    
    self.coinCountView.text = model.coins;
    
    self.fansCountView.text = model.fans;
    
    self.audienceCountView.text = model.memberCnt;
    
    RNTLog(@"model time3 = %@", time);

    self.zanCountView.text = model.support;
    
    self.timeView.text = time;
}


//按钮点击
-(void)certainClick
{
    if (self.ensureClickBlock) {
        self.ensureClickBlock();
    }
    [self removeFromSuperview];
}


@end
