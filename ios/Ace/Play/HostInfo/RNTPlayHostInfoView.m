//
//  RNTPlayHostInfoView.m
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayHostInfoView.h"
#import "Masonry.h"
#import "RNTPlayShowInfo.h"
#import "UIImageView+WebCache.h"
#import "RNTUserManager.h"
#import "RNTPlayNetWorkTool.h"

@interface RNTPlayHostInfoView()
@property (nonatomic, weak) UIImageView *iconView;
@property (nonatomic, weak) UILabel *nameLabel;
@property (nonatomic, weak) UIButton *countButton;
@property (nonatomic, weak) UIButton *supportButton;
@property (nonatomic, weak) UIButton *attentionBtn;

@property (nonatomic, strong) NSTimer *timer;
@end

static CGFloat attentionWidth = 35;

@implementation RNTPlayHostInfoView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        [self setupSubViews];
    }
    return self;
}


#pragma mark - 布局子控件
- (void)setupSubViews
{
    // yellow view
    UIView* yellowView = [[UIView alloc] init];
    [self addSubview:yellowView];
    yellowView.backgroundColor = [UIColor yellowColor];
    [yellowView makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.left.equalTo(self);
        make.width.equalTo(3);
    }];
    
    // backView
    UIImageView* backView = [[UIImageView alloc] init];
    [self addSubview:backView];
    backView.image = [UIImage imageWithName:@"play_corner_back"];
    [backView makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(yellowView.right);
        make.top.bottom.right.equalTo(self);
    }];
    
//    backView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.4];
//    UIBezierPath* path = [UIBezierPath bezierPathWithRoundedRect:CGRectMake(0, 0, HostInfoViewWidth - 3, 50) byRoundingCorners:UIRectCornerBottomRight | UIRectCornerTopRight cornerRadii:CGSizeMake(50, 50)];
//    CAShapeLayer* shapeLayer = [CAShapeLayer layer];
//    shapeLayer.path = path.CGPath;
//    backView.layer.mask = shapeLayer;

    // icon
    UIImageView* iconView = [[UIImageView alloc] init];
    [self addSubview:iconView];
    self.iconView = iconView;
    iconView.image = [UIImage imageNamed:@"PlaceholderIcon"];
    iconView.backgroundColor = [UIColor whiteColor];
    CGFloat iconWH = 40;
    iconView.layer.cornerRadius = iconWH * 0.5;
    iconView.clipsToBounds = YES;
    [iconView makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(iconWH, iconWH));
        make.left.equalTo(self).offset(8);
        make.centerY.equalTo(self);
    }];
    
    // nameLabel
    UILabel* nameLabel = [[UILabel alloc] init];
    [self addSubview:nameLabel];
    [nameLabel sizeToFit];
    self.nameLabel = nameLabel;
    nameLabel.text = @"";
    nameLabel.textColor = [UIColor whiteColor];
    nameLabel.font = [UIFont systemFontOfSize:13];
    [nameLabel makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(iconView);
        make.bottom.equalTo(iconView.centerY);
        make.left.equalTo(iconView.right).offset(5);
        make.right.equalTo(self).offset(-10).priority(500);
    }];
    
    // count
    UIButton* countButton = [[UIButton alloc] init];
    countButton.userInteractionEnabled = NO;
    [countButton setContentHuggingPriority:1000 forAxis:UILayoutConstraintAxisHorizontal];
    [self addSubview:countButton];
    self.countButton = countButton;
    [countButton setImage:[UIImage imageNamed:@"play_hostInfo_userCount"] forState:UIControlStateNormal];
    countButton.titleLabel.font = [UIFont systemFontOfSize:10];
    [countButton setTitle:@"0" forState:UIControlStateNormal];
    [countButton sizeToFit];
    [countButton makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(nameLabel);
        make.top.equalTo(nameLabel.bottom).offset(6);
    }];

    
    // line
    UIView* lineView = [[UIView alloc] init];
    [self addSubview:lineView];
    lineView.backgroundColor = [UIColor colorWithRed:1 green:1 blue:1 alpha:0.6];
    [lineView makeConstraints:^(MASConstraintMaker *make) {
        make.height.centerY.equalTo(countButton);
        make.left.equalTo(countButton.right).offset(6);
        make.width.equalTo(1);
    }];
    
    // support
    UIButton* supportButton = [[UIButton alloc] init];
    [supportButton setContentHuggingPriority:1000 forAxis:UILayoutConstraintAxisHorizontal];
    supportButton.userInteractionEnabled = NO;
    [self addSubview:supportButton];
    self.supportButton = supportButton;
    [supportButton setImage:[UIImage imageNamed:@"play_hostInfo_support"] forState:UIControlStateNormal];
    supportButton.titleLabel.font = [UIFont systemFontOfSize:10];
    [supportButton setTitle:@"0" forState:UIControlStateNormal];
    [supportButton sizeToFit];
    [supportButton makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(lineView);
        make.left.equalTo(lineView.right).offset(6);
        make.right.lessThanOrEqualTo(self).offset(-10);
    }];

}

- (void)setInfo:(RNTPlayShowInfo *)info
{
    RNTLog(@"%@", info.userId);
    _info = info;
    if (info.profile.length) {
        [self.iconView sd_setImageWithURL:[NSURL URLWithString:info.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    }
    if (info.nickName.length) {
        NSString* nickName = info.nickName;
        if (nickName.length > 8) {
            nickName = [NSString stringWithFormat:@"%@...", [nickName substringToIndex:7]];
        }
        self.nameLabel.text = nickName;
    }
    [self.countButton setTitle:info.memberCount forState:UIControlStateNormal];
    [self.supportButton setTitle:info.totalLikeCount forState:UIControlStateNormal];
    
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!self.attentionBtn && info.userId.integerValue != -1 && manager.logged && manager.user.userId.integerValue != info.userId.integerValue) {
        [RNTPlayNetWorkTool getUserInfoWithUserID:self.info.userId getSuccess:^(NSDictionary *dict, BOOL attentionState) {
            if (!attentionState) {
                [self addAttentionBtn];
            }
        }];
    }
}

- (void)addAttentionBtn{
    // attentionButton
    UIButton* attentionBtn = [[UIButton alloc] init];
    [self addSubview:attentionBtn];
    self.attentionBtn = attentionBtn;
    [attentionBtn addTarget:self action:@selector(attentionBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    [attentionBtn setImage:[UIImage imageNamed:@"play_attention_image"] forState:UIControlStateNormal];
    [attentionBtn makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self);
        make.right.equalTo(self).offset(-10);
        make.width.equalTo(attentionWidth);
    }];
    
    [self.supportButton updateConstraints:^(MASConstraintMaker *make) {
        make.right.lessThanOrEqualTo(self).offset(-10 - attentionWidth);
    }];
    [self.nameLabel updateConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self).offset(-10 - attentionWidth).priority(500);
    }];
    
    [self fireTimer];
}

- (void)fireTimer{
    NSTimer* timer = [NSTimer scheduledTimerWithTimeInterval:60 target:self selector:@selector(hiddenAttentionBtn) userInfo:nil repeats:NO];
    [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    self.timer = timer;
}


- (void)hiddenAttentionBtn{
    [self.timer invalidate];
    self.timer = nil;
    
    [self.supportButton updateConstraints:^(MASConstraintMaker *make) {
        make.right.lessThanOrEqualTo(self).offset(-10);
    }];
    [self.nameLabel updateConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self).offset(-10).priority(500);
    }];
    
    self.attentionBtn.hidden = YES;
}

- (void)attentionBtnClicked:(UIButton *)btn{
    [self hiddenAttentionBtn];
    RNTUserManager* manager = [RNTUserManager sharedManager];
    [RNTPlayNetWorkTool followUserWithCurrentUserID:manager.user.userId followedUserID:self.info.userId getSuccess:^(BOOL status) {
        if (status) {
            [MBProgressHUD showSuccess:@"关注成功"];
        }else{
            [MBProgressHUD showError:@"关注失败"];
        }
    }];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    if ([self.delegate respondsToSelector:@selector(hostInfoViewClick:withModel:)]) {
        [self.delegate hostInfoViewClick:self withModel:nil];
    }
}

- (void)dealloc
{
    RNTLog(@"--");
}

@end
