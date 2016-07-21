//
//  RNTHomeBannerView.m
//  Ace
//
//  Created by 靳峰 on 16/3/5.
//  Copyright © 2016年 RNT. All rights reserved.
//
#define kBannerScale 1/3
#define kBannerHeight (CGFloat) kBannerScale*kScreenWidth

#import "RNTHomeBannerView.h"
#import "RNTBannerModel.h"
#import "UIButton+WebCache.h"
#import "RNTWebViewController.h"


@interface RNTHomeBannerView ()<UIScrollViewDelegate>
//轮播scroll
@property(nonatomic,strong) UIScrollView *scroll;
//pageContro
@property(nonatomic,strong) UIPageControl *pageContro;
//定时器
@property(nonatomic,strong) NSTimer *timer;
//banner数量
@property(nonatomic,assign) int count;
@end

@implementation RNTHomeBannerView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
    }
    return self;
}

-(void)setBannerModel:(NSArray *)bannerModel
{
    _bannerModel = bannerModel;
    [self.timer invalidate];
    self.timer = nil;
    [self setSubviews];
    self.count = (int)bannerModel.count;
}

// 定时源方法
-(void)changePage
{
    int page = self.scroll.contentOffset.x/kScreenWidth;
    CGPoint point = CGPointMake((page+1)*kScreenWidth, 0);
    
    if (self.scroll.contentOffset.x < (self.bannerModel.count-1)*kScreenWidth) {
        [self.scroll setContentOffset:point animated:YES];
    }else{
        self.scroll.contentOffset = CGPointMake(0, 0);
    }
}

#pragma mark - 按钮点击
-(void)bannerBtnClick:(UIButton *)btn
{
    RNTBannerModel *model = self.bannerModel[btn.tag];
    if (self.banerClick) {
        self.banerClick(model.title,model.addres);
    }
}

#pragma mark - UIScrollViewDelegate
//开始拖拽
-(void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self.timer invalidate];
    self.timer = nil;
}

//停止拖拽
-(void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    NSDate *date = [NSDate dateWithTimeIntervalSinceNow:2];
    self.timer.fireDate = date;
}

-(void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    int page = scrollView.contentOffset.x/kScreenWidth;
    
    if (self.pageContro.currentPage != page) {
        self.pageContro.currentPage = page;
    }
}

#pragma mark - 布局子控件
-(void)setSubviews
{
    
    if (self.scroll && self.count == self.bannerModel.count){
        NSDate *date = [NSDate dateWithTimeIntervalSinceNow:2];
        self.timer.fireDate = date;
        return;
    }
    
    for (UIView *childView in self.subviews) {
        [childView removeFromSuperview];
    }
    
    //scroll
    self.scroll = [[UIScrollView alloc] initWithFrame:self.bounds];
    self.scroll.pagingEnabled = YES;
    self.scroll.scrollsToTop = NO;
    self.scroll.showsHorizontalScrollIndicator = NO;
    self.scroll.contentSize = CGSizeMake(self.bannerModel.count*kScreenWidth,kBannerHeight);
    self.scroll.delegate = self;
    [self addSubview:self.scroll];
    
    //btn
    for (int i = 0; i<self.bannerModel.count; i++) {
        UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(kScreenWidth*i, 0, kScreenWidth, kBannerHeight)];
        btn.backgroundColor = kRandomColor;
        btn.tag = i;
        [btn addTarget:self action:@selector(bannerBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        RNTBannerModel *model = self.bannerModel[i];
        [btn sd_setBackgroundImageWithURL:[NSURL URLWithString:model.imgUrl] forState:UIControlStateNormal placeholderImage:[UIImage imageNamed:@"home_banner_placeholder"]];
        
        [self.scroll addSubview:btn];
    }
    
    //页数控制器
    self.pageContro = [[UIPageControl alloc] initWithFrame:CGRectMake(0, 0, 100, 20)];
    self.pageContro.numberOfPages = self.bannerModel.count;
    self.pageContro.pageIndicatorTintColor = [UIColor grayColor];
    self.pageContro.currentPageIndicatorTintColor = [UIColor yellowColor];
    self.pageContro.center = CGPointMake(kScreenWidth*0.5,kBannerHeight-10);
    [self addSubview:self.pageContro];
    
    //定时器
    NSDate *date = [NSDate dateWithTimeIntervalSinceNow:2];
    self.timer.fireDate = date;
}


#pragma mark - 懒加载
-(NSTimer *)timer
{
    if (!_timer) {
        _timer = [NSTimer timerWithTimeInterval:2 target:self selector:@selector(changePage) userInfo:nil repeats:YES];
        [[NSRunLoop mainRunLoop] addTimer:_timer forMode:NSDefaultRunLoopMode];
    }
    
    return _timer;

}

@end
