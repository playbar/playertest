//
//  RNTCaptureStartAniView.m
//  Ace
//
//  Created by Ranger on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureStartAniView.h"

@interface RNTCaptureStartAniView ()
@property (nonatomic, weak) UIImageView *readyView;
@property (nonatomic, weak) UIImageView *goView;

@end

@implementation RNTCaptureStartAniView

+ (void)playAnimation
{
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    
    RNTCaptureStartAniView *startView = [[RNTCaptureStartAniView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    [window addSubview:startView];
    [startView playAnimation];
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = RNTAlphaColor_16(0x000000, 0.3);
        
        [self setupSubview];
    }
    return self;
}

- (void)setupSubview
{
    UIImageView *readyView  = [[UIImageView alloc] init];
    readyView.image = [UIImage imageNamed:@"capture_Ready"];
    readyView.alpha = 0;
    [self addSubview:readyView];
    self.readyView = readyView;
    
    
    UIImageView *goView = [[UIImageView alloc] init];
    goView.image = [UIImage imageNamed:@"capture_GO"];
    goView.alpha = 0;
    [self addSubview:goView];
    self.goView = goView;
    
    [readyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(readyView.image.size);
        make.center.equalTo(self);
    }];
    
    [goView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(goView.image.size);
        make.center.equalTo(self);
    }];
    
}


- (void)playAnimation
{
    [UIView animateWithDuration:1 animations:^{
        self.readyView.alpha = 1;
        self.readyView.transform = CGAffineTransformMakeScale(1.5, 1.5);
    } completion:^(BOOL finished) {
        [self.readyView removeFromSuperview];
        
        [UIView animateWithDuration:1 animations:^{
            self.goView.alpha = 1;
            self.goView.transform = CGAffineTransformMakeScale(1.5, 1.5);
        } completion:^(BOOL finished) {
            [self.goView removeFromSuperview];
            [self removeFromSuperview];
        }];
    }];
}


@end
