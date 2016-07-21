//
//  RNTUserManager.h
//  Ace
//
//  Created by 周兵 on 16/3/7.
//  Copyright © 2016年 RNT. All rights reserved.
//



#import <Foundation/Foundation.h>
#import "Singleton.h"
#import "RNTUser.h"

@class RNTSysConfigModel;


@interface RNTUserManager : NSObject

//三方登录类型
typedef NS_ENUM(NSInteger, thirdType) {
    LOGIN_QQ = 0,
    LOGIN_WX,
    LOGIN_SINA
};

//用户信息
@property (nonatomic, strong) RNTUser *user;
//token
@property(nonatomic,copy)NSString *userToken;
//是否登录  用于我的界面
@property (nonatomic, assign, getter=isLogged)BOOL logged;
//是否三方登录
@property (nonatomic, assign, getter=isThirdPartLogged)BOOL thirdPartLogged;


//总控模型
@property(nonatomic,strong) RNTSysConfigModel *sysConfigModel;

//余额
@property (nonatomic, copy) NSString *blance;

/**
 *  刷新余额
 */
- (void)refreshBlanceSuccess:(void (^)(NSString *blance))success;

/**
 *  根据是否过期自动登录
 */
- (void)autoLogin;

/**
 *  普通登录
 *
 *  @param userName     用户名
 *  @param userPassword 密码
 */
- (void)loginWithName:(NSString *)userName password:(NSString *)userPassword;

/**
 *  第三方登录
 *
 *  @param loginType       登录类型
 *  @param loginController 登录的控制器
 */
-(void)loginByThirdPart:(thirdType) loginType loginVC:(UIViewController *)loginController;

/**
 *  退出登陆
 */
- (void)logOff;

/**
 *  更新用户信息
 */
-(void)updateUserInfo;


/**
 *  添加账号
 *
 *  @param account 账号
 */
- (void)addUserAccout:(NSString *)account;
/**
 *  已存储的账号
 *
 *  @return 数组
 */
+ (NSMutableArray *)userAccouts;


/**
 *  删除已存储的账号
 *
 *  @param index 数组里的第几位
 */
+ (void)deleteAccountWithIndex:(unsigned long)index;

+ (void)getSysConfig;
/**
 *  是否可分享
 *
 *  @return YES，可进入 NO，不可进入
 */
+ (BOOL)canShare;
/**
 *  是否显示A豆兑换
 *
 *  @return YES，可进入 NO，不可进入
 */
+ (BOOL)canShowSilverExchange;
/**
 *  是否显示评论
 *
 *  @return YES，可进入 NO，不可进入
 */
+ (BOOL)canShowComments;

SingletonH(Manager)

@end
