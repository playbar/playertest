//
//  RNTPlayGiftSendButton.m
//  Ace
//
//  Created by 于传峰 on 16/3/19.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayGiftSendButton.h"

@interface RNTPlayGiftSendButton()
@property (nonatomic, weak) UIImageView *gifImage;
@property (nonatomic, weak) UIButton *centerButton;
@property (nonatomic, weak) UILabel *countLabel;
@end



@implementation RNTPlayGiftSendButton

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = [UIColor clearColor];
        self.hidden = YES;
        [self setupSubViews];
    }
    return self;
}

- (void)setupSubViews
{
    UIImageView* gifImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_caromSend_5"]];
    [self addSubview:gifImage];
    self.gifImage = gifImage;
    [gifImage makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(self);
    }];
    
    UIButton* centerButton = [[UIButton alloc] init];
    [self addSubview:centerButton];
    [centerButton setImage:[UIImage imageNamed:@"play_caromSend_center_nm"] forState:UIControlStateNormal];
    [centerButton setImage:[UIImage imageNamed:@"play_caromSend_center_hl"] forState:UIControlStateHighlighted];
    centerButton.userInteractionEnabled = NO;
    self.centerButton = centerButton;
    [centerButton makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
    
    UILabel* countLabel = [[UILabel alloc] init];
    [self addSubview:countLabel];
    countLabel.font = [UIFont boldSystemFontOfSize:60];
    countLabel.textColor = RNTColor_16(0x9ca2b7);
    [countLabel sizeToFit];
    self.countLabel = countLabel;
    [countLabel makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(self);
    }];
}

- (void)setCaromCount:(NSUInteger)caromCount
{
    _caromCount = caromCount;
    
    RNTLog(@"caromcount %zd", caromCount);
}

- (void)setLeftTime:(NSUInteger)leftTime
{
    _leftTime = leftTime;
    RNTLog(@"leftTime = %zd", leftTime);
    NSString* imageName = [NSString stringWithFormat:@"play_caromSend_%zd", leftTime];
    self.countLabel.text = [NSString stringWithFormat:@"%zd", leftTime];
    self.gifImage.image = [UIImage imageNamed:imageName];
    if (leftTime == 0) {
        self.hidden = YES;
    }else{
        self.hidden = NO;
    }
}

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    self.centerButton.highlighted = highlighted;
}

@end
