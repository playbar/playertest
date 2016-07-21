//
//  RNTHomeSlider.m
//  选择器
//
//  Created by 靳峰 on 16/5/6.
//  Copyright © 2016年 靳峰. All rights reserved.
//

#import "RNTHomeSlider.h"
#import "UIImage+RNT.h"

@interface RNTHomeSlider ()

@property(nonatomic,strong) UIView *blakView;
@property(nonatomic,strong) UILabel *hotLabForegroud;
@end

@implementation RNTHomeSlider

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setSubviews];
    }
    return self;
}

#pragma mark - set方法
-(void)setSliderOffset:(CGFloat)sliderOffset
{
    _sliderOffset = sliderOffset;
    
    CGRect sliderFrame = CGRectMake(sliderOffset, 0, 76, 28);
    CGRect foregroudotFrame = CGRectMake(-sliderOffset, 0, 152, 28);
    
    self.blakView.frame = sliderFrame;
    self.hotLabForegroud.frame = foregroudotFrame;
}

#pragma mark - 按钮点击
-(void)segmentClick:(UIButton *)btn
{
    self.isClick = YES;
    
    if ([self.delegate respondsToSelector:@selector(sliderBtnClickToChangePage:)]) {
        [self.delegate performSelector:@selector(sliderBtnClickToChangePage:) withObject:btn];
    }
    
    CGRect sliderFrame = CGRectMake(76*btn.tag, 0, 76, 28);
    CGRect foregroudotFrame = CGRectMake(-76*btn.tag, 0, 152, 28);

    [UIImageView animateWithDuration:0.25 animations:^{
        self.blakView.frame = sliderFrame;
        self.hotLabForegroud.frame = foregroudotFrame;
    }completion:^(BOOL finished) {
        self.isClick = NO;
    }];
}
#pragma mark - 布局子控件
-(void)setSubviews
{
    //底部字体
    UILabel *hotLabBottom = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 152, 28)];
    [self addSubview:hotLabBottom];
    hotLabBottom.text = @"     热门         关注";
//    hotLabBottom.font = [UIFont systemFontOfSize:15];
    hotLabBottom.textColor = RNTColor_16(0x4a3c17);
    
    //滑块
    UIImageView *blackView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 76, 28)];
//    blackView.image = [UIImage  imageWithColor:RNTColor_16(0x4a3c17)];
    blackView.image = [UIImage  imageWithColor:[UIColor blackColor]];
    blackView.layer.cornerRadius = 14;
    blackView.clipsToBounds = YES;
    self.blakView = blackView;
    [self addSubview:blackView];
    
    //顶部字体
    UILabel *hotLabForegroud = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 152, 28)];
    self.hotLabForegroud = hotLabForegroud;
//    hotLabForegroud.font = [UIFont systemFontOfSize:15];
    [self.blakView addSubview:hotLabForegroud];
    hotLabForegroud.text =  @"     热门         关注";
    hotLabForegroud.textColor = RNTMainColor;
    
    UIButton *hotBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 76, 28)];
    hotBtn.backgroundColor = [UIColor clearColor];
    [hotBtn addTarget:self action:@selector(segmentClick:) forControlEvents:UIControlEventTouchUpInside];
    hotBtn.tag = 0;
    [self addSubview:hotBtn];
    
    UIButton *recomandBtn = [[UIButton alloc] initWithFrame:CGRectMake(76, 0, 76, 28)];
    recomandBtn.backgroundColor = [UIColor clearColor];
    [recomandBtn addTarget:self action:@selector(segmentClick:) forControlEvents:UIControlEventTouchUpInside];
    recomandBtn.tag = 1;
    [self addSubview:recomandBtn];
}
@end
