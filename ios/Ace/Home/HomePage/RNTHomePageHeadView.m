//
//  RNTHomePageHeadView.m
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHomePageHeadView.h"
#import "RNTOrderFansBtn.h"

@implementation RNTHomePageHeadView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setSubviews];
    }
    return self;
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    //背景图片
    UIImageView *imageBg = [[UIImageView alloc] init];
    imageBg.backgroundColor = kRandomColor;
    [self addSubview:imageBg];
    [imageBg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.mas_equalTo(self);
        make.height.mas_equalTo(299);
    }];
    imageBg.userInteractionEnabled = YES;
    
    //返回按钮
    UIButton *backBtn = [[UIButton alloc] init];
    backBtn.backgroundColor = RNTAlphaColor_16(0xffffff, 0.5);
    [backBtn setImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateNormal];
    backBtn.layer.cornerRadius = 14;
    backBtn.clipsToBounds = YES;
    [imageBg addSubview:backBtn];
    [backBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(imageBg).offset(8);
        make.top.mas_equalTo(imageBg).offset(30);
        make.size.mas_equalTo(CGSizeMake(28, 28));
    }];
    self.backBtn = backBtn;
    
    //头像
    UIImageView *iconImg = [[UIImageView alloc] init];
//    iconImg.backgroundColor = kRandomColor;
    iconImg.layer.cornerRadius =36;
    iconImg.layer.borderWidth = 3;
    iconImg.layer.borderColor = RNTColor_16(0xffffff).CGColor;
    iconImg.clipsToBounds = YES;
    [imageBg addSubview:iconImg];
    [iconImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(imageBg).offset(60);
        make.centerX.mas_equalTo(imageBg);
        make.size.mas_equalTo(CGSizeMake(72, 72));
    }];
    
    //名字
    UILabel *nameLab = [[UILabel alloc] init];
    nameLab.text = @"尼姑庵的老方丈";
    nameLab.lineBreakMode = NSLineBreakByTruncatingTail;
    nameLab.textColor = RNTColor_16(0xffffff);
    nameLab.font = [UIFont systemFontOfSize:16];
    nameLab.textAlignment = NSTextAlignmentCenter;
    [imageBg addSubview:nameLab];
    [nameLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(iconImg.mas_bottom).offset(8);
        make.centerX.mas_equalTo(imageBg);
        make.size.mas_equalTo(CGSizeMake(128, 20));
    }];
    
    //签名
    UILabel *desLab = [[UILabel alloc] init];
    desLab.textColor = RNTColor_16(0x7f7f7f);
    desLab.text = @"生而抱歉";
    desLab.font = [UIFont systemFontOfSize:12];
    [desLab sizeToFit];
    [imageBg addSubview:desLab];
    [desLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(nameLab.mas_bottom).offset(9.5);
        make.centerX.mas_equalTo(imageBg);
    }];
    
    
    //位置
    UILabel *locationLab = [[UILabel alloc] init];
    locationLab.text = @"北京 - 六环";
    [locationLab sizeToFit];
    locationLab.textColor = RNTColor_16(0x7f7f7f);
    locationLab.font = [UIFont systemFontOfSize:12];
    [imageBg addSubview:locationLab];
    [locationLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(desLab.mas_bottom).offset(6);
        make.right.mas_equalTo(imageBg.mas_centerX).offset(-6);
    }];
    
    //定位图标
    UIImageView *locationImg = [[UIImageView alloc] init];
    locationImg.image = [UIImage imageNamed:@"homePage_location"];
    [locationImg sizeToFit];
    [imageBg addSubview:locationImg];
    [locationImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(locationLab.left).offset(-6);
        make.top.mas_equalTo(desLab.mas_bottom).offset(6);
    }];
    
    //ID
    UILabel *IDLab = [[UILabel alloc] init];
    IDLab.text = @"ID:666666";
    [IDLab sizeToFit];
    IDLab.textColor = RNTColor_16(0x7f7f7f);
    IDLab.font = [UIFont systemFontOfSize:12];
    [imageBg addSubview:IDLab];
    [IDLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(desLab.mas_bottom).offset(6);
        make.left.mas_equalTo(imageBg.mas_centerX).offset(6);
    }];
    
    //黑色背景
    UIView *blackView = [[UIView alloc] init];
    blackView.backgroundColor = [UIColor colorWithWhite:0 alpha:0.3];
    [imageBg addSubview:blackView];
    [blackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.bottom.right.mas_equalTo(imageBg);
        make.height.mas_equalTo(67);
    }];
    
    //订阅按钮
    RNTOrderFansBtn *orderBtn = [[RNTOrderFansBtn alloc] init];
    [blackView addSubview:orderBtn];
    [orderBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.bottom.top.mas_equalTo(blackView);
        make.width.mas_equalTo(kScreenWidth * 0.5-0.5);
    }];
    self.orderBtn = orderBtn;
    
    
    //粉丝按钮
    RNTOrderFansBtn *fansBtn = [[RNTOrderFansBtn alloc] init];
    [blackView addSubview:fansBtn];
    [fansBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.bottom.top.mas_equalTo(blackView);
        make.width.mas_equalTo(kScreenWidth * 0.5-0.5);
    }];
    self.fansBtn = fansBtn;
    
    //竖线
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = RNTColor_16(0xe6e6eb);
    [blackView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(1, 36));
        make.center.mas_equalTo(blackView);
    }];
}
@end
