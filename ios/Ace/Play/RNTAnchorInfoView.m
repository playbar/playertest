//
//  RNTAnchorInfoView.m
//  Ace
//
//  Created by 靳峰 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTAnchorInfoView.h"
#import "RNTPlayUserInfo.h"
#import "UIImageView+WebCache.h"
#import "RNTPlayNetWorkTool.h"
#import "RNTUserManager.h"
#import "MJExtension.h"
#import "UIImage+RNT.h"

@interface RNTAnchorInfoView ()

//模型
@property (nonatomic, strong) RNTPlayUserInfo *model;

//user模型
@property(nonatomic,strong) RNTUser *userModel;

@property (nonatomic, copy) NSString *userID;
@property (nonatomic, weak) UIImageView *iconView;
@property (nonatomic, weak) UIButton *reportBtn;
@property (nonatomic, weak) UILabel *nameLabel;
@property (nonatomic, weak) UILabel *desLabel;
@property (nonatomic, weak) UILabel *fansCountLabel;
@property (nonatomic, weak) UIButton *attentionButton;
@property(nonatomic,assign) NSInteger count;
@property(nonatomic,strong) UILabel *IDlabel;
@property(nonatomic,strong) UIButton *homePageBtn;
//关注回调
@property(nonatomic,copy) void(^attentionBtnClick)();
//主页回调
@property(nonatomic,copy) void(^homePageVC)();
//举报回调
@property(nonatomic,copy) void(^reportBtnClick)();

@end

static __weak RNTAnchorInfoView *_infoView;
@implementation RNTAnchorInfoView

+ (void)showAnchorViewWithUserID:(NSString*)userID clickType:(RNTAnchorInfoType)type attention:(void(^)()) attentionBtnClick homePage : (void(^)()) homePageVC report:(void(^)()) reportBtnClick;
{
    if (!userID.length || userID.intValue == -1) return;
    
    UIWindow *window = [UIApplication sharedApplication].windows.lastObject;
    
    RNTAnchorInfoView *infoView = [[RNTAnchorInfoView alloc] initWithFrame:window.bounds];
    
    infoView.attentionBtnClick = attentionBtnClick;
    infoView.homePageVC = homePageVC;
    infoView.reportBtnClick = reportBtnClick;
    infoView.userID = userID;
    
    [infoView setSubViews];
    
    [infoView getUserData];
    
    NSString *clickType;
    switch (type) {
        case RNTAnchorInfoTypeClickAnchor:
            clickType = @"举报";
            break;
        case RNTAnchorInfoTypeClickUser:
            clickType = @"禁言";
            break;
        case RNTAnchorInfoTypeClickSelf:
            infoView.reportBtn.hidden = YES;
            break;
        case RNTAnchorInfoTypeAnchorClickSelf:
            infoView.reportBtn.hidden = YES;
            infoView.homePageBtn.hidden = YES;
        default:
            break;
    }
    
    [infoView.reportBtn setTitle:clickType forState:UIControlStateNormal];
    if (type == RNTAnchorInfoTypeClickSelf) {
        infoView.attentionButton.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.4];
        infoView.attentionButton.enabled = NO;
    }else if(type == RNTAnchorInfoTypeClickUser){
        infoView.homePageBtn.hidden = YES;
    }
    
    infoView.alpha = 0.3;
    [window addSubview:infoView];
    _infoView = infoView;
    [UIView animateWithDuration:0.2 animations:^{
        infoView.alpha = 1;
    }];
    
//    return infoView;

}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
    [self remove];
}

+ (void)dismiss
{
    UIWindow *window = [UIApplication sharedApplication].windows.lastObject;
    for (UIView* subView in window.subviews) {
        if ([subView isKindOfClass:[RNTAnchorInfoView class]]) {
            [self dismiss:subView];
        }
    }
}

+ (void)dismiss:(UIView*)view
{
    [UIView animateWithDuration:0.2 animations:^{
        view.alpha = 0;
    } completion:^(BOOL finished) {
        [view removeFromSuperview];
    }];
}

- (void)remove{
    UIWindow *window = [UIApplication sharedApplication].windows.lastObject;
    for (UIView* subView in window.subviews) {
        if ([subView isKindOfClass:[RNTAnchorInfoView class]]) {
            [self remove:subView];
        }
    }
}
- (void)remove:(UIView*)view
{
    [UIView animateWithDuration:0.2 animations:^{
        view.alpha = 0;
    } completion:^(BOOL finished) {
        [view removeFromSuperview];
    }];
}
#pragma mark - 按钮点击
//进入主页
-(void)homePageBtnClick
{
    if (self.homePageVC) {
        self.homePageVC();
    }
    [self remove];
}

//举报
-(void)reportClick
{
    
    if (self.reportBtnClick) {
        self.reportBtnClick();
    }
    [self remove];

}

//关注
-(void)attentionClick:(UIButton *)button
{
    button.selected = !button.selected;
    if (button.selected) {
        button.backgroundColor = RNTColor_16(0xeeeeee);
        
        self.fansCountLabel.text = [NSString stringWithFormat:@"粉丝: %ld  |  %@",self.count+=1,self.model.location];
    }else{
        button.backgroundColor = RNTMainColor;
         self.fansCountLabel.text = [NSString stringWithFormat:@"粉丝: %ld  |  %@",self.count-=1,self.model.location];
    }
    
    if (self.attentionBtnClick) {
        self.attentionBtnClick();
    }
    
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        [self remove];
        return;
    }
    
    if (!button.isSelected) {
        [RNTPlayNetWorkTool cancelFollowUserWithCurrentUserID:manager.user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
            if (status) {
                [MBProgressHUD showSuccess:@"取消关注成功"];
            }else{
                [MBProgressHUD showError:@"取消关注失败"];
            }
        }];
    }else{
        [RNTPlayNetWorkTool followUserWithCurrentUserID:manager.user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
            if (status) {
                [MBProgressHUD showSuccess:@"关注成功"];
            }else{
                [MBProgressHUD showError:@"关注失败"];
            }
        }];
    }
    
    [self remove];

}

#pragma mark - 模型赋值
- (void)setModel:(RNTPlayUserInfo *)model
{
    _model = model;

    [self.iconView  sd_setImageWithURL:[NSURL URLWithString:model.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    if (model.profile.length == 0 || model.profile == nil) {
        self.iconView.image = [UIImage imageNamed:@"PlaceholderIcon"];
    }
    self.nameLabel.text = model.nickName;
    if (model.signature.length) {
        self.desLabel.text = model.signature;
    }else{
        self.desLabel.text = @"这个家伙的签名私奔了";
    }
    self.IDlabel.text = [NSString stringWithFormat:@"ID : %@",model.userId];
    if (model.location.length<=0) {
        model.location = @"地球的背面";
    }
    self.fansCountLabel.text = [NSString stringWithFormat:@"粉丝: %@  |  %@", model.fansCount, model.location];
    self.attentionButton.selected = model.isAttentioned;
    self.attentionButton.hidden = NO;
    if (self.attentionButton.isSelected) {
        self.attentionButton.backgroundColor =RNTColor_16(0xeeeeee);
    }
    self.count = [model.fansCount integerValue];
}

#pragma mark - 网络请求
- (void)getUserData
{
    if (self.userID.length == 0 || self.userID == nil || self.userID.intValue == -1) return;
    
    [RNTPlayNetWorkTool getUserInfoWithUserID:self.userID getSuccess:^(NSDictionary *dict, BOOL attentionState) {
        self.model = [RNTPlayUserInfo playUserInfoWithDict:dict[@"user"]];
    }];
}

#pragma mark - 设置子控件
-(void)setSubViews
{
    self.backgroundColor = [UIColor colorWithWhite:0 alpha:0.3];
    
    //白色背景
    UIView *whiteBg = [[UIView alloc] init];
    whiteBg.backgroundColor = RNTColor_16(0xffffff);
    whiteBg.layer.cornerRadius = 12;
    whiteBg.clipsToBounds = YES;
    [self addSubview:whiteBg];
    [whiteBg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(270, 370));
        make.top.mas_equalTo(self).offset(112);
        make.centerX.mas_equalTo(self);
    }];
    
    //举报
    UIButton *reportBtn = [[UIButton alloc] init];
    reportBtn.backgroundColor = RNTColor_16(0xeeeeee);
    self.reportBtn = reportBtn;
    reportBtn.titleLabel.font = [UIFont systemFontOfSize:13];
    [reportBtn setTitle:@"举报" forState:UIControlStateNormal];
    [reportBtn setTitleColor:RNTColor_16(0x5f6060) forState:UIControlStateNormal];
    reportBtn.layer.cornerRadius =11;
    reportBtn.clipsToBounds = YES;
    [whiteBg addSubview:reportBtn];
    [reportBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(43, 22));
        make.top.mas_equalTo(whiteBg).offset(21);
        make.left.mas_equalTo(whiteBg).offset(13);
    }];
    [reportBtn addTarget:self action:@selector(reportClick) forControlEvents:UIControlEventTouchUpInside];
    
    //主页
    UIButton *homePage = [[UIButton alloc] init];
    homePage.backgroundColor = RNTColor_16(0xeeeeee);
    homePage.titleLabel.font = [UIFont systemFontOfSize:13];
    [homePage setTitle:@"主页" forState:UIControlStateNormal];
    [homePage setTitleColor:RNTColor_16(0x5f6060) forState:UIControlStateNormal];
    homePage.layer.cornerRadius =11;
    homePage.clipsToBounds = YES;
    [whiteBg addSubview:homePage];
    [homePage mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(43, 22));
        make.top.mas_equalTo(whiteBg).offset(21);
        make.right.mas_equalTo(whiteBg).offset(-13);
    }];
    [homePage addTarget:self action:@selector(homePageBtnClick) forControlEvents:UIControlEventTouchUpInside];
    self.homePageBtn = homePage;
    
    //头像
    UIImageView *iconImg = [[UIImageView alloc] init];
    iconImg.backgroundColor = [UIColor whiteColor];
    iconImg.layer.cornerRadius = 72;
    self.iconView = iconImg;
    iconImg.clipsToBounds = YES;
    [whiteBg addSubview:iconImg];
    [iconImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(144, 144));
        make.top.mas_equalTo(whiteBg).offset(29);
        make.centerX.mas_equalTo(whiteBg);
    }];
    [iconImg setImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    
    //名字
    UILabel *nameLab = [[UILabel alloc] init];
    self.nameLabel = nameLab;
//    nameLab.text = @"ACE用户";
    nameLab.font = [UIFont systemFontOfSize:18];
    nameLab.textColor = RNTColor_16(0x19191a);
    nameLab.textAlignment = NSTextAlignmentCenter;
    [whiteBg addSubview:nameLab];
    [nameLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(iconImg.mas_bottom).offset(14);
        make.centerX.mas_equalTo(whiteBg);
        make.size.mas_equalTo(CGSizeMake(162, 22));
    }];

    //ID
    UILabel *IDlabel = [[UILabel alloc] init];
    IDlabel.font = [UIFont systemFontOfSize:11];
    IDlabel.textColor = RNTColor_16(0x5f6060);
    IDlabel.text = @" ";
    [whiteBg addSubview:IDlabel];
    [IDlabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.nameLabel.mas_bottom).offset(4);
        make.centerX.mas_equalTo(nameLab);
    }];
    self.IDlabel = IDlabel;
    
    //签名
    UILabel *desLab = [[UILabel alloc] init];
    self.desLabel = desLab;
    desLab.numberOfLines = 2;
    desLab.text = @" ";
    desLab.textAlignment = NSTextAlignmentCenter;
    desLab.textColor = RNTColor_16(0x5f6060);
    desLab.font = [UIFont systemFontOfSize:13];
    [whiteBg addSubview:desLab];
    [desLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(IDlabel.mas_bottom).offset(13);
        make.centerX.mas_equalTo(whiteBg);
        make.size.mas_equalTo(CGSizeMake(130,32));
    }];
    
    //粉丝地址
    UILabel *fansLab = [[UILabel alloc] init];
    self.fansCountLabel = fansLab;
    fansLab.text = @" ";
    fansLab.textColor = RNTColor_16(0x5f6060);
    fansLab.font = [UIFont systemFontOfSize:11];
    [whiteBg addSubview:fansLab];
    [fansLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(desLab.mas_bottom).offset(11);
        make.centerX.mas_equalTo(whiteBg);
    }];
    
    //关注
    UIButton *attenBtn = [[UIButton alloc] init];
    self.attentionButton = attenBtn;
    [attenBtn setTitle:@"关注" forState:UIControlStateNormal];
    [attenBtn setTitle:@"已关注" forState:UIControlStateSelected];

    [attenBtn setTitleColor:RNTColor_16(0x8f8f8f) forState:UIControlStateSelected];
    attenBtn.titleLabel.textColor =RNTColor_16(0x19191a);

    [attenBtn setTitleColor:RNTColor_16(0x19191a) forState:UIControlStateNormal];
    attenBtn.backgroundColor = RNTMainColor;
    attenBtn.titleLabel.font = [UIFont systemFontOfSize:16];
    attenBtn.layer.cornerRadius = 22;
    attenBtn.clipsToBounds=  YES;
    [whiteBg addSubview:attenBtn];
    [attenBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(fansLab.mas_bottom).offset(14);
        make.centerX.mas_equalTo(whiteBg);
        make.size.mas_equalTo(CGSizeMake(176, 44));
    }];
    [attenBtn addTarget:self action:@selector(attentionClick:) forControlEvents:UIControlEventTouchUpInside];
    attenBtn.hidden = YES;
}



- (void)dealloc
{
    RNTLog(@"--");
}



@end
