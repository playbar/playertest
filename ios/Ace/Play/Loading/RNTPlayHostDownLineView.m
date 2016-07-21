//
//  RNTPlayHostDownLineView.m
//  Ace
//
//  Created by 于传峰 on 16/3/16.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayHostDownLineView.h"
#import "RNTPlayNetWorkTool.h"

@interface RNTPlayHostDownLineView()
@property (nonatomic, weak) UIButton *attentionBtn;
@property (nonatomic, weak) UIButton *closeBtn;

@property (nonatomic, copy) void(^closeBlock)();
@property (nonatomic, copy) void(^attentionedBlock)();
@property (nonatomic, copy) NSString *userID;
@end

@implementation RNTPlayHostDownLineView


+ (instancetype)showDownLineViewWithUseID:(NSString *)userID selectedClosed:(void (^)())closed selectedAttention:(void (^)())attentioned
{
    RNTPlayHostDownLineView* downView = [[self alloc] init];
    
    downView.userID = userID;
    downView.closeBlock = [closed copy];
    downView.attentionedBlock = [attentioned copy];

    
    [downView initAttentionBtnTitle];
    return downView;
}


- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.userInteractionEnabled = YES;
        self.backgroundColor = [UIColor clearColor];
        self.image = [UIImage imageNamed:@"play_Gaussian_Blur_image"];
        [self setupSubViews];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(initAttentionBtnTitle) name:LOGIN_RESULT_NOTIFICATION object:nil];
        
    }
    return self;
}


- (void)setupSubViews
{
    
    UIImageView* ceneterView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_host_down"]];
    [self addSubview:ceneterView];
    
    UIButton* attentionBtn = [[UIButton alloc] init];
    [attentionBtn setBackgroundImage:[UIImage imageNamed:@"play_host_down_normal"] forState:UIControlStateNormal];
    [attentionBtn setBackgroundImage:[UIImage imageNamed:@"play_host_down_HL"] forState:UIControlStateHighlighted];
    [self addSubview:attentionBtn];
    [attentionBtn addTarget:self action:@selector(attentionSelected:) forControlEvents:UIControlEventTouchUpInside];
    [attentionBtn setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateNormal];
    [attentionBtn setTitle:@"关注" forState:UIControlStateNormal];
    [attentionBtn setTitle:@"已关注" forState:UIControlStateSelected];
    self.attentionBtn = attentionBtn;
    
    UIButton* closeBtn = [[UIButton alloc] init];
    [closeBtn setBackgroundImage:[UIImage imageNamed:@"play_host_down_normal"] forState:UIControlStateNormal];
    [closeBtn setBackgroundImage:[UIImage imageNamed:@"play_host_down_HL"] forState:UIControlStateHighlighted];
    [closeBtn setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateNormal];
    [closeBtn setTitle:@"退出" forState:UIControlStateNormal];
    [self addSubview:closeBtn];
    [closeBtn addTarget:self action:@selector(closeSelected:) forControlEvents:UIControlEventTouchUpInside];
    self.closeBtn = closeBtn;
    
    [ceneterView makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.centerY.equalTo(self).offset(-40);
    }];
    [attentionBtn makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(ceneterView.bottom).offset(50);
        make.centerX.equalTo(ceneterView);
        make.width.equalTo(280);
        make.height.equalTo(45);
    }];
    [closeBtn makeConstraints:^(MASConstraintMaker *make) {
        make.size.centerX.equalTo(attentionBtn);
        make.top.equalTo(attentionBtn.bottom).offset(16);
    }];
}

- (void)initAttentionBtnTitle
{
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        self.attentionBtn.selected = NO;
    }else{
        [RNTPlayNetWorkTool getUserInfoWithUserID:self.userID getSuccess:^(NSDictionary *dict, BOOL attentionState) {
            self.attentionBtn.selected = attentionState;
        }];
    }
}

-  (void)attentionSelected:(UIButton *)button
{
    if (self.attentionedBlock) {
        self.attentionedBlock();
    }
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
//        button.enabled = NO;
        return;
    }
    
    
    if (button.isSelected) {
        [RNTPlayNetWorkTool cancelFollowUserWithCurrentUserID:manager.user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
            if (status) {
                [MBProgressHUD showSuccess:@"取消关注成功"];
                button.selected = !button.isSelected;
            }else{
                [MBProgressHUD showError:@"取消关注失败"];
            }
        }];
    }else{
        [RNTPlayNetWorkTool followUserWithCurrentUserID:manager.user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
            if (status) {
                [MBProgressHUD showSuccess:@"关注成功"];
                button.selected = !button.isSelected;
            }else{
                [MBProgressHUD showError:@"关注失败"];
            }
        }];
    }
    
    
}
-  (void)closeSelected:(UIButton *)button
{
    if (self.closeBlock) {
        self.closeBlock();
    }
}

- (void)dealloc
{
    RNTLog(@"---");
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
