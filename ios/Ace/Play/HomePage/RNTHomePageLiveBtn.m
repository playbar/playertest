//
//  RNTHomePageLiveBtn.m
//  Ace
//
//  Created by 靳峰 on 16/3/22.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHomePageLiveBtn.h"

@interface RNTHomePageLiveBtn ()

@property(nonatomic,strong) UIImageView *bigImg;
@property(nonatomic,strong) UIImageView *littleImg;
@property(nonatomic,strong) UILabel *liveLab;

@end

@implementation RNTHomePageLiveBtn

- (instancetype)init
{
    self = [super init];
    if (self) {
        [self setSubviews];
        [self sizeToFit];
    }
    return self;
}


-(void)setSubviews
{
    [self addSubview:self.bigImg];
    [self.bigImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self);
        make.centerX.mas_equalTo(self);
    }];
    
    [self addSubview:self.littleImg];
    [self.littleImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(self.bigImg);
    }];
    
    [self addSubview:self.liveLab];
    [self.liveLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.bigImg.mas_bottom).offset(4);
        make.centerX.mas_equalTo(self);
    }];
    self.liveLab.text = @"直播中...";
    self.liveLab.font = [UIFont systemFontOfSize:10];
    self.liveLab.textColor = RNTMainColor;
    
    [self fireAnimation];
}


//开启动画
-(void)fireAnimation
{
    [UIView animateWithDuration:0.5 animations:^{
        self.bigImg.alpha = 0.5;
        self.bigImg.transform = CGAffineTransformScale(self.bigImg.transform, 1.4, 1.4);
        self.littleImg.alpha = 0.5;
    } completion:^(BOOL finished) {
        [UIView animateWithDuration:0.5 animations:^{
            self.bigImg.alpha = 1;
            self.bigImg.transform = CGAffineTransformIdentity;
            self.littleImg.alpha = 1;
        }];
    }];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self fireAnimation];
    });
}

#pragma mark - 懒加载
-(UIImageView *)bigImg
{
    if (!_bigImg) {
        _bigImg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"homePage_bigImg"] highlightedImage:nil];
    }
    return _bigImg;
}

-(UIImageView *)littleImg
{
    if (!_littleImg) {
        _littleImg = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"homePage_littleImg"] highlightedImage:nil];
    }
    return _littleImg;
}

-(UILabel *)liveLab
{
    if (!_liveLab) {
        _liveLab = [[UILabel alloc] init];
        _liveLab.text = @"直播中...";
        [_liveLab sizeToFit];
    }
    return _liveLab;
}

-(void)dealloc
{
    RNTLog(@"---");
}

@end
