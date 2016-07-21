//
//  RNTAboutController.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#define TextFont [UIFont systemFontOfSize:15]

@interface RNTAboutButton : UIButton
@property (nonatomic, strong) UILabel *leftLabel;
@property (nonatomic, strong) UILabel *rightLabel;
@end

@implementation RNTAboutButton

+ (instancetype)buttonWithLeftTitle:(NSString *)leftTitle rightTitle:(NSString *)rightTitle
{
    RNTAboutButton *btn = [[RNTAboutButton alloc] init];
    btn.leftLabel.text = leftTitle;
    btn.rightLabel.text = rightTitle;
    return btn;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupSubview];
        self.layer.cornerRadius = 22.0;
        self.layer.masksToBounds = YES;
        self.backgroundColor = [UIColor whiteColor];
    }
    return self;
}

- (void)setupSubview
{
    [self addSubview:self.leftLabel];
    [self addSubview:self.rightLabel];
    
    [self.leftLabel makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self);
        make.left.equalTo(20);
        make.right.equalTo(self).offset(-20);
    }];
    
    [self.rightLabel makeConstraints:^(MASConstraintMaker *make) {
        make.size.centerX.centerY.equalTo(self.leftLabel);
    }];
}

- (UILabel *)leftLabel
{
    if (_leftLabel == nil) {
        _leftLabel = [[UILabel alloc] init];
        _leftLabel.textAlignment = NSTextAlignmentLeft;
        _leftLabel.font = TextFont;
    }
    return _leftLabel;
}

- (UILabel *)rightLabel
{
    if (_rightLabel == nil) {
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.textAlignment = NSTextAlignmentRight;
        _rightLabel.font = TextFont;
    }
    return _rightLabel;
}

@end

#import "RNTAboutController.h"

@interface RNTAboutController ()
@property (nonatomic, strong) UILabel *bottomLabel;
@property (nonatomic, strong) UILabel *versionLabel;
@property (nonatomic, strong) UIImageView *logoView;
@property (nonatomic, strong) RNTAboutButton *websiteBtn;
@property (nonatomic, strong) RNTAboutButton *emailBtn;
@end

@implementation RNTAboutController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"关于";
    self.view.backgroundColor = RNTMainColor;
    
    [self setupSubview];
}

- (void)setupSubview
{
    [self.view addSubview:self.bottomLabel];
    [self.view addSubview:self.logoView];
    [self.view addSubview:self.versionLabel];
    [self.view addSubview:self.websiteBtn];
    [self.view addSubview:self.emailBtn];
    
    [self.logoView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.view).offset(56);
        make.size.equalTo(self.logoView.image.size);
        make.centerX.equalTo(self.view);
    }];
    
    [self.versionLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.top.equalTo(self.logoView.bottom).offset(12);
        make.height.equalTo(22);
    }];
    
    [self.bottomLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.bottom.equalTo(self.view).offset(-30);
        make.height.equalTo(26);
    }];
    
    [self.websiteBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(12);
        make.right.equalTo(self.view).offset(-12);
        make.bottom.equalTo(self.view.centerY).offset(-8);
        make.height.equalTo(44);
    }];
    
    [self.emailBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(self.websiteBtn);
        make.top.equalTo(self.view.centerY).offset(8);
    }];
}

- (void)btnClick:(RNTAboutButton *)btn
{
    NSString *str;
    if (btn == self.websiteBtn) {
        str = [@"http://" stringByAppendingString:btn.rightLabel.text];
    } else if (btn == self.emailBtn) {
        NSMutableString *mailUrl = [[NSMutableString alloc] init];
        [mailUrl appendFormat:@"mailto:%@", btn.rightLabel.text];
        
        str = [mailUrl stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    }
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:str]];
}

#pragma mark -
- (UILabel *)bottomLabel
{
    if (_bottomLabel == nil) {
        _bottomLabel = [[UILabel alloc] init];
        _bottomLabel.textAlignment = NSTextAlignmentCenter;
        _bottomLabel.textColor = RNTColor_16(0x614e00);
        _bottomLabel.text = @"微播网络技术（北京）有限公司";
        _bottomLabel.font = [UIFont systemFontOfSize:13];
    }
    return _bottomLabel;
}

- (UIImageView *)logoView
{
    if (_logoView == nil) {
        _logoView = [[UIImageView alloc] init];
        _logoView.image = [UIImage imageNamed:@"logo"];
    }
    return _logoView;
}

- (UILabel *)versionLabel
{
    if (_versionLabel == nil) {
        _versionLabel = [[UILabel alloc] init];
        _versionLabel.text = [@"V" stringByAppendingString:[[NSBundle mainBundle].infoDictionary objectForKey:@"CFBundleShortVersionString"]];
        _versionLabel.font = [UIFont systemFontOfSize:22];
        _versionLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _versionLabel;
}

- (RNTAboutButton *)websiteBtn
{
    if (_websiteBtn == nil) {
        _websiteBtn = [RNTAboutButton buttonWithLeftTitle:@"官网" rightTitle:@"www.17ace.cn"];
        [_websiteBtn addTarget:self action:@selector(btnClick:) forControlEvents:UIControlEventTouchUpInside];
        _websiteBtn.userInteractionEnabled = [RNTUserManager canShowSilverExchange];
    }
    return _websiteBtn;
}

- (RNTAboutButton *)emailBtn
{
    if (_emailBtn == nil) {
        _emailBtn = [RNTAboutButton buttonWithLeftTitle:@"反馈邮箱" rightTitle:@"kefu@rednovo.com"];
        [_emailBtn addTarget:self action:@selector(btnClick:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _emailBtn;
}
@end
