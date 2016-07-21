//
//  RNTHomeTableViewCell.m
//  Ace
//
//  Created by 靳峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//




#import "RNTHomeTableViewCell.h"
#import "UIImageView+WebCache.h"
#import "UIImage+RNT.h"

#define kWhiteH (68 *  kScreenWidth / 375)
#define iconImgW (kWhiteH - 16)

@interface RNTHomeTableViewCell ()
//头像
@property(nonatomic,strong) UIImageView *iconImg;
//名字
@property(nonatomic,strong) UILabel *nameLab;
//人数定位
@property(nonatomic,strong) UILabel *countLab;
//直播状态
@property(nonatomic,strong) UIImageView *liveImg;
//封面
@property(nonatomic,strong) UIImageView *pageImg;
//标题
@property(nonatomic,strong) UILabel *titleLab;

@end

@implementation RNTHomeTableViewCell

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    [self setSubviews];
    return self;
}
#pragma mark - 数据赋值
-(void)setShowModel:(RNTShowListModel *)showModel
{
    _showModel = showModel;
    
    if (showModel.title.length>0 && showModel.title != nil) {
        
        self.titleLab.text = showModel.title;
    }else{
        [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
            make.height.mas_equalTo(0);
        }];
    }
    if (showModel.position.length == 0) {
        showModel.position = @"地球的背面";
    }
    showModel.position = [showModel.position stringByReplacingOccurrencesOfString:@"/" withString:@"-"];
    self.countLab.text = [NSString stringWithFormat:@"人数 : %@ / %@",showModel.memberCnt,showModel.position];
}

-(void)setUserModel:(RNTUser *)userModel
{
    _userModel = userModel;
    if (userModel.profile.length != 0 || userModel.profile == nil) {
        
        [self.iconImg sd_setImageWithURL:[NSURL URLWithString:userModel.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    }else{
        self.iconImg.image = [UIImage imageNamed:@"PlaceholderIcon"];
    }
    
    [self.pageImg sd_setImageWithURL:[NSURL URLWithString:userModel.showImg] placeholderImage:[UIImage imageNamed:@"home_cell_placeholder"]];
    
    NSString *name = userModel.nickName;
    if (name.length>7) {
        name= [name substringToIndex:7];
        self.nameLab.text = [NSString stringWithFormat:@"%@..",name];
    }else{
        self.nameLab.text = userModel.nickName;
    }
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    //分割线
    UIView *separatorLine = [[UIView alloc] init];
    [self.contentView addSubview:separatorLine];
    separatorLine.backgroundColor = RNTSeparatorColor;
    [separatorLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.mas_equalTo(self.contentView);
        make.height.mas_equalTo(8);
    }];
    
    //白色背景
    UIView *whiteBg = [[UIView alloc] init];
    [self.contentView addSubview:whiteBg];
    [whiteBg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(separatorLine);
        make.top.mas_equalTo(separatorLine.mas_bottom);
        make.size.mas_equalTo(CGSizeMake(kScreenWidth, kWhiteH));
    }];
    //    whiteBg.backgroundColor = [UIColor redColor];
    
    //头像
    self.iconImg = [[UIImageView alloc] init];
    [self.contentView addSubview:self.iconImg];
    self.iconImg.layer.cornerRadius = iconImgW*0.5;
    self.iconImg.clipsToBounds = YES;
    [self.iconImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(iconImgW, iconImgW));
        make.left.mas_equalTo(separatorLine).offset(8);
        make.top.mas_equalTo(separatorLine.mas_bottom).offset(8);
    }];
    
    //昵称
    self.nameLab = [[UILabel alloc] init];
    //    self.nameLab.backgroundColor = [UIColor redColor];
    [self.contentView addSubview:self.nameLab];
    self.nameLab.lineBreakMode = NSLineBreakByTruncatingTail;
    self.nameLab.font = [UIFont systemFontOfSize:15];
    self.nameLab.textColor = RNTColor_16(0x7b7362);
    [self.nameLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.iconImg.mas_right).offset(8);
        make.top.mas_equalTo(separatorLine.mas_bottom).offset(15*kScreenWidth/375);
    }];
    
    
    //人数及定位
    self.countLab =[[UILabel alloc] init];
    [self.contentView addSubview:self.countLab];
    self.countLab.font =[UIFont systemFontOfSize:12];
    self.countLab.textColor = RNTColor_16(0x4a3c17);
    [self.countLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.iconImg.mas_right).offset(8);
        make.top.mas_equalTo(self.iconImg.mas_centerY).offset(8);
    }];
    
    
    //封面
    self.pageImg = [[UIImageView alloc] init];
    [self.contentView addSubview:self.pageImg];
    self.pageImg.backgroundColor = RNTColor_16(0xf0f0f0);
    self.pageImg.image = [UIImage imageNamed:@"home_cell_placeholder"];
    [self.pageImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.iconImg.mas_bottom).offset(8);
        make.size.mas_equalTo(CGSizeMake(kScreenWidth,kScreenWidth));
        make.left.right.mas_equalTo(self.contentView);
    }];
    
    //直播状态
    self.liveImg =[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"home_live"]];
    [self.liveImg sizeToFit];
    [ self.pageImg addSubview:self.liveImg];
    [self.liveImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.pageImg).offset(8);
        make.right.mas_equalTo(self.pageImg).offset(-8);
        make.size.mas_equalTo(CGSizeMake(32, 16));
    }];
    
    //标题
    self.titleLab = [[UILabel alloc] init];
    [self.pageImg addSubview:self.titleLab];
    self.titleLab.textColor = RNTColor_16(0x7f7f7f);
    self.titleLab.lineBreakMode = NSLineBreakByTruncatingTail;
    self.titleLab.font = [UIFont systemFontOfSize:15];
    [self.titleLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.pageImg.mas_bottom);
        make.left.mas_equalTo(self.contentView).offset(8);
        make.bottom.mas_equalTo(self.contentView);
        make.right.mas_equalTo(self.contentView);
    }];
}

-(void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    
}

-(void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{ 
    
}
@end
