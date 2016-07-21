//
//  RNTLaunchImage.m
//  Weibo
//
//  Created by Ranger on 15/12/7.
//  Copyright © 2015年 Rednovo. All rights reserved.
//

#import "RNTLaunchImage.h"
#import "UIImageView+WebCache.h"
#import "SDImageCache.h"
#import "AppDelegate.h"
#import "RNTTabBarContronller.h"
#import "RNTWebViewController.h"
#import "RNTNavigationController.h"
#import "UIButton+WebCache.h"
#import "RNTBootPicModel.h"
#import "RNTLaunchBtn.h"
#import "RNTPlayViewController.h"
#import "RNTIntoPlayModel.h"
#import "RNTSysConfigModel.h"

@interface RNTLaunchImage ()

@property(nonatomic,strong) UIImageView *picImg;

@property(nonatomic,strong) NSTimer *timer;

@property(nonatomic,strong) UIButton *timeBtn;

@property(nonatomic,strong) RNTBootPicModel *model;

@end

@implementation RNTLaunchImage

static UIWindow *launchWindow = nil;

+(void)show
{

    RNTBootPicModel *model = [RNTUserManager sharedManager].sysConfigModel.bootPic;
    
    
    if (model.picUrl.length>0 && model.picUrl != nil) {
        
        launchWindow = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
        launchWindow.backgroundColor = [UIColor clearColor];
        launchWindow.userInteractionEnabled = YES;
        launchWindow.windowLevel = UIWindowLevelAlert + 10;
        
        RNTLaunchImage *launchView = [[RNTLaunchImage alloc] init];
        launchView.view.frame = launchWindow.bounds;
        launchView.view.backgroundColor = [UIColor clearColor];
        launchView.model = model;
    
        [launchView setSubviews];
        
        launchWindow.rootViewController = launchView;
    }
    
}

+(void)showPic:(RNTBootPicModel *)bootPic{
    
    NSURL *picUrl = [NSURL URLWithString:bootPic.picUrl];
    
    UIImageView *picCurrent = [[UIImageView alloc] initWithFrame:launchWindow.frame];
   [picCurrent sd_setImageWithURL:picUrl];
    
//    [RNTBootPicModel saveLaunchImageModel:bootPic];

    
}

+ (void)hiddenWithView
{
    // 动画
    [UIView animateWithDuration:0.5 animations:^{
        launchWindow.transform = CGAffineTransformMakeScale(1.5, 1.5);
        launchWindow.alpha = 0;
    } completion:^(BOOL finished) {
        [self clear];
    }];
}

+ (void)clear
{
    launchWindow.userInteractionEnabled = NO;
    [launchWindow removeFromSuperview];
    launchWindow = nil;
}


#pragma mark - 布局子控件
-(void)setSubviews
{
    RNTLaunchBtn *picBtn = [[RNTLaunchBtn alloc] initWithFrame:launchWindow.frame];
    [picBtn addTarget:self action:@selector(pushInto:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:picBtn];
    
    //倒计时按钮
    UIButton *timeBtn = [[UIButton alloc] init];    
    
    timeBtn.backgroundColor = [UIColor grayColor];
    timeBtn.layer.cornerRadius = 5;
    [timeBtn addTarget:self action:@selector(cancelPage) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:timeBtn];
    [timeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.view).offset(-10);
        make.top.mas_equalTo(self.view).offset(10);
        make.size.mas_equalTo(CGSizeMake(55*1.25, 26*1.25));
    }];
    [timeBtn setBackgroundImage:[UIImage imageNamed:@"launch_jump_normal"] forState:UIControlStateNormal];
    [timeBtn setBackgroundImage:[UIImage imageNamed:@"launch_jump_highlighted"] forState:UIControlStateHighlighted];
    self.timeBtn = timeBtn;
    
    NSString *url = self.model.picUrl;
    
    if (url.length>0) {
        
        [picBtn sd_setBackgroundImageWithURL:[NSURL URLWithString:url]  forState:UIControlStateNormal placeholderImage:[UIImage imageNamed:[self getLaunchImageName]]];

        launchWindow.hidden = NO;
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [RNTLaunchImage hiddenWithView];
    });

}

//跳过按钮
-(void)cancelPage
{
    [self.timer invalidate];

    // 动画
    [UIView animateWithDuration:0.5 animations:^{
        launchWindow.transform = CGAffineTransformMakeScale(1.5, 1.5);
        launchWindow.alpha = 0;
    } completion:^(BOOL finished) {
        [RNTLaunchImage clear];
    }];
}

//跳转
-(void)pushInto:(UIButton *)btn
{
    btn.userInteractionEnabled = NO;
    
    AppDelegate *delegate = (AppDelegate *)[UIApplication sharedApplication].delegate;


    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *openID = [userDefaults objectForKey:USER_OPENID];
    NSString *userID = [userDefaults objectForKey:ACCESSTOKEN];
    
    if (openID == nil && userID == nil && ![self.model.type isEqualToString:@"0"]) {
        delegate.window.rootViewController = [[RNTTabBarContronller alloc] init];
        
    }
    
    RNTTabBarContronller *tabVC = (RNTTabBarContronller *)delegate.window.rootViewController;
    RNTNavigationController *homeVC  = tabVC.selectedViewController;
    
    

    if ([self.model.type isEqualToString:@"1"]) {
        //跳网页
        [self cancelPage];
        RNTWebViewController *webVC = [RNTWebViewController webViewControllerWithTitle:self.model.title url:self.model.linkUrl];
        [homeVC pushViewController:webVC animated:NO];
        
    }else if ([self.model.type isEqualToString:@"2"]){
        //跳房间
        [self cancelPage];
        RNTIntoPlayModel *user = [[RNTIntoPlayModel alloc] init];
        
        user.showId = self.model.showId;
        user.userId = self.model.showId;
        
        RNTPlayViewController *playVC = [[RNTPlayViewController alloc] init];
        playVC.model = user;
        [homeVC pushViewController:playVC animated:YES];
    }else{
        
    }
}
//计时器
-(void)countDown
{
    RNTLog(@"定时器");
    int time =  (int)[self.timeBtn.currentTitle integerValue];
    time--;
    [self.timeBtn setTitle:[NSString stringWithFormat:@"%d",time] forState:UIControlStateNormal];
}

-(void)dealloc
{
    RNTLog(@"dealloc--launch");
    [self.timer invalidate];
    self.timer = nil;
}


//获得launchImage
-(NSString *)getLaunchImageName
{
    CGSize viewSize = self.view.bounds.size;
    NSString *viewOrientation = @"Portrait";    //横屏请设置成 @"Landscape"
    NSString *launchImage = nil;
    NSArray* imagesDict = [[[NSBundle mainBundle] infoDictionary] valueForKey:@"UILaunchImages"];
    for (NSDictionary* dict in imagesDict)
    {
        CGSize imageSize = CGSizeFromString(dict[@"UILaunchImageSize"]);
        
        if (CGSizeEqualToSize(imageSize, viewSize) && [viewOrientation isEqualToString:dict[@"UILaunchImageOrientation"]])
        {
            launchImage = dict[@"UILaunchImageName"];
        }
    }
    
    return launchImage;
}

#pragma mark - 懒加载
-(NSTimer *)timer
{
    if (!_timer ) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1 target:self selector:@selector(countDown) userInfo:nil repeats:YES];
    }
    return _timer;
}

@end
