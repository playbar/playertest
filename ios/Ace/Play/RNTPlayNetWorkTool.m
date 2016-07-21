//
//  RNTPlayNetWorkTool.m
//  Ace
//
//  Created by 于传峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayNetWorkTool.h"
#import "RNTNetTool.h"
#import "RNTUserManager.h"
#import "SDWebImageManager.h"
#import "RNTSocketTool.h"
#import "RNTSocketSummary.h"
#import "MBProgressHUD+RNT.h"
#import "AFURLSessionManager.h"
#import "SSZipArchive.h"


@implementation RNTPlayNetWorkTool

+ (void)getLivingUserListWithPageNum:(NSInteger)page pageSize:(NSInteger)size showID:(NSString *)showID getSuccess:(void (^)(NSDictionary *))success
{
    NSDictionary* params = @{
                             @"page":@(page),
                             @"pageSize":@(size),
                             @"showId":showID ? showID : @"-1"
                             };
    [RNTNetTool getJSONWithURL:@"002-005" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            success(responseDic);
        }
    } failure:^(NSError *error) {
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];
}

+ (void)getUserBlanceGetSuccess:(void (^)(NSUInteger))success
{
    RNTUserManager* manager = [RNTUserManager sharedManager];
    
    NSDictionary* params = @{
                             @"userId":manager.user.userId
                             };
    [RNTNetTool getJSONWithURL:@"001-002" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            success([responseDic[@"blance"] integerValue]);
        }
    } failure:^(NSError *error) {
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];
}

+ (void)getUserInfoWithUserID:(NSString *)userID getSuccess:(void (^)(NSDictionary *, BOOL))success
{
    if(!userID) return;
    RNTUserManager* manager = [RNTUserManager sharedManager];
    NSDictionary* params;
    if (manager.isLogged) {
        params = @{
                   @"userId":manager.user.userId ? manager.user.userId : @"-1",
                     @"starId":userID ? userID : @"-1"
                 };
    }else{
        params = @{
                   @"starId":userID ? userID : @"-1"
                   };
    }
    
    [RNTNetTool getJSONWithURL:@"001-001" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            if (!responseDic[@"errCode"]) {
                success(responseDic, [responseDic[@"user"][@"extendData"][@"relatoin"] boolValue]);
            }
        }
    } failure:^(NSError *error) {
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];
}

+ (void)followUserWithCurrentUserID:(NSString *)userID followedUserID:(NSString *)followedID getSuccess:(void (^)(BOOL))success
{
    NSDictionary* params = @{
                             @"userId":userID ? userID : @"-1",
                             @"starId":followedID ? followedID : @"-1"
                             };
    [RNTNetTool getJSONWithURL:@"003-001" params:params success:^(NSDictionary *responseDic) {
        if (success) {
            success([responseDic[@"exeStatus"] integerValue] == 1|| [responseDic[@"errCode"] isEqualToString:@"206" ]);
        }
    } failure:^(NSError *error) {
        if(success){
            success(NO);
        }
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];
}

+ (void)cancelFollowUserWithCurrentUserID:(NSString *)userID followedUserID:(NSString *)followedID getSuccess:(void (^)(BOOL))success
{
    NSDictionary* params = @{
                             @"userId":userID ? userID : @"-1",
                             @"starId":followedID ? followedID : @"-1"
                             };
    [RNTNetTool getJSONWithURL:@"003-006" params:params success:^(NSDictionary *responseDic) {
        RNTLog(@"请求成功＝＝＝＝＝＝%@",responseDic);
        if (success) {
            success([responseDic[@"exeStatus"] integerValue] == 1);
        }
    } failure:^(NSError *error) {
        if(success){
            success(NO);
        }
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];
}

+ (void)reportUserWithCurrentUserID:(NSString *)userID reportedUserID:(NSString *)reportedID getSuccess:(void (^)(BOOL))success
{
    NSDictionary* params = @{
                             @"userId":userID ? userID : @"-1",
                             @"starId":reportedID ? reportedID : @"-1"
                             };
    [RNTNetTool getJSONWithURL:@"003-002" params:params success:^(NSDictionary *responseDic) {
        if (success) {
            success([responseDic[@"exeStatus"] integerValue] == 1);
        }
    } failure:^(NSError *error) {
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];
}

+ (void)shareUserWithCurrentUserID:(NSString *)userID sharedShowID:(NSString *)showID shareChannle:(NSString *)channle getSuccess:(void (^)(BOOL))success
{
    NSDictionary* params = @{
                             @"userId":userID ? userID : @"-1",
                             @"showId":showID ? showID : @"-1",
                             @"channel":channle ? channle : @"-1"
                             };
    [RNTNetTool getJSONWithURL:@"002-011" params:params success:^(NSDictionary *responseDic) {
        if (success) {
            success([responseDic[@"exeStatus"] integerValue] == 1);
        }
    } failure:^(NSError *error) {
        RNTLog(@"请求失败＝＝＝＝＝＝%@", error);
    }];

}


//获得关注列表
+ (void)getSubscribeListWithPageNum:(NSInteger)page pageSize:(NSInteger)size userID:(NSString *)userID  getSuccess:(void (^)(NSDictionary *))success
{
    NSDictionary* params = @{
                             @"page":@(page),
                             @"pageSize":@(size),
                             @"userId":userID ? userID : @"-1"
                             };
    [RNTNetTool postJSONWithURL:@"003-005" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            success(responseDic);
        }
    } failure:nil];
    
}

+ (void)getFansListWithPageNum:(NSInteger)page pageSize:(NSInteger)size userID:(NSString *)userID getSuccess:(void (^)(NSDictionary *))success
{
    NSDictionary* params = @{
                             @"page":@(page),
                             @"pageSize":@(size),
                             @"userId":userID ? userID : @"-1"
                             };
    [RNTNetTool postJSONWithURL:@"003-004" params:params success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            success(responseDic);
        }
    } failure:nil];

}

+ (void)getGiftListGetSuccess:(void (^)(NSDictionary *))success
{

    [RNTNetTool getJSONWithURL:@"001-014" params:nil success:^(NSDictionary *responseDic) {
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            success(responseDic);
        }
    } failure:nil];
}

+ (void)getShareInfoWithShowID:(NSString *)showID type:(int)type getSuccess:(void (^)(NSDictionary* dict))success{
   
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.logged || !showID) {
        return;
    }
    NSDictionary* params = @{
                             @"userId":manager.user.userId,
                             @"showId":showID ? : @"-1",
                             @"type":@(type)
                             };
    [RNTNetTool getJSONWithURL:@"002-019" params:params success:^(NSDictionary *responseDic) {
         RNTLog(@"share = %@", responseDic);
        if (success && [responseDic[@"exeStatus"] integerValue] == 1) {
            success(responseDic);
        }
    } failure:nil];
}

+ (void)sendGiftWithGiftID:(NSString *)giftID senderID:(NSString *)senderID receiverID:(NSString *)receiverID showID:(NSString *)showID giftCount:(NSUInteger)count sendSuccess:(void (^)(NSDictionary*))success
{

    NSDictionary* params = @{
                             @"cnt":@(count),
                             @"giftId":giftID ? giftID : @"-1",
                             @"senderId":senderID ? senderID : @"-1",
                             @"receiverId":receiverID ? receiverID : @"-1",
                             @"showId":showID ? showID : @"-1"
                             };
    RNTSocketSummary* sum = [[RNTSocketSummary alloc] init];
    sum.requestKey = @"002-010";
    sum.receiverId = receiverID;
//    NSDictionary* dict = @{
//                           @"senderId":manager.user.userId,
//                           @"receiverId":self.userID,
//                           @"showId":self.showID,
//                           @"giftId":model.ID,
//                           @"cnt":@"1"
//                           };
    RNTSocketTool* tool = [RNTSocketTool shareInstance];
    [tool writeMessage:[params mutableCopy] andSummary:sum withTag:204];
    if(!success) return;
    tool.socketResponse = ^(NSData* data, RNTSocketSummary* summ){
        NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if ([summ.requestKey isEqualToString:@"002-010"]) {
            if (success) {
                success(dict);
            }
        }
    };
//    [RNTNetTool getJSONWithURL:@"001-010" params:params success:^(NSDictionary *responseDic) {
//        if (success) {
//            success([responseDic[@"exeStatus"] integerValue] == 1);
//        }
//    } failure:nil];
}

+ (void)sendChatWithReceiverID:(NSString *)recID showID:(NSString *)showID message:(NSString *)message sendSuccess:(void (^)(NSDictionary *))success{
    RNTUserManager* manager = [RNTUserManager sharedManager];
    RNTSocketTool* tool = [RNTSocketTool shareInstance];
    RNTSocketSummary* summary = [[RNTSocketSummary alloc] init];
    summary.senderName = manager.user.nickName;
    summary.requestKey = @"002-009";
    summary.showId = showID;
    NSString* userID = [RNTUserManager sharedManager].user.userId;
    NSDictionary* dict = @{
                           @"senderId":userID ? : @"-1",
                           @"receiverId":@"999999",
                           @"showId":showID ? : @"-1",
                           @"txt":message
                           };
    [tool writeMessage:[dict mutableCopy]andSummary:summary withTag:202];
    tool.socketResponse = ^(NSData* data, RNTSocketSummary* sum){
        NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if ([sum.requestKey isEqualToString:@"002-009"] && success) {
            RNTLog(@"%@", dict);
            success(dict);
        }
    };
}

+ (void)sendLikeWithSenderID:(NSString *)senderID showID:(NSString *)showID sendSuccess:(void (^)(NSDictionary *))success
{
    NSDictionary* params = @{
                             @"cnt":@(1),
                             @"userId":senderID ? senderID : @"-1",
                             @"showId":showID ? showID : @"-1"
                             };
    RNTSocketSummary* sum = [[RNTSocketSummary alloc] init];
    sum.requestKey = @"002-013";
    RNTSocketTool* tool = [RNTSocketTool shareInstance];
    [tool writeMessage:[params mutableCopy] andSummary:sum withTag:205];
    if(!success) return;
    tool.socketResponse = ^(NSData* data, RNTSocketSummary* summ){
        NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if ([summ.requestKey isEqualToString:@"002-013"]) {
            if (success) {
                success(dict);
            }
        }
    };
}



+ (void)connetSocketWithShowID:(NSString *)showID sendSuccess:(void (^)(NSDictionary *))success
{
    if (showID.length == 0) {
        return;
    }
    RNTSocketTool* tool = [RNTSocketTool shareInstance];
    NSMutableDictionary* parameter = [@{@"showId":showID} mutableCopy];
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (manager.isLogged) {
        parameter[@"userId"] = manager.user.userId;
        parameter[@"profile"] = manager.user.profile;
        parameter[@"sex"] = manager.user.sex;
    }else{
        parameter[@"userId"] = @"-1";
    }
    RNTSocketSummary* summary = [[RNTSocketSummary alloc] init];
    summary.requestKey = @"002-001";
    summary.showId = showID;
    [tool writeMessage:parameter andSummary:summary withTag:201];
    if(!success) return;
    tool.socketResponse = ^(NSData* data, RNTSocketSummary* summ){
        NSDictionary* dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        if ([summ.requestKey isEqualToString:@"002-001"]) {
            if (success) {
                success(dict);
            }
        }
    };
}


+(void)silenceWithShowID:(NSString *)showID silenceID:(NSString *)userID
{
    if (showID.length>0 && userID>0 && ![userID isEqualToString:@"-1"]) {
        
        NSDictionary *params = @{
                                 @"showId":showID,
                                 @"userId":userID
                                 };
        
        [RNTNetTool postJSONWithURL:@"002-018" params:params success:^(NSDictionary *responseDic) {
            if ([responseDic[@"exeStatus"] isEqualToString:@"1"]) {
                [MBProgressHUD showSuccess:@"禁言30分钟"];
            }else{
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:responseDic[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            [MBProgressHUD showError:@"网络错误"];
        }];
    }
    
}

// 下载图片
+ (void)reDownloadGiftImages
{
    [self getGiftListGetSuccess:^(NSDictionary *dict) {
        RNTLog(@"%@", dict);
        
        [self downLoadGiftSources:dict];
        
        if (!([dict[@"exeStatus"] intValue] == 1)) return ;
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            
            NSArray* giftDicts = dict[@"giftList"];

            NSString* giftsPath = GiftImagesPath;
            NSFileManager* manager = [NSFileManager defaultManager];
            if (![manager fileExistsAtPath:giftsPath]) {
                [manager createDirectoryAtPath:giftsPath withIntermediateDirectories:YES attributes:nil error:nil];
            }
            
            NSMutableArray* dictsArray = [[NSMutableArray alloc] init];
            NSMutableDictionary* giftPicDict = [[NSMutableDictionary alloc] init];
            for (NSDictionary* dict in giftDicts) {
                NSString* giftID = dict[@"id"];
                NSString* giftUrl = dict[@"pic"];
                giftPicDict[giftUrl] = giftID;
                [dictsArray addObject:@{giftID : dict}];
                [[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:giftUrl] options:SDWebImageRetryFailed | SDWebImageRefreshCached | SDWebImageContinueInBackground progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                        if (finished) {
                            NSString* giftIDString = giftPicDict[imageURL.absoluteString];
                            NSString* giftPath = [giftsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.png", giftIDString]];
                            NSData* imageData = UIImagePNGRepresentation(image);
                            [imageData writeToFile:giftPath atomically:YES];
                        }
                    });
                }];
                
            }
            NSString* dictPath = [giftsPath stringByAppendingPathComponent:@"gifts.plist"];
            [dictsArray writeToFile:dictPath atomically:YES];
            
        });


    }];
}

+ (UIImage *)giftImageWithGiftID:(NSString *)giftID
{
    NSString* giftPath = GiftImagePathWithGiftID(giftID);
    UIImage* image = [UIImage imageWithContentsOfFile:giftPath];
    if (!image) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            [self reDownloadGiftImages];
        });
        return nil;
    }
    return image;
}


//获取超级礼物
+ (void)downLoadGiftSources:(NSDictionary *)dict
{
    if (!([dict[@"exeStatus"] intValue] == 1)) return ;
     NSArray* giftDicts = dict[@"giftList"];
    
    for (NSDictionary *dic in giftDicts) {
//        if ([dic[@"type"] isEqualToString:@"1"]) {
            [RNTPlayNetWorkTool downLoadGiftAnimationSources:dic[@"id"]];
//        }
    }
}

//下载动画资源包
+(void)downLoadGiftAnimationSources:(NSString *)giftID
{
    NSFileManager* fileManager = [NSFileManager defaultManager];
    if (![fileManager fileExistsAtPath:GiftSourcesPathWithGiftID(giftID)]) {
        [fileManager createDirectoryAtPath:GiftSourcesPathWithGiftID(giftID) withIntermediateDirectories:YES attributes:nil error:nil];
    }else{
        //JSON存在不下载
        NSString *jsonPath = [GiftSourcesPathWithGiftID(giftID) stringByAppendingPathComponent:@"novo_info.json"];
        
        //容错
        NSString *jsonPathFalse = [[GiftSourcesPathWithGiftID(giftID) stringByAppendingPathComponent:giftID] stringByAppendingPathComponent:@"novo_info.json"];

        if ([fileManager fileExistsAtPath:jsonPath isDirectory:false] || [fileManager fileExistsAtPath:jsonPathFalse isDirectory:false]) {
            RNTLog(@"动画资源已经存在");
            return ;
        }
    }
    
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:configuration];
    
    NSURL *URL = [NSURL URLWithString:GiftSourcesDownUrl(giftID)];
    NSURLRequest *request = [NSURLRequest requestWithURL:URL];
    
    NSURLSessionDownloadTask *downloadTask = [manager downloadTaskWithRequest:request progress:nil destination:^NSURL *(NSURL *targetPath, NSURLResponse *response) {
        
        NSURL *downUrl = [NSURL fileURLWithPath:GiftSourcesPathWithGiftID(giftID)];
        return [downUrl URLByAppendingPathComponent:response.suggestedFilename];
        
    } completionHandler:^(NSURLResponse *response, NSURL *filePath, NSError *error) {
        
        //解压缩
        BOOL isUnzip =   [SSZipArchive unzipFileAtPath:GiftSourcesZipPathWithGiftID(giftID) toDestination:GiftSourcesPathWithGiftID(giftID)];
        RNTLog(@"压缩包路径%@,解压路径%@",GiftSourcesZipPathWithGiftID(giftID),GiftSourcesPathWithGiftID(giftID));
        
        NSFileManager *mgr = [NSFileManager defaultManager];
        if (isUnzip) {
            
            [mgr removeItemAtPath:GiftSourcesZipPathWithGiftID(giftID)  error:nil];
            
        }else{
             [SSZipArchive unzipFileAtPath:GiftSourcesZipPathWithGiftID(giftID) toDestination:GiftSourcesPathWithGiftID(giftID)];
        }
        
    }];
    
    [downloadTask resume];
    
}

@end
