//
//  RNTPlaySuperGiftAnimationView.m
//  Ace
//
//  Created by 于传峰 on 16/5/12.
//  Copyright © 2016年 RNT. All rights reserved.
//


#import "RNTPlaySuperGiftAnimationView.h"

#import "cocos2d.h"
#import "platform/ios/CCEAGLView-ios.h"
#import "CocosAppDelegate.h"

#import "platform/ios/CCDirectorCaller-ios.h"
#import "LayerManager.h"
#import "RNTPlayNetWorkTool.h"

@interface RNTPlaySuperGiftAnimationView()

@property (nonatomic, strong) NSMutableArray *superGiftIDs;
@property(nonatomic,assign) BOOL isSet;
@end


@implementation RNTPlaySuperGiftAnimationView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self.isSet) {
        [_instace setCocos];
        return _instace;
    }
    _instace = [super initWithFrame:frame];

    if (_instace) {
        NSNotificationCenter* notiCenter = [NSNotificationCenter defaultCenter];
        [notiCenter addObserver:self selector:@selector(endCocos) name:RNTGIftAnimationEnd object:nil];
        [_instace setCocos];
    }
    return _instace;
}

- (NSMutableArray *)superGiftIDs{
    if (!_superGiftIDs) {
        _superGiftIDs = [[NSMutableArray alloc] init];
    }
    return _superGiftIDs;
}

- (void)fireSuperAnimationWithGiftID:(NSString *)giftID
{
    [self.superGiftIDs addObject:giftID];
    if (self.superGiftIDs.count == 1) {
        [self playSuperGiftAnimation];
    }
}

- (void)dealloc
{
    NSNotificationCenter* notiCenter = [NSNotificationCenter defaultCenter];
    [notiCenter removeObserver:self];
}

-(void)remove
{
//    CCDirectorCaller *caller = [CCDirectorCaller sharedDirectorCaller];
//    [caller stopMainLoop];
    [self.superGiftIDs removeAllObjects];
}

#pragma mark - superGiftAnima

- (void)playSuperGiftAnimation{
    if (self.superGiftIDs.count == 0) {
        return;
    }
    NSString* giftID = self.superGiftIDs[0];
    
    [self playCocosAnimtion:giftID];
}
//播放动画
-(void)playCocosAnimtion:(NSString *)giftID
{
    cocos2d::Application *app = cocos2d::Application::getInstance();
    app->initGLContextAttrs();
    cocos2d::GLViewImpl::convertAttrs();
    
    NSFileManager* fileManager = [NSFileManager defaultManager];
    NSString *jsonPath = [GiftSourcesPathWithGiftID(giftID) stringByAppendingPathComponent:@"novo_info.json"];
    //容错
    NSString *jsonPathFalse = [[GiftSourcesPathWithGiftID(giftID) stringByAppendingPathComponent:giftID] stringByAppendingPathComponent:@"novo_info.json"];
    
    //JSON不存在重新下载
    if (![fileManager fileExistsAtPath:jsonPath] && ![fileManager fileExistsAtPath:jsonPathFalse]) {
        RNTLog(@"不存在重新下载");
        [RNTPlayNetWorkTool downLoadGiftAnimationSources:giftID];
    }
    
    const char *giftPath = [self giftPath:giftID];
    app->setResPath(giftPath);
}

//结束动画
-(void)endCocos
{
    LayerManager::getInstance()->delLayer(cocos2d::Application::getInstance()->GetResPath());
    if (self.superGiftIDs.count) {
        [self.superGiftIDs removeObjectAtIndex:0];
    }
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self playSuperGiftAnimation];
    });

    RNTLog(@"动画结束回调");
}

//返回礼物路径
-(const char *)giftPath:(NSString *)giftID
{
    NSString *jsonPathFalse = [[GiftSourcesPathWithGiftID(giftID) stringByAppendingPathComponent:giftID] stringByAppendingPathComponent:@"novo_info.json"];
    
    NSFileManager* fileManager = [NSFileManager defaultManager];
    
    NSString *path;
    
    if ([fileManager fileExistsAtPath:jsonPathFalse]) {
        path =  [GiftSourcesPathWithGiftID(giftID) stringByAppendingPathComponent:giftID];
    }else{
        path = GiftSourcesPathWithGiftID(giftID);
    }
    
    const char *pathStr = [path UTF8String];
    
    return pathStr;
}

//设置cocos动画
-(void)setCocos
{
    cocos2d::Application *app = cocos2d::Application::getInstance();
    app->initGLContextAttrs();
    cocos2d::GLViewImpl::convertAttrs();
    
    CCEAGLView *eaglView = [CCEAGLView viewWithFrame: self.bounds
                                         pixelFormat:kEAGLColorFormatRGBA8
                                         depthFormat: GL_DEPTH24_STENCIL8_OES
                                  preserveBackbuffer: NO
                                          sharegroup: nil
                                       multiSampling: NO
                                     numberOfSamples: 0 ];
    
    cocos2d::GLView *glview = cocos2d::GLViewImpl::createWithEAGLView((__bridge void *)eaglView);
    
    cocos2d::Director::getInstance()->setOpenGLView(glview);

    eaglView.backgroundColor = [UIColor clearColor];
    eaglView.opaque = NO;
    [eaglView setMultipleTouchEnabled:NO];
    eaglView.userInteractionEnabled = NO;
    
    
    [self addSubview:eaglView];
    self.isSet = YES;
    app->run();
}

SingletonM(playSuperGiftAnimationView)


@end
