//
//  RNTCaptureAuthorizedView.m
//  Ace
//
//  Created by Ranger on 16/5/9.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureAuthorizedView.h"
#import "RNTDeviceAuthStateTool.h"
#import <AVFoundation/AVFoundation.h>
#import <Photos/Photos.h>

#define ButtonRate 0.6
#define TitleText @"手机开启一下权限，才能开始直播"
#define ButtonGrayColor RNTColor_16(0xcccccc)
#define ButtonWhiteColor RNTColor_16(0xffffff)
#define ButtonBlackColor RNTColor_16(0x000000)

@interface RNTCaptureAuthStateButton : UIButton

@end

@interface RNTCaptureAuthorizedView ()

@property (nonatomic, weak) RNTCaptureAuthStateButton *cameraButton;

@property (nonatomic, weak) RNTCaptureAuthStateButton *micButton;

@property (nonatomic, weak) RNTCaptureAuthStateButton *photoButton;

@end

@implementation RNTCaptureAuthorizedView

+ (BOOL)showAuthView
{
    DeviceAuthStatus cameraState = [RNTDeviceAuthStateTool checkCameraAuth];
    DeviceAuthStatus micState = [RNTDeviceAuthStateTool checkMicrophoneAuth];
    DeviceAuthStatus photoState = [RNTDeviceAuthStateTool checkPhotoAuth];
    
    if (cameraState == DeviceAuthStatusAuthorized && micState == DeviceAuthStatusAuthorized && photoState == DeviceAuthStatusAuthorized) {
        return YES;
    }
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    
    // 显示
    RNTCaptureAuthorizedView *authView = [[RNTCaptureAuthorizedView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    [window addSubview:authView];
    
    return NO;

}


- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = RNTAlphaColor_16(0x000000, 0.5);
        [self addTarget:self action:@selector(BgClick) forControlEvents:UIControlEventTouchUpInside];
        
        [self setupSubview];
        
    }
    return self;
}

- (void)setupSubview
{
    // 容器
    UIView *containerView = [[UIView alloc] init];
    containerView.backgroundColor = [UIColor whiteColor];
    containerView.layer.cornerRadius = 6;
    containerView.layer.masksToBounds = YES;
    [self addSubview:containerView];
    
    // 标题
    UILabel *titleLabel = [[UILabel alloc] init];
    titleLabel.text= TitleText;
    titleLabel.textAlignment = NSTextAlignmentLeft;
    titleLabel.numberOfLines = 0;
    titleLabel.font = [UIFont systemFontOfSize:16];
    titleLabel.textColor = [UIColor blackColor];
    [self addSubview:titleLabel];
    
    // 相机
    RNTCaptureAuthStateButton *cameraButton = [[RNTCaptureAuthStateButton alloc] init];
    [cameraButton setTitleColor:ButtonWhiteColor forState:UIControlStateNormal];
    [cameraButton setTitleColor:ButtonBlackColor forState:UIControlStateSelected];
    [cameraButton setTitle:@"相机权限" forState:UIControlStateNormal];
    cameraButton.titleLabel.font = [UIFont systemFontOfSize:18];
    [cameraButton setImage:[UIImage imageNamed:@"capture_cry"] forState:UIControlStateNormal];
    [cameraButton setImage:[UIImage imageNamed:@"capture_smile"] forState:UIControlStateSelected];
    cameraButton.layer.cornerRadius = 25;
    cameraButton.layer.masksToBounds = YES;
    [cameraButton addTarget:self action:@selector(cameraClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:cameraButton];
    self.cameraButton = cameraButton;
    
    DeviceAuthStatus cameraState = [RNTDeviceAuthStateTool checkCameraAuth];
    if (cameraState == DeviceAuthStatusAuthorized) {
        cameraButton.selected = YES;
    }else {
        cameraButton.selected = NO;
    }
    
    // 相册
    RNTCaptureAuthStateButton *photoButton = [[RNTCaptureAuthStateButton alloc] init];
    [photoButton setTitleColor:ButtonWhiteColor forState:UIControlStateNormal];
    [photoButton setTitleColor:ButtonBlackColor forState:UIControlStateSelected];
    [photoButton setTitle:@"相册权限" forState:UIControlStateNormal];
    photoButton.titleLabel.font = [UIFont systemFontOfSize:18];
    [photoButton setImage:[UIImage imageNamed:@"capture_cry"] forState:UIControlStateNormal];
    [photoButton setImage:[UIImage imageNamed:@"capture_smile"] forState:UIControlStateSelected];
    photoButton.layer.cornerRadius = 25;
    photoButton.layer.masksToBounds = YES;
    [photoButton addTarget:self action:@selector(photoClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:photoButton];
    self.photoButton = photoButton;
    
    DeviceAuthStatus photoState = [RNTDeviceAuthStateTool checkPhotoAuth];
    if (photoState == DeviceAuthStatusAuthorized) {
        photoButton.selected = YES;
    }else {
        photoButton.selected = NO;
    }
    
    // 麦克风
    RNTCaptureAuthStateButton *micButton = [[RNTCaptureAuthStateButton alloc] init];
    [micButton setTitleColor:ButtonWhiteColor forState:UIControlStateNormal];
    [micButton setTitleColor:ButtonBlackColor forState:UIControlStateSelected];
    [micButton setTitle:@"麦克风权限" forState:UIControlStateNormal];
    micButton.titleLabel.font = [UIFont systemFontOfSize:18];
    [micButton setImage:[UIImage imageNamed:@"capture_cry"] forState:UIControlStateNormal];
    [micButton setImage:[UIImage imageNamed:@"capture_smile"] forState:UIControlStateSelected];
    micButton.layer.cornerRadius = 25;
    micButton.layer.masksToBounds = YES;
    [micButton addTarget:self action:@selector(micClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:micButton];
    self.micButton = micButton;
    
    DeviceAuthStatus micState = [RNTDeviceAuthStateTool checkMicrophoneAuth];
    if (micState == DeviceAuthStatusAuthorized) {
        micButton.selected = YES;
    }else {
        micButton.selected = NO;
    }
    
    // 布局
    [containerView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(self);
        make.size.mas_equalTo(CGSizeMake(230, 285));
    }];
    
    
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(containerView).offset(30);
        make.width.equalTo(@(190));
        make.left.equalTo(containerView).offset(28);
    }];
    
    
    [cameraButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(titleLabel.bottom).offset(15);
        make.left.equalTo(containerView).offset(20);
        make.size.mas_equalTo(CGSizeMake(190, 50));
    }];
    
    
    [photoButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(cameraButton.bottom).offset(10);
        make.left.equalTo(cameraButton.left);
        make.size.mas_equalTo(CGSizeMake(190, 50));
    }];
    
    
    [micButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(photoButton.bottom).offset(10);
        make.left.equalTo(cameraButton.left);
        make.size.mas_equalTo(CGSizeMake(190, 50));
    }];
    
}


#pragma mark - 按钮点击
- (void)BgClick
{
    [self removeFromSuperview];
}

- (void)cameraClick
{
    DeviceAuthStatus cameraState = [RNTDeviceAuthStateTool checkCameraAuth];
    if (cameraState == DeviceAuthStatusAuthorized) {
        return;
    }else if (cameraState == DeviceAuthStatusNotAuthorized){
        [RNTDeviceAuthStateTool openAuthSetting];
    }else if (cameraState == DeviceAuthStatusNotDetermined) {
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
            self.cameraButton.selected = granted;
        }];
    }
}

- (void)photoClick
{
    DeviceAuthStatus cameraState = [RNTDeviceAuthStateTool checkPhotoAuth];
    if (cameraState == DeviceAuthStatusAuthorized) {
        return;
    }else if (cameraState == DeviceAuthStatusNotAuthorized){
        [RNTDeviceAuthStateTool openAuthSetting];
    }else if (cameraState == DeviceAuthStatusNotDetermined) {
        [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
            if (status == PHAuthorizationStatusAuthorized) {
                self.photoButton.selected = YES;
            }else {
                self.photoButton.selected = NO;
            }
        }];
    }
}

- (void)micClick
{
    DeviceAuthStatus micState = [RNTDeviceAuthStateTool checkMicrophoneAuth];
    if (micState == DeviceAuthStatusAuthorized) {
        return;
    }else if (micState == DeviceAuthStatusNotAuthorized){
        [RNTDeviceAuthStateTool openAuthSetting];
    }else if (micState == DeviceAuthStatusNotDetermined) {
        [AVCaptureDevice requestAccessForMediaType:AVMediaTypeAudio completionHandler:^(BOOL granted) {
            self.micButton.selected = granted;
        }];
    }
}

@end


@implementation RNTCaptureAuthStateButton

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.titleLabel.textAlignment = NSTextAlignmentRight;
//        self.titleLabel.backgroundColor = kRandomColor;
        self.imageView.contentMode = UIViewContentModeCenter;
    }
    return self;
}

- (CGRect)imageRectForContentRect:(CGRect)contentRect
{
    CGFloat x = contentRect.size.width * ButtonRate;
    CGFloat y = 0;
    CGFloat h = 50;
    CGFloat w = contentRect.size.width * (1 - ButtonRate);
    return CGRectMake(x, y, w, h);
}


- (CGRect)titleRectForContentRect:(CGRect)contentRect
{
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat h = 50;
    CGFloat w = contentRect.size.width * ButtonRate;
    return CGRectMake(x, y, w, h);
}


- (void)setSelected:(BOOL)selected
{
    if (selected) {
        [self setBackgroundColor:RNTMainColor];
    }else {
        [self setBackgroundColor:ButtonGrayColor];
    }
    [super setSelected:selected];
}

@end
