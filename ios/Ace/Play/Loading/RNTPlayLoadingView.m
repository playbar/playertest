//
//  RNTPlayLoadingView.m
//  Ace
//
//  Created by 于传峰 on 16/3/17.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayLoadingView.h"

@interface RNTPlayLoadingView()
@property (nonatomic, weak) UIImageView *animaView;
@property (nonatomic, weak) UIButton *bottomTitleButton;
@property (nonatomic, weak) UIImageView *leaveView;
@end

@implementation RNTPlayLoadingView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        self.image = [UIImage imageNamed:@"play_Gaussian_Blur_image"];
        [self setAnimationView];
        
        [self setLeaveView];
    }
    return self;
}

- (void)setAnimationView
{
    UIImageView* animaView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ani_00"]];
    [self addSubview:animaView];
    self.animaView = animaView;
    [animaView makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.centerY.equalTo(self).offset(-70);
    }];
    
    NSInteger count = 6;
    
    UIImage *animatedImage;
    
    NSMutableArray *images = [NSMutableArray array];
    
    NSTimeInterval duration = 0.0f;
    
    for (size_t i = 0; i < count; i++) {
        
        
        [images addObject:[UIImage imageNamed:[NSString stringWithFormat:@"ani_0%zd", i]]];
        
    }
    
    duration = (2.0f / 10.0f) * count;
    
    animatedImage = [UIImage animatedImageWithImages:images duration:duration];
    
    animaView.image = animatedImage;
    
    UIButton* bottomTitleButton = [[UIButton alloc] init];
    [self addSubview:bottomTitleButton];
    self.bottomTitleButton = bottomTitleButton;
    bottomTitleButton.userInteractionEnabled = NO;
    [bottomTitleButton setBackgroundImage:[UIImage imageNamed:@"play_loading_text_back"] forState:UIControlStateNormal];
    bottomTitleButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [bottomTitleButton setTitle:@"精彩内容 马上呈现" forState:UIControlStateNormal];
    bottomTitleButton.contentEdgeInsets = UIEdgeInsetsMake(0, 8, 0, 8);
    [bottomTitleButton sizeToFit];
    [bottomTitleButton makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.bottom.equalTo(self).offset(-100);
    }];
}

- (void)setLeaveView{
    UIImageView* leaveView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_host_leave"]];
    [self addSubview:leaveView];
    self.leaveView = leaveView;
    [leaveView makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(self.animaView);
    }];
    leaveView.hidden = YES;
}

- (void)setHidden:(BOOL)hidden
{
    [super setHidden:hidden];
    
    if (hidden) {
        [self.animaView stopAnimating];
    }else{
        [self.animaView startAnimating];
    }
}

- (void)showAnimationView
{
    [self.animaView startAnimating];
    self.animaView.hidden = NO;
    self.leaveView.hidden = YES;
    self.bottomTitleButton.hidden = NO;
}
- (void)hiddenAnimationView
{
    [self.animaView stopAnimating];
    self.animaView.hidden = YES;
    self.bottomTitleButton.hidden = NO;
}

- (void)showLeaveView{
    self.leaveView.hidden = NO;
    self.animaView.hidden = YES;
    self.bottomTitleButton.hidden = YES;
}

- (void)hiddenLeaveView{
    self.leaveView.hidden = YES;
}

//- (void)didMoveToSuperview{
//    [super didMoveToSuperview];
//    if (self.superview) {
//        [self showLeaveView];
//    }
//}

- (void)dealloc
{
    RNTLog(@"---");
}

@end
