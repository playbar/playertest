//
//  RNTMineNetTool.m
//  Ace
//
//  Created by 周兵 on 16/3/9.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTMineNetTool.h"
#import "RNTNetTool.h"

@implementation RNTMineNetTool

+ (void)updatePersonalInfoWithUserId:(NSString *)userId success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;

    NSDictionary *dict = @{@"userId" : userId, @"starId" : userId};
    
    [RNTNetTool postJSONWithURL:@"001-001" params:dict success:^(NSDictionary *responseDic) {
            if (success) {
                success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)modifySignatureWithUserId:(NSString *)userId signature:(NSString *)signature success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;

    NSDictionary *dict = @{@"userId" : userId, @"signature" : signature};
    
    [RNTNetTool postJSONWithURL:@"001-018" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)modifyNickNameWithUserId:(NSString *)userId nickName:(NSString *)nickName success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;

    NSDictionary *dict = @{@"userId" : userId, @"nickName" : nickName};
    [RNTNetTool postJSONWithURL:@"001-017" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)modifyIconWithUserId:(NSString *)userId iconData:(NSData *)iconData success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;

    NSDictionary *dict = @{@"userId" : userId};
    [RNTNetTool postWithURL:@"001-015" params:dict data:iconData dataKey:@"profile" success:^(NSDictionary *dict) {
        if (success) {
            success(dict);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)feedbackWithUserId:(NSString *)userId contactInfo:(NSString *)contactInfo contact:(NSString *)contact success:(void (^)(NSDictionary* dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;

    NSDictionary *dict = @{@"userId" : userId, @"starId" : contact , @"contactInfo" : contactInfo};
    [RNTNetTool postJSONWithURL:@"003-003" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)getFansListWithUserId:(NSString *)userId page:(NSString *)page pageSize:(NSString *)pageSize success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;

    NSDictionary *dict = @{@"userId" : userId, @"pageSize" : pageSize, @"page" : page};
    
    [RNTNetTool postJSONWithURL:@"003-004" params:dict success:^(NSDictionary *responseDic) {
        if ([responseDic[@"exeStatus"] isEqualToString:@"1"] && success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)getSubscribeListWithUserId:(NSString *)userId page:(NSString *)page pageSize:(NSString *)pageSize success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId, @"pageSize" : pageSize, @"page" : page};
    
    [RNTNetTool postJSONWithURL:@"003-005" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)changePWDWithUserId:(NSString *)userId oldPasswd:(NSString *)oldPasswd newPasswd:(NSString *)newPasswd success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId, @"newPasswd" : newPasswd, @"oldPasswd" : oldPasswd};
    
    [RNTNetTool postJSONWithURL:@"001-016" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)verificationWithReceipt:(NSString *)receipt userId:(NSString *)userId version:(NSString *)version success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil || receipt == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId, @"ticket" : receipt, @"version" : version};
    
    [RNTNetTool postJSONWithURL:@"001-020" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)changeGenderWithUserId:(NSString *)userId sex:(NSString *)sex success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId, @"sex" : sex};
    
    [RNTNetTool postJSONWithURL:@"001-023" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)getSilverWithUserId:(NSString *)userId success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId};
    
    [RNTNetTool postJSONWithURL:@"001-019" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)getWithdrawBindInfoWithUserId:(NSString *)userId success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId};
    
    [RNTNetTool postJSONWithURL:@"001-024" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)bindWechatWithUserId:(NSString *)userId weChatId:(NSString *)weChatId verifyCode:(NSString *)verifyCode mobileId:(NSString *)mobileId success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId, @"weChatId" : weChatId, @"verifyCode" : verifyCode, @"mobileId" : mobileId};
    
    [RNTNetTool postJSONWithURL:@"001-025" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}

+ (void)withdrawWithUserId:(NSString *)userId coinAmount:(NSString *)coinAmount rmbAmount:(NSString *)rmbAmount verifyCode:(NSString *)verifyCode success:(void (^)(NSDictionary *dict))success failure:(void (^)(NSError *error))failure
{
    if (userId == nil) return;
    
    NSDictionary *dict = @{@"userId" : userId, @"coinAmount" : coinAmount, @"rmbAmount" : rmbAmount, @"verifyCode" : verifyCode};
    
    [RNTNetTool postJSONWithURL:@"001-026" params:dict success:^(NSDictionary *responseDic) {
        if (success) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        if (failure) {
            failure(error);
        }
    }];
}
@end
