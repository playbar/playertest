//
//  RNTSettingCell.m
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingCell.h"
#import "RNTLevelView.h"
#import "UIImageView+WebCache.h"

#define TextLabelFont [UIFont systemFontOfSize:16]
#define RightLabelTextColor RNTColor_16(0x7f7f7f)

@interface RNTSettingCell ()

@property (nonatomic, strong) UIImageView *arrowView; //右侧箭头
@property (nonatomic, strong)UILabel *buttonLabel; //金币余额显示Label
@property (nonatomic, strong)UIButton *rightBtn; //充值按钮
@property (nonatomic, strong)UILabel *rightLabel;
@property (nonatomic, strong)UIImageView *photoImage; //头像
@property (nonatomic,strong)UILabel *rightArrowLabel;
@property (nonatomic,strong)UILabel *sinatureLabel;  //签名Label

@property (nonatomic, strong) RNTLevelView *levelView; //进度条

@end

@implementation RNTSettingCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.textLabel.font = TextLabelFont;
        self.textLabel.textColor = RNTCellTextColor;
        self.selectedBackgroundView = [[UIView alloc] initWithFrame:self.frame];
        self.selectedBackgroundView.backgroundColor = RNTColor_16(0xe6e6eb);
    }
    return self;
}

- (void)setItem:(RNTSettingItem *)item
{
    _item = item;
    self.textLabel.text = _item.tilte;
    if ([item isKindOfClass:[RNTSettingArrowItem class]]) {
        self.arrowView.hidden = NO;
        self.levelView.hidden = YES;
        self.buttonLabel.hidden = YES;
        self.rightLabel.hidden = YES;
        self.photoImage.hidden = YES;
        self.rightArrowLabel.hidden = YES;
        self.sinatureLabel.hidden = YES;
        
        self.accessoryView = self.arrowView;
        self.imageView.image = [UIImage imageNamed:item.icon];
    } else if ([item isKindOfClass:[RNTSettingRightButtonAndLabelItem class]]) {
        self.arrowView.hidden = YES;
        self.levelView.hidden = YES;
        self.buttonLabel.hidden = NO;
        self.rightLabel.hidden = YES;
        self.photoImage.hidden = YES;
        self.rightArrowLabel.hidden = YES;
        self.sinatureLabel.hidden = YES;
        
        self.accessoryView = nil;
        
        RNTSettingRightButtonAndLabelItem *newItem = (RNTSettingRightButtonAndLabelItem *)item;
        self.buttonLabel.text = newItem.labelText;
        [self.rightBtn setTitle:newItem.rightBtnTitle forState:UIControlStateNormal];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    
    } else if ([item isKindOfClass:[RNTSettingLevelItem class]]) {
        self.arrowView.hidden = YES;
        self.levelView.hidden = NO;
        self.buttonLabel.hidden = YES;
        self.rightLabel.hidden = YES;
        self.photoImage.hidden = YES;
        self.rightArrowLabel.hidden = YES;
        self.sinatureLabel.hidden = YES;
        
        self.accessoryView = nil;
        
        RNTSettingLevelItem *newItem = (RNTSettingLevelItem *)item;
        self.levelView.item = newItem;
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    } else if ([_item isKindOfClass:[RNTSettingRightLabelItem class]]) {
        self.arrowView.hidden = YES;
        self.levelView.hidden = YES;
        self.buttonLabel.hidden = YES;
        self.rightLabel.hidden = NO;
        self.photoImage.hidden = YES;
        self.rightArrowLabel.hidden = YES;
        self.sinatureLabel.hidden = YES;
        
        self.accessoryView = nil;
        
        RNTSettingRightLabelItem *newItem = (RNTSettingRightLabelItem *)item;
        self.rightLabel.text = newItem.labelText;
    } else if ([_item isKindOfClass:[RNTSettingPhotoItem class]]) {
        self.arrowView.hidden = NO;
        self.levelView.hidden = YES;
        self.buttonLabel.hidden = YES;
        self.rightLabel.hidden = YES;
        self.photoImage.hidden = NO;
        self.rightArrowLabel.hidden = YES;
        self.sinatureLabel.hidden = YES;
        
        self.accessoryView = self.arrowView;
        
        RNTSettingPhotoItem *newItem = (RNTSettingPhotoItem *)item;
        if (newItem.image) {
            self.photoImage.image = newItem.image;
        } else {
            if (newItem.imageUrl.length > 0) {
                NSURL *imageUrl = [NSURL URLWithString:newItem.imageUrl];
                [self.photoImage sd_setImageWithURL:imageUrl placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
            } else {
                self.photoImage.image = [UIImage imageNamed:@"PlaceholderIcon"];
            }
        }
    } else if ([_item isKindOfClass:[RNTSettingRightLabelAndArrowItem class]]) {
        self.arrowView.hidden = NO;
        self.levelView.hidden = YES;
        self.buttonLabel.hidden = YES;
        self.rightLabel.hidden = YES;
        self.photoImage.hidden = YES;
        self.rightArrowLabel.hidden = NO;
        self.sinatureLabel.hidden = YES;
        
        self.accessoryView = self.arrowView;
        self.imageView.image = [UIImage imageNamed:item.icon];
        
        //充值右边金币颜色和收益右边A豆颜色
        if ([self.textLabel.text isEqualToString:@"我的金币"] || [self.textLabel.text isEqualToString:@"我的收益"]) {
            self.rightArrowLabel.textColor = RNTColor_16(0xff3232);
        } else {
            self.rightArrowLabel.textColor = [UIColor grayColor];
        }
        
        RNTSettingRightLabelAndArrowItem *newItem = (RNTSettingRightLabelAndArrowItem *)item;
        self.rightArrowLabel.text = newItem.rightLabelText;
    } else if ([_item isKindOfClass:[RNTSettingSinatureItem class]]) {
        self.arrowView.hidden = NO;
        self.levelView.hidden = YES;
        self.buttonLabel.hidden = YES;
        self.rightLabel.hidden = YES;
        self.photoImage.hidden = YES;
        self.rightArrowLabel.hidden = YES;
        self.sinatureLabel.hidden = NO;
        
        self.accessoryView = self.arrowView;
        
        RNTSettingSinatureItem *newItem = (RNTSettingSinatureItem *)item;
        NSString *sinature = newItem.sinature;
        if (sinature.length > 0) {
            self.sinatureLabel.text = sinature;
            float labelH = [RNTSettingSinatureItem signatureHeight];
            
            //获取sinatureLabel行数
            CGFloat labelHeight = [self.sinatureLabel sizeThatFits:CGSizeMake(kScreenWidth - 110, MAXFLOAT)].height;
            NSNumber *count = @((labelHeight) / self.sinatureLabel.font.lineHeight);
            
            //设置对齐模式 1行时右对齐 大于1行时左对齐
            if (count.integerValue > 1) {
                self.sinatureLabel.textAlignment = NSTextAlignmentLeft;
            } else {
                self.sinatureLabel.textAlignment = NSTextAlignmentRight;
            }
            
            [self.sinatureLabel mas_updateConstraints:^(MASConstraintMaker *make) {
                make.width.equalTo(kScreenWidth - 130);
                make.right.equalTo(-5);
                make.centerY.equalTo(self.contentView);
                make.height.equalTo(labelH);
            }];

        } else {
            self.sinatureLabel.hidden = YES;
            self.rightArrowLabel.hidden = NO;
            self.rightArrowLabel.text = @"未填写";
        }
    }
}

#pragma mark - 点击事件
/**
 *  button的点击事件
 */
- (void)rightBtnClick:(UIButton *)btn
{
    RNTSettingRightButtonAndLabelItem *newItem = (RNTSettingRightButtonAndLabelItem *)self.item;
    if (newItem.handler) {
        newItem.handler(newItem.destVC);
    }
}

#pragma mark - 各控件
/**
 *  箭头图片
 */
- (UIImageView *)arrowView
{
    if (_arrowView == nil) {
        _arrowView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"CellArrow"]];
        [self.contentView addSubview:_arrowView];
    }
    return _arrowView;
}

- (UILabel *)buttonLabel
{
    if (_buttonLabel == nil) {
        _buttonLabel = [[UILabel alloc] init];
        _buttonLabel.textColor = RNTColor_16(0x7f7f7f);
        _buttonLabel.font = [UIFont systemFontOfSize:16];
        _buttonLabel.textAlignment = NSTextAlignmentRight;
        
        [self.contentView addSubview:_buttonLabel];
        
        UIView *lineView =[[UIView alloc] init];
        lineView.backgroundColor = [UIColor grayColor];
        lineView.alpha = 0.5;
        [_buttonLabel addSubview:lineView];
        
        [lineView makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self);
            make.height.equalTo(12);
            make.right.equalTo(_buttonLabel).offset(10);
            make.width.equalTo(0.5);
        }];
        
        [_buttonLabel makeConstraints:^(MASConstraintMaker *make) {
            make.bottom.top.equalTo(self);
            make.right.equalTo(self).offset(-70);
            make.width.equalTo(100);
        }];
    }
    return _buttonLabel;
}

- (UIButton *)rightBtn
{
    if (_rightBtn == nil) {
        _rightBtn = [[UIButton alloc] init];
        [_rightBtn setTitleColor:RNTColor_16(0xfa0000) forState:UIControlStateNormal];
        [self.contentView addSubview:_rightBtn];
        
        [_rightBtn makeConstraints:^(MASConstraintMaker *make) {
            make.right.top.bottom.equalTo(self);
            make.width.equalTo(60);
        }];
        
        [_rightBtn addTarget:self action:@selector(rightBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _rightBtn;
}

- (RNTLevelView *)levelView
{
    if (_levelView == nil) {
        _levelView = [[RNTLevelView alloc] init];
        _levelView.frame = self.bounds;
        [self.contentView addSubview:self.levelView];
    }
    return _levelView;
}

/**
 *  右侧的label 只有label 没有箭头
 */
- (UILabel *)rightLabel
{
    if (_rightLabel == nil) {
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.textAlignment = NSTextAlignmentRight;
        _rightLabel.textColor = RightLabelTextColor;
        _rightLabel.font = TextLabelFont;
        [self.contentView addSubview:_rightLabel];
        [_rightLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self).offset(-18);
            make.top.bottom.equalTo(self);
            make.width.equalTo(@200);
        }];
    }
    return _rightLabel;
}

- (UIImageView *)photoImage
{
    if (_photoImage == nil) {
        _photoImage = [[UIImageView alloc] init];
        _photoImage.layer.cornerRadius = 32;
        _photoImage.layer.masksToBounds = YES;
        [self.contentView addSubview:_photoImage];
        [_photoImage makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self);
            make.size.equalTo(CGSizeMake(64, 64));
            make.right.equalTo(self.contentView).offset(-10);
        }];
    }
    return _photoImage;
}

- (UILabel *)rightArrowLabel
{
    if (_rightArrowLabel == nil) {
        _rightArrowLabel = [[UILabel alloc] init];
        _rightArrowLabel.textColor = [UIColor grayColor];
        _rightArrowLabel.textAlignment = NSTextAlignmentRight;
        [self.contentView addSubview:_rightArrowLabel];
        [_rightArrowLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.right.equalTo(self).offset(-35);
            make.top.bottom.equalTo(self);
            make.width.equalTo(@200);
        }];
    }
    return _rightArrowLabel;
}

- (UILabel *)sinatureLabel
{
    if (!_sinatureLabel) {
        _sinatureLabel = [[UILabel alloc] init];
        _sinatureLabel.numberOfLines = 0;
        _sinatureLabel.font = TextLabelFont;
        _sinatureLabel.textColor = RightLabelTextColor;
        [self.contentView addSubview:_sinatureLabel];
    }
    return _sinatureLabel;
}
@end
