//
//  RNTCaptureShareView.m
//  Ace
//
//  Created by Ranger on 16/5/9.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureShareView.h"
#import "UMSocial.h"
#import "RNTUserManager.h"
#import "RNTPlayNetWorkTool.h"
#import "SDWebImageManager.h"

@interface RNTCaptureShareView ()<UMSocialUIDelegate>

@property (nonatomic, strong) UIImage *shareImage;

@property (nonatomic, copy) NSString *title;

@property (nonatomic, copy) NSString *content;

@property (nonatomic, copy) NSString *sinaContent;// 新浪专用

@property (nonatomic, copy) NSString *shareUrl;

@property (nonatomic, strong) RNTUser *user;


@end

@implementation RNTCaptureShareView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        
        self.user = [RNTUserManager sharedManager].user;
        
        self.title = @"Ace带你发现世界的精彩！";
        self.content = [NSString stringWithFormat:@"%@ 正在直播，精彩不要错过，速来围观！", self.user.nickName];
        self.shareUrl = [NSString stringWithFormat:@"http://api.17ace.cn/share/index2.html?showId=%@", self.user.userId];
        self.shareImage = [UIImage imageNamed:@"PlaceholderIcon"];
        self.sinaContent = [NSString stringWithFormat:@"%@ %@ 正在直播，精彩不要错过，速来围观！%@", self.title, self.user.nickName, self.shareUrl];
        
        WEAK(self);
        
        [RNTPlayNetWorkTool getShareInfoWithShowID:self.user.userId type:1 getSuccess:^(NSDictionary *dict) {
            
            NSString *imageUrl  = dict[@"imgSrc"];
            weakself.content = dict[@"sumy"];
            weakself.title = dict[@"title"];
            weakself.shareUrl = dict[@"url"];
            
            weakself.sinaContent = [NSString stringWithFormat:@"%@ %@ 正在直播，精彩不要错过，速来围观！%@", weakself.title, weakself.user.nickName, weakself.shareUrl];
            
            [[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:imageUrl] options:0 progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
                if (image) {
                    weakself.shareImage = image ? image : [UIImage imageNamed:@"PlaceholderIcon"];
                }
            }];
        }];
        
//        [[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:self.user.showImg] options:0 progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
//            self.shareImage = image ? image : [UIImage imageNamed:@"PlaceholderIcon"];
//        }];
        
        NSArray *types = @[@(UMSocialSnsTypeWechatSession),@(UMSocialSnsTypeWechatTimeline),
                           @(UMSocialSnsTypeMobileQQ),@(UMSocialSnsTypeQzone),
                           @(UMSocialSnsTypeSina)];
        
        NSArray *imageArray = @[@"capture_share_wx",@"capture_share_friend",
                                @"capture_share_qq",@"capture_share_qqzone",
                                @"capture_share_sina"];
        
        
        for (NSInteger i = 0; i < types.count; i++) {
            UIButton *button = [[UIButton alloc] init];
            
            NSString *imageName = imageArray[i];
            NSString *imageNameH = [NSString stringWithFormat:@"%@_hl", imageName];
            [button setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
            [button setImage:[UIImage imageNamed:imageNameH] forState:UIControlStateSelected];
            button.tag = [types[i] integerValue];
            
            [button addTarget:self action:@selector(shareClick:) forControlEvents:UIControlEventTouchUpInside];
            [self addSubview:button];
            [button mas_makeConstraints:^(MASConstraintMaker *make) {
                make.size.mas_equalTo(ImageSize);
                make.top.equalTo(self.top);
                make.left.equalTo(self.left).offset((Margin + ImageSize.width) * i);
            }];
        }
        
    }
    return self;
}


- (void)shareClick:(UIButton *)button
{
    UMSocialSnsType type = (UMSocialSnsType)button.tag;
    int shareType = -1;
    switch (type) {
        case UMSocialSnsTypeWechatSession:
        case UMSocialSnsTypeWechatTimeline:
            shareType = 1;
            break;
        case UMSocialSnsTypeMobileQQ:
        case UMSocialSnsTypeQzone:
            shareType = 2;
            break;
        case UMSocialSnsTypeSina:
            shareType = 5;
            break;
        default:
            break;
    }
    
    
    NSString* snsName = [UMSocialSnsPlatformManager getSnsPlatformString:type];
    UMSocialSnsPlatform *snsPlatform = [UMSocialSnsPlatformManager getSocialPlatformWithName:snsName];
    
    UMSocialData* data = [UMSocialData defaultData];
    data.shareText = self.content;
    data.shareImage = self.shareImage;
    
    NSString* title = self.title;
    
//    NSString* imageStr = @"ace";
//    if (self.user.showImg.length > 0) {
//        imageStr = self.user.showImg.lastPathComponent;
//        if (imageStr.length > 4) {
//            imageStr = [imageStr substringToIndex:imageStr.length - 4];
//        }else{
//            imageStr = @"ace";
//        }
//    }

    NSString* shareUrl = self.shareUrl;

    switch (type) {
        case UMSocialSnsTypeWechatSession:
            data.extConfig.wechatSessionData.url = shareUrl;
            data.extConfig.wechatSessionData.title = title;
            break;
        case UMSocialSnsTypeWechatTimeline:
            data.extConfig.wechatTimelineData.url = shareUrl;
            data.extConfig.wechatTimelineData.title = title;
            break;
        case UMSocialSnsTypeMobileQQ:
            data.extConfig.qqData.url = shareUrl;
            data.extConfig.qqData.title = title;
            break;
        case UMSocialSnsTypeQzone:
            data.extConfig.qzoneData.url = shareUrl;
            data.extConfig.qzoneData.title = title;
            break;
        case UMSocialSnsTypeSina:
            data.shareText = self.sinaContent;
            break;
        default:
            break;
    }

    UMSocialControllerService* service = [[UMSocialControllerService alloc] init];
    service.socialData = data;
    //    service.currentSnsPlatformName = snsName;
    service.socialUIDelegate = self;
    
    
    snsPlatform.snsClickHandler([UIApplication sharedApplication].keyWindow.rootViewController, service,YES);

}


//下面得到分享完成的回调
-(void)didFinishGetUMSocialDataInViewController:(UMSocialResponseEntity *)response
{
    
    [UMSocialControllerService defaultControllerService].socialData = [UMSocialData defaultData];
    
    //根据`responseCode`得到发送结果,如果分享成功
    if(response.responseCode == UMSResponseCodeSuccess)
    {
        /*
         qq qzone :002
         wxsession wxtimeline : 003
         sina : 004
         */
        //得到分享到的微博平台名
        NSString* snsName = response.data.allKeys[0];
        NSString* channel = @"";
        if ([snsName isEqualToString:@"qq"] || [snsName isEqualToString:@"qzone"]) {
            channel = @"002";
        }else if ([snsName isEqualToString:@"wxsession"] || [snsName isEqualToString:@"wxtimeline"]) {
            channel = @"003";
        }else if ([snsName isEqualToString:@"sina"]) {
            channel = @"004";
        }
        
        [RNTPlayNetWorkTool shareUserWithCurrentUserID:self.user.userId sharedShowID:self.user.userId shareChannle:channel getSuccess:^(BOOL status) {
            if (status) {
                //                [MBProgressHUD showSuccess:@"分享成功"];
                RNTLog(@"分享成功＝＝");
            }else{
                [MBProgressHUD showError:@"分享失败"];
            }
        }];
    }
}


@end
