//
//  RNTCaptureNetTool.m
//  Ace
//
//  Created by Ranger on 16/3/11.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureNetTool.h"
#import "RNTNetTool.h"
#import "RNTCaptureStartModel.h"
#import "RNTCaptureEndModel.h"
#import "MJExtension.h"
#import "MBProgressHUD+RNT.h"

@implementation RNTCaptureNetTool


+ (void)sendStartWithModel:(RNTCaptureStartModel *)model success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSDictionary *dict))failure;
{
    NSMutableDictionary *para = [NSMutableDictionary dictionary];
    
    para[@"userId"] = model.userId;
    if (model.title) {
        para[@"title"] = model.title;
    }
    
    if (model.position) {
        para[@"position"] = model.position;
    }

    
    [RNTNetTool postWithURL:@"002-002" params:para data:model.image dataKey:@"showImg" success:^(NSDictionary *dict) {
        
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            if (success) {
                success(dict);
            }
        }else {
//            if ([dict[@"errCode"] isEqualToString:@"222"]) {// 封号
//                [MBProgressHUD showError:@""];
//            }else {
//                
//                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
//            }
            
            if (failure) {
                failure(dict);
            }
        }
        
    } failure:^(NSError *error) {
        [MBProgressHUD showError:@"网络异常"];
        if (failure) {
            failure(nil);
        }
    }];
}

+ (void)sendEndWithShowId:(NSString *)showId success:(void (^)(RNTCaptureEndModel *endModel))success
{
    NSMutableDictionary *para = [NSMutableDictionary dictionary];
    para[@"showId"] = showId;
    
    [RNTNetTool postJSONWithURL:@"002-016" params:para success:^(NSDictionary *responseDic) {
        RNTCaptureEndModel *endModel = [RNTCaptureEndModel mj_objectWithKeyValues:responseDic];
        
        if (success) {
            success(endModel);
        }
        
    } failure:nil];
}


+ (void)getUserIdentificationState:(NSString *)userId success:(void (^)(NSDictionary *dict))success
{
    NSMutableDictionary *para = [NSMutableDictionary dictionary];
    para[@"userId"] = userId;
    
    [RNTNetTool postJSONWithURL:@"001-043" params:para success:^(NSDictionary *responseDic) {
        
        RNTLog(@"实名认证 = %@", responseDic);
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
            if (success) {
                success(responseDic);
            }
        }else {
             [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:responseDic[@"errCode"]]];
        }

    }failure:^(NSError *error) {
        [MBProgressHUD showError:@"网络异常"];
    }];
}


@end
