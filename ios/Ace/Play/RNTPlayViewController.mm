//
//  RNTPlayViewController.m
//  Ace
//
//  Created by 于传峰 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayViewController.h"
#import "Masonry.h"
#import "RNTPlayView.h"
#import "RNTPlayHostInfoView.h"
#import "RNTPlayUserListView.h"
#import "RNTPlayChatView.h"
#import "RNTPlayMessageModel.h"
#import "RNTPlayToolBarView.h"
#import "RNTPlaySendChatView.h"
#import "RNTPlayLikeView.h"
#import "RNTPlayGiftView.h"
#import "RNTPlayNetWorkTool.h"
#import "RNTPlayUserListUserInfo.h"
#import "UMSocial.h"
#import "RNTAnchorInfoView.h"
#import "RNTHomePageViewController.h"
#import "RNTPlayUserInfo.h"
#import "RNTLoginViewController.h"
#import "RNTSocketTool.h"
#import "RNTSocketSummary.h"
#import "RNTPlayShowInfo.h"
#import "RNTIntoPlayModel.h"
#import "RNTRechargeController.h"
//#import "RNTPlayShareInfo.h"
#import "SDWebImageManager.h"
#import "RNTPlayShareView.h"
#import "RNTDatabaseTool.h"
#import "NSString+Extension.h"
#import "RNTPlaySendGiftInfo.h"
#import "RNTGiftAnimationView.h"
#import "RNTPlayHostDownLineView.h"
#import "RNTPlayLoadingView.h"
#import "RNTNetTool.h"
#import "RNTPlayAttentionView.h"
#import "RNTPlaySuperGiftAnimationView.h"
#import "RNTWebViewController.h"
#import "RNTPlayHostBalanceView.h"

#import "cocos2d.h"
#import "platform/ios/CCEAGLView-ios.h"
#import "CocosAppDelegate.h"

#import "platform/ios/CCDirectorCaller-ios.h"
#import "LayerManager.h"


@interface RNTPlayViewController ()<RNTPlayHostInfoViewDelegate, RNTPlayUserListViewDelegate, RNTPlayChatViewDelegate, RNTPlayToolBarViewDelegate, RNTPlayGiftViewDelegate, UMSocialUIDelegate, RNTPlaySendChatViewDelegate, RNTSocketToolDelegate, RNTPlayShareViewDelegate>
@property (nonatomic, weak) RNTPlayHostInfoView *hostInfoView;
@property (nonatomic, weak) RNTPlayHostBalanceView *hostBaView;
@property (nonatomic, weak) RNTPlaySendChatView *sendChatView;
@property (nonatomic, weak) RNTPlayUserListView *userListView;
@property (nonatomic, weak) RNTPlayLikeView *likeView;
@property (nonatomic, weak) RNTPlayChatView *chatView;
@property (nonatomic, weak) RNTGiftAnimationView *animationView;
@property (nonatomic, weak) RNTPlayLoadingView *loadingView;
@property (nonatomic, weak) RNTPlayHostDownLineView *hostDownLineView;
@property (nonatomic, weak) RNTPlayToolBarView *toolBarView;
@property (nonatomic, weak) RNTPlaySuperGiftAnimationView *superGiftAnimationView;
@property (nonatomic, weak) UIButton *coverButton;
@property (nonatomic, assign) BOOL connented;

//用户ID
@property (nonatomic, copy) NSString *userID;
// 房间ID
@property (nonatomic, copy) NSString *showID;
@end

#define SendChatHeight 33
#define GiftAnimationTopMargin 50
#define ChatViewHeight 150

@implementation RNTPlayViewController

static  CocosAppDelegate s_sharedApplication;

#pragma mark - lazy
- (RNTGiftAnimationView *)animationView
{
    if (!_animationView) {
        RNTGiftAnimationView* animationView = [[RNTGiftAnimationView alloc] init];
        [self.coverButton addSubview:animationView];
        _animationView = animationView;
        [animationView makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.coverButton).offset(GiftAnimationTopMargin + 70);
            make.left.equalTo(self.coverButton);
            make.size.equalTo(CGSizeMake(itemWidth, 110));
        }];
    }
    
    return _animationView;
}

- (void)setModel:(RNTIntoPlayModel *)model
{
    _model = model;
    self.userID = model.userId;
    self.showID = model.showId;
    RNTLog(@"%@", model.nickName);
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
}
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    self.navigationController.interactivePopGestureRecognizer.enabled = NO;
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.navigationController setNavigationBarHidden:NO animated:YES];
}
- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [self.likeView stopAnimation];
    self.navigationController.interactivePopGestureRecognizer.enabled = YES;
}
- (void)viewDidLoad {
    [super viewDidLoad];
//    self.showID = self.userID;
    [RNTPlayMessageModel changeMessageModelTypeTo:PlayMessageModelTypePlay];
    
    NSAssert(self.userID.length && self.showID.length && self.model, @"进入直播间必须设置主播id 和 直播间 id");
    if (!self.showID || self.showID.length==0) {
        return;
    }
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
//    [RNTDatabaseTool saveAnchor:self.model];
    
    [self setupSubViews];
    
    [self getData];
    
    [self setupNotification];
//
    [self connectSocket];
}


#pragma mark - subViews
- (void)setupSubViews
{
    [self setupPlayView];
    
    [self setupSuperGiftAnimationView];
    
    [self setupCoverButton];
    
    [self setupHostInfoView];
    
    [self setupUserListView];
    
    [self setupChatView];
    
    [self setupToolBarView];
    
    [self setupSendChatView];
    
    [self setupLikeView];
    
//    [self setupAttentionView];
}

- (void)setupPlayView
{
    UIImageView* backImageView = [[UIImageView alloc] init];
    [self.view addSubview:backImageView];
    backImageView.image = [UIImage imageNamed:@"play_Gaussian_Blur_image"];
    [backImageView makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];

    RNTPlayView* playView = [RNTPlayView sharedPlayView];
    [self.view addSubview:playView];
    [playView makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];

    NSString *downStreanUrl = self.model.downStreanUrl;
    [RNTNetTool getNGBWithOriginalURL:downStreanUrl success:^(NSString *rtmpString) {
        [playView playLiveVideoWithUrl:[rtmpString stringByReplacingOccurrencesOfString:@"\n" withString:@""]];
    } failure:^(NSError *error) {
        [playView playLiveVideoWithUrl:downStreanUrl];
    }];
    
    // loading
    RNTPlayLoadingView* loadingView = [[RNTPlayLoadingView alloc] init];
    [self.view addSubview:loadingView];
    self.loadingView = loadingView;
    [loadingView makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];
}

- (void)setupCoverButton
{
    RNTLog(@"setupCoverButton--");
    UIButton* coverButton = [[UIButton alloc] init];
    [self.view addSubview:coverButton];
    self.coverButton = coverButton;
    coverButton.backgroundColor = [UIColor clearColor];
    [coverButton addTarget:self action:@selector(coverButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [self.coverButton makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.view);
        make.height.equalTo(self.view);
        make.bottom.equalTo(self.view);
    }];
}
- (void)coverButtonClicked:(UIButton *)coverButton {
    if (self.sendChatView.isHidden) {
        RNTUserManager* manager = [RNTUserManager sharedManager];
        if (manager.isLogged) {
            [RNTPlayNetWorkTool sendLikeWithSenderID:manager.user.userId showID:self.showID sendSuccess:nil];
            [self.likeView fireAnimation];
        }else{
            [RNTPlayNetWorkTool sendLikeWithSenderID:@"-1" showID:self.showID sendSuccess:nil];
            [self.likeView fireAnimation];
        }
    }else{
        [self.sendChatView dismiss];
    }
}


- (void)setupHostInfoView
{
    RNTLog(@"setupHostInfoView--");
    RNTPlayHostInfoView* hostInfoView = [[RNTPlayHostInfoView alloc] init];
    hostInfoView.delegate = self;
    [self.coverButton addSubview:hostInfoView];
    self.hostInfoView = hostInfoView;
    [hostInfoView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.coverButton).offset(25);
//        make.size.equalTo(CGSizeMake(HostInfoViewWidth, 50));
        make.height.equalTo(50);
        make.left.equalTo(self.coverButton);
    }];
    
    RNTPlayHostBalanceView* hostBaView = [[RNTPlayHostBalanceView alloc] init];
    [self.coverButton addSubview:hostBaView];
    self.hostBaView = hostBaView;
    [hostBaView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(hostInfoView.bottom).offset(10);
        make.left.equalTo(self.coverButton);
        make.height.equalTo(25);
    }];
}

- (void)setupUserListView
{
    RNTPlayUserListView* userListView = [[RNTPlayUserListView alloc] init];
    userListView.userID = self.userID;
    [self.coverButton addSubview:userListView];
    self.userListView = userListView;
    userListView.backgroundColor = [UIColor clearColor];
    userListView.delegate = self;
    [userListView makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.hostInfoView.right).offset(10);
        make.top.equalTo(self.hostInfoView);
        make.right.equalTo(self.coverButton);
        make.height.equalTo(userListIconWH);
    }];
}

- (void)setupChatView
{
    RNTLog(@"setupChatView--");
    RNTPlayChatView* chatView = [[RNTPlayChatView alloc] init];
    [self.coverButton addSubview:chatView];
    chatView.delegate = self;
    self.chatView = chatView;
    [chatView makeConstraints:^(MASConstraintMaker *make) {
        make.size.equalTo(CGSizeMake(cellMaxWidth, ChatViewHeight));
        make.left.equalTo(self.coverButton);
        make.bottom.equalTo(self.coverButton).offset(-55);
    }];
}

- (void)setupToolBarView
{
    RNTPlayToolBarView* toolBarView = [[RNTPlayToolBarView alloc] initWithType:RNTPlayToolBarTypeAudience];
    [self.view addSubview:toolBarView];
    self.toolBarView = toolBarView;
    toolBarView.delegate = self;
    [toolBarView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self.view);
        make.height.equalTo(52);
    }];
}

- (void)setupSendChatView
{
    RNTPlaySendChatView* sendChatView = [[RNTPlaySendChatView alloc] init];
    sendChatView.delegate = self;
    [self.coverButton addSubview:sendChatView];
    self.sendChatView = sendChatView;
    sendChatView.hidden = YES;
    [sendChatView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.coverButton);
        make.height.equalTo(SendChatHeight);
        make.bottom.equalTo(self.coverButton).offset(-8);
    }];
}
- (void)setupLikeView
{
    RNTPlayLikeView* likeView = [[RNTPlayLikeView alloc] init];
    [self.coverButton addSubview:likeView];
    self.likeView = likeView;
    [likeView makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.coverButton).offset(-50);
        make.bottom.equalTo(self.coverButton).offset(-60);
        make.size.equalTo(CGSizeMake(50, 200));
    }];
}

//- (void)setupAttentionView
//{
//    RNTPlayAttentionView* attentionView = [[RNTPlayAttentionView alloc] init];
//    [self.coverButton addSubview:attentionView];
//    attentionView.userID = self.userID;
//    [attentionView makeConstraints:^(MASConstraintMaker *make) {
//        make.top.equalTo(self.userListView.bottom).offset(13);
//        make.right.equalTo(self.coverButton).offset(-18);
//        make.size.equalTo(CGSizeMake(40, 40));
//    }];
//}
#pragma mark - getData
- (void)getData
{
    // userList
    [RNTPlayNetWorkTool getLivingUserListWithPageNum:1 pageSize:1000 showID:self.userID getSuccess:^(NSDictionary *dict) {
        RNTPlayShowInfo* info = [[RNTPlayShowInfo alloc] init];
        info.totalLikeCount = dict[@"supportCnt"];
        info.memberCount = dict[@"memberSize"];
        info.nickName = self.model.nickName;
        info.profile = self.model.profile;
        info.userId = self.userID;
        self.hostInfoView.info = info;
    }];
}

#pragma mark - setupNotification
- (void)setupNotification
{
    NSNotificationCenter* notiCenter = [NSNotificationCenter defaultCenter];
    // keyboard
    [notiCenter addObserver:self selector:@selector(keyBoardFrameChange:) name:UIKeyboardWillChangeFrameNotification object:nil];
    
    // login
    [notiCenter addObserver:self selector:@selector(loginResult:) name:LOGIN_RESULT_NOTIFICATION object:nil];
    
    // play
    [notiCenter addObserver:self selector:@selector(playLoadEnd:) name:RNTPlayEndLoadingNotification object:nil];
    [notiCenter addObserver:self selector:@selector(playLoadStart:) name:RNTPlayStartLoadingNotification object:nil];
    
    // chat
    [notiCenter addObserver:self selector:@selector(playChatViewSelectedUser:) name:RNTPlayChatShowInfoNotification object:nil];
    [notiCenter addObserver:self selector:@selector(playChatViewClickLink:) name:RNTPlayChatClickLinkNotification object:nil];
    
    //animation
//    [notiCenter addObserver:self selector:@selector(endCocos) name:RNTGIftAnimationEnd object:nil];
}
- (void)keyBoardFrameChange:(NSNotification *)noti
{
    if (self.presentedViewController) {
        return;
    }
    CGFloat duration = [noti.userInfo[UIKeyboardAnimationDurationUserInfoKey] floatValue];
    CGRect endRect = [noti.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    CGFloat bottomOffset = endRect.origin.y - [UIScreen mainScreen].bounds.size.height;

    [self.coverButton updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.view).offset(bottomOffset);
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
    
    if (iPhone4 | iPhone5) {
        if (bottomOffset >= 0) { // 下去
            [self.chatView updateConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(ChatViewHeight);
            }];
        }else{
            [self.chatView updateConstraints:^(MASConstraintMaker *make) {
                make.height.equalTo(ChatViewHeight - 20);
            }];
        }
    }
    
    [UIView animateWithDuration:duration animations:^{
        [self.coverButton layoutIfNeeded];
    } completion:^(BOOL finished) {
//        RNTLog(@"%@", self.coverButton);
    }];
}

- (void)loginResult:(NSNotification *)noti
{
    if ([noti.userInfo[@"msg"] isEqualToString:@"success"]) {
        RNTSocketTool* socket = [RNTSocketTool shareInstance];
        [socket socketDisconnect];
        
        [self connectSocket];
//        [RNTPlayNetWorkTool connetSocketWithShowID:self.showID sendSuccess:^(NSDictionary *dict) {
//            RNTLog(@"--%@", dict);
//        }];
    }
}
- (void)playLoadEnd:(NSNotification *)noti
{

    BOOL result = [noti.userInfo[RNTPlayEndLoadResultKey] boolValue];
    if (!result) {
        if (self.hostDownLineView) {
            self.loadingView.hidden = YES;            
        }else{
//            [self.loadingView hiddenAnimationView];
            [self.sendChatView dismiss];
            UIAlertController* alertVC = [UIAlertController alertControllerWithTitle:@"视频加载失败！" message:@"请退出房间重新进入" preferredStyle:UIAlertControllerStyleAlert];
//            WEAK(self)
            [alertVC addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//                [weakself dismissPlayView];
            }]];
            [self presentViewController:alertVC animated:YES completion:nil];
        }
    }else{
        self.loadingView.hidden = YES;
    }
    
    
}


- (void)playLoadStart:(NSNotification *)noti
{
    self.loadingView.hidden = NO;
    [self.loadingView showAnimationView];
}

#pragma mark -  connectSocket
- (void)connectSocket
{
    RNTSocketTool* socket = [RNTSocketTool shareInstance];
    socket.delegate = self;
    [socket socketSetup];
    WEAK(self)
//    [self testSocket];
    [socket socketConnectSuccess:^{
//        if (weakself.connented) return ;
//        weakself.connented = YES;
        [RNTPlayNetWorkTool connetSocketWithShowID:weakself.showID sendSuccess:^(NSDictionary *dict) {
            RNTLog(@"%@", dict);
            if ([dict[@"exeStatus"] integerValue] == 1) {
                
            }else{
                [weakself showEndLiveView];
            }
        }];
        
    } fail:nil];
    
    
    
//    socket.socketResponse = ^(NSData* data, RNTSocketSummary* summ){
//        NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
//        if ([summ.requestKey isEqualToString:@"002-004"]) {
//            RNTLog(@"%@", dict);
//        }
//    };
}
#pragma mark - RNTSocketToolDelegate
- (void)socketDidReceiveChatMessage:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* chatDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTPlayMessageModel* model = [RNTPlayMessageModel chatMessageModelWithDict:chatDict summary:summary];
    [self.chatView addMessage:model];
    
    RNTLog(@"chatData = %@---name: %@", chatDict, summary.senderName);
}
- (void)socketDidReceiveGift:(NSData *)data summary:(RNTSocketSummary *)summary
{
    // 2 3 5
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
    RNTLog(@"giftData = %@", giftDict);
    
    RNTPlayMessageModel* model = [RNTPlayMessageModel sendGiftMessageModelWithInfo:info];
    [self.chatView addMessage:model];
}
- (void)socketDidReceiveRoomState:(NSData *)data summary:(RNTSocketSummary *)summary
{
    NSDictionary* roomStateDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTLog(@"roomState = %@", roomStateDict);
    RNTPlayShowInfo* info = [[RNTPlayShowInfo alloc] init];
    info.totalLikeCount = roomStateDict[@"totalSuptCnt"];
    info.addLikeCount = roomStateDict[@"newSuptCnt"];
    info.memberCount = roomStateDict[@"memberCnt"];
    info.userId = self.userID;
    self.hostInfoView.info = info;
    if(!self.sendChatView.isHidden) return;
    for (int i = 0; i<info.addLikeCount.intValue; i++) {
        [self.likeView fireAnimation];
    }
    
    self.hostBaView.balance = roomStateDict[@"income"];
}

- (void)socketDidReceiveKickOut:(NSData *)data summary:(RNTSocketSummary *)summary
{
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        return;
    }
    NSDictionary* kiceOutDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    
    if (manager.user.userId.integerValue == [kiceOutDict[@"userId"] integerValue]) {
        [self dismissPlayView];
        [MBProgressHUD showError:@"您已经被踢出房间！"];
    }
}
- (void)socketDidReceiveEnd:(NSData *)data summary:(RNTSocketSummary *)summary
{
    [self showEndLiveView];
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
    
    if (info.userId.integerValue > 0) {
        summary.senderName = info.nickName;
        RNTPlayMessageModel* model = [RNTPlayMessageModel enterRoomMessageModelWithDict:inOutDict summary:summary];
        [self.chatView addUserEnterTip:model];
    }
//    RNTLog(@"inOutData = %@", inOutDict);
}

/**
 *  收到广播 type 1礼物 2分享 3点赞 4系统广播 5关注
 */
- (void)socketDidReceiveBroadcast:(NSData *)data summary:(RNTSocketSummary *)summary{
    NSDictionary* inOutDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    RNTPlayMessageModel* model = [RNTPlayMessageModel allTipsMessageModelWithDict:inOutDict summary:summary];
    [self.chatView addMessage:model];
    RNTLog(@"tips = %@ \n %@", inOutDict, summary.senderName);
}

/**
 *  封禁账号
 */
//- (void)socketDidReceiveBannedAccount:(NSData *)data summary:(RNTSocketSummary *)summary{
//    NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
//    RNTLog(@"账号封 ＝ %@", dict);
//}


/**
 *  直播暂停
 */
//- (void)socketDidReceivePause:(NSData *)data summary:(RNTSocketSummary *)summary
//{
//    NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
//    RNTLog(@"直播暂停 = %@", dict);
//    int type = [dict[@"type"] intValue];
//    if (type == 0) {
//        self.loadingView.hidden = NO;
//        [self.loadingView showLeaveView];
//    }else if (type == 1){
//        self.loadingView.hidden = YES;
//    }
//}


- (void)showLoginView
{
    [self.sendChatView dismiss];
    UIAlertController* alertVC = [UIAlertController alertControllerWithTitle:@"请登录！" message:nil preferredStyle:UIAlertControllerStyleAlert];
    [alertVC addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil]];
    WEAK(self)
    [alertVC addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        RNTLog(@"登录----");
        RNTLoginViewController* loginVC = [[RNTLoginViewController alloc] init];
        UINavigationController* navVC = [[UINavigationController alloc] initWithRootViewController:loginVC];
        [weakself presentViewController:navVC animated:YES completion:nil];
    }]];
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self presentViewController:alertVC animated:YES completion:nil];
//    });
    
}

- (void)showShutView:(NSString*)message{
    [self.sendChatView dismiss];
    UIAlertController* alertVC = [UIAlertController alertControllerWithTitle:@"账号封禁" message:message preferredStyle:UIAlertControllerStyleAlert];
    WEAK(self)
    [alertVC addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        RNTLog(@"登录----");
        [weakself dismissPlayView];
    }]];
    
    [self presentViewController:alertVC animated:YES completion:nil];
}

- (void)showUserInfoViewWithUserID:(NSString *)userID
{
    if (!userID || userID.integerValue == -1) {
        return;
    }
    RNTAnchorInfoType type;
    RNTUserManager* manager = [RNTUserManager sharedManager];
//    if (manager.user.userId.integerValue == userID.integerValue) {
//        return;
//    }
    if (manager.isLogged) {
        if (userID.integerValue == manager.user.userId.integerValue) {
            type = RNTAnchorInfoTypeClickSelf;
        }else{
            type = RNTAnchorInfoTypeClickAnchor;
        }
    }else{
        type = RNTAnchorInfoTypeClickAnchor;
    }
    
    [RNTAnchorInfoView showAnchorViewWithUserID:userID clickType:type attention:^{
        
        //点击关注
        RNTLog(@"我被关注了,贼开心");
        if (!manager.isLogged) {
            [self showLoginView];
        }
        
    } homePage:^{
        //跳转主页
        RNTHomePageViewController *homePage = [[RNTHomePageViewController alloc] init] ;
        homePage.userId =userID ;
         homePage.isShowInto = YES;
        [self.navigationController pushViewController:homePage animated:YES];
    } report:^{
        //点击举报
        RNTLog(@"我被举报了,贼不开心");
        if (manager.isLogged) {
            [RNTPlayNetWorkTool reportUserWithCurrentUserID:manager.user.userId reportedUserID:self.userID getSuccess:^(BOOL status) {
                if (status) {
                    [MBProgressHUD showSuccess:@"举报成功"];
                }else{
                    [MBProgressHUD showError:@"举报失败"];
                }
            }];
        }else{
            [self showLoginView];
        }
    }];
}

#pragma mark - RNTPlayChatViewDelegate
- (void)playChatViewSelectedUser:(NSNotification *)noti
{
    NSString* userID = noti.userInfo[RNTPlayChatShowInfoUserIDKey];
    if (userID.intValue == -1) {
        [self coverButtonClicked:nil];
    }else{
        [self showUserInfoViewWithUserID:userID];
    }
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

#pragma mark - RNTPlayHostInfoViewDelegate
-(void)hostInfoViewClick:(RNTPlayHostInfoView *)hostInfoView withModel:(NSObject *)object
{
    [self showUserInfoViewWithUserID:self.userID];
}
#pragma mark - RNTPlayUserListViewDelegate
- (void)userListView:(RNTPlayUserListView *)listView selectedUserWith:(RNTPlayUserListUserInfo *)object
{
    [self showUserInfoViewWithUserID:object.userId];
}



#pragma mark - RNTPlayGiftViewDelegate
- (void)playGiftViewShowLoginView:(RNTPlayGiftView *)giftView
{
    [self showLoginView];
}
- (void)playGiftViewShowChargeView:(RNTPlayGiftView *)giftView
{
    RNTRechargeController* chargeVC = [[RNTRechargeController alloc] init];
    [self.navigationController pushViewController:chargeVC animated:YES];
}
#pragma mark - RNTPlaySendChatViewDelegate
- (void)playSendChatViewShowLoginView:(RNTPlaySendChatView *)chatView
{
    [self playGiftViewShowLoginView:nil];
}
- (void)playSendChatView:(RNTPlaySendChatView *)chatView sendMessage:(NSString *)message
{
    [RNTPlayNetWorkTool sendChatWithReceiverID:@"999999" showID:self.showID message:message sendSuccess:^(NSDictionary *dict) {
        if ([dict[@"exeStatus"] intValue] == 1) {
            RNTUserManager* manager = [RNTUserManager sharedManager];
            RNTSocketSummary* summary = [[RNTSocketSummary alloc] init];
            summary.senderName = manager.user.nickName;
            summary.requestKey = @"002-009";
            summary.showId = self.showID;
            NSString* userID = [RNTUserManager sharedManager].user.userId;
            NSDictionary* dict = @{
                                   @"senderId":userID ? userID : @"-1",
                                   @"receiverId":@"999999",
                                   @"showId":self.showID ? self.showID : @"-1",
                                   @"txt":message
                                   };
            RNTPlayMessageModel* model = [RNTPlayMessageModel chatMessageModelWithDict:dict summary:summary];
            [self.chatView addMessage:model];
        }else if ([dict[@"errCode"] intValue] == 221){
            NSString* errMsg = dict[@"errMsg"];
            RNTPlayMessageModel* model = [RNTPlayMessageModel systemTipsMessageModelWithCotent:errMsg];
            [self.chatView addMessage:model];
        }else if ([dict[@"errCode"] intValue] == 223){
            [self showShutView:dict[@"errMsg"]];
        }
    }];
    
}

#pragma mark - RNTPlayShareViewDelegate
- (void)playShareViewShowLoginView:(RNTPlayShareView *)shareView
{
    [self showLoginView];
}

#pragma mark - RNTPlayToolBarViewDelegate

//弹出动画
- (void)popGiftView
{
    WEAK(self)
    self.toolBarView.hidden = YES;
    self.chatView.hidden = YES;
    RNTPlayGiftView* giftView = [RNTPlayGiftView showWithUserID:self.userID showID:self.showID dismiss:^{
        weakself.toolBarView.hidden = NO;
        weakself.chatView.hidden = NO;
    }];
    giftView.delegate = self;
}
- (void)playToolBarView:(RNTPlayToolBarView *)view button:(UIButton *)btn selectedOption:(RNTPlayToolBarOption)option
{
    NSLog(@"%zd--", option);
    switch (option) {
        case RNTPlayToolBarOptionClose:
            [self dismissPlayView];
            break;
        case RNTPlayToolBarOptionChat:
            [self popKeyboardView];
            break;
        case RNTPlayToolBarOptionGift:
            [self popGiftView];
            break;
        case RNTPlayToolBarOptionShare:
            [self setupShareView];
            break;
            
        default:
            break;
    }
}

- (void)showEndLiveView
{
    RNTPlayView* playView = [RNTPlayView sharedPlayView];
    [playView stop];
    
    [self dismissAlerView];
    [[RNTSocketTool shareInstance] socketDisconnect];
    WEAK(self)
    RNTPlayHostDownLineView* downView = [RNTPlayHostDownLineView showDownLineViewWithUseID:self.userID selectedClosed:^{
        RNTLog(@"close---");
        [weakself removePlayView];
    } selectedAttention:^{
        RNTUserManager* manager = [RNTUserManager sharedManager];
        if (!manager.isLogged) {
            [weakself showLoginView];
        }
    }];
    [self.view addSubview:downView];
    self.hostDownLineView = downView;
    [downView makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];
}

- (void)popKeyboardView
{
    [self.sendChatView pop];
}

- (void)setupShareView
{
    WEAK(self)
    self.toolBarView.hidden = YES;
    self.chatView.hidden = YES;
    RNTPlayShareView* shareView = [RNTPlayShareView popShareViewWithShowID:self.showID dismiss:^{
        weakself.toolBarView.hidden = NO;
        weakself.chatView.hidden = NO;
    }];
    shareView.delegate = self;
//    __block RNTPlayShareInfo* info = [[RNTPlayShareInfo alloc] init];
//    info.playInfo = self.model;
//    RNTUserManager* manager = [RNTUserManager sharedManager];
//    if (manager.isLogged) {
//        info.userId = manager.user.userId;
//        info.nickName = manager.user.nickName;
//    }else{
//        info.userId = @"-1";
//        info.nickName = @"";
//    }
//    [[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:self.model.showImg] options:0 progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
//        info.image = image ? image : [UIImage imageNamed:@"PlaceholderIcon"];
//        
//        WEAK(self)
//        self.toolBarView.hidden = YES;
//        self.chatView.hidden = YES;
//        RNTPlayShareView* shareView = [RNTPlayShareView popShareViewWithInfo:info dismiss:^{
//            weakself.toolBarView.hidden = NO;
//            weakself.chatView.hidden = NO;
//        }];
//        shareView.delegate = self;
//    }];
}



- (void)dismissPlayView
{
    
    RNTPlayView* playView = [RNTPlayView sharedPlayView];
    [playView stop];
    [[RNTSocketTool shareInstance] socketDisconnect];
    
    [self dismissAlerView];
    [self removePlayView];
    RNTLog(@"stopPlayView===");
}

- (void)removePlayView
{
    [self.superGiftAnimationView remove];
    cocos2d::Director::getInstance()->end();
  
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self.coverButton.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)dismissAlerView
{
    [RNTPlayGiftView dismiss];
    [self.sendChatView  dismiss];
    [RNTAnchorInfoView dismiss];
    [RNTPlayShareView dismiss];
}

- (void)dealloc
{
    
    RNTLog(@"--");
}



#pragma mark - cocos

- (void)setupSuperGiftAnimationView{
    
    
    RNTPlaySuperGiftAnimationView *superGiftAnimationView = [[RNTPlaySuperGiftAnimationView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:superGiftAnimationView];
    self.superGiftAnimationView = superGiftAnimationView;
    self.superGiftAnimationView.backgroundColor = [UIColor clearColor];
    
//    [self setCocos];
}

////设置cocos动画
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
//    cocos2d::GLView *glview = cocos2d::GLViewImpl::createWithEAGLView((__bridge void *)eaglView);
//    
//    cocos2d::Director::getInstance()->setOpenGLView(glview);
//    
//    eaglView.backgroundColor = [UIColor clearColor];
//    eaglView.opaque = NO;
//    [eaglView setMultipleTouchEnabled:NO];
//    eaglView.userInteractionEnabled = NO;
//    
//    
//    [self.superGiftAnimationView addSubview:eaglView];
//    
//    app->run();
//}
@end
