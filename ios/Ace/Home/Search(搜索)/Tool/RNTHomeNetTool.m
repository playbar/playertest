//
//  RNTHomeNetTool.m
//  Ace
//
//  Created by 靳峰 on 16/3/6.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTHomeNetTool.h"
#import "RNTNetTool.h"
#import "RNTShowListModel.h"
#import "MJExtension.h"
#import "RNTUser.h"
#import "RNTDatabaseTool.h"
#import "RNTBannerModel.h"
#import "NSString+Extension.h"
#import "RNTIntoPlayModel.h"
#import "RNTJPushTool.h"

@implementation RNTHomeNetTool
//获得直播列表
+ (void)getShowListWithPageNum:(NSInteger)page pageSize:(NSInteger)size  getSuccess:(void (^)(NSArray* showArr,NSArray* userArr))success getFail:(void (^)())failed
{
    NSDictionary* params = @{
                             @"page":@(page),
                             @"pageSize":@(size),
                             };
    [RNTNetTool postJSONWithURL:@"002-004" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus" ] isEqualToString:@"1"]) {
            
            NSArray *showArr = responseDic[@"showList"];
            NSArray  *userArr = responseDic[@"userList"];

            NSArray *showListModel = [RNTShowListModel mj_objectArrayWithKeyValuesArray:showArr];
            NSArray *userModel = [RNTUser mj_objectArrayWithKeyValuesArray:userArr];
            success(showListModel,userModel);
    
            
            for (RNTShowListModel *listModel in showListModel) {
//                NSLog(@"title%@",listModel.title);
                if (listModel.title.length>0 && listModel != nil) {
                    listModel.cellHeight = (491 *  kScreenWidth / 375);
                }else{
                     listModel.cellHeight = (451 *  kScreenWidth / 375);
                }
            }
            //写入缓存
//            if (showListModel.count != 0) {
//
//                [RNTDatabaseTool updataHomepageWithShowList:showArr userList:userArr];
//            }

        }
    } failure:^(NSError *error) {
        if (failed) {
            failed();
        }
    }];

}

//获得关注列表
+ (void)getSubscribeListWithUserID:(NSString *)userID  getSuccess:(void (^)(NSArray* userArr,NSArray *showArr,NSString *headTittle))success getFail:(void (^)())failed
{
    NSDictionary* params;
    if (userID.length == 0 || userID == nil || userID.intValue == -1) {
        params = nil;
    }else{
        params = @{@"userId":userID};
    }
    [RNTNetTool postJSONWithURL:@"001-029" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            if (responseDic[@"showList"] != nil) {
                NSArray *showArr = [RNTShowListModel mj_objectArrayWithKeyValuesArray:responseDic[@"showList"]];
                NSArray *userArr = [RNTUser mj_objectArrayWithKeyValuesArray:responseDic[@"userList"]];
                
                for (RNTShowListModel *listModel in showArr) {
                    if (listModel.title.length>0 && listModel != nil) {
                        listModel.cellHeight = (491 *  kScreenWidth / 375);
                    }else{
                        listModel.cellHeight = (451 *  kScreenWidth / 375);
                    }
                }
                
                success(userArr,showArr,nil);
                
            }else if(responseDic[@"recommandList"] !=nil){
                
                 NSArray *showArr = [RNTShowListModel mj_objectArrayWithKeyValuesArray:responseDic[@"recommandList"]];
                 NSArray *userArr = [RNTUser mj_objectArrayWithKeyValuesArray:responseDic[@"userList"]];
                
                for (RNTShowListModel *listModel in showArr) {
                    if (listModel.title.length>0 && listModel != nil) {
                        listModel.cellHeight = (491 *  kScreenWidth / 375);
                    }else{
                        listModel.cellHeight = (451 *  kScreenWidth / 375);
                    }
                }
                
                if (![RNTUserManager sharedManager].isLogged) {
                    
                    success(userArr,showArr,@"还没登录呦!");
                    
                }else if ([responseDic[@"ifSubscribe"] isEqualToString:@"0"]) {
                    
                    success(userArr,showArr,@"这么精彩,还不关注?");
                    
                }else{
                    success(userArr,showArr,@"关注的主播没直播?去  \"热门\"  找找吧!");
                }
                }
        }else{
            failed();
        }

        }failure:failed];
}
//获得首页banner数据
+(void)getBannerDataSuccess:(void (^)(NSArray *bannerArr))success getFail:(void (^)())failed
{
    [RNTNetTool postJSONWithURL:@"009-002" params:nil success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
             NSArray *bannerArr = responseDic[@"ad"];
            if (success) {
                NSArray *bannerModel = [RNTBannerModel mj_objectArrayWithKeyValuesArray:bannerArr];
                success(bannerModel);
            }
        }else{
            success(nil);
        }
    } failure:^(NSError *error) {
        RNTLog(@"error");
    }];
}

//申请验证码
+(void)getVerifyCodeWithPhoneNumber:(NSString *)number
{
    NSDictionary *dic = @{@"mobileNo":number};
    [RNTNetTool postJSONWithURL:@"009-003" params:dic success:^(NSDictionary *responseDic) {
        RNTLog(@"%@",responseDic);
    } failure:^(NSError *error) {
        RNTLog(@"%@",error);
    }];
}

//注册
+(void)checkVerifyCodeWithPhoneNumber:(NSString *)number verifyCode:(NSString *)verify passWord:(NSString *)passWD
{
    NSDictionary *dic = @{@"mobile":number,@"verifyCode":verify,@"passwd":passWD};
    [RNTNetTool postJSONWithURL:@"001-007" params:dic success:^(NSDictionary *responseDic) {
        //注册成功登陆
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            RNTUserManager *mgr = [RNTUserManager sharedManager];
            [mgr addUserAccout:number];
            
            mgr.user = [RNTUser mj_objectWithKeyValues:responseDic[@"user"]];
            
            mgr.logged = YES;
            [mgr refreshBlanceSuccess:nil];
            
            //本地服务器注册JPush registrationID
            NSString *registrationID = [JPUSHService registrationID];
            [RNTJPushTool registrationLocalJPushServerWithUserId:mgr.user.userId registrationID:registrationID];
            
            [[NSUserDefaults standardUserDefaults] setObject:mgr.user.userId forKey:ACCESSTOKEN];
            [[NSUserDefaults standardUserDefaults] setObject:passWD forKey:USER_PASSWORD];
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:USER_OPENID];
            [[NSUserDefaults standardUserDefaults] setObject:nil forKey:USER_CHANNEL];
            
            [[NSUserDefaults standardUserDefaults] synchronize];
            
            [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"msg":@"success"}];

        }else{
            NSString *errorDes =    [NSString getErrorMessageWIthErrorCode:responseDic[@"errCode"]];
            if ([errorDes isEqualToString:@"登录失败,请重试"]) {
                errorDes = @"注册失败,请重试";
            }
           [[NSNotificationCenter defaultCenter] postNotificationName:LOGIN_RESULT_NOTIFICATION object:nil userInfo:@{@"error":errorDes}];
        }
    } failure:^(NSError *error) {
        
    }];
}
//搜索
+(void)getSearchListWithText:(NSString *)text   pageNum:(NSInteger)page pageSize:(NSInteger)size
getSuccess:(void (^)(NSArray *searchArr))success getFail:(void (^)())failed;
{
    NSDictionary *param = @{@"key":text,@"page":@(page),@"pageSize":@(size)};
    [RNTNetTool postJSONWithURL:@"001-021" params:param success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            
            NSArray *arr = responseDic[@"userList"];
            NSArray *modelArr = [RNTUser mj_objectArrayWithKeyValuesArray:arr];
            if (success) {
                success(modelArr);
            }
        }else{
            RNTLog(@"%@",responseDic);
        }
    } failure:^(NSError *error) {
        RNTLog(@"%@",error);
    }];
}
//更改密码
+(void)changePassWordWithPhoneNum:(NSString *)number   verifyCode:(NSString *)verify newPWD:(NSString *)passwd checkSuccess:(void (^)()) success getFail:(void (^)(NSString *))failed
{
    NSDictionary *param = @{@"mobileId":number,@"verifyCode":verify,@"newPasswd":passwd};
    [RNTNetTool postJSONWithURL:@"001-022" params:param success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                if (success) {
                    success();
                }
            });
        }else{
           NSString *errStr  = [NSString getErrorMessageWIthErrorCode:responseDic[@"errCode"]];
            if (failed) {
                failed(errStr);
            }
        }

    } failure:^(NSError *error) {
        if (failed) {
            failed(@"网络超时");
        }
    }];
}
@end
