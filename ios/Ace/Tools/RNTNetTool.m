
//
//  AFNTool.m
//  AFN封装
//
//  Created by 杜 维欣 on 15/6/2.
//  Copyright (c) 2015年 ADo. All rights reserved.
//

#import "RNTNetTool.h"
#import "AFNetworking.h"
#import "UIDevice+RNTDeviceType.h"
#import "OpenUDID.h"

//网宿NGB请求地址
#define WSRequestURL @"http://sdkoptedge.chinanetcenter.com"

static AFHTTPSessionManager* _manager;

@implementation RNTNetTool


+ (AFHTTPSessionManager *)shareManager
{
    if (!_manager) {
        _manager = [AFHTTPSessionManager manager];
        _manager.requestSerializer.timeoutInterval = 30;
    }
    return _manager;
}

+ (void)getHTTPWithURL:(NSString *)requestKey params:(NSDictionary *)params
                success:(void (^)(id responseHTTP))success
                failure:(void (^)(NSError *error))failure
{
    NSMutableDictionary *mDict = [[NSMutableDictionary alloc] initWithDictionary:params];
    [mDict addEntriesFromDictionary:[self BasicData]];
    
    AFHTTPSessionManager *mgr =  [self shareManager];
    
    NSString *url = [RNTURLHEADER stringByAppendingString:requestKey];
    NSString* urlStr = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    [mgr GET:urlStr parameters:mDict progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if (success) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failure) {
            failure(error);
            if (error.code == -1009) {
                [RNTNetTool showMBProgressHUDWithMessage:@"请检查网络"];
            }
        }
    }];
}

+ (void)getJSONWithURL:(NSString *)requestKey params:(NSDictionary *)params success:(void (^)(NSDictionary *))success failure:(void (^)(NSError *))failure
{
    NSMutableDictionary *mDict = [[NSMutableDictionary alloc] initWithDictionary:params];
    [mDict addEntriesFromDictionary:[self BasicData]];
    
    AFHTTPSessionManager* manager = [self shareManager];
    
    NSString *url = [RNTURLHEADER stringByAppendingString:requestKey];
    NSString* urlStr = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    [manager GET:urlStr parameters:mDict progress:nil success:^(NSURLSessionDataTask * _Nonnull task, NSDictionary* responseObject) {
        if (success) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)postHTTPWithURL:(NSString *)requestKey params:(NSDictionary *)params
                success:(void (^)(id responseHTTP))success
                failure:(void (^)(NSError *error))failure
{
    NSMutableDictionary *mDict = [[NSMutableDictionary alloc] initWithDictionary:params];
    [mDict addEntriesFromDictionary:[self BasicData]];
    
    AFHTTPSessionManager *mgr =  [self shareManager];
    
    NSString *url = [RNTURLHEADER stringByAppendingString:requestKey];
    NSString* urlStr = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    [mgr POST:urlStr parameters:mDict progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if (success) {
            success(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failure) {
            failure(error);
            if (error.code == -1009) {
                [RNTNetTool showMBProgressHUDWithMessage:@"请检查网络"];
            }
        }
    }];
}

+ (void)postJSONWithURL:(NSString *)requestKey params:(NSDictionary *)params
                success:(void (^)(NSDictionary *))success
                failure:(void (^)(NSError *error))failure
{
    NSMutableDictionary *mDict = [[NSMutableDictionary alloc] initWithDictionary:params];
    [mDict addEntriesFromDictionary:[self BasicData]];
    
    AFHTTPSessionManager *mgr =  [self shareManager];
    
    NSString *url = [RNTURLHEADER stringByAppendingString:requestKey];
    NSString* urlStr = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    [mgr POST:urlStr parameters:mDict progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if (success) {
            NSDictionary *responseDic = (NSDictionary *)responseObject;
            success(responseDic);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failure) {
            failure(error);
            if (error.code == -1009) {
                [RNTNetTool showMBProgressHUDWithMessage:@"请检查网络"];
            }
        }
    }];
}

+ (void)postWithURL:(NSString *)requestKey params:(NSDictionary *)params
               data:(NSData *)data dataKey:(NSString *)dataKey
            success:(void (^)(NSDictionary *dict))success
            failure:(void (^)(NSError *error))failure
{
    NSMutableDictionary *mDict = [[NSMutableDictionary alloc] initWithDictionary:params];
    [mDict addEntriesFromDictionary:[self BasicData]];
    
    AFHTTPSessionManager *mgr =  [self shareManager];
    
    NSString *url = [RNTURLHEADER stringByAppendingString:requestKey];
    NSString* urlStr = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    [mgr POST:urlStr parameters:mDict constructingBodyWithBlock:^(id<AFMultipartFormData>  _Nonnull formData) {
        if (data) {
            [formData appendPartWithFileData:data name:dataKey fileName:[dataKey stringByAppendingString:@".png"] mimeType:@"image/png"];
        }
        
    } progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        if (success) {
            NSDictionary *responseDic = (NSDictionary *)responseObject;
            success(responseDic);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failure) {
            failure(error);
            if (error.code == -1009) {
                [RNTNetTool showMBProgressHUDWithMessage:@"请检查网络"];
            }
        }
    }];
}

+ (void)getNGBWithOriginalURL:(NSString *)originalUrl success:(void (^)(NSString *rtmpString))success failure:(void (^)(NSError *error))failure
{
    AFHTTPSessionManager* manager = [AFHTTPSessionManager manager];
    //告诉AFN不要去解析服务器返回的数据，保持原来的data即可
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer.timeoutInterval = 2;
    
    //NGB所需参数
    [manager.requestSerializer setValue:originalUrl forHTTPHeaderField:@"WS_URL"];
    [manager.requestSerializer setValue:@"1" forHTTPHeaderField:@"WS_RETIP_NUM"];
    [manager.requestSerializer setValue:@"3" forHTTPHeaderField:@"WS_URL_TYPE"];
    
    NSString* urlStr = [WSRequestURL stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    [manager GET:urlStr parameters:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, NSData *responseObject) {
        NSString *rtmpString = [[NSString alloc] initWithData:responseObject encoding:NSUTF8StringEncoding];
        if (success) {
            success(rtmpString);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)showMBProgressHUDWithMessage:(NSString *)message {
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    MBProgressHUD *progressHUD = [MBProgressHUD showHUDAddedTo:window animated:YES];
    progressHUD.mode = MBProgressHUDModeText;
    progressHUD.label.text = message;
    progressHUD.margin = 10;
    progressHUD.removeFromSuperViewOnHide = YES;
    [progressHUD hideAnimated:YES afterDelay:1];
}

//申请验证码
+(void)getVerifyCodeWithPhoneNumber:(NSString *)number  success:(void (^)())success  failure:(void (^)(NSString *error ))failure
{
    NSDictionary *dic = @{@"mobileNo":number};
    [RNTNetTool postJSONWithURL:@"009-003" params:dic success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            if (success) {
                success();
            }
        }else{
            if (failure) {
                failure(@"获取验证码失败,请重试");
            }
        }
    } failure:^(NSError *error) {
        //根据errCode返回相应信息
        if (failure) {
            failure(@"获取验证码失败,请重试");
        }
        RNTLog(@"%@",error);
    }];
}

#pragma mark - BasicData
+ (NSDictionary *)BasicData
{
    //设备型号
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *platform = [defaults valueForKey:@"platform"];
    if (!platform) {
        platform = [UIDevice getDevicePlatform];
        [defaults setValue:platform forKey:@"platform"];
        [defaults synchronize];
    }
    //系统版本
    NSString *systemVersion = [UIDevice currentDevice].systemVersion;
    //应用版本
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary]objectForKey:@"CFBundleShortVersionString"];
    //udid
    NSString *udid = [OpenUDID value];
    //时间戳
    NSString *timestamp = [NSString stringWithFormat:@"%.f", [[NSDate date] timeIntervalSince1970] * 1000];
    
    return @{@"platform" : platform, @"systemVersion" : systemVersion, @"appVersion" : appVersion, @"udid" : udid, @"timestamp" : timestamp};
}
@end
