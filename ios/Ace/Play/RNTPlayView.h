//
//  CFPlayView.h
//  08-视频播放器
//
//  Created by yuchuanfeng on 15/5/9.
//  Copyright (c) 2015年 yuchuanfeng. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Singleton.h"



@interface RNTPlayView : UIView
SingletonH(PlayView)

@property(nonatomic, copy) NSString* playingRoomID;
@property(nonatomic, copy) NSString* playingUrl;


- (BOOL)isPlaying;
- (BOOL)isLoading;
- (void)stop;

- (void)playLiveVideoWithUrl:(NSString *)urlStr;
@end
