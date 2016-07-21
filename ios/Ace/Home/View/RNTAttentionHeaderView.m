//
//  RNTAttentionHeaderView.m
//  Ace
//
//  Created by 靳峰 on 16/3/1.
//  Copyright © 2016年 RNT. All rights reserved.
//

#define kHeadColor RNTColor_16(0x7f7f7f)

#import "RNTAttentionHeaderView.h"

@interface RNTAttentionHeaderViewBtn : UIButton

@end


@interface RNTAttentionHeaderView ()<UIAlertViewDelegate>

@property(nonatomic,strong) RNTAttentionHeaderViewBtn *tipBtn;

@end



@implementation RNTAttentionHeaderView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setSubviews];
    }
    return self;
}

#pragma mark - 按钮点击
-(void)tipBtnLogin{
    if ([self.tipStr isEqualToString:@"还没登录呦!"]) {
        UIAlertView *loginAlert = [[UIAlertView alloc] initWithTitle:@"请登录" message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"登录", nil];
        loginAlert.delegate = self;
        [loginAlert show];
    }
}

#pragma mark - set方法
-(void)setTipStr:(NSString *)tipStr
{
    _tipStr = tipStr;
    
    [self.tipBtn setTitle:self.tipStr forState:UIControlStateNormal];
    if ([self.tipStr isEqualToString:@"还没登录呦!"]) {
        
        [self.tipBtn setImage:[UIImage imageNamed:@"home_Atteion_login"] forState:UIControlStateNormal];
        
    }else if([self.tipStr isEqualToString:@"这么精彩,还不关注?"]){
        
        [self.tipBtn setImage:[UIImage imageNamed:@"home_Atteion_order"] forState:UIControlStateNormal];
        
    }else{
        
        [self.tipBtn setImage:[UIImage imageNamed:@"home_Atteion_hot"] forState:UIControlStateNormal];
    }
}

#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1) {
        if (self.loginBlock) {
            self.loginBlock();
        }
    }
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    self.backgroundColor = RNTColor_16(0xfbfbfb);
    
    //提示
    RNTAttentionHeaderViewBtn *tipBtn = [[RNTAttentionHeaderViewBtn alloc] initWithFrame:self.bounds];
    [tipBtn setTitleColor:RNTColor_16(0xc3c3c3) forState:UIControlStateNormal];
    [self addSubview:tipBtn];
    [tipBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.bottom.right.mas_equalTo(self);
    }];
    self.tipBtn  = tipBtn;
    [self.tipBtn addTarget:self action:@selector(tipBtnLogin) forControlEvents:UIControlEventTouchUpInside];
    
    //黄色
    UIView *yellowView = [[UIView alloc] init];
    [self addSubview:yellowView];
    yellowView.backgroundColor = RNTMainColor;
    [yellowView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(4, 15));
        make.left.mas_equalTo(self).offset(8);
        make.bottom.mas_equalTo(self).offset(-8);
    }];
    
    //精彩推荐
    UILabel *recommendLab = [[UILabel alloc] init];
    recommendLab.font = [UIFont systemFontOfSize:15];
    recommendLab.text = @"精彩推荐";
    recommendLab.textColor = RNTColor_16(0x8a8a8a);
    [self addSubview:recommendLab];
    [recommendLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(yellowView).offset(8);
        make.centerY.mas_equalTo(yellowView);
    }];
    
    //下边横线
    UIView *bottomLine = [[UIView alloc] init];
    bottomLine.backgroundColor = RNTSeparatorColor;
    [self addSubview:bottomLine];
    [bottomLine mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.mas_equalTo(self);
        make.height.mas_equalTo(0.5);
    }];
}
@end

@implementation RNTAttentionHeaderViewBtn
-(void)layoutSubviews
{
    [super layoutSubviews];
    
    [self.imageView sizeToFit];
    
    self.imageView.frame = CGRectMake(0, 0, Width(self.imageView), Height(self.imageView));
    self.imageView.center = CGPointMake(self.center.x, self.center.y-10-Height(self.imageView)*0.5);
    
    [self.titleLabel sizeToFit];
    self.titleLabel.font = [UIFont systemFontOfSize:13];
    self.titleLabel.frame = CGRectMake(0, 0, 115, 40);
    self.titleLabel.numberOfLines = 0 ;
    self.titleLabel.center = CGPointMake(self.center.x, self.center.y+20);
    self.titleLabel.textAlignment = NSTextAlignmentCenter;

}
@end

