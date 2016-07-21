//
//  RNTGPUImageVideoController.m
//  Ace
//
//  Created by Ranger on 16/4/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTGPUImageVideoController.h"
//@import CoreTelephony;
#import <CoreTelephony/CTCallCenter.h>
#import <CoreTelephony/CTCall.h>

#import "RNTWebViewController.h"
#import "RNTCaptureStartView.h"
#import "RNTCaptureNetTool.h"
#import "RNTCaptureStartModel.h"

#import "RNTUserManager.h"
#import "RNTLocationInfo.h"

#import "Masonry.h"

#import "RNTSocketSummary.h"
#import "RNTSocketTool.h"

#import "RNTPlayToolBarView.h"
#import "RNTPlaySendChatView.h"
#import "RNTPlayChatView.h"
#import "RNTPlayHostInfoView.h"
#import "RNTPlayHostBalanceView.h"
#import "RNTPlayUserListView.h"
#import "RNTPlayLikeView.h"
#import "RNTLoginViewController.h"
#import "RNTAnchorInfoView.h"
#import "RNTHomePageViewController.h"
#import "RNTCaptureStartAniView.h"
#import "RNTGiftAnimationView.h"
#import "RNTCaptureEndView.h"
#import "RNTCaptureWarnMessageView.h"

#import "RNTPlayMessageModel.h"
#import "RNTPlayUserListUserInfo.h"
#import "RNTPlayShowInfo.h"
#import "RNTPlaySendGiftInfo.h"

#import "RNTPlayNetWorkTool.h"
#import "RNTNetTool.h"

#import "GPUImage.h"
#import "EngineAdaptor.h"

#import "RNTPlaySuperGiftAnimationView.h"

#import "cocos2d.h"
//#import "PlayInfo.h"
#import "platform/ios/CCEAGLView-ios.h"
#import "CocosAppDelegate.h"

#define GiftAnimationTopMargin 50
#define ChatViewHeightBotton ((iPhone4 || iPhone5) ? 250:350)
#define ChatViewHeightTop ((iPhone4 || iPhone5) ? 130:200)

@interface RNTGPUImageVideoController ()<UIImagePickerControllerDelegate, UINavigationControllerDelegate, RNTCaptureStartViewDelegate, RNTPlayToolBarViewDelegate, RNTPlaySendChatViewDelegate, RNTPlayChatViewDelegate, RNTPlayHostInfoViewDelegate, RNTPlayUserListViewDelegate, RNTSocketToolDelegate, GPUImageVideoCameraDelegate>
@property (nonatomic, strong) RNTUser *user;

@property (nonatomic, copy) NSString *location;// 位置

@property (nonatomic, copy) NSString *upLiveUrl;
@property (nonatomic, copy) NSString *showId;

@property (nonatomic, strong) CTCallCenter *callCenter;// 来电监控

/** 开始页面*/
@property (nonatomic, strong) RNTCaptureStartView *startView;// 准备开始view
@property (nonatomic, strong) UIAlertController *actionSheetC;// 拍照 or 照片
@property (nonatomic, strong) UIImage *showImage;// 封面

/** 直播页面*/
@property (nonatomic, weak) RNTPlayToolBarView *toolBarView;
@property (nonatomic, weak) UIButton *coverButton;
@property (nonatomic, weak) RNTPlaySendChatView *sendChatView;
@property (nonatomic, weak) RNTPlayChatView *chatView;
@property (nonatomic, weak) RNTPlayHostInfoView* hostInfoView;
@property (nonatomic, weak) RNTPlayHostBalanceView *hostBaView;
@property (nonatomic, weak) RNTPlayUserListView* userListView;
@property (nonatomic, weak) RNTPlayLikeView *likeView;
@property (nonatomic, weak) RNTGiftAnimationView *animationView;
//@property (nonatomic, weak) RNTCaptureWarnMessageView *warnMessageView;
@property (nonatomic, weak) RNTPlaySuperGiftAnimationView *superGiftAnimationView;

@property (nonatomic, assign) NSInteger closeCount;// 关闭操作只执行一次

@property (nonatomic, assign) BOOL isPauseConnect;// 是否是因为暂停而重新连接

@property (nonatomic, strong) EngineAdaptor *engine;// 推流

/** 美颜*/
@property (nonatomic, strong) GPUImageView *filterView;
@property (nonatomic, strong) GPUImageVideoCamera *videoCamera;


@end

@implementation RNTGPUImageVideoController

//static  CocosAppDelegate s_sharedApplication;

- (void)viewDidLoad {
    [super viewDidLoad];

    [RNTPlayMessageModel changeMessageModelTypeTo:PlayMessageModelTypeShoot];
    
    self.view.backgroundColor = [UIColor blackColor];
    self.closeCount = 1;
    
    // 定位
    [RNTLocationInfo getLoacationInfo:^(NSString *location) {
        if ([location isEqualToString:@"error"]) {
            self.location = nil;
        }else {
            self.location = location;
        }
    }];
    
    self.user = [RNTUserManager sharedManager].user;
    
    [self setupLiveView];
    
    [self setupStartView];
    
    [self setupCallCenter];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:NO];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self.likeView stopAnimation];
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
}

- (void)dealloc
{
    RNTLog(@"$$$$$$退出房间￥￥￥￥￥");
}


#pragma mark - 相机
//static long long startTime;
- (void)setupCamera
{
//    [self checkDevice];
    
    // 相机
    // 前后位置
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    
    NSString *orientation = [userDefault objectForKey:Camera_Position];
    if (orientation) {
        if ([orientation isEqualToString:Camera_Front]) {
            
            self.videoCamera = [[GPUImageVideoCamera alloc] initWithSessionPreset:AVCaptureSessionPreset640x480 cameraPosition:AVCaptureDevicePositionFront];
            
        }else if([orientation isEqualToString:Camera_Back]){
            
            self.videoCamera = [[GPUImageVideoCamera alloc] initWithSessionPreset:AVCaptureSessionPreset640x480 cameraPosition:AVCaptureDevicePositionBack];
        }
        
    }else {
        self.videoCamera = [[GPUImageVideoCamera alloc] initWithSessionPreset:AVCaptureSessionPreset640x480 cameraPosition:AVCaptureDevicePositionFront];
    }

    self.videoCamera.delegate = self;
    self.videoCamera.outputImageOrientation = UIInterfaceOrientationPortrait;
    self.videoCamera.horizontallyMirrorFrontFacingCamera = NO;// 镜面相机
    self.videoCamera.horizontallyMirrorRearFacingCamera = NO;
    
    [self.videoCamera addAudioInputsAndOutputs];
    
    // 滤镜
    GPUImageOutput<GPUImageInput>* filterBeauty = [[GPUImageFilter alloc] initWithFragmentShaderFromFile:@"Beauty"];
    
    GPUImageCropFilter *filterCut = [[GPUImageCropFilter alloc] initWithCropRegion:CGRectMake((480.0-368.0) /2.0 / 480.0, 0.0, 368.0 / 480.0, 1.0)];
    
    GPUImageRawDataOutput *rawDataOutput = [[GPUImageRawDataOutput alloc] initWithImageSize:CGSizeMake(368.0, 640.0) resultsInBGRAFormat:NO];
    
    
    [self.videoCamera addTarget:filterBeauty];
    
    // 回显
    self.filterView = [[GPUImageView alloc] initWithFrame:self.view.bounds];
    self.filterView.fillMode = kGPUImageFillModePreserveAspectRatioAndFill;
    [self.view addSubview:self.filterView];
    [self.view sendSubviewToBack:self.filterView];
    [filterBeauty addTarget:self.filterView];
    
    [filterBeauty addTarget:filterCut];
    [filterCut addTarget:rawDataOutput];
    
    [self rotationPreviewView];// 预览画面
    
    
//    NSTimeInterval time = [[NSDate date] timeIntervalSince1970] * 1000;
//    startTime = [[NSNumber numberWithDouble:time] longLongValue];
    
    // 视频数据处理
    __unsafe_unretained GPUImageRawDataOutput * weakOutput = rawDataOutput;
    [rawDataOutput setNewFrameAvailableBlock:^{
        [weakOutput lockFramebufferForReading];
        GLubyte *outputBytes = [weakOutput rawBytesForImage];
        NSInteger bytesPerRow = [weakOutput bytesPerRowInOutput];
        
        uint8_t* pixels = outputBytes;
        NSInteger frameSize = bytesPerRow * 640;
        [self.engine encodeWith:pixels bufferSize:(int)frameSize bufferType:0];
        
//        NSTimeInterval time2 = [[NSDate date] timeIntervalSince1970] * 1000;
//        long long endTime = [[NSNumber numberWithDouble:time2] longLongValue];
//        long long useTime = endTime - startTime;
//        startTime = endTime;
        
//        RNTLog(@"used time=%lld", useTime);
        //        {
        //            int size = bytesPerRow * 640;
        //            static int cnt = 0;
        //            if(cnt == 100)
        //            {
        //                NSString *ss = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
        //                ss = [ss stringByAppendingString:@"/111.bgra"];
        //                const char* pPath = [ss UTF8String];
        //
        //                FILE* pFile = fopen(pPath, "wb");
        //                if(NULL != pFile)
        //                {
        //                    int iByteWrite = fwrite(outputBytes, 1, size, pFile);
        //                    fclose(pFile);
        //                }
        //                int j = 0;
        //            }
        //            cnt++;0
        //        }
        
        
        [weakOutput unlockFramebufferAfterReading];
    }];
    
    [self.videoCamera startCameraCapture];
}

- (void)setupSuperGiftAnimationView{
    
    RNTPlaySuperGiftAnimationView *superGiftAnimationView = [[RNTPlaySuperGiftAnimationView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:superGiftAnimationView];
    self.superGiftAnimationView = superGiftAnimationView;
    self.superGiftAnimationView.backgroundColor = [UIColor clearColor];
}
//-(void)setCocos
//{
//    cocos2d::Application *app = cocos2d::Application::getInstance();
//    app->initGLContextAttrs();
//    cocos2d::GLViewImpl::convertAttrs();
//    
//    CCEAGLView *eaglView = [CCEAGLView viewWithFrame: self.view.bounds
//                                         pixelFormat:kEAGLColorFormatRGBA8
//                                         depthFormat: GL_DEPTH24_STENCIL8_OES
//                                  preserveBackbuffer: NO
//                                          sharegroup: nil
//                                       multiSampling: NO
//                                     numberOfSamples: 0 ];
//    
//    
//    
//    //glClearColor(0, 0, 0, 0);
//    
//    cocos2d::GLView *glview = cocos2d::GLViewImpl::createWithEAGLView((__bridge void *)eaglView);
//    
//    cocos2d::Director::getInstance()->setOpenGLView(glview);
//    
//    eaglView.backgroundColor = [UIColor clearColor];
//    eaglView.opaque = NO;
//    
//    
//    
//    // Enable or disable multiple touches
//    [eaglView setMultipleTouchEnabled:NO];
//    eaglView.userInteractionEnabled = NO;
//    
//    self.view.backgroundColor = [UIColor clearColor];
//    [self.view addSubview:eaglView];
//    
////    PlayInfo *pinfo = PlayInfo::getInstance();
////    pinfo->setKeletonDataFile("spine/ship.json");
////    pinfo->setAtlasFile("spine/ship.atlas");
////    
////    pinfo->setposx( 400);
////    pinfo->setposy(300 );
//    
//    
//    app->run();
//
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(8 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        cocos2d::Director::getInstance()->end();
//    });
//    
//}
// 音频数据处理
- (void)didOuptutAudioSampleBuffer:(CMSampleBufferRef)audioSampleBuffer
{
    CMItemCount numSamples = CMSampleBufferGetNumSamples(audioSampleBuffer);
    NSUInteger channelIndex = 0;
    
    CMBlockBufferRef audioBlockBuffer = CMSampleBufferGetDataBuffer(audioSampleBuffer);
    size_t audioBlockBufferOffset = (channelIndex * numSamples * sizeof(SInt16));
    size_t lengthAtOffset = 0;
    size_t totalLength = 0;
    SInt16 *samples = NULL;
    CMBlockBufferGetDataPointer(audioBlockBuffer, audioBlockBufferOffset, &lengthAtOffset, &totalLength, (char **)(&samples));
    size_t len = CMBlockBufferGetDataLength(audioBlockBuffer);

    [self.engine encodeWith:(uint8_t *)samples bufferSize:(int)len bufferType:1];
    
}

// 翻转相机
- (void)switchCamera
{
    [self.videoCamera rotateCamera];

    [self rotationPreviewView];
}

// 翻转预览画面并存储相机位置
- (void)rotationPreviewView
{
    if (self.videoCamera.cameraPosition == AVCaptureDevicePositionFront) {
        [self.filterView setInputRotation:kGPUImageFlipHorizonal atIndex:0];
        [[NSNotificationCenter defaultCenter] postNotificationName:RNTSwitcCameraFrontNotification object:nil];
        [self saveCameraOrientation:Camera_Front];
    }else {
        [self.filterView setInputRotation:kGPUImageNoRotation atIndex:0];
        [[NSNotificationCenter defaultCenter] postNotificationName:RNTSwitcCameraBackNotification object:nil];
        [self saveCameraOrientation:Camera_Back];
    }
    self.videoCamera.frameRate = CameraFPS;
}

// 存用户的相机方向
- (void)saveCameraOrientation:(NSString *)position
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:position forKey:Camera_Position];
}

/**
// 权限提示
- (void)checkDevice
{
    AVAuthorizationStatus videoAuthStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    
    if(videoAuthStatus != AVAuthorizationStatusAuthorized){
        [self showAlertWithTitle:@"无法启动相机" message:@"是否为Ace开放相机权限"];
    }
    
    AVAuthorizationStatus audioAuthStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio];
    
    if(audioAuthStatus != AVAuthorizationStatusAuthorized){
        
        [self showAlertWithTitle:@"无法启动麦克风" message:@"是否为Ace开放麦克风权限"];
    }
}

// 对焦 曝光
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *touch = [touches anyObject];
    CGPoint touchPoint = [touch locationInView:self.view];
    
    CGPoint focusPont = CGPointMake(touchPoint.x/self.view.frame.size.width, touchPoint.y/self.view.frame.size.height);
    
    RNTLog(@"对焦 = %@", NSStringFromCGPoint(focusPont));
    
    if([self.videoCamera.inputCamera isFocusPointOfInterestSupported]&&[self.videoCamera.inputCamera isFocusModeSupported:AVCaptureFocusModeAutoFocus])
    {
        
        if([self.videoCamera.inputCamera lockForConfiguration :nil])
        {
            [self.videoCamera.inputCamera setFocusMode:AVCaptureFocusModeAutoFocus];
            [self.videoCamera.inputCamera setFocusPointOfInterest :focusPont];
//            [self.videoCamera.inputCamera setFocusMode :AVCaptureFocusModeLocked];
            if([self.videoCamera.inputCamera isExposurePointOfInterestSupported])
            {
                [self.videoCamera.inputCamera setExposureMode:AVCaptureExposureModeAutoExpose];
//                [self.videoCamera.inputCamera setExposurePointOfInterest:focusPont];
            }
            [self.videoCamera.inputCamera unlockForConfiguration];
        }
    }
}
*/


- (void)removeCamera
{
    [self.filterView removeFromSuperview];
    self.filterView = nil;
    
    [self.videoCamera removeAllTargets];
    [self.videoCamera removeInputsAndOutputs];
    [self.videoCamera stopCameraCapture];
    self.videoCamera = nil;
    
}

#pragma mark - 异常处理
- (void)setupCallCenter
{
    WEAK(self);
    self.callCenter = [[CTCallCenter alloc] init];
    self.callCenter.callEventHandler = ^(CTCall* call) {

        if (call.callState == CTCallStateIncoming) {// 来电
            RNTLog(@"电话 来电");
//            [weakself.engine setPause:YES];
//            [weakself socketLivePause:@"0"];
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakself closeBtnClick];
            });
        }else if (call.callState == CTCallStateConnected) {
            RNTLog(@"电话 连接");
            
        }else if (call.callState == CTCallStateDisconnected) {
            RNTLog(@"电话 断开");
//            weakself.videoCamera.frameRate = CameraFPS;
//            [weakself.engine setPause:NO];
//            weakself.isPauseConnect = YES;
//            [weakself socketLivePause:@"1"];
            
        };
    };
}

#pragma mark - UI
- (void)setupStartView
{
    [self.view addSubview:self.startView];
    [self.startView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.right.bottom.equalTo(self.view);

    }];
}

- (void)setupLiveView
{
//    // 创建键盘监听
//    [self setupNotification];
    
    
    [self setupSuperGiftAnimationView];
    
    // 遮罩
    UIButton* coverButton = [[UIButton alloc] init];
    [self.view addSubview:coverButton];
    self.coverButton = coverButton;
    coverButton.backgroundColor = [UIColor clearColor];
    [coverButton addTarget:self action:@selector(coverButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [coverButton makeConstraints:^(MASConstraintMaker *make) {
        //        make.top.bottom.left.right.equalTo(self.view);
        make.left.right.equalTo(self.view);
        make.height.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];
    
    // 底部条
    RNTPlayToolBarView* toolBarView = [[RNTPlayToolBarView alloc] initWithType:RNTPlayToolBarTypeAnchor];
    [self.view addSubview:toolBarView];
    self.toolBarView = toolBarView;
    toolBarView.delegate = self;
    [toolBarView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.view);
        make.height.equalTo(52);
    }];
    
    // 聊天条
    RNTPlaySendChatView* sendChatView = [[RNTPlaySendChatView alloc] init];
    sendChatView.delegate = self;
    [coverButton addSubview:sendChatView];
    self.sendChatView = sendChatView;
    sendChatView.hidden = YES;
    [sendChatView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(coverButton);
        make.height.equalTo(33);
        make.bottom.equalTo(coverButton).offset(-8);
    }];
    
    // 聊天框
    RNTPlayChatView* chatView = [[RNTPlayChatView alloc] init];
    chatView.liveType = YES;
    [coverButton addSubview:chatView];
    chatView.delegate = self;
    self.chatView = chatView;
    [chatView makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(cellMaxWidth, ChatViewHeightBotton));
        make.left.equalTo(self.coverButton);
        make.bottom.equalTo(self.coverButton).offset(-55);
    }];
    
    // 主播信息
    RNTPlayHostInfoView* hostInfoView = [[RNTPlayHostInfoView alloc] init];
    RNTPlayShowInfo *info = [[RNTPlayShowInfo alloc] initWithUser:self.user];
    info.totalLikeCount = @"0";
    info.memberCount = @"1";
    hostInfoView.info = info;
    hostInfoView.delegate = self;
    [coverButton addSubview:hostInfoView];
    self.hostInfoView = hostInfoView;
    [hostInfoView makeConstraints:^ (MASConstraintMaker *make) {
        make.top.equalTo(coverButton.top).offset(25);
        make.height.equalTo(50);
        make.left.equalTo(coverButton);
    }];
    
    RNTPlayHostBalanceView* hostBaView = [[RNTPlayHostBalanceView alloc] init];
    [self.coverButton addSubview:hostBaView];
    self.hostBaView = hostBaView;
    [hostBaView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(hostInfoView.bottom).offset(10);
        make.left.equalTo(self.coverButton);
        make.height.equalTo(25);
    }];
    
    // 用户列表
    RNTPlayUserListView* userListView = [[RNTPlayUserListView alloc] init];
    userListView.userID = self.user.userId;
    [coverButton addSubview:userListView];
    self.userListView = userListView;
    userListView.backgroundColor = [UIColor clearColor];
    userListView.delegate = self;
    [userListView makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(hostInfoView.right).offset(10);
        make.top.equalTo(hostInfoView);
        make.right.equalTo(coverButton);
        make.height.equalTo(userListIconWH);
    }];
    
    // 赞
    RNTPlayLikeView* likeView = [[RNTPlayLikeView alloc] init];
    [coverButton addSubview:likeView];
    self.likeView = likeView;
    [likeView makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(coverButton).offset(-50);
        make.bottom.equalTo(coverButton).offset(-60);
        make.size.equalTo(CGSizeMake(50, 200));
    }];
    
}


- (void)setupWarnMessageView:(NSString *)message
{
    RNTCaptureWarnMessageView *messageView = [[RNTCaptureWarnMessageView alloc] init];
    messageView.warnMessage = message;
    [self.view addSubview:messageView];
    [messageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.view).offset(8);
        make.right.equalTo(self.view).offset(-8);
        make.bottom.equalTo(self.toolBarView.top).offset(-12);
        make.height.mas_equalTo(messageView.viewHeight);
    }];

}

#pragma mark - 网络

#pragma mark socket
// 踢出
- (void)socketKickOutWithUserId:(NSString *)userId
{
    RNTSocketSummary *summary = [[RNTSocketSummary alloc] init];
    summary.requestKey = @"002-007";
    
    NSMutableDictionary *para = [NSMutableDictionary dictionary];
    para[@"starId"] = self.user.userId;
    para[@"userId"] = userId;
    para[@"showId"] = self.showId;
    
    RNTSocketTool *socekt= [RNTSocketTool shareInstance];
    
    [socekt writeMessage:para andSummary:summary withTag:303];
    
    [self.userListView removeUserID:userId];
}

// 退房间
- (void)socketExitRoom
{
    RNTSocketTool *socket = [RNTSocketTool shareInstance];
    // 退出房间
    RNTSocketSummary *summary = [[RNTSocketSummary alloc] init];
    summary.requestKey= @"002-006";
    
    NSMutableDictionary *para= [NSMutableDictionary dictionary];
    para[@"userId"] = self.user.userId;
    para[@"showId"] = self.showId;
    [socket writeMessage:para andSummary:summary withTag:302];
}


// 发送进场消息
- (void)socketComeIn:(NSString *)showId
{
    RNTSocketTool *socket = [RNTSocketTool shareInstance];
    
    RNTSocketSummary *summary = [[RNTSocketSummary alloc] init];
    summary.requestKey= @"002-001";
    
    NSMutableDictionary *para= [NSMutableDictionary dictionary];
    para[@"userId"] = self.user.userId;
    para[@"showId"] = showId;
    [socket writeMessage:para andSummary:summary withTag:301];
}

// 暂停
- (void)socketLivePause:(NSString *)pause
{
    WEAK(self);
    RNTSocketTool *socket = [RNTSocketTool shareInstance];
    // 暂停
    RNTSocketSummary *summary = [[RNTSocketSummary alloc] init];
    summary.requestKey= @"004-005";
    
    NSMutableDictionary *para= [NSMutableDictionary dictionary];
    para[@"type"] = pause;
    para[@"showId"] = self.showId;
    
    if (socket.isConnected) {

        [socket writeMessage:para andSummary:summary withTag:304];
    }else {// 已断开 重联 
        [socket socketConnectSuccess:^{
            
            if (weakself.isPauseConnect) {// 由于暂停的重联 发暂停事件
                 [socket writeMessage:para andSummary:summary withTag:304];
            }else {// 由于断网的重联，还是发进房间事件
                [weakself socketComeIn:weakself.showId];
            }
            
        } fail:nil];
    }
}

#pragma mark - 按钮点击
- (void)coverButtonClicked:(UIButton *)coverButton {
    if (!self.sendChatView.isHidden) {
        [self.sendChatView dismiss];
    }
}

// 结束直播
- (void)closeBtnClick
{
    [self.superGiftAnimationView remove];
    cocos2d::Director::getInstance()->end();
    if (self.closeCount == 1) {
        self.closeCount ++;
        
        [self.view endEditing:YES];
        [[NSNotificationCenter defaultCenter] removeObserver:self];
        WEAK(self);
        // 发送结束直播 结算数据
        [RNTCaptureEndView showPlayEndViewWithView:self.view showId:self.showId EnsureClickBlock:^{
            [self.coverButton.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
            [[RNTSocketTool shareInstance] socketDisconnect];
            
            [weakself dismissViewControllerAnimated:YES completion:nil];
        }];
        
        // 退出操作
        [self stopLive];
    }
}

// 结束直播
- (void)stopLive
{
    [self removeCamera];
    [self.engine closeEngine];
    [[RNTUserManager sharedManager] updateUserInfo];// 更新用户资料 刷新封面图片
}

// 显示资料卡片
- (void)showUserInfoViewWithUserId:(NSString *)userId
{
    //    if ([userId isEqualToString:self.user.userId]) return;
    RNTUserManager* manager = [RNTUserManager sharedManager];
    
    RNTAnchorInfoType infoType;
    if ([userId isEqualToString:self.user.userId]) {
        //        infoType = RNTAnchorInfoTypeClickSelf;
        infoType = RNTAnchorInfoTypeAnchorClickSelf;
    }else {
        infoType = RNTAnchorInfoTypeClickUser;
    }
    
    WEAK(self);
    
    [RNTAnchorInfoView showAnchorViewWithUserID:userId clickType:infoType attention:^{
        //点击关注
        RNTLog(@"我被关注了,贼开心");
        
    } homePage:^{
        //跳转主页
        RNTHomePageViewController *homePage = [[RNTHomePageViewController alloc] init] ;
        homePage.userId = userId;
        homePage.isShowInto = YES;
        [weakself.navigationController pushViewController:homePage animated:YES];
        
    } report:^{
        if (infoType == RNTAnchorInfoTypeClickUser) {// 禁言

            [RNTPlayNetWorkTool silenceWithShowID:self.user.userId silenceID:userId];
            
        }else if (infoType == RNTAnchorInfoTypeClickAnchor) {// 举报
            RNTLog(@"我被举报了,贼不开心");
            [RNTPlayNetWorkTool reportUserWithCurrentUserID:manager.user.userId reportedUserID:userId getSuccess:^(BOOL status) {
                if (status) {
                    [MBProgressHUD showSuccess:@"举报成功"];
                }else{
                    [MBProgressHUD showError:@"举报失败"];
                }
            }];
        }
    }];
}

#pragma mark - 通知
#pragma mark setupNotification
- (void)setupNotification
{
    NSNotificationCenter* notiCenter = [NSNotificationCenter defaultCenter];
    // keyboard
    [notiCenter addObserver:self selector:@selector(keyBoardFrameChange:) name:UIKeyboardWillChangeFrameNotification object:nil];
    
    // 进入后台
    [notiCenter addObserver:self selector:@selector(closeBtnClick) name:UIApplicationDidEnterBackgroundNotification object:nil];
//    [notiCenter addObserver:self selector:@selector(appToBackgroud) name:UIApplicationDidEnterBackgroundNotification object:nil];
    // 回到前台
//    [notiCenter addObserver:self selector:@selector(backgroudToApp) name:UIApplicationDidBecomeActiveNotification object:nil];
    
    // chat
    [notiCenter addObserver:self selector:@selector(playChatViewSelectedUser:) name:RNTPlayChatShowInfoNotification object:nil];
    [notiCenter addObserver:self selector:@selector(playChatViewClickLink:) name:RNTPlayChatClickLinkNotification object:nil];
}

#pragma mark NotificationCenter
- (void)keyBoardFrameChange:(NSNotification *)noti
{
    if (self.presentedViewController) {
        return;
    }
    CGFloat duration = [noti.userInfo[UIKeyboardAnimationDurationUserInfoKey] floatValue];
    CGRect endRect = [noti.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat bottomOffset = endRect.origin.y - [UIScreen mainScreen].bounds.size.height;
    
    [self.coverButton updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view.bottom).offset(bottomOffset);
    }];
    
    RNTLog(@"%f", bottomOffset);
    self.sendChatView.hidden = bottomOffset >= 0;
    if (bottomOffset >= 0) { // 下去
        [self.animationView remakeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.hostInfoView.bottom).offset(GiftAnimationTopMargin);
            make.left.equalTo(self.coverButton);
            make.size.equalTo(CGSizeMake(itemWidth, 110));
        }];
    }else{
        [self.animationView remakeConstraints:^(MASConstraintMaker *make) {
            make.bottom.equalTo(self.chatView.top).offset(-5);
            make.left.equalTo(self.coverButton);
            make.size.equalTo(CGSizeMake(itemWidth, 110));
        }];
    }
    
    if (bottomOffset >= 0) { // 下去
        [self.chatView updateConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(ChatViewHeightBotton);
        }];
    }else{
        [self.chatView updateConstraints:^(MASConstraintMaker *make) {
            make.height.equalTo(ChatViewHeightTop);
        }];
    }
    
    
    [UIView animateWithDuration:duration animations:^{
        [self.coverButton layoutIfNeeded];
    } completion:nil];
}

// 回到前台 继续推流 发事件
- (void)backgroudToApp
{
    self.videoCamera.frameRate = CameraFPS;
    [self.engine setPause:NO];
    self.isPauseConnect = YES;
    [self socketLivePause:@"1"];
}

// 进入后台 暂停推流 发事件
- (void)appToBackgroud
{
    [self.engine setPause:YES];
    [self socketLivePause:@"0"];
}

#pragma mark - 自定义代理
#pragma mark RNTSocketToolDelegate
- (void)socketDidReceiveChatMessage:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* chatDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTPlayMessageModel* model = [RNTPlayMessageModel chatMessageModelWithDict:chatDict summary:summary];
    [self.chatView addMessage:model];
    RNTLog(@"聊天 = %@---name: %@", chatDict, summary.senderName);
}

- (void)socketDidReceiveGift:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* giftDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTPlaySendGiftInfo* info = [RNTPlaySendGiftInfo giftInfoWithDict:giftDict];
    
    
    NSArray* giftDicts = [NSArray arrayWithContentsOfFile:GiftDictsPath];
    NSDictionary* giftDictCache;
    for (NSDictionary* dict in giftDicts) {
        NSString* giftID = dict.allKeys[0];
        if (giftID.intValue == info.giftId.intValue) {
            giftDictCache = dict.allValues[0];
            break;
        }
    }
    
    
    if ([giftDictCache[@"type"] intValue] == 1) {
        [self.superGiftAnimationView fireSuperAnimationWithGiftID:info.giftId];
    }else{
        [self.animationView fireItem:info];
    }
    
    RNTPlayMessageModel* model = [RNTPlayMessageModel sendGiftMessageModelWithInfo:info];
    [self.chatView addMessage:model];
}

- (void)socketDidReceiveRoomState:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* roomStateDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTPlayShowInfo* info = [[RNTPlayShowInfo alloc] init];
    info.totalLikeCount = roomStateDict[@"totalSuptCnt"];
    info.addLikeCount = roomStateDict[@"newSuptCnt"];
    info.memberCount = roomStateDict[@"memberCnt"];
    self.hostInfoView.info = info;
    for (int i = 0; i<info.addLikeCount.intValue; i++) {
        [self.likeView fireAnimation];
    }
    
    self.hostBaView.balance = roomStateDict[@"income"];
    RNTLog(@"房间动态 = %@", roomStateDict);
}

- (void)socketDidReceiveUserInOutRoom:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* inOutDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTPlayUserListUserInfo* info = [[RNTPlayUserListUserInfo alloc] init];
    info.userId = inOutDict[@"userId"];
    info.profile = inOutDict[@"profile"];
    info.sex = inOutDict[@"sex"];
    info.nickName = inOutDict[@"nickName"];
    [self.userListView addUser:info];
    
    if (![info.userId isEqualToString:@"-1"]) {
        summary.senderName = info.nickName;
        RNTPlayMessageModel* model = [RNTPlayMessageModel enterRoomMessageModelWithDict:inOutDict summary:summary];
        [self.chatView addUserEnterTip:model];
    }
    RNTLog(@"进出房间 = %@", inOutDict);
}


- (void)socketDidReceiveDownLive:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTLog(@"后台下播 = %@", dict);
    if ([dict[@"userId"] isEqualToString:self.user.userId]) {
        [self stopLive];
        [self socketExitRoom];
        [[RNTSocketTool shareInstance] socketDisconnect];
        
        [self showBadAlertWithTitle:@"直播封禁" message:dict[@"txt"]];
    }
}


- (void)socketDidReceiveBannedAccount:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTLog(@"后台封号 = %@", dict);
    if ([dict[@"userId"] isEqualToString:self.user.userId]) {
        [self stopLive];
        [self socketExitRoom];
        [[RNTSocketTool shareInstance] socketDisconnect];
        [[RNTUserManager sharedManager] logOff];
        
        [self showBadAlertWithTitle:@"账号封禁" message:dict[@"txt"]];
    }
}


/**
 *  收到广播 type 2分享 3点赞 4系统广播 5关注
 */
- (void)socketDidReceiveBroadcast:(NSData *)data summary:(RNTSocketSummary *)summary{
    NSDictionary* inOutDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    if ([inOutDict[@"type"] isEqualToString:@"1"]) {
        [self setupWarnMessageView:inOutDict[@"msg"]];
    }else {
        RNTPlayMessageModel* model = [RNTPlayMessageModel allTipsMessageModelWithDict:inOutDict summary:summary];
        [self.chatView addMessage:model];
    }
    
    RNTLog(@"tips = %@ \n %@", inOutDict, summary.senderName);
}


#pragma mark RNTPlayToolBarViewDelegate
- (void)playToolBarView:(RNTPlayToolBarView *)view button:(UIButton *)btn selectedOption:(RNTPlayToolBarOption)option
{
    switch (option) {
        case RNTPlayToolBarOptionChat:
            [self.sendChatView pop];
            break;
            
        case RNTPlayToolBarOptionMute:
            RNTLog(@"静音");
            btn.selected = !btn.selected;
            [self.engine setMute:btn.selected];
            break;
            
        case RNTPlayToolBarOptionSwitch:
            
            [self switchCamera];
            break;
            
        case RNTPlayToolBarOptionFlashLight:
            btn.selected = !btn.selected;
            [self.videoCamera flashLight:btn.selected];
            break;
            
        case RNTPlayToolBarOptionClose:
//            [self closeBtnClick];
            [self showEndAlertWithTitle:nil message:@"是否结束直播"];
            break;
            
        default:
            break;
    }
}

#pragma mark RNTPlaySendChatViewDelegate
- (void)playSendChatView:(RNTPlaySendChatView *)chatView sendMessage:(NSString *)message
{
    RNTSocketTool* tool = [RNTSocketTool shareInstance];
    RNTSocketSummary* summary = [[RNTSocketSummary alloc] init];
    
    summary.requestKey = @"002-009";
    summary.showId = self.showId;
    NSDictionary* dict = @{
                           @"senderId":[RNTUserManager sharedManager].user.userId,
                           @"receiverId":@"999999",
                           @"showId":self.showId,
                           @"txt":message
                           };
    [tool writeMessage:[dict mutableCopy]andSummary:summary withTag:202];
    
    RNTPlayMessageModel* model = [RNTPlayMessageModel chatMessageModelWithDict:dict summary:summary];
    [self.chatView addMessage:model];
}


#pragma mark RNTPlayChatViewDelegate
//- (void)playChatView:(RNTPlayChatView *)chatView selectedUser:(NSString *)userId
//{
//    [self showUserInfoViewWithUserId:userId];
//}
- (void)playChatViewSelectedUser:(NSNotification *)noti
{
    NSString* userID = noti.userInfo[RNTPlayChatShowInfoUserIDKey];
    [self showUserInfoViewWithUserId:userID];

}
- (void)playChatViewClickLink:(NSNotification *)noti
{
    NSString* urlStr = noti.userInfo[RNTPlayChatClickLinkUrlStrKey];
    RNTWebViewController* webVC = [RNTWebViewController webViewControllerWithTitle:@" " url:urlStr];
    [self.navigationController pushViewController:webVC animated:YES];
}

- (void)playChatViewTapAction:(RNTPlayChatView *)chatView
{
    [self coverButtonClicked:nil];
}

#pragma mark RNTPlayHostInfoViewDelegate
-(void)hostInfoViewClick:(RNTPlayHostInfoView *)hostInfoView withModel:(NSObject *)object
{
    [self showUserInfoViewWithUserId:self.user.userId];
}


#pragma mark RNTPlayUserListViewDelegate
- (void)userListView:(RNTPlayUserListView *)listView selectedUserWith:(RNTPlayUserListUserInfo *)object
{
    [self showUserInfoViewWithUserId:object.userId];
}


#pragma mark RNTCaptureStartViewDelegate
-(void)captureViewDidClickStartBtn:(UIButton *)btn showTitle:(NSString *)title
{
    btn.enabled = NO;
    RNTCaptureStartModel *startModel = [[RNTCaptureStartModel alloc] init];
    startModel.title = title;
    startModel.image = self.showImage;
    startModel.position = self.location;
    
    WEAK(self);
    [RNTCaptureNetTool sendStartWithModel:startModel success:^(NSDictionary *dict){
        RNTLog(@"开播 = %@", dict);
        weakself.showId = dict[@"showId"];
//        weakself.upLiveUrl = dict[@"upStream"];
        btn.enabled = YES;
        
        self.engine = [EngineAdaptor shareInstance];

        // 获取ngb地址
        [RNTNetTool getNGBWithOriginalURL:dict[@"upStream"] success:^(NSString *rtmpString) {
            // 开始采集
            [self.engine setupEngine:(char *)[rtmpString stringByReplacingOccurrencesOfString:@"\n" withString:@""].UTF8String];
        } failure:^(NSError *error) {
            // 开始采集
            NSString *url = dict[@"upStream"];
            [self.engine setupEngine:(char *)url.UTF8String];
        }];
        
        // 删除startview
        [weakself.startView remove];
//        // 显示直播view
//        [weakself setupLiveView];
        
        // 创建键盘监听
        [weakself setupNotification];

        // 打开相机
        [weakself setupCamera];
        
        // 链接socket
        RNTSocketTool *socket = [RNTSocketTool shareInstance];
        socket.delegate = self;
        [socket socketSetup];
        [socket socketConnectSuccess:^{
            //            self.connectCount++;
            //            if (self.connectCount ==1) {
            // 进场
            [weakself socketComeIn:dict[@"showId"]];
//            RNTSocketSummary *summary = [[RNTSocketSummary alloc] init];
//            summary.requestKey= @"002-001";
//            
//            NSMutableDictionary *para= [NSMutableDictionary dictionary];
//            para[@"userId"] = self.user.userId;
//            para[@"showId"] = dict[@"showId"];
//            [socket writeMessage:para andSummary:summary withTag:301];
            //            }
            
        } fail:nil];
        
        [RNTCaptureStartAniView playAnimation];
        
    }failure:^(NSDictionary *dict) {
        btn.enabled = YES;
        
        if (dict) {// 有错误信息
            if ([dict[@"errCode"] isEqualToString:@"222"]) {// 封号
                [weakself showBadAlertWithTitle:@"直播封禁" message:dict[@"errMsg"]];
                
            }else {
                
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        }
    }];
}


- (void)captureViewDidClickCloseBtn
{
    
    [self dismissViewControllerAnimated:YES completion:^{
//        [self removeCamera];
    }];
}


- (void)captureViewDidClickImageBtn
{
    [self presentViewController:self.actionSheetC animated:YES completion:nil];
}


//- (void)captureViewDidClickSwitchBtn
//{
//    [self switchCamera];
//}

#pragma mark - 懒加载
- (RNTCaptureStartView *)startView
{
    if (_startView == nil) {
        _startView = [[RNTCaptureStartView alloc] init];
        _startView.delegate = self;
        
        [_startView setCoverImage:nil imageUrl:self.user.showImg];
    }
    return _startView;
}


- (UIAlertController *)actionSheetC
{
    if (_actionSheetC == nil) {
        _actionSheetC = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
        
        WEAK(self);
        
        UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
        
        UIAlertAction *shoot = [UIAlertAction actionWithTitle:@"拍照" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//            [weakself removeCamera];
            [weakself setIconFromeCamera];
        }];
        
        UIAlertAction *photo = [UIAlertAction actionWithTitle:@"从手机相册选择" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//            [weakself removeCamera];
            [weakself setIconFromePhotoLibrary];
        }];
        
        
        [_actionSheetC addAction:cancel];
        [_actionSheetC addAction:shoot];
        [_actionSheetC addAction:photo];
    }
    return _actionSheetC;
}

- (RNTGiftAnimationView *)animationView
{
    if (!_animationView) {
        RNTGiftAnimationView* animationView = [[RNTGiftAnimationView alloc] init];
        [self.coverButton addSubview:animationView];
        _animationView = animationView;
        [animationView makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.hostInfoView.bottom).offset(GiftAnimationTopMargin);
            make.left.equalTo(self.coverButton);
            make.size.equalTo(CGSizeMake(itemWidth, 110));
        }];
    }
    
    return _animationView;
}


#pragma mark - 相册
- (void)setIconFromeCamera
{
    // 创建图片选择器
    UIImagePickerController *picker = [self imagePickerControllerWithSourceType:UIImagePickerControllerSourceTypeCamera];
    
//    //判断有无相机权限
//    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
//    if (authStatus == AVAuthorizationStatusRestricted || authStatus ==AVAuthorizationStatusDenied)
//    {
//        [self showAlertWithTitle:@"无法启动相机" message:@"是否为Ace开放相机权限"];
//    } else {
        // 3.显示相机
        [self presentViewController:picker animated:YES completion:nil];
//    }
}

- (void)setIconFromePhotoLibrary
{
    // 1.创建图片选择器
    UIImagePickerController *picker = [self imagePickerControllerWithSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    
//    ALAuthorizationStatus author = [ALAssetsLibrary authorizationStatus];
//    if (author == ALAuthorizationStatusRestricted || author ==ALAuthorizationStatusDenied)
//    {
//        [self showAlertWithTitle:@"无法启动照片" message:@"是否为Ace开放照片权限"];
//    } else {
        //显示图片选择器
        [self presentViewController:picker animated:YES completion:nil];
//    }
}


- (UIImagePickerController *)imagePickerControllerWithSourceType:(UIImagePickerControllerSourceType)sourceType
{
    // 1.创建图片选择器
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    // 设置图片是否可以编辑
    picker.allowsEditing = YES;
    picker.delegate = self;
    
    //设置选择器的类型,拍照,调取摄像头
    picker.sourceType = sourceType;
    return picker;
}


// 提示
- (void)showEndAlertWithTitle:(NSString *)title message:(NSString *)message
{
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    
    UIAlertAction *confirmAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self closeBtnClick];
    }];
    
    [alert addAction:cancelAction];
    [alert addAction:confirmAction];
    
    [self presentViewController:alert animated:YES completion:nil];
}


// 下播、封号
- (void)showBadAlertWithTitle:(NSString *)title message:(NSString *)message
{
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    
//    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    
    UIAlertAction *confirmAction = [UIAlertAction actionWithTitle:@"我知道了" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//        [self closeBtnClick];
        [self dismissViewControllerAnimated:YES completion:nil];
    }];
    
//    [alert addAction:cancelAction];
    [alert addAction:confirmAction];
    
    [self presentViewController:alert animated:YES completion:nil];
}


#pragma mark UIImagePickerControllerDelegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    WEAK(self);
    [self.presentedViewController dismissViewControllerAnimated:YES completion:^{
        
        //获取选中图片
        UIImage *image = info[@"UIImagePickerControllerEditedImage"];
        
        [weakself.startView setCoverImage:image imageUrl:nil];
        weakself.showImage = image;

//        [weakself setupCamera];
    }];
}


- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
//    WEAK(self);
    [self.presentedViewController dismissViewControllerAnimated:YES completion:^{

//        [weakself setupCamera];

    }];
}



@end
