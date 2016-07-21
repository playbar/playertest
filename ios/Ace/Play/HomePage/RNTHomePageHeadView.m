//
//  RNTHomePageHeadView.m
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHomePageHeadView.h"
#import "RNTOrderFansBtn.h"
#import "UIImageView+WebCache.h"
#import "RNTPlayNetWorkTool.h"
#import "RNTDatabaseTool.h"
#import "RNTLoginViewController.h"
#import "MJExtension.h"
#import "RNTPlayViewController.h"
#import "RNTIntoPlayModel.h"
#import "RNTNavigationController.h"

@interface RNTHomePageHeadView()<UIAlertViewDelegate>

@property (nonatomic, weak) UIImageView *backgroundImageView;
@property (nonatomic, weak) UIImageView *iconImageView;
@property (nonatomic, weak) UILabel *nameLabel;
@property (nonatomic, weak) UILabel *desLabel;
@property (nonatomic, weak) UILabel *locationLabel;
@property (nonatomic, weak) UILabel *idLabel;
@property(nonatomic,strong) UIImageView *levelImg;
//关注按钮
@property(nonatomic,strong) RNTOrderFansBtn *orderBtn;
//粉丝按钮
@property(nonatomic,strong) RNTOrderFansBtn *fansBtn;
//主页对象模型
@property(nonatomic,strong) RNTUser *userModel;
//拉入Alert
@property(nonatomic,strong) UIAlertView *intoBlackAlert;
//移除Alert
@property(nonatomic,strong) UIAlertView *outBlakcAlert;
//选中的按钮
@property(nonatomic,strong) UIButton *selectedBtn;
//拉黑按钮
@property(nonatomic,strong) UIButton *blackBtn;
//关注
@property(nonatomic,strong) UIButton *attentionBtn;
//返回按钮
@property(nonatomic,strong) UIButton *backBtn;
//直播中
@property(nonatomic,strong) RNTHomePageLiveBtn *liveBtn;

@end

@implementation RNTHomePageHeadView

+(instancetype)homePageHeadViewWithFrame:(CGRect)frame
{
    RNTHomePageHeadView *headVC = [[RNTHomePageHeadView alloc] init];
    headVC.view.frame = frame;
    
    return headVC;
}

-(void)viewDidLoad
{
    [self setSubviews];
}
#pragma mark - 按钮点击
//拉黑按钮
-(void)blackList:(UIButton *)btn
{
    RNTUserManager *mgr = [RNTUserManager sharedManager];
    if (!mgr.logged) {
        UIAlertView *loginAlert = [[UIAlertView alloc] initWithTitle:@"请登录" message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"登录", nil];
        loginAlert.delegate = self;
        [loginAlert show];
        return;
    }
    
    if ([mgr.user.userId isEqualToString:self.userModel.userId]) return;
    if (self.userModel.userId.length<=0 || self.userModel.userId.intValue == -1) return;
    
    if (btn.selected) {
        [self.outBlakcAlert show];
    }else{
        [self.intoBlackAlert show];
    }
}


//关注或粉丝点击
-(void)orderOrfansClick:(UIButton *)btn
{
    //点击被选中按钮直接
    if (btn == self.selectedBtn) return;
    
    //切换两个按钮选中状态
    self.orderBtn.selected = !self.orderBtn.selected;
    self.fansBtn.selected = !self.fansBtn.selected;
    
    self.selectedBtn = btn;
    
    if (self.orderFansBtnClick) {
        CGPoint point = CGPointMake(btn.tag*kScreenWidth, 0);
        self.orderFansBtnClick(point);
    }
}

//进入直播间
-(void)intoLiveRoom:(UIButton *)btn
{
    btn.enabled = NO;
    
    RNTPlayViewController *playVC  = [[RNTPlayViewController alloc] init];
    
    if (self.userModel.userId == nil) {
        return;
    }
    
    playVC.model = [RNTIntoPlayModel getModelWithUerMode:self.userModel andShowModel:nil];
    
    [self.navigationController pushViewController:playVC animated:YES];
}

//点击关注
-(void)attentionBtnClick:(UIButton *)btn
{
    RNTUserManager *mgr = [RNTUserManager sharedManager];
    if (!mgr.logged) {
        UIAlertView *loginAlert = [[UIAlertView alloc] initWithTitle:@"请登录" message:nil delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"登录", nil];
        loginAlert.delegate = self;
        [loginAlert show];
        return;
    }
    if ([mgr.user.userId isEqualToString:self.userID]) return;
    
    btn.enabled = NO;
    
        if ([self.attentionBtn.currentTitle isEqualToString:@"关注"]) {
            [RNTPlayNetWorkTool followUserWithCurrentUserID:[RNTUserManager sharedManager].user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
                if (status) {
                    int count = [self.fansBtn.count.text intValue]+1;
                    self.fansBtn.count.text = [NSString stringWithFormat:@"%d",count];
                    [self.attentionBtn setTitle:@"已关注" forState:UIControlStateNormal];
                    
                    if (self.attentionBtnClick) {
                        self.attentionBtnClick(YES);
                    }

                }else{
                    [MBProgressHUD showError:@"关注失败"];
                }
                btn.enabled = YES;
            }];
        }else{
            [RNTPlayNetWorkTool cancelFollowUserWithCurrentUserID:[RNTUserManager sharedManager].user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
                if (status) {
    
                    [self.attentionBtn setTitle:@"关注" forState:UIControlStateNormal];
                    
                    int count = [self.fansBtn.count.text intValue]-1;
                    self.fansBtn.count.text = [NSString stringWithFormat:@"%d",count];

                    
                    if (self.attentionBtnClick) {
                        self.attentionBtnClick(NO);
                    }
                }else{
                    [MBProgressHUD showError:@"取消关注失败"];
                }
                btn.enabled = YES;
    
            }];
        }
}

-(void)back
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - UIAlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView == self.intoBlackAlert) {
        if (buttonIndex == 0) {
            [RNTDatabaseTool saveBlackList:self.userModel];
            [self.blackBtn setTitle:@"已拉黑" forState:UIControlStateNormal];
            [MBProgressHUD showSuccess:[NSString stringWithFormat:@"已将%@拉入黑名单",self.userModel.nickName]];
            self.blackBtn.selected = YES;
        }
    }else if(alertView == self.outBlakcAlert){
        if (buttonIndex == 0) {
            [RNTDatabaseTool deleteFromBlackList:self.userModel];
            [self.blackBtn setTitle:@"拉黑" forState:UIControlStateNormal];
            [MBProgressHUD showSuccess:[NSString stringWithFormat:@"已将%@移出黑名单",self.userModel.nickName]];
            self.blackBtn.selected = NO;
        }
    }else{
        if (buttonIndex == 1) {
            RNTLoginViewController *loginVC = [[RNTLoginViewController alloc] init];
            RNTNavigationController *loginNV= [[RNTNavigationController alloc] initWithRootViewController:loginVC];
            [self presentViewController:loginNV animated:YES completion:nil];
        }
    }
}


#pragma mark - set方法
-(void)setUserID:(NSString *)userID
{
    _userID = userID;
    //拉取用户数据
    [RNTPlayNetWorkTool getUserInfoWithUserID:userID getSuccess:^(NSDictionary *dict, BOOL attentionState) {
        RNTUser *user = [RNTUser mj_objectWithKeyValues:dict[@"user"]];
        self.userModel = user;
        self.isShowInto = self.isShowInto;
        
        if (attentionState) {
            [self.attentionBtn setTitle:@"已关注" forState:UIControlStateNormal];
        }else{
            [self.attentionBtn setTitle:@"关注" forState:UIControlStateNormal];
        }
        
        self.blackBtn.selected = [RNTDatabaseTool isBlackList:user];
        if (self.blackBtn.selected) {
            [self.blackBtn setTitle:@"已拉黑" forState:UIControlStateNormal];
        }else{
            [self.blackBtn setTitle:@"拉黑" forState:UIControlStateNormal];
        }
    }];
}


-(void)setIsShowInto:(BOOL)isShowInto
{
    _isShowInto = isShowInto;
    
    self.liveBtn.hidden = isShowInto;
    
    if (!isShowInto) {
        self.liveBtn.hidden = !self.userModel.isShow.boolValue;
    }
}

-(void)setIsOrderSelected:(BOOL)isOrderSelected
{
    if (isOrderSelected) {
        self.selectedBtn = self.orderBtn;
        self.orderBtn.selected = YES;
        self.fansBtn.selected = NO;
    }else{
        self.selectedBtn = self.fansBtn;
        self.orderBtn.selected = NO;
        self.fansBtn.selected = YES;
    }

}

#pragma mark - 模型赋值
-(void)setUserModel:(RNTUser *)userModel
{
    _userModel = userModel;
    
    [self.backgroundImageView sd_setImageWithURL:[NSURL URLWithString:userModel.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    [self.iconImageView sd_setImageWithURL:[NSURL URLWithString:userModel.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
    self.nameLabel.text = userModel.nickName;
    self.desLabel.text = userModel.signature;
    if (userModel.signature.length == 0) {
        self.desLabel.text = @"这个家伙的签名私奔了";
    }
    self.idLabel.text =[NSString stringWithFormat:@"   ID:%@", userModel.userId];
    self.orderBtn.count.text = userModel.subscribeCnt;
    self.fansBtn.count.text = userModel.fansCnt;
    self.locationLabel.text = userModel.postion;
    if (self.locationLabel.text.length == 0) {
        self.locationLabel.text = @"地球的背面";
    }
    
    self.attentionBtn.hidden = [[RNTUserManager sharedManager].user.userId isEqualToString:userModel.userId];
    self.blackBtn.hidden = self.attentionBtn.hidden;
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    //背景图片
    UIImageView *imageBg = [[UIImageView alloc] init];
    imageBg.image = [UIImage imageNamed:@"0_Icon_Color"];
    self.backgroundImageView = imageBg;
    [self.view addSubview:imageBg];
    [imageBg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.mas_equalTo(self.view);
        make.height.mas_equalTo(286);
    }];
    imageBg.userInteractionEnabled = YES;
    
    UIBlurEffect* effect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleLight];
    UIVisualEffectView* effectView = [[UIVisualEffectView alloc] initWithEffect:effect];
    [imageBg addSubview:effectView];
    [effectView makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(imageBg);
    }];
    
    //背景图片的遮盖
    UIView *coverView = [[UIView alloc] init];
    coverView.backgroundColor = [UIColor colorWithWhite:0.0 alpha:0.3];
    [imageBg addSubview:coverView];
    [coverView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.bottom.right.mas_equalTo(imageBg);
    }];
    
    //返回按钮
    UIButton *backBtn = [[UIButton alloc] init];
    [backBtn setImage:[UIImage imageNamed:@"homePage_navBack_normal"] forState:UIControlStateNormal];
    [backBtn setImage:[UIImage imageNamed:@"homePage_back_highlighted"] forState:UIControlStateHighlighted];
    [imageBg addSubview:backBtn];
    [backBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(imageBg).offset(8);
        make.top.mas_equalTo(imageBg).offset(30);
        make.size.mas_equalTo(CGSizeMake(34, 34));
    }];
    self.backBtn = backBtn;
    [self.backBtn addTarget:self action:@selector(back) forControlEvents:UIControlEventTouchUpInside];
    
    //正在直播按钮
    RNTHomePageLiveBtn *liveBtn = [[RNTHomePageLiveBtn alloc] init];
    [imageBg addSubview:liveBtn];
    [liveBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(imageBg).offset(-8);
        make.top.mas_equalTo(imageBg).offset(30);
        make.size.mas_equalTo(CGSizeMake(34, 34));
    }];
    self.liveBtn = liveBtn;
    [self.liveBtn addTarget:self action:@selector(intoLiveRoom:) forControlEvents:UIControlEventTouchUpInside];
    
    //头像
    UIImageView *iconImg = [[UIImageView alloc] init];
    iconImg.image = [UIImage imageNamed:@"0_Icon_Color"];
    self.iconImageView = iconImg;
    iconImg.layer.cornerRadius =36;
    iconImg.layer.borderWidth = 3;
    iconImg.layer.borderColor = RNTColor_16(0xffffff).CGColor;
    iconImg.clipsToBounds = YES;
    [imageBg addSubview:iconImg];
    [iconImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(imageBg).offset(32);
        make.centerX.mas_equalTo(imageBg);
        make.size.mas_equalTo(CGSizeMake(72, 72));
    }];

    
    //名字
    UILabel *nameLab = [[UILabel alloc] init];
    self.nameLabel = nameLab;
    self.nameLabel.text = @" ";
    nameLab.lineBreakMode = NSLineBreakByTruncatingTail;
    nameLab.textColor = RNTColor_16(0xffffff);
    nameLab.font = [UIFont systemFontOfSize:16];
    nameLab.textAlignment = NSTextAlignmentCenter;
    [imageBg addSubview:nameLab];
    [nameLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(iconImg.mas_bottom).offset(12);
        make.centerX.mas_equalTo(imageBg);
    }];
    
    //等级
    UIImageView *levelImg = [[UIImageView alloc] init];
    [levelImg sizeToFit];
    [self.view addSubview:levelImg];
    [levelImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.nameLabel);
        make.left.mas_equalTo(self.nameLabel.right).offset(2);
    }];
    self.levelImg = levelImg;
    
    //签名
    UILabel *desLab = [[UILabel alloc] init];
    self.desLabel = desLab;
    self.desLabel.text = @" ";
    desLab.textColor =  RNTAlphaColor_16(0xffffff, 0.3);
    desLab.textAlignment = NSTextAlignmentCenter;
    desLab.font = [UIFont systemFontOfSize:12];
    [desLab sizeToFit];
    [imageBg addSubview:desLab];
    [desLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(nameLab.mas_bottom).offset(12);
        make.centerX.mas_equalTo(imageBg);
        make.left.mas_equalTo(self.view).offset(60);
        make.right.mas_equalTo(self.view).offset(-60);
    }];
    
    
    //位置
    UILabel *locationLab = [[UILabel alloc] init];
    self.locationLabel = locationLab;
    locationLab.text = @" ";
    [locationLab sizeToFit];
    locationLab.textColor = RNTAlphaColor_16(0xffffff, 0.3);
    locationLab.font = [UIFont systemFontOfSize:12];
    [imageBg addSubview:locationLab];
    [locationLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(desLab.mas_bottom).offset(6);
        make.right.mas_equalTo(imageBg.mas_centerX).offset(-6);
    }];
    
    //定位图标
    UIImageView *locationImg = [[UIImageView alloc] init];
    locationImg.image = [UIImage imageNamed:@"homePage_location"];
    [locationImg sizeToFit];
    [imageBg addSubview:locationImg];
    [locationImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(locationLab.left).offset(-6);
        make.top.mas_equalTo(desLab.mas_bottom).offset(6);
    }];
    
    //ID
    UILabel *IDLab = [[UILabel alloc] init];
    self.idLabel = IDLab;
    IDLab.text = @"ID:";
    [IDLab sizeToFit];
    IDLab.textColor = RNTAlphaColor_16(0xffffff, 0.3);
    IDLab.font = [UIFont systemFontOfSize:12];
    [imageBg addSubview:IDLab];
    [IDLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(desLab.mas_bottom).offset(6);
        make.left.mas_equalTo(imageBg.mas_centerX).offset(6);
    }];
    
    //关注按钮
    UIButton *attentionBtn = [[UIButton alloc] init];
    [self.view addSubview:attentionBtn];
    attentionBtn.layer.borderColor = RNTMainColor.CGColor;
    attentionBtn.layer.borderWidth = 0.5;
    attentionBtn.layer.cornerRadius = 10;
    attentionBtn.clipsToBounds = YES;
    attentionBtn.titleLabel.font = [UIFont systemFontOfSize:11];
    [attentionBtn setTitle:@"关注" forState:UIControlStateNormal];
    [attentionBtn setTitle:@"已关注" forState:UIControlStateSelected];
    [attentionBtn setTitleColor:RNTMainColor forState:UIControlStateNormal];
    [attentionBtn setTitleColor:[UIColor colorWithWhite:0.0 alpha:0.3] forState:UIControlStateHighlighted];
    [attentionBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(62, 20));
        make.left.mas_equalTo(self.iconImageView.mas_centerX).offset(6);
        make.top.mas_equalTo(self.locationLabel.mas_bottom).offset(15);
    }];
    self.attentionBtn = attentionBtn;
    [self.attentionBtn addTarget:self action:@selector(attentionBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    
    //拉黑按钮
    UIButton *blackBtn = [[UIButton alloc] init];
    [self.view addSubview:blackBtn];
    blackBtn.layer.borderColor = RNTMainColor.CGColor;
    blackBtn.layer.borderWidth = 0.5;
    blackBtn.layer.cornerRadius = 10;
    blackBtn.clipsToBounds = YES;
    blackBtn.titleLabel.font = [UIFont systemFontOfSize:11];
    [blackBtn setTitle:@"拉黑" forState:UIControlStateNormal];
    [blackBtn setTitleColor:RNTMainColor forState:UIControlStateNormal];
    [blackBtn setTitleColor:[UIColor colorWithWhite:0.0 alpha:0.3] forState:UIControlStateHighlighted];
    [blackBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(62, 20));
        make.right.mas_equalTo(self.iconImageView.mas_centerX).offset(-6);
        make.top.mas_equalTo(self.attentionBtn);
    }];
    self.blackBtn = blackBtn;
    [blackBtn addTarget:self action:@selector(blackList:) forControlEvents:UIControlEventTouchUpInside];
    
    
    //黑色背景
    UIView *blackView = [[UIView alloc] init];
    blackView.backgroundColor = [UIColor colorWithWhite:0 alpha:0.3];
    [imageBg addSubview:blackView];
    [blackView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.bottom.right.mas_equalTo(imageBg);
        make.height.mas_equalTo(55);
    }];


    
    //关注按钮
    RNTOrderFansBtn *orderBtn = [[RNTOrderFansBtn alloc] init];
    [blackView addSubview:orderBtn];
    orderBtn.tag = 0;
    orderBtn.titleText = @"关注";
    [orderBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.bottom.top.mas_equalTo(blackView);
        make.width.mas_equalTo(kScreenWidth * 0.5-0.5);
    }];
    self.orderBtn = orderBtn;
    self.selectedBtn = orderBtn;
    self.orderBtn.selected = YES;
    [orderBtn addTarget:self action:@selector(orderOrfansClick:) forControlEvents:UIControlEventTouchUpInside];
    
    
    //粉丝按钮
    RNTOrderFansBtn *fansBtn = [[RNTOrderFansBtn alloc] init];
    fansBtn.selected = YES;
    fansBtn.tag = 1;
    fansBtn.titleText = @"粉丝";
    [blackView addSubview:fansBtn];
    [fansBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.bottom.top.mas_equalTo(blackView);
        make.width.mas_equalTo(kScreenWidth * 0.5-0.5);
    }];
    self.fansBtn = fansBtn;
    self.fansBtn.selected = NO;
    [fansBtn addTarget:self action:@selector(orderOrfansClick:) forControlEvents:UIControlEventTouchUpInside];

    
    //竖线
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = RNTColor_16(0xe6e6eb);
    [blackView addSubview:line];
    [line mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(1, 36));
        make.center.mas_equalTo(blackView);
    }];
}

#pragma mark - 懒加载
-(UIAlertView *)intoBlackAlert
{
    if (!_intoBlackAlert) {
        _intoBlackAlert= [[UIAlertView alloc] initWithTitle:@"确定将该用户加入黑名单吗?" message:nil delegate:self cancelButtonTitle:@"确定" otherButtonTitles:@"取消", nil];
    }
    return _intoBlackAlert;
}

-(UIAlertView *)outBlakcAlert
{
    if (!_outBlakcAlert) {
        _outBlakcAlert= [[UIAlertView alloc] initWithTitle:@"是否移出黑名单?" message:nil delegate:self cancelButtonTitle:@"移出" otherButtonTitles:@"取消", nil];
    }
    return _outBlakcAlert;
}
@end
