//
//  RNTPlayShareView.m
//  Ace
//
//  Created by 于传峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayShareView.h"
#import "RNTPlayNetWorkTool.h"
#import "UMSocial.h"
//#import "RNTPlayShareInfo.h"
#import "RNTIntoPlayModel.h"
#import "SDWebImageManager.h"

#define shareHeight (kScreenWidth * 0.3)

@interface RNTShareButton : UIButton

@end

@interface RNTPlayShareView()<UMSocialUIDelegate>
@property (nonatomic, copy) void(^dismissBlock)();

@property (nonatomic, strong) UIImage *shareImage;

@property (nonatomic, copy) NSString *title;

@property (nonatomic, copy) NSString *content;

//@property (nonatomic, copy) NSString *sinaContent;// 新浪专用

@property (nonatomic, copy) NSString *shareUrl;

@end
__weak UIButton* _coverButton;
//RNTPlayShareInfo *_info;
static NSString* _showID;
@implementation RNTPlayShareView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.7];
        
        // 分享数据
        self.title = @"Ace带你发现世界的精彩！";
        self.content = [NSString stringWithFormat:@"%@ 正在直播，精彩不要错过，速来围观！", _showID];
        self.shareUrl = [NSString stringWithFormat:@"http://api.17ace.cn/share/index2.html?showId=%@", _showID];
        self.shareImage = [UIImage imageNamed:@"PlaceholderIcon"];
//        self.sinaContent = [NSString stringWithFormat:@"%@ %@ 正在直播，精彩不要错过，速来围观！%@", self.title, _showID, self.shareUrl];
        
        WEAK(self);
        
        [RNTPlayNetWorkTool getShareInfoWithShowID:_showID type:0 getSuccess:^(NSDictionary *dict) {
            RNTLog(@"%@", dict);
            if (dict) {
                NSString *imageUrl  = dict[@"imgSrc"];
                weakself.content = dict[@"sumy"];
                weakself.title = dict[@"title"];
                weakself.shareUrl = dict[@"url"];
                
                //            weakself.sinaContent = [NSString stringWithFormat:@"%@ %@ %@", weakself.title, weakself.content, weakself.shareUrl];
                
                [[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:imageUrl] options:0 progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
                    if (image) {
                        weakself.shareImage = image ? image : [UIImage imageNamed:@"PlaceholderIcon"];
                    }
                }];
            }
        }];
        
        
        
        NSArray* types = @[@(UMSocialSnsTypeWechatSession),@(UMSocialSnsTypeWechatTimeline),
                           @(UMSocialSnsTypeMobileQQ),@(UMSocialSnsTypeQzone),
                           @(UMSocialSnsTypeSina)];
        NSArray* imageArray = @[@"play_share_wechat",@"play_share_friend",
                                @"play_share_QQ",@"play_share_QQzone",
                                @"play_share_sina"];
        CGFloat width = kScreenWidth * 0.9;
        CGFloat magin = (kScreenWidth - width) * 0.5;
        CGFloat buttonWidth = width / types.count;
        CGFloat buttonHeight = buttonWidth;
        for (int i = 0; i<types.count; i++) {
            RNTShareButton* button = [[RNTShareButton alloc] init];
            [self addSubview:button];
            NSString* snsName = [UMSocialSnsPlatformManager getSnsPlatformString:(UMSocialSnsType)[types[i] integerValue]];
            UMSocialSnsPlatform *snsPlatform = [UMSocialSnsPlatformManager getSocialPlatformWithName:snsName];
            [button setTitle:snsPlatform.displayName forState:UIControlStateNormal];
            NSString* imageName = imageArray[i];
            NSString* imageHL = [NSString stringWithFormat:@"%@_HL", imageName];
            [button setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
            [button setImage:[UIImage imageNamed:imageHL] forState:UIControlStateHighlighted];
            button.tag = [types[i] integerValue];
            [button addTarget:self action:@selector(buttonSelected:) forControlEvents:UIControlEventTouchUpInside];
            [button makeConstraints:^(MASConstraintMaker *make) {
                make.size.equalTo(CGSizeMake(buttonWidth, buttonHeight));
                make.top.equalTo(self).offset(10);
                make.left.equalTo(self).offset(magin + i * buttonWidth);
            }];
        }
        

    }
    return self;
}

- (void)buttonSelected:(UIButton *)button
{
//    RNTUserManager* manager = [RNTUserManager sharedManager];
//    if (!manager.isLogged) {
//        [RNTPlayShareView coverButtonClicked:_coverButton];
//        if ([self.delegate respondsToSelector:@selector(playShareViewShowLoginView:)]) {
//            [self.delegate playShareViewShowLoginView:self];
//        }
//        
//        return;
//    }

    UMSocialSnsType type = (UMSocialSnsType)button.tag;
    
    [self shareWithContent:self.content title:self.title icon:self.shareImage shareUrlStr:self.shareUrl shareType:type];
//    [RNTPlayNetWorkTool getShareInfoWithShowID:_showID getSuccess:^(NSDictionary *dict) {
//        RNTLog(@"%@", dict);
//        NSDictionary* contentDict = dict[@"share"];
//        NSString* imageUrlStr = contentDict[@"img"];
//        NSString* content = contentDict[@"content"];
//        NSString* title = contentDict[@"title"];
//        NSString* urlStr = contentDict[@"url"];
//        [[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:imageUrlStr] options:0 progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
//            if (image) {
//                [self shareWithContent:content title:title icon:image shareUrlStr:urlStr shareType:type];
//            }
//        }];
//    }];
}

- (void)shareWithContent:(NSString *)content title:(NSString *)title icon:(UIImage *)image shareUrlStr:(NSString *)urlStr shareType:(UMSocialSnsType)type{
    
    NSString* snsName = [UMSocialSnsPlatformManager getSnsPlatformString:type];
    UMSocialSnsPlatform *snsPlatform = [UMSocialSnsPlatformManager getSocialPlatformWithName:snsName];
    
    UMSocialData* data = [UMSocialData defaultData];
    data.shareText = content;
    data.shareImage = image;

    switch (type) {
        case UMSocialSnsTypeWechatSession:
            data.extConfig.wechatSessionData.url = urlStr;
            data.extConfig.wechatSessionData.title = title;
            break;
        case UMSocialSnsTypeWechatTimeline:
            data.extConfig.wechatTimelineData.url = urlStr;
            data.extConfig.wechatTimelineData.title = title;
            break;
        case UMSocialSnsTypeMobileQQ:
            data.extConfig.qqData.url = urlStr;
            data.extConfig.qqData.title = title;
            break;
        case UMSocialSnsTypeQzone:
            data.extConfig.qzoneData.url = urlStr;
            data.extConfig.qzoneData.title = title;
            break;
        case UMSocialSnsTypeSina:
            data.shareText = [NSString stringWithFormat:@"%@ %@ %@", title, content, urlStr];
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
    [RNTPlayShareView coverButtonClicked:_coverButton];
    
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
        RNTUserManager* manage = [RNTUserManager sharedManager];
        [RNTPlayNetWorkTool shareUserWithCurrentUserID:manage.user.userId sharedShowID:_showID shareChannle:channel getSuccess:^(BOOL status) {
            if (status) {
//                [MBProgressHUD showSuccess:@"分享成功"];
                RNTLog(@"分享成功＝＝");
            }else{
                [MBProgressHUD showError:@"分享失败"];
            }
        }];
    }
}


+ (instancetype)popShareViewWithShowID:(NSString *)showID dismiss:(void(^)())dismissBlock
{
    _showID = showID;
    UIButton* coverButton = [[UIButton alloc] init];
    [coverButton addTarget:self action:@selector(coverButtonClicked:) forControlEvents:UIControlEventTouchDown];
    UIWindow* keyWindow = [UIApplication sharedApplication].keyWindow;
    [keyWindow addSubview:coverButton];
    _coverButton = coverButton;
    [coverButton makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.height.equalTo(keyWindow);
        make.bottom.equalTo(keyWindow).offset(shareHeight);
    }];
    
    RNTPlayShareView* shareView = [[RNTPlayShareView alloc] init];
    shareView.dismissBlock = [dismissBlock copy];
    [coverButton addSubview:shareView];
    [shareView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(coverButton);
        make.height.equalTo(shareHeight);
    }];
    
    [coverButton layoutIfNeeded];
    
    [coverButton updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(keyWindow);
    }];
    [UIView animateWithDuration:0.25 animations:^{
        [coverButton layoutIfNeeded];
    }];
    
    
    
    
    return shareView;
}

+ (void)coverButtonClicked:(UIButton *)button
{
    button.enabled = NO;
    [button updateConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(button.superview).offset(shareHeight);
    }];
    
    [UIView animateWithDuration:0.25 animations:^{
        [button layoutIfNeeded];
    } completion:^(BOOL finished) {
        [button.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
        [button removeFromSuperview];
        _coverButton = nil;
    }];
}

+ (void)dismiss
{
    [self coverButtonClicked:_coverButton];
}

- (void)dealloc
{
    if (self.dismissBlock) {
        self.dismissBlock();
    }
    RNTLog(@"--");
}

@end

@implementation RNTShareButton

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.titleLabel.font = [UIFont systemFontOfSize:13];
        self.titleLabel.textAlignment = NSTextAlignmentCenter;
        [self setTitleColor:RNTColor_16(0xfff100) forState:UIControlStateNormal];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    CGRect imageFrame = self.imageView.frame;
    imageFrame.origin.y = 0;
    self.imageView.frame = imageFrame;
    
    CGFloat centerX = self.bounds.size.width * 0.5;
    CGPoint imageCenter = self.imageView.center;
    imageCenter.x = centerX;
    self.imageView.center = imageCenter;
    
    CGRect labelFrame = self.titleLabel.frame;
    labelFrame.origin.y = CGRectGetMaxY(self.imageView.frame) + 10;
    labelFrame.size.width = self.bounds.size.width;
    self.titleLabel.frame = labelFrame;
    
    CGPoint labelCenter = self.titleLabel.center;
    labelCenter.x = centerX;
    self.titleLabel.center = labelCenter;
    
}

@end