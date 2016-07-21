//
//  RNTWebViewController.m
//  Ace
//
//  Created by 周兵 on 16/3/13.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTWebViewController.h"

@interface RNTWebViewController ()
@property (nonatomic, strong) UIWebView *webView;
@end

@implementation RNTWebViewController

+ (instancetype)webViewControllerWithTitle:(NSString *)title url:(NSString *)url
{
    RNTWebViewController *webVC = [[RNTWebViewController alloc] init];
    webVC.title = title;
    NSURL * requestUrl = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:requestUrl];
    [webVC.webView loadRequest:request];
    return webVC;
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.transform = CGAffineTransformMakeTranslation(0, 0);
     self.tabBarController.tabBar.transform = CGAffineTransformMakeTranslation(0, 0);
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.view addSubview:self.webView];
    
    [self.webView makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.top.bottom.equalTo(self.view);
    }];
}

#pragma mark -
- (UIWebView *)webView
{
    if (_webView == nil) {
        _webView = [[UIWebView alloc] init];
        _webView.scalesPageToFit = YES;
        _webView.scrollView.showsVerticalScrollIndicator = NO;
        _webView.dataDetectorTypes = UIDataDetectorTypeNone;
    }
    return _webView;
}
@end
