//
//  RNTSearchTableViewCell.m
//  Ace
//
//  Created by 靳峰 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSearchTableViewCell.h"
#import "UIImageView+WebCache.h"

@interface RNTSearchTableViewCell ()
//头像
@property(nonatomic,strong) UIImageView *iconImg;
//昵称
@property(nonatomic,strong) UILabel *nameLab;
////等级
//@property(nonatomic,strong) UIImageView *levelImg;
////直播
//@property(nonatomic,strong) UIImageView *liveImg;
//个性签名
@property(nonatomic,strong) UILabel  *deslabel;
@end

@implementation RNTSearchTableViewCell

+ (instancetype)cellWithTableView:(UITableView *)tableView
{
    static NSString * ID = @"fansCell";
    
    RNTSearchTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    if (cell == nil) {
        cell = [[RNTSearchTableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:ID];
    }
    return cell;
}

-(instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    [self setSubviews];
    return self;
}

#pragma mark - 数据赋值
- (void)setUser:(RNTUser *)user
{
    _user = user;
    self.nameLab.text = user.nickName;
    if (user.profile.length > 0) {
        [self.iconImg sd_setImageWithURL:[NSURL URLWithString:user.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    } else {
        self.iconImg.image = [UIImage imageNamed:@"PlaceholderIcon"];
    }
    self.deslabel.text = user.signature;
    if (user.signature.length == 0 || user.signature == nil) {
        self.deslabel.text = @"这个家伙的签名私奔了";
    }
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    //头像
    self.iconImg = [[UIImageView alloc] init];
    [self.contentView addSubview:self.iconImg];
    self.iconImg.layer.cornerRadius = 22;
    self.iconImg.clipsToBounds = YES;
    self.iconImg.backgroundColor = kRandomColor;
    [self.iconImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(44, 44));
        make.centerY.mas_equalTo(self);
        make.left.mas_equalTo(self).offset(8);
    }];
    
    //名字
    self.nameLab = [[UILabel alloc] init];
    self.nameLab.font = [UIFont systemFontOfSize:16];
    self.nameLab.textColor = RNTColor_16(0x19191a);
    self.nameLab.lineBreakMode = NSLineBreakByTruncatingTail;
    [self.contentView addSubview:self.nameLab];
    [self.nameLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.iconImg.right).offset(15);
        make.size.mas_equalTo(CGSizeMake(130, 20));
        make.centerY.mas_equalTo(self.iconImg);
    }];
    
    //签名
    UILabel *deslabel = [[UILabel alloc] init];
    deslabel.font = [UIFont systemFontOfSize:12];
    deslabel.textColor = RNTColor_16(0xa7a7a8);
    deslabel.textAlignment = NSTextAlignmentRight;
    [self addSubview:deslabel];
    [deslabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self).offset(-8);
        make.bottom.mas_equalTo(self).offset(-5);
        make.size.mas_equalTo(CGSizeMake(122,14.5));
    }];
    self.deslabel = deslabel;
}

#pragma mark - 取消高亮状态
-(void)setSelected:(BOOL)selected animated:(BOOL)animated

{
    
}

-(void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated

{
    
}

@end
