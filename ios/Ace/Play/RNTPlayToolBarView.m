//
//  RNTPlayToolBarView.m
//  Ace
//
//  Created by 于传峰 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayToolBarView.h"
#import "RNTUserManager.h"

@interface RNTPlayToolBarView ()
@property (nonatomic, weak) UIButton *flashLightBtn;

@end

@implementation RNTPlayToolBarView


//- (instancetype)initWithFrame:(CGRect)frame
//{
//    if (self = [super initWithFrame:frame]) {
//        self.backgroundColor = [UIColor clearColor];
//        [self setupSubViews];
//    }
//    return self;
//}

- (instancetype)initWithType:(RNTPlayToolBarType)type
{
    if (self = [super init]) {
        self.backgroundColor = [UIColor clearColor];
        [self setupSubViewsWithType:type];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(cameraBack) name:RNTSwitcCameraBackNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(cameraFront) name:RNTSwitcCameraFrontNotification object:nil];
        
    }
    return self;
}

- (void)setupSubViewsWithType:(RNTPlayToolBarType)type
{
    CGSize buttonSize = CGSizeMake(44, 44);
    // chat
    UIButton* chatButton = [self toolBarButtonWithImageName:@"play_toolBar_talk" hilightImageName:@"play_toolBar_talk_HL"];
    [self addSubview:chatButton];
    chatButton.tag = 1;
    [chatButton addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchUpInside];
    [chatButton makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(buttonSize);
        make.left.equalTo(self).offset(8);
        make.bottom.equalTo(self).offset(-8);
    }];
    
    
    // close
    UIButton* closeButton = [self toolBarButtonWithImageName:@"play_toolBar_off" hilightImageName:@"play_toolBar_off_HL"];
    [self addSubview:closeButton];
    closeButton.tag = 2;
    [closeButton addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchUpInside];
    [closeButton makeConstraints:^(MASConstraintMaker *make) {
        make.size.bottom.equalTo(chatButton);
        make.right.equalTo(self).offset(-8);
    }];
    
    if (type == RNTPlayToolBarTypeAudience) {
        // share
        UIButton* shareButton = [self toolBarButtonWithImageName:@"play_toolBar_share" hilightImageName:@"play_toolBar_share_HL"];
        shareButton.hidden = ![RNTUserManager canShare];
        [self addSubview:shareButton];
        shareButton.tag = 4;
        [shareButton addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchUpInside];
        [shareButton makeConstraints:^(MASConstraintMaker *make) {
            make.size.bottom.equalTo(chatButton);
            make.right.equalTo(closeButton.left).offset(-8);
        }];
        
        // gift
        UIButton* giftButton = [[UIButton alloc] init];
        [self addSubview:giftButton];
        [giftButton addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchUpInside];
        giftButton.tag = 3;
        [giftButton setImage:[UIImage imageNamed:@"play_toolBar_gift"] forState:UIControlStateNormal];
        giftButton.layer.cornerRadius = 22;
        giftButton.clipsToBounds = YES;
        giftButton.backgroundColor = RNTColor_16(0xfff100);
        [giftButton makeConstraints:^(MASConstraintMaker *make) {
            make.size.equalTo(buttonSize);
            make.centerX.equalTo(self);
            make.centerY.equalTo(chatButton);
        }];

    }else if (type == RNTPlayToolBarTypeAnchor){
        // 静音
        UIButton *muteBtn = [self toolBarButtonWithImageName:@"play_toolBar_mic" selectedImageName:@"play_toolBar_mic_SL"];
        [self addSubview:muteBtn];
        muteBtn.tag = 5;
        [muteBtn addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchDown];
        [muteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(chatButton.right).offset(8);
            make.bottom.equalTo(chatButton.bottom);
            make.size.mas_equalTo(buttonSize);
            
        }];
        
        //旋转
        UIButton *switchBtn = [self toolBarButtonWithImageName:@"play_toolBar_change" hilightImageName:@"play_toolBar_change_HL"];
        [self addSubview:switchBtn];
        switchBtn.tag = 6;
        [switchBtn addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchUpInside];
        [switchBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(muteBtn.right).offset(8);
            make.bottom.equalTo(chatButton.bottom);
            make.size.mas_equalTo(buttonSize);
        }];
        
        
        //闪光灯
        UIButton *flashLighBtn = [self toolBarButtonWithImageName:@"play_toolBar_ray" selectedImageName:@"play_toolBar_ray_SL"];
//        [flashLighBtn setImage:[UIImage imageNamed:@"play_toolBar_ray_disable"] forState:UIControlStateDisabled];
        [self addSubview:flashLighBtn];
        self.flashLightBtn = flashLighBtn;
        flashLighBtn.tag = 7;
        [flashLighBtn addTarget:self action:@selector(optionClicked:) forControlEvents:UIControlEventTouchDown];
        
        NSString *cameraPosition = [[NSUserDefaults standardUserDefaults] objectForKey:Camera_Position];
        if (cameraPosition.length) {
            if ([cameraPosition isEqualToString:Camera_Front]) {
                flashLighBtn.hidden = YES;
            }
        }else {
            flashLighBtn.hidden = YES;
        }
        
        
        [flashLighBtn mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.equalTo(switchBtn.right).offset(8);
            make.bottom.equalTo(chatButton.bottom);
            make.size.mas_equalTo(buttonSize);
        }];
    }
}

- (UIButton *)toolBarButtonWithImageName:(NSString *)imageName hilightImageName:(NSString *)hlImageName
{
    UIButton* button = [[UIButton alloc] init];
    [button setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
//    [button setImage:[UIImage imageNamed:hlImageName] forState:UIControlStateHighlighted];
    
    button.layer.cornerRadius = 22;
    button.clipsToBounds = YES;
    button.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
    
    return button;
}

- (UIButton *)toolBarButtonWithImageName:(NSString *)imageName selectedImageName:(NSString *)selectedImageName
{
    UIButton* button = [[UIButton alloc] init];
    [button setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    [button setImage:[UIImage imageNamed:selectedImageName] forState:UIControlStateSelected];
    
    button.layer.cornerRadius = 22;
    button.clipsToBounds = YES;
    button.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.5];
    
    return button;
}


- (void)optionClicked:(UIButton *)button
{
    if (![self.delegate respondsToSelector:@selector(playToolBarView:button:selectedOption:)]) {
        return;
    }
    
    switch (button.tag) {
        case 1:
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionChat];
            break;
        case 2:
            //用来弹出评论框
            [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"IsWatched"];
            [[NSUserDefaults standardUserDefaults] synchronize];
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionClose];
            break;
        case 3:
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionGift];
            break;
        case 4:
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionShare];
            break;
        case 5:
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionMute];
            break;
        case 6:
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionSwitch];
            break;
        case 7:
            [self.delegate playToolBarView:self button:button selectedOption:RNTPlayToolBarOptionFlashLight];
            break;
            
        default:
            break;
    }
}

- (void)cameraBack
{
    self.flashLightBtn.selected = NO;
    self.flashLightBtn.hidden = NO;
//    self.flashLightBtn.enabled = YES;
}

- (void)cameraFront
{
    self.flashLightBtn.selected = NO;
    self.flashLightBtn.hidden = YES;
//    self.flashLightBtn.enabled = NO;
    
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    RNTLog(@"--");
}

@end
