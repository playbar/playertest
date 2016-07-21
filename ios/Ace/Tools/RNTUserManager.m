//
//  RNTUserManager.m
//  Ace
//
//  Created by 周兵 on 16/3/7.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTUserManager.h"
#import "RNTNetTool.h"
#import "UMSocial.h"
#import "NSString+Extension.h"
#import "MJExtension.h"
#import "UMOnlineConfig.h"
#import "RNTPlayNetWorkTool.h"
#import "RNTLaunchImage.h"
#import "RNTJPushTool.h"
#import "RNTBootPicModel.h"
#import "RNTSysConfigModel.h"
#import "AFNetworking.h"
#import "SSZipArchive.h"

#define VersionString [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]

@implementation RNTUserManager

SingletonM(Manager)

- (void)refreshBlanceSuccess:(void (^)(NSString *blance))success
{
    if (self.user.userId == nil) return;
    NSDictionary *dict = @{@"userId" : self.user.userId};
    
    [RNTNetTool postJSONWithURL:@"001-002" params:dict success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            _blance = responseDic[@"blance"];
            if (success) {
                success(_blance);
            }
        }else{
            _blance = @"0";
        }
    } failure:^(NSError *error) {
        RNTLog(@"%@", error.localizedDescription);
    }];
}

#pragma mark - 登录
//自动登录
- (void)autoLogin
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *openID = [userDefaults objectForKey:USER_OPENID];
    NSString *userID = [userDefaults objectForKey:ACCESSTOKEN];
    NSString *channel = [userDefaults objectForKey:USER_CHANNEL];
    //三方登录
    if (openID.length != 0 && channel.length != 0) {
        
        NSDictionary *param = @{@"channel":channel,
                                @"tokenId":openID};
        [self thirdLoginWithParams:param];
        
    }else if(userID.length != 0 && userID.intValue != -1){
        
        //本地登录
        [self loginWithName:userID password:[[NSUserDefaults standardUserDefaults] objectForKey:USER_PASSWORD]];
        
    }else{
//        self.noAcount = YES;
//        [MBProgressHUD showError:@"登录失败"];
    }
}

/**
 *  普通登录
 *
 *  @param userName     用户名
 *  @param userPassword 密码
 */
- (void)loginWithName:(NSString *)userName password:(NSString *)userPassword
{
    if (userName == nil || userName.length == 0 || userPassword ==nil || userPassword.length == 0) return;
    
    NSString *channel;
    if (userName.length == 11) {
        channel = @"3";
    }else{
        channel = @"4";
    }
    NSDictionary *param = @{@"channel":channel,
                            @"passwd":userPassword,
                            @"tokenId":userName
                            };
    
    [RNTNetTool postJSONWithURL:@"001-013" params:param success:^(NSDictionary *responseDic) {
        
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            
            [self addUserAccout:userName];
            
            self.user = [RNTUser mj_objectWithKeyValues:responseDic[@"user"]];
            
            [[NSUserDefaults standardUserDefaults] setObject:self.user.userId forKey:ACCESSTOKEN];
            [[NSUserDefaults standardUserDefaults] setObject:userPassword forKey:USER_PASSWORD];
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:USER_OPENID];
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:USER_CHANNEL];
            
            [[NSUserDefaults standardUserDefaults] synchronize];
            
            [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"msg":@"success"}];
            
            self.logged = YES;
//            self.noAcount = NO;
            [self refreshBlanceSuccess:nil];
            
            NSString *serverRegistrationID = responseDic[@"user"][@"extendData"][@"pushDevNo"];
            NSString *registrationID = [JPUSHService registrationID];
            
            if (![serverRegistrationID isEqualToString:registrationID]) {
                //注册极光
                [RNTJPushTool registrationLocalJPushServerWithUserId:self.user.userId registrationID:registrationID];
            }
        }else{

            [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"error":[NSString getErrorMessageWIthErrorCode:responseDic[@"errCode"]]}];
        }
    } failure:^(NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"error":@"登录超时"}];
    }];
}


/**
 *  第三方登录
 *
 *  @param loginType       登录类型
 *  @param loginController 登录的控制器
 */
-(void)loginByThirdPart:(thirdType) loginType loginVC:(UIViewController *)loginController;
{
    
    //判断由谁登录
    NSString *channel;
    NSString *login;
    switch (loginType) {
        case 0:
            login = UMShareToQQ;
            channel = @"2";
            break;
        case 1:
            login = UMShareToWechatSession;
            channel = @"1";
            break;
        case 2:
            login = UMShareToSina;
            channel = @"5";
            break;
            
        default:
            break;
    }
    
    //QQ\微信\新浪登录
    UMSocialSnsPlatform *snsPlatform = [UMSocialSnsPlatformManager getSocialPlatformWithName:login];
    
    snsPlatform.loginClickHandler(loginController,[UMSocialControllerService defaultControllerService],YES,^(UMSocialResponseEntity *response){
        //登录成功
        if (response.responseCode == UMSResponseCodeSuccess) {
            
            UMSocialAccountEntity *snsAccount = [[UMSocialAccountManager socialAccountDictionary] valueForKey:login];
            if (snsAccount == nil) {
                [MBProgressHUD showError:@"系统错误,请重试"];
                return;
            }
            NSString *openId;
            switch (loginType) {
                case 0:
                    openId = snsAccount.openId;
                    break;
                case 1:
                    openId = snsAccount.unionId;
                    break;
                case 2:
                    openId = snsAccount.usid;
                    break;
                    
                default:
                    break;
            }
            NSDictionary *param = @{@"channel":channel,
                                    @"nickName":snsAccount.userName,
                                    @"profile":snsAccount.iconURL,
                                    @"tokenId":openId};
            
            [self thirdLoginWithParams:param];
 
            
        }else if (response.responseCode == UMSResponseCodeNetworkError){
            
            [MBProgressHUD showError:@"网络错误" ];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [MBProgressHUD hideHUD];
            });
            
        }else if(response.responseCode == UMSResponseCodeCancel){
            
            [MBProgressHUD showError:@"您已取消登录"];
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [MBProgressHUD hideHUD];
            });
            
        }
    });
}


//三方登录
-(void)thirdLoginWithParams:(NSDictionary *)param
{
    NSString *tokenID = param[@"tokenId"];
    if (param.count == 0 || param == nil || !(tokenID.length)) {
        [MBProgressHUD showError:@"系统错误,请重试"];
        return;
    }
    [RNTNetTool postJSONWithURL:@"001-013" params:param success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {

            self.user = [RNTUser mj_objectWithKeyValues:responseDic[@"user"]];            
            [[NSUserDefaults standardUserDefaults] setObject:param[USER_CHANNEL] forKey:USER_CHANNEL];
            [[NSUserDefaults standardUserDefaults] setObject:self.user.tokenId forKey:USER_OPENID];
            [[NSUserDefaults standardUserDefaults] setObject:nil  forKey:ACCESSTOKEN];
            
            self.logged = YES;
//            self.noAcount = NO;
            self.thirdPartLogged = YES;
            [self refreshBlanceSuccess:nil];
            
            NSString *serverRegistrationID = responseDic[@"user"][@"extendData"][@"pushDevNo"];
            NSString *registrationID = [JPUSHService registrationID];
            
            if (![serverRegistrationID isEqualToString:registrationID]) {
                //注册极光
                [RNTJPushTool registrationLocalJPushServerWithUserId:self.user.userId registrationID:registrationID];
            }
            
            [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"msg":@"success"}];
        }else{
            NSString *errorMsg = [NSString getErrorMessageWIthErrorCode:responseDic[@"errCode"]];
           [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"error":errorMsg}];
        }
    } failure:^(NSError *error) {
        [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"error":@"登录超时"}];
    }];
}

//更新用户信息
-(void)updateUserInfo
{
    NSString *userToken = self.user.userId;

    if (!userToken) {
        return;
    }
    
    NSDictionary *params = @{@"starId":userToken};
    
    [RNTNetTool postJSONWithURL:@"001-001" params:params success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            self.user = [RNTUser mj_objectWithKeyValues:responseDic[@"user"]];
            RNTLog(@"更新用户信息成功");

        }
    } failure:^(NSError *error) {
         RNTLog(@"更新用户信息失败");
    }];
    
}


//登出
- (void)logOff
{
    self.logged = NO;
    self.thirdPartLogged = NO;
    self.userToken = nil;
    _blance = @"0";
    self.user = [[RNTUser alloc] init];
//    self.noAcount = YES;
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:nil forKey:USER_PASSWORD];
    [userDefaults setObject:nil forKey:USER_OPENID];
    [userDefaults setObject:nil forKey:ACCESSTOKEN];
}

#pragma mark - 账号存储

//每一个新的用户登陆后判断是否为同一个用户  不是 再看是否有三个用户  如果有三个 把最后一个删除  不是就添加   同一个用户 则交换位置
//添加账户
- (void)addUserAccout:(NSString *)account
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSMutableArray *userAccountArr = [RNTUserManager userAccouts];
    if (!userAccountArr) {
        userAccountArr = [NSMutableArray arrayWithCapacity:3];
    }
    for (int i = 0; i < userAccountArr.count; i ++) {
        if ([account isEqualToString:userAccountArr[i]]) {
            [userAccountArr removeObjectAtIndex:i];
        }
    }
    if (userAccountArr.count == 3) {
        [userAccountArr removeObjectAtIndex:2];
    }
    [userAccountArr insertObject:account atIndex:0];
    NSData *accoutsData = [NSKeyedArchiver archivedDataWithRootObject:userAccountArr];
    
    [userDefaults setObject:accoutsData forKey:USER_ACCOUNTS];
    [userDefaults synchronize];
}
/**
 *  已存储的账号
 *
 *  @return 数组
 */
+ (NSMutableArray *)userAccouts
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSData *accoutsData = [userDefaults objectForKey:USER_ACCOUNTS];
    NSMutableArray *userAccountArr = [NSKeyedUnarchiver unarchiveObjectWithData:accoutsData];
    return userAccountArr;
}

/**
 *  删除已存储的账号
 *
 *  @param index 数组里的第几位
 */
+ (void)deleteAccountWithIndex:(unsigned long)index
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];

    NSMutableArray *userAccountArr = [RNTUserManager userAccouts];
    [userAccountArr removeObjectAtIndex:index];
    
    NSData *accoutsData = [NSKeyedArchiver archivedDataWithRootObject:userAccountArr];
    
    [userDefaults setObject:accoutsData forKey:USER_ACCOUNTS];
    [userDefaults synchronize];

}

+ (BOOL)canShare
{
    //提交审核时将友盟后台配置改为当前审核版本，当为审核版本或者配置为空时直播间分享开关为关闭状态
    NSString *CanShare =[UMOnlineConfig getConfigParams:@"CanShare"];
    
    if (!CanShare || [CanShare isEqualToString:VersionString]) {
        return NO;
    }
    return YES;
}

+ (BOOL)canShowSilverExchange
{
    //提交审核时将友盟后台配置改为当前审核版本，当为审核版本或者配置为空时A豆兑换开关为关闭状态
    NSString *CanShare =[UMOnlineConfig getConfigParams:@"CanShowSilverExchange"];
    
    if (!CanShare || [CanShare isEqualToString:VersionString]) {
        return NO;
    }
    return YES;
}

+ (BOOL)canShowComments
{
    //提交审核时将友盟后台配置改为当前审核版本，当为审核版本或者配置为空时提示评论开关为关闭状态
    NSString *CanShare =[UMOnlineConfig getConfigParams:@"CanShowComments"];
    
    if (!CanShare || [CanShare isEqualToString:VersionString]) {
        return NO;
    }
    return YES;
}

#pragma mark - 全局接口
/*
 sysUpdateType : 1,
	sysVer : 2.94,
	exeStatus : 1,
	ad : 1,
	bootPic : http://172.16.150.21/images/boot.jpg,
	goodVer : 2.94,
	giftVer : 2.94
 */
+ (void)getSysConfig
{
    [RNTNetTool getJSONWithURL:@"009-001" params:nil success:^(NSDictionary *responseDic) {

        if ([responseDic[@"exeStatus"] intValue] == 1) {
            
            RNTSysConfigModel *sysModel = [RNTSysConfigModel mj_objectWithKeyValues:responseDic];
            
            RNTUserManager *mgr = [RNTUserManager sharedManager];
            
            // 存储网络启动图
            [RNTLaunchImage showPic:sysModel.bootPic];
            
            NSString *oldGiftVersion = mgr.sysConfigModel.giftVer;
            NSString *giftVersion = sysModel.giftVer;
            
            if (!oldGiftVersion || ![oldGiftVersion isEqualToString:@"122"]) {
                [RNTPlayNetWorkTool reDownloadGiftImages];
            }
            
            mgr.sysConfigModel = sysModel;
            [RNTSysConfigModel saveSysConfigModel:sysModel];
            
        }
        RNTLog(@"%@", responseDic);

    } failure:nil];
}

#pragma mark - 懒加载
-(RNTUser *)user
{
    if (!_user) {
        _user = [[RNTUser alloc] init];
    }
    return _user;
}

-(RNTSysConfigModel *)sysConfigModel
{
    if (!_sysConfigModel) {
        _sysConfigModel = [RNTSysConfigModel getSysConfigModel];
    }
    return _sysConfigModel;
}

@end
