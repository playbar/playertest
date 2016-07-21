//
//  PlayerViewController.m
//  AVPlayer
//
//  Created by apple on 2/27/14.
//  Copyright (c) 2014 iMoreApps Inc. All rights reserved.
//

#import "RNTPlayView.h"
#import "IJKFFMoviePlayerController.h"
#import "IJKFFOptions.h"
#include "ijkPlayer/ijkmedia/ijkplayer/my_def.h"

@interface RNTPlayView ()

@property (nonatomic, strong) NSString *mediaPath;
@property (nonatomic, strong) IJKFFMoviePlayerController *player;
@end

@implementation RNTPlayView

SingletonM(PlayView)

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        [self installMovieNotificationObservers];
        self.backgroundColor = [UIColor clearColor];

    }
    return self;
}

- (void)playLiveVideoWithUrl:(NSString *)urlStr
{
    IJKFFOptions *options = [IJKFFOptions optionsByDefault];
    options.showHudView = YES;
//     ijkmp_set_option(_mediaPlayer, IJKMP_OPT_CATEGORY_FORMAT, "analyzeduration","500000");
    [options setFormatOptionValue:@"ijktcphook" forKey:@"http-tcp-hook"];
    [options setFormatOptionValue:@"500000" forKey:@"analyzeduration"];
    [options setOptionIntValue:IJK_AVDISCARD_DEFAULT forKey:@"skip_frame" ofCategory:kIJKFFOptionCategoryCodec];
    [options setOptionIntValue:IJK_AVDISCARD_DEFAULT forKey:@"skip_loop_filter" ofCategory:kIJKFFOptionCategoryCodec];

//    [options setOptionIntValue:0 forKey:@"packet-buffering" ofCategory:kIJKFFOptionCategoryPlayer];
    
    self.player = [[IJKFFMoviePlayerController alloc] initWithContentURL:[NSURL URLWithString:urlStr] withOptions:options];
    self.player.view.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
    self.player.view.backgroundColor = [UIColor clearColor];
    self.player.scalingMode = IJKMPMovieScalingModeAspectFit;
    self.player.shouldAutoplay = YES;
    
//    RNTLog(@"==%@", self.subviews);
    [self addSubview:self.player.view];
    
    [self setViewObsever];
    
    CGSize screenSize = [UIScreen mainScreen].bounds.size;
    CGFloat scale = screenSize.height / screenSize.width;
    CGFloat leftMargin = 2;
    CGFloat topMargin = leftMargin * scale;
    
    if (iPhone5) {
        [self.player.view makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self).insets(UIEdgeInsetsMake(-topMargin*1, -leftMargin*0.5, -topMargin*1, -leftMargin*0.5));
        }];
    }
    else if (iPhone6){
        [self.player.view makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self).insets(UIEdgeInsetsMake(-topMargin*0.8, -leftMargin*0, -topMargin*0.8, -leftMargin*0));
        }];
    }else if(iPhone4){
        [self.player.view makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    else{
        [self.player.view makeConstraints:^(MASConstraintMaker *make) {
           make.edges.equalTo(self).insets(UIEdgeInsetsMake(-topMargin*1, -leftMargin*1, -topMargin*1, -leftMargin*1));
        }];
    }

    
    [self.player prepareToPlay];
}

- (void)stop
{
    [self.player shutdown];
    [self.player stop];
    [self.player.view removeFromSuperview];
    self.player = nil;
}
- (BOOL)isPlaying
{
    return self.player.isPlaying;
}
- (BOOL)isLoading
{
    return self.player.isPreparedToPlay;
}

#pragma mark - KVO
- (void)setViewObsever
{
    CGSize screenSize = [UIScreen mainScreen].nativeBounds.size;
    my_set_out_size(screenSize.width, screenSize.height);
    
//    [self.player.view addObserver:self forKeyPath:@"bounds" options:NSKeyValueObservingOptionNew | NSKeyValueObservingOptionOld context:nil];
}
//- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSString *,id> *)change context:(void *)context
//{
//    if ([keyPath isEqualToString:@"bounds"]) {
//        CGSize oldSize = [change[@"old"] CGRectValue].size;
//        CGSize newSize = [change[@"new"] CGRectValue].size;
//        if (!CGSizeEqualToSize(oldSize, newSize)) {
//            my_set_out_size(newSize.width + 10, newSize.height + 10);
//        }
//    }
//}

#pragma mark Install Movie Notifications

/* Register observers for the various movie object notifications. */
-(void)installMovieNotificationObservers
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(loadStateDidChange:)
                                                 name:IJKMPMoviePlayerLoadStateDidChangeNotification
                                               object:_player];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(moviePlayBackDidFinish:)
                                                 name:IJKMPMoviePlayerPlaybackDidFinishNotification
                                               object:_player];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(mediaIsPreparedToPlayDidChange:)
                                                 name:IJKMPMediaPlaybackIsPreparedToPlayDidChangeNotification
                                               object:_player];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(moviePlayBackStateDidChange:)
                                                 name:IJKMPMoviePlayerPlaybackStateDidChangeNotification
                                               object:_player];
}
- (void)loadStateDidChange:(NSNotification*)notification
{
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    IJKMPMovieLoadState loadState = _player.loadState;
    
    if ((loadState & IJKMPMovieLoadStatePlaythroughOK) != 0) {
        [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(YES)}];
        RNTLog(@"loadStateDidChange: IJKMPMovieLoadStatePlaythroughOK: %d\n", (int)loadState);
    } else if ((loadState & IJKMPMovieLoadStateStalled) != 0) {
        RNTLog(@"loadStateDidChange: IJKMPMovieLoadStateStalled: %d\n", (int)loadState);
//        [center postNotificationName:RNTPlayStartLoadingNotification object:self userInfo:nil];
    } else {
        RNTLog(@"loadStateDidChange: ???: %d\n", (int)loadState);
        [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(NO)}];
    }
}

- (void)moviePlayBackDidFinish:(NSNotification*)notification
{
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    int reason = [[[notification userInfo] valueForKey:IJKMPMoviePlayerPlaybackDidFinishReasonUserInfoKey] intValue];
    
    switch (reason)
    {
        case IJKMPMovieFinishReasonPlaybackEnded:
            RNTLog(@"playbackStateDidChange: IJKMPMovieFinishReasonPlaybackEnded: %d\n", reason);
            break;
            
        case IJKMPMovieFinishReasonUserExited:
            RNTLog(@"playbackStateDidChange: IJKMPMovieFinishReasonUserExited: %d\n", reason);
            break;
            
        case IJKMPMovieFinishReasonPlaybackError:
            RNTLog(@"playbackStateDidChange: IJKMPMovieFinishReasonPlaybackError: %d\n", reason);
            [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(NO)}];
            break;
            
        default:
            RNTLog(@"playbackPlayBackDidFinish: ???: %d\n", reason);
            [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(NO)}];
            break;
    }
}

- (void)mediaIsPreparedToPlayDidChange:(NSNotification*)notification
{
    RNTLog(@"mediaIsPreparedToPlayDidChange\n");
}

- (void)moviePlayBackStateDidChange:(NSNotification*)notification
{
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    switch (_player.playbackState)
    {
        case IJKMPMoviePlaybackStateStopped: {
            [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(NO)}];
            RNTLog(@"IJKMPMoviePlayBackStateDidChange %d: stoped", (int)_player.playbackState);
            break;
        }
        case IJKMPMoviePlaybackStatePlaying: {
            [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(YES)}];
            RNTLog(@"IJKMPMoviePlayBackStateDidChange %d: playing", (int)_player.playbackState);
            break;
        }
        case IJKMPMoviePlaybackStatePaused: {
//            [center postNotificationName:RNTPlayEndLoadingNotification object:self userInfo:@{RNTPlayEndLoadResultKey : @(NO)}];
            RNTLog(@"IJKMPMoviePlayBackStateDidChange %d: paused", (int)_player.playbackState);
            break;
        }
        case IJKMPMoviePlaybackStateInterrupted: {
            RNTLog(@"IJKMPMoviePlayBackStateDidChange %d: interrupted", (int)_player.playbackState);
            break;
        }
        case IJKMPMoviePlaybackStateSeekingForward:
        case IJKMPMoviePlaybackStateSeekingBackward: {
            RNTLog(@"IJKMPMoviePlayBackStateDidChange %d: seeking", (int)_player.playbackState);
            break;
        }
        default: {
            RNTLog(@"IJKMPMoviePlayBackStateDidChange %d: unknown", (int)_player.playbackState);
            break;
        }
    }
}



- (void)dealloc
{
    [self stop];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
