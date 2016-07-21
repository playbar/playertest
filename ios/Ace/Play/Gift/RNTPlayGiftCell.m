//
//  RNTPlayGiftCell.m
//  Ace
//
//  Created by 于传峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayGiftCell.h"
#import "UIButton+WebCache.h"
#import "RNTPlayGiftModel.h"
#import "RNTPlayNetWorkTool.h"

@interface RNTPlayGiftCell()

@property (nonatomic, weak) id target;
@property (nonatomic, assign) SEL action;

@property (nonatomic, weak) UIButton *iconView;
@property (nonatomic, weak) UILabel *nameLabel;
@property (nonatomic, weak) UIButton *coinLabel;
@end

@implementation RNTPlayGiftCell

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        [self setupSubViews];
        
        self.layer.cornerRadius = 3;
        self.clipsToBounds = YES;
        self.layer.borderColor = RNTColor_16(0xfff100).CGColor;
    }
    return self;
}

- (void)setupSubViews
{
    // iconView
    UIButton* iconView = [[UIButton alloc] init];
    iconView.backgroundColor = [UIColor clearColor];
//    iconView.layer.borderColor = RNTColor_16(0xfff100).CGColor;
    iconView.userInteractionEnabled = NO;
    [self addSubview:iconView];
    self.iconView = iconView;
//    iconView.layer.cornerRadius = 3;
//    iconView.clipsToBounds = YES;
    [iconView makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.equalTo(self).insets(UIEdgeInsetsMake(3, 10, 0, 10));
        make.height.equalTo(iconView.width);
    }];
    
    
    // coinLabel
    UIButton* coinLabel = [[UIButton alloc] init];
    coinLabel.userInteractionEnabled = NO;
    [self addSubview:coinLabel];
    self.coinLabel = coinLabel;
    //    coinLabel.font = [UIFont systemFontOfSize:10];
    //    coinLabel.textColor = [UIColor whiteColor];
    //    coinLabel.highlightedTextColor = RNTColor_16(0xfff100);
    coinLabel.titleLabel.font = [UIFont systemFontOfSize:15];
//    [coinLabel setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [coinLabel setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateNormal];
    [coinLabel sizeToFit];
    [coinLabel setTitle:@"0" forState:UIControlStateNormal];
    [coinLabel setImage:[UIImage imageNamed:@"play_gift_coin"] forState:UIControlStateNormal];
    //    coinLabel.textAlignment = NSTextAlignmentRight;
    //    coinLabel.text = @"0";
    [coinLabel makeConstraints:^(MASConstraintMaker *make) {
        make.right.left.equalTo(self);
        make.bottom.equalTo(self).offset(-5);
    }];
    
    // nameLabel
    UILabel* nameLabel = [[UILabel alloc] init];
    [self addSubview:nameLabel];
    self.nameLabel = nameLabel;
    nameLabel.font = [UIFont systemFontOfSize:12];
    nameLabel.textColor = [UIColor whiteColor];
    nameLabel.highlightedTextColor = RNTColor_16(0xfff100);
    [nameLabel sizeToFit];
    nameLabel.textAlignment = NSTextAlignmentCenter;
    nameLabel.text = @"";
    //    [nameLabel setContentCompressionResistancePriority:200 forAxis:UILayoutConstraintAxisHorizontal];
    [nameLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self);
        make.bottom.equalTo(coinLabel.top).offset(0);
    }];

}

- (void)setSelected:(BOOL)selected
{
    if (_selected == selected) {
        return;
    }
    _selected = selected;
    if (selected) {
        self.layer.borderWidth = 1.5;
//        self.nameLabel.highlighted = YES;
//        self.coinLabel.highlighted = YES;
    }else{
        self.layer.borderWidth = 0;
//        self.nameLabel.highlighted = NO;
//        self.coinLabel.highlighted = NO;
    }
}

- (void)setModel:(RNTPlayGiftModel *)model
{
    _model = model;
//    [self.iconView sd_setImageWithURL:[NSURL URLWithString:model.pic] forState:UIControlStateNormal placeholderImage:nil];
    [self.iconView setImage:[RNTPlayNetWorkTool giftImageWithGiftID:model.ID] forState:UIControlStateNormal];
    self.nameLabel.text = model.name;
    NSString* text = [NSString stringWithFormat:@" %@", model.transformPrice];
    [self.coinLabel setTitle:text forState:UIControlStateNormal];
}


- (void)addTarget:(id)target action:(SEL)action
{
    self.target = target;
    self.action = action;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    if (self.target) {
        if ([self.target respondsToSelector:self.action]) {
            SuppressPerformSelectorLeakWarning(
                                               [self.target performSelector:self.action withObject:self];);
        }
    }
}

- (void)dealloc
{
    RNTLog(@"--");
}

@end
