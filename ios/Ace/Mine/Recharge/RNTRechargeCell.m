//
//  RNTRechargeCell.m
//  Ace
//
//  Created by 周兵 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTRechargeCell.h"

#define TextColor RNTColor_16(0xfe5700)

@interface RNTRechargeCell ()
@property (nonatomic, strong) UIView *bottomSeparatorLine;
@property (nonatomic, strong) UILabel *goldCoinLabel;
@property (nonatomic, strong) UIButton *priceBtn;
@property (nonatomic, strong) UIView *verticalLine;
@end

@implementation RNTRechargeCell

+ (instancetype)cellWithTableView:(UITableView *)tableView
{
    // 1.定义一个标识
    static NSString *ID = @"RechargeCell";
    
    // 2.去缓存池中取出可循环利用的cell
    RNTRechargeCell *cell = [tableView dequeueReusableCellWithIdentifier:ID];
    
    // 3.如果缓存中没有可循环利用的cell
    if (cell == nil) {
        cell = [[RNTRechargeCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:ID];
    }
    return cell;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // 添加所有需要显示的子控件
        [self setupSubViews];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    return self;
}

- (void)setupSubViews
{
    [self.contentView addSubview:self.goldCoinLabel];
    [self.contentView addSubview:self.topSeparatorLine];
    [self.contentView addSubview:self.bottomSeparatorLine];
    [self.contentView addSubview:self.verticalLine];
    [self.contentView addSubview:self.priceBtn];
    
    UILabel *label = [[UILabel alloc] init];
    label.text = @"币";
    label.textColor = TextColor;
    [label sizeToFit];
    label.font = [UIFont systemFontOfSize:11];
    [self.contentView addSubview:label];
    
    [self.verticalLine makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(0.5, 44));
        make.left.equalTo(112);
        make.centerY.equalTo(self);
    }];
    
    [label makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.verticalLine.left).offset(-12);
        make.bottom.equalTo(self.bottom).offset(-16);
    }];
    
    [self.goldCoinLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(14);
        make.right.equalTo(label.left);
        make.height.equalTo(30);
        make.centerY.equalTo(self);
    }];
    
    [self.priceBtn makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(80, 32));
        make.right.equalTo(self).offset(-18);
        make.centerY.equalTo(self);
    }];
    
    [self.topSeparatorLine makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.top.equalTo(self.contentView);
        make.height.equalTo(0.5);
    }];
    
    [self.bottomSeparatorLine makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.contentView);
        make.height.equalTo(0.5);
    }];
}

- (void)priceBtnClick
{
    if (self.priceBtnClickBlock) {
        self.priceBtnClickBlock();
    }
}

- (void)setProduct:(SKProduct *)product
{
    _product = product;
    
    NSNumberFormatter *numberFormatter = [[NSNumberFormatter alloc]init];
    [numberFormatter setFormatterBehavior:NSNumberFormatterBehavior10_4];
    [numberFormatter setNumberStyle:NSNumberFormatterCurrencyStyle];
    [numberFormatter setLocale:product.priceLocale];

    NSString *price = [[numberFormatter stringFromNumber:product.price] stringByReplacingOccurrencesOfString:@".00" withString:@""];
    [self.priceBtn setTitle:price forState:UIControlStateNormal];

    self.goldCoinLabel.text = [product.localizedTitle stringByReplacingOccurrencesOfString:@"金币" withString:@""];
}

#pragma mark -
- (UILabel *)goldCoinLabel
{
    if (_goldCoinLabel == nil) {
        _goldCoinLabel = [[UILabel alloc] init];
        _goldCoinLabel.font = [UIFont systemFontOfSize:30];
        _goldCoinLabel.textColor = TextColor;
        _goldCoinLabel.textAlignment = NSTextAlignmentCenter;
        _goldCoinLabel.text = @"1234";
    }
    return _goldCoinLabel;
}

- (UIButton *)priceBtn
{
    if (_priceBtn == nil) {
        _priceBtn = [[UIButton alloc] init];
        _priceBtn.layer.cornerRadius = 3.0;
        _priceBtn.layer.masksToBounds = YES;
        [_priceBtn setBackgroundImage:[UIImage imageWithColor:RNTMainColor] forState:UIControlStateNormal];
        [_priceBtn setBackgroundImage:[UIImage imageWithColor:RNTMainHighlightColor] forState:UIControlStateHighlighted];
        [_priceBtn setTitleColor:RNTColor_16(0x4a3c17) forState:UIControlStateNormal];
        _priceBtn.titleLabel.font = [UIFont systemFontOfSize:18];
        [_priceBtn addTarget:self action:@selector(priceBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _priceBtn;
}

- (UIView *)topSeparatorLine
{
    if (_topSeparatorLine == nil) {
        _topSeparatorLine = [[UIView alloc] init];
        _topSeparatorLine.backgroundColor = RNTSeparatorColor;
        _topSeparatorLine.hidden = YES;
    }
    return _topSeparatorLine;
}

- (UIView *)bottomSeparatorLine
{
    if (_bottomSeparatorLine == nil) {
        _bottomSeparatorLine = [[UIView alloc] init];
        _bottomSeparatorLine.backgroundColor = RNTSeparatorColor;
    }
    return _bottomSeparatorLine;
}

- (UIView *)verticalLine
{
    if (_verticalLine == nil) {
        _verticalLine = [[UIView alloc] init];
        _verticalLine.backgroundColor = RNTSeparatorColor;
    }
    return _verticalLine;
}

@end
