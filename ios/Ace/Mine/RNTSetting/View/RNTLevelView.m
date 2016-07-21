//
//  RNTLevelView.m
//  Ace
//
//  Created by 周兵 on 16/3/19.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTLevelView.h"

@interface RNTLevelView ()
@property (nonatomic, strong) UIButton *levelBtn; //等级图标
@property (nonatomic, strong) UILabel *EXPLabel; //经验值
@end

@implementation RNTLevelView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupSubViews];
    }
    return self;
}

- (void)setupSubViews
{
    [self addSubview:self.levelBtn];
    [self addSubview:self.EXPLabel];
    
    UILabel *titleLabel1 = [[UILabel alloc] init];
    titleLabel1.text = @"等级：";
    titleLabel1.textColor = RNTColor_16(0x333333);
    titleLabel1.font = [UIFont systemFontOfSize:15];
    [self addSubview:titleLabel1];
    
    UILabel *titleLabel2 = [[UILabel alloc] init];
    titleLabel2.text = @"记得多发言哦，听说可以升级";
    titleLabel2.textColor = RNTColor_16(0x7f7f7f);
    titleLabel2.font = [UIFont systemFontOfSize:11];
    [self addSubview:titleLabel2];
    
    UILabel *titleLabel3 = [[UILabel alloc] init];
    titleLabel3.text = @"消费能让升级更快哦";
    titleLabel3.textColor = RNTColor_16(0x7f7f7f);
    titleLabel3.font = [UIFont systemFontOfSize:11];
    [self addSubview:titleLabel3];
    
    NSDictionary *att1 = @{NSFontAttributeName : titleLabel1.font};
    CGSize titleLabel1S = [titleLabel1.text sizeWithAttributes:att1];
    [titleLabel1 makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(10);
        make.top.equalTo(15);
        make.size.equalTo(titleLabel1S);
    }];
    
    [self.levelBtn makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(self.levelBtn.currentBackgroundImage.size);
        make.centerY.equalTo(self);
        make.left.equalTo(titleLabel1.right).offset(16);
    }];
    
    [self.EXPLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.levelBtn.right).offset(22);
        make.top.equalTo(self.levelBtn);
        make.right.equalTo(self);
        make.height.equalTo(15);
    }];

    NSDictionary *att2 = @{NSFontAttributeName : titleLabel2.font};
    CGSize titleLabel2S = [titleLabel2.text sizeWithAttributes:att2];
    [titleLabel2 makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.EXPLabel);
        make.top.equalTo(self.EXPLabel.bottom).offset(9);
        make.size.equalTo(titleLabel2S);
    }];
    
    NSDictionary *att3 = @{NSFontAttributeName : titleLabel3.font};
    CGSize titleLabel3S = [titleLabel3.text sizeWithAttributes:att3];
    [titleLabel3 makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(titleLabel2);
        make.top.equalTo(titleLabel2.bottom).offset(5);
        make.size.equalTo(titleLabel3S);
    }];
}

- (void)setItem:(RNTSettingLevelItem *)item
{
    _item = item;
    
    NSString *EXPStr = item.EXP;
    NSMutableAttributedString *EXPAtt = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"距离升级还需 %@ 经验", EXPStr]];
    [EXPAtt addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x333333) range:NSMakeRange(0, 6)];
    [EXPAtt addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0xfa0000) range:NSMakeRange(6, EXPAtt.length - 8)];
    [EXPAtt addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x333333) range:NSMakeRange(EXPAtt.length - 2, 2)];
    [EXPAtt addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:15] range:NSMakeRange(0, EXPAtt.length)];
    self.EXPLabel.attributedText = EXPAtt;
    
    NSString *level = item.level;
    NSMutableAttributedString *levelAtt = [[NSMutableAttributedString alloc] initWithString:[NSString stringWithFormat:@"LV%@", level]];
    [levelAtt addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0xffffff) range:NSMakeRange(0, levelAtt.length)];
    [levelAtt addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:11] range:NSMakeRange(0, 2)];
    [levelAtt addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:18] range:NSMakeRange(2, levelAtt.length - 2)];
    [self.levelBtn setAttributedTitle:levelAtt forState:UIControlStateNormal];
}

#pragma mark -
- (UIButton *)levelBtn
{
    if (_levelBtn == nil) {
        _levelBtn = [[UIButton alloc] init];
        [_levelBtn setBackgroundImage:[UIImage imageNamed:@"MemberCenter_level"] forState:UIControlStateNormal];
        _levelBtn.enabled = NO;
    }
    return _levelBtn;
}

- (UILabel *)EXPLabel
{
    if (_EXPLabel == nil) {
        _EXPLabel = [[UILabel alloc] init];
    }
    return _EXPLabel;
}
@end
