//
//  RNTMineHeaderView.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#define IconWidth 79
#define BottomBtnHeight 59
#define TextFont [UIFont systemFontOfSize:16]
#define IDFont [UIFont systemFontOfSize:12]

#define BottomToolTextCorol RNTColor_16(0x19191a)
#define IDTextCorol RNTColor_16(0xa0731d)
#define NickNameTextCorol RNTColor_16(0x6b4008)
#define LoginBtnColor RNTColor_16(0x6b4008)

@interface RNTMineHeaderBottomBtn : UIButton
@property (nonatomic, strong) UILabel *numLabel;
@end

@implementation RNTMineHeaderBottomBtn
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setTitleEdgeInsets:UIEdgeInsetsMake(BottomBtnHeight * 0.5, 0, 0, 0)];
        [self setupSubview];
    }
    return self;
}

- (void)setupSubview
{
    [self addSubview:self.numLabel];
    
    [self.numLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.equalTo(self);
        make.bottom.equalTo(self.titleLabel.top);
    }];
}

- (UILabel *)numLabel
{
    if (_numLabel == nil) {
        _numLabel = [[UILabel alloc] init];
        _numLabel.font = TextFont;
        _numLabel.text = @"0";
        _numLabel.textAlignment = NSTextAlignmentCenter;
    }
    return _numLabel;
}
@end

#import "RNTMineHeaderView.h"
#import "UIImage+RNT.h"
#import "UIButton+WebCache.h"
#import "RNTUser.h"

@interface RNTMineHeaderView ()

@property (nonatomic, strong) RNTMineHeaderBottomBtn *focusBtn;
@property (nonatomic, strong) RNTMineHeaderBottomBtn *fansBtn;
//@property (nonatomic, strong) UIButton *historyBtn;

@property (nonatomic, strong) UIButton *iconBtn;
@property (nonatomic, strong) UILabel *nickNameLabel;
//@property (nonatomic, strong) UIImageView *levelImageView;
@property (nonatomic, strong) UILabel *IDLabel;
@property (nonatomic, strong) UIButton *editBtn;

@property (nonatomic, strong) UIButton *loginBtn;
@property (nonatomic, strong) UIButton *registerBtn;
@property (nonatomic, strong) UIView *topView;//上部分黄色
@property (nonatomic, strong) UIImageView *bottomView;//下部分弧形黄色

@end

@implementation RNTMineHeaderView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupSubviews];
    }
    return self;
}

- (void)setupSubviews
{
    [self addSubview:self.bottomView];
    [self addSubview:self.topView];
    [self addSubview:self.focusBtn];
    [self addSubview:self.fansBtn];
//    [self addSubview:self.historyBtn];
    [self addSubview:self.iconBtn];
    [self addSubview:self.nickNameLabel];
//    [self addSubview:self.levelImageView];
    [self addSubview:self.IDLabel];
    [self addSubview:self.editBtn];
    [self addSubview:self.loginBtn];
    [self addSubview:self.registerBtn];
    
    //分割线
    UIView *leftLine = [[UIView alloc] init];
    leftLine.backgroundColor = RNTSeparatorColor;
    [self addSubview:leftLine];
    
//    UIView *rightLine = [[UIView alloc] init];
//    rightLine.backgroundColor = RNTSeparatorColor;
//    [self addSubview:rightLine];
    
    UIView *bottomLine = [[UIView alloc] init];
    bottomLine.backgroundColor = RNTSeparatorColor;
    [self addSubview:bottomLine];
    
    [bottomLine makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self);
        make.height.equalTo(0.5);
    }];
    
    [self.focusBtn makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.left.equalTo(self);
        make.height.equalTo(BottomBtnHeight);
        make.width.equalTo(self.mj_w / 2);
    }];
    
    [leftLine makeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(1);
        make.height.equalTo(37);
        make.centerX.equalTo(self.focusBtn.right);
        make.centerY.equalTo(self.focusBtn);
    }];
    
    [self.fansBtn makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.left.equalTo(self.focusBtn.right);
        make.height.equalTo(self.focusBtn);
        make.width.equalTo(self.focusBtn);
    }];
    
//    [rightLine makeConstraints:^(MASConstraintMaker *make) {
//        make.width.height.centerY.equalTo(leftLine);
//        make.centerX.equalTo(self.fansBtn.right);
//    }];
    
//    [self.historyBtn makeConstraints:^(MASConstraintMaker *make) {
//        make.bottom.equalTo(self);
//        make.left.equalTo(self.fansBtn.right);
//        make.right.equalTo(self);
//        make.height.equalTo(self.focusBtn);
//    }];
    
    [self.IDLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self);
        make.bottom.equalTo(self.focusBtn.top).offset(-24);
        make.height.equalTo(12);
    }];
    
    [self.iconBtn makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(IconWidth, IconWidth));
        make.centerX.equalTo(self);
        make.bottom.equalTo(self.IDLabel.top).offset(-40);
    }];
    
    [self.editBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.iconBtn.right).offset(-16);
        make.bottom.equalTo(self.iconBtn);
        make.size.equalTo(self.editBtn.currentImage.size);
    }];
    
    [self.loginBtn makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(80, 32));
        make.bottom.equalTo(self.focusBtn.top).offset(-20);
        make.right.equalTo(self.centerX).offset(-8);
    }];
    
    [self.registerBtn makeConstraints:^(MASConstraintMaker *make) {
        make.size.bottom.equalTo(self.loginBtn);
        make.left.equalTo(self.centerX).offset(8);
    }];
    
    [self.bottomView makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.iconBtn.centerY);
        make.centerX.equalTo(self);
    }];
    
    [self.topView makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.equalTo(self);
        make.bottom.equalTo(self.bottomView.top);
    }];
}

#pragma mark - 点击方法
- (void)focusBtnClick
{
    if (self.focusBtnClickBlock) {
        self.focusBtnClickBlock();
    }
}

- (void)fansBtnClick
{
    if (self.fansBtnClickBlock) {
        self.fansBtnClickBlock();
    }
}

//- (void)historyBtnClick
//{
//    if (self.historyBtnClickBlock) {
//        self.historyBtnClickBlock();
//    }
//}

- (void)iconBtnClick
{
    if (self.editBtnClickBlock) {
        self.editBtnClickBlock();
    }
}

- (void)editBtnClick
{
    if (self.editBtnClickBlock) {
        self.editBtnClickBlock();
    }
}

- (void)registerBtnClick
{
    if (self.registerBtnClickBlock) {
        self.registerBtnClickBlock();
    }
}

- (void)loginBtnClick
{
    if (self.loginBtnClickBlock) {
        self.loginBtnClickBlock();
    }
}

- (void)setIsLogged:(BOOL)isLogged
{
    self.loginBtn.hidden = isLogged;
    self.registerBtn.hidden = isLogged;
    self.nickNameLabel.hidden = !isLogged;
    self.IDLabel.hidden = !isLogged;
//    self.levelImageView.hidden = !isLogged;
    if (isLogged) {
        RNTUser *user = [RNTUserManager sharedManager].user;
        [self updateFrameWithUser:user];
    } else {
        [self.iconBtn setBackgroundImage:[UIImage imageNamed:@"PlaceholderIcon"] forState:UIControlStateNormal];
        self.fansBtn.numLabel.text = @"0";
        self.focusBtn.numLabel.text = @"0";
    }
}

- (void)setUser:(RNTUser *)user
{
    _user = user;
    [self updateFrameWithUser:user];
}

#pragma mark - 
//更新显示
- (void)updateFrameWithUser:(RNTUser *)user
{
    if (user.fansCnt) {
        self.fansBtn.numLabel.text = user.fansCnt;
    }
    
    if (user.subscribeCnt) {
        self.focusBtn.numLabel.text = user.subscribeCnt;
    }
    
    if (user.profile.length > 0) {
        [self.iconBtn sd_setBackgroundImageWithURL:[NSURL URLWithString:user.profile]  forState:UIControlStateNormal placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    } else {
        [self.iconBtn setBackgroundImage:[UIImage imageNamed:@"PlaceholderIcon"] forState:UIControlStateNormal];
    }
    
    //避免userId为空时崩溃
    self.nickNameLabel.text = user.nickName;
    NSString *IDLabelText = @"ID:";
    if (user.userId) {
        IDLabelText = [IDLabelText stringByAppendingString:user.userId];
    }
    self.IDLabel.text = IDLabelText;
    
    //注释代码等到等级开放会用
//    UIImage *levelImage = [UIImage imageNamed:[NSString stringWithFormat:@"anchor_level_%@", user.rank]];
//    self.levelImageView.image = levelImage;
    
//    CGSize imageS = levelImage.size;
    CGSize size = [self nickNameSizeWithText:user.nickName];
    
    [self.nickNameLabel updateConstraints:^(MASConstraintMaker *make) {
//        make.centerX.equalTo(self).offset(-(imageS.width * 0.5));
        make.centerX.equalTo(self);
        make.bottom.equalTo(self.IDLabel.top).offset(-8);
        make.size.equalTo(size);
    }];
    
//    [self.levelImageView updateConstraints:^(MASConstraintMaker *make) {
//        make.size.equalTo(imageS);
//        make.centerY.equalTo(self.nickNameLabel);
//        make.left.equalTo(self.nickNameLabel.right).offset(10);
//    }];
}

- (CGSize)nickNameSizeWithText:(NSString *)nickName
{
    NSDictionary *att = @{NSFontAttributeName : TextFont};
    return [[nickName stringByAppendingString:@" "] sizeWithAttributes:att];
}

#pragma mark - 懒加载
- (RNTMineHeaderBottomBtn *)focusBtn
{
    if (_focusBtn == nil) {
        _focusBtn = [[RNTMineHeaderBottomBtn alloc] init];
        _focusBtn.backgroundColor = [UIColor whiteColor];
        [_focusBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _focusBtn.titleLabel.font = TextFont;
        [_focusBtn setTitle:@"关注" forState:UIControlStateNormal];
        [_focusBtn setTitleColor:BottomToolTextCorol forState:UIControlStateNormal];
        [_focusBtn addTarget:self action:@selector(focusBtnClick) forControlEvents:UIControlEventTouchUpInside];
        [_focusBtn setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0xe6e6eb)] forState:UIControlStateHighlighted];
        _focusBtn.backgroundColor = [UIColor clearColor];
    }
    return _focusBtn;
}

- (RNTMineHeaderBottomBtn *)fansBtn
{
    if (_fansBtn == nil) {
        _fansBtn = [[RNTMineHeaderBottomBtn alloc] init];
        _fansBtn.backgroundColor = [UIColor whiteColor];
        [_fansBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _fansBtn.titleLabel.font = TextFont;
        [_fansBtn setTitle:@"粉丝" forState:UIControlStateNormal];
        [_fansBtn setTitleColor:BottomToolTextCorol forState:UIControlStateNormal];
        [_fansBtn addTarget:self action:@selector(fansBtnClick) forControlEvents:UIControlEventTouchUpInside];
        [_fansBtn setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0xe6e6eb)] forState:UIControlStateHighlighted];
        _fansBtn.backgroundColor = [UIColor clearColor];
    }
    return _fansBtn;
}

//- (UIButton *)historyBtn
//{
//    if (_historyBtn == nil) {
//        _historyBtn = [[UIButton alloc] init];
//        _historyBtn.backgroundColor = [UIColor whiteColor];
//        [_historyBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
//        _historyBtn.titleLabel.font = TextFont;
//        [_historyBtn setTitleColor:BottomToolTextCorol forState:UIControlStateNormal];
//        [_historyBtn setImage:[UIImage imageNamed:@"Mine_history"] forState:UIControlStateNormal];
//        [_historyBtn setTitle:@" 浏览记录" forState:UIControlStateNormal];
//        [_historyBtn addTarget:self action:@selector(historyBtnClick) forControlEvents:UIControlEventTouchUpInside];
//        [_historyBtn setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0xe6e6eb)] forState:UIControlStateHighlighted];
//    }
//    return _historyBtn;
//}

- (UIButton *)iconBtn
{
    if (_iconBtn == nil) {
        _iconBtn = [[UIButton alloc] init];
        _iconBtn.layer.cornerRadius = IconWidth * 0.5;
        _iconBtn.layer.masksToBounds = YES;
        _iconBtn.layer.borderWidth = 3.0;
        _iconBtn.layer.borderColor = [UIColor whiteColor].CGColor;
        [_iconBtn addTarget:self action:@selector(iconBtnClick) forControlEvents:UIControlEventTouchUpInside];
//        [_iconBtn setImage:[UIImage imageNamed:@"PlaceholderIcon"] forState:UIControlStateNormal];
        [_iconBtn setBackgroundImage:[UIImage imageNamed:@"PlaceholderIcon"] forState:UIControlStateNormal];
    }
    return _iconBtn;
}

- (UILabel *)nickNameLabel
{
    if (_nickNameLabel == nil) {
        _nickNameLabel = [[UILabel alloc] init];
        _nickNameLabel.backgroundColor = [UIColor clearColor];
        _nickNameLabel.font = TextFont;
        _nickNameLabel.textAlignment = NSTextAlignmentCenter;//水平对齐方式
        _nickNameLabel.textColor = NickNameTextCorol;
    }
    return _nickNameLabel;
}

//- (UIImageView *)levelImageView
//{
//    if (_levelImageView == nil) {
//        _levelImageView = [[UIImageView alloc] init];
//    }
//    return _levelImageView;
//}

- (UILabel *)IDLabel
{
    if (_IDLabel == nil) {
        _IDLabel = [[UILabel alloc] init];
        _IDLabel.text = @"ID:123123";
        _IDLabel.textAlignment = NSTextAlignmentCenter;
        _IDLabel.font = IDFont;
        _IDLabel.textColor = IDTextCorol;
    }
    return _IDLabel;
}

- (UIButton *)editBtn
{
    if (_editBtn == nil) {
        _editBtn = [[UIButton alloc] init];
        [_editBtn setImage:[UIImage imageNamed:@"Mine_edit"] forState:UIControlStateNormal];
        [_editBtn addTarget:self action:@selector(editBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _editBtn;
}

- (UIButton *)loginBtn
{
    if (_loginBtn == nil) {
        _loginBtn = [[UIButton alloc] init];
        _loginBtn.layer.cornerRadius = 3.0;
        _loginBtn.layer.masksToBounds = YES;
        _loginBtn.layer.borderWidth = 0.5;
        _loginBtn.layer.borderColor = LoginBtnColor.CGColor;
        [_loginBtn setTitle:@"登录" forState:UIControlStateNormal];
        [_loginBtn setTitleColor:LoginBtnColor forState:UIControlStateNormal];
        [_loginBtn addTarget:self action:@selector(loginBtnClick) forControlEvents:UIControlEventTouchUpInside];
        _loginBtn.titleLabel.font = TextFont;
    }
    return _loginBtn;
}

- (UIButton *)registerBtn
{
    if (_registerBtn == nil) {
        _registerBtn = [[UIButton alloc] init];
        _registerBtn.layer.cornerRadius = 3.0;
        _registerBtn.layer.masksToBounds = YES;
        _registerBtn.layer.borderWidth = 0.5;
        _registerBtn.layer.borderColor = LoginBtnColor.CGColor;
        [_registerBtn setTitle:@"注册" forState:UIControlStateNormal];
        [_registerBtn setTitleColor:LoginBtnColor forState:UIControlStateNormal];
        [_registerBtn addTarget:self action:@selector(registerBtnClick) forControlEvents:UIControlEventTouchUpInside];
        _registerBtn.titleLabel.font = TextFont;
    }
    return _registerBtn;
}

- (UIView *)topView
{
    if (_topView == nil) {
        _topView = [[UIView alloc] init];
        _topView.backgroundColor = RNTMainColor;
    }
    return _topView;
}

- (UIImageView *)bottomView
{
    if (_bottomView == nil) {
        _bottomView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Mine_YellowArc"]];
//        _bottomView.image = [UIImage imageNamed:@"Mine_YellowArc"];
    }
    return _bottomView;
}
@end
