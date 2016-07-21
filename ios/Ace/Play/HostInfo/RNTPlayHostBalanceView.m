//
//  RNTPlayHostBalanceView.m
//  Ace
//
//  Created by 于传峰 on 16/5/19.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayHostBalanceView.h"

@interface RNTPlayHostBalanceView()
@property (nonatomic, weak) UILabel *baLabel;
@end

@implementation RNTPlayHostBalanceView

- (instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        [self setupSubViews];
    }
    return self;
}

- (void)setupSubViews{
    UIImageView* backView = [[UIImageView alloc] init];
    backView.image = [UIImage imageNamed:@"play_hostBalance_back"];
    [self addSubview:backView];
    [backView makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
    
    UILabel* titleLabel = [[UILabel alloc] init];
    [self addSubview:titleLabel];
    [titleLabel sizeToFit];
    titleLabel.text = @" A豆 ";
    titleLabel.font = [UIFont systemFontOfSize:15];
    titleLabel.textColor = RNTColor_16(0xffd200);
    [titleLabel makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.left.equalTo(self);
    }];
    
    UILabel* baLabel = [[UILabel alloc] init];
    [self addSubview:baLabel];
    [baLabel sizeToFit];
    self.baLabel = baLabel;
    baLabel.text = @"0";
    baLabel.font = [UIFont systemFontOfSize:15];
    baLabel.textColor = [UIColor whiteColor];
    [baLabel makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self);
        make.left.equalTo(titleLabel.right);
        make.right.equalTo(self).offset(-10);
    }];
}

- (void)setBalance:(NSString *)balance{
    if (!balance) {
        return;
    }
    _balance = balance;
    self.baLabel.text = [NSString stringWithFormat:@"%@", balance];
}

@end
