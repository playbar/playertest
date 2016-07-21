//
//  EngineAdaptor.m
//  t_allLibrary
//
//  Created by Ranger on 16/3/7.
//  Copyright © 2016年 RedNovo. All rights reserved.
//

#import "EngineAdaptor.h"
#import "PushStreamInterface.h"



class PushStreamNotify : public CPushStreamNotify
{
    void NetworkStauts(int iStatus, double dRealFrameRate, double dRealBitrate, double dLostVFrameRate, int iReconnectCnt, bool bConnected)
    {
        NSDictionary *dict = @{@"status" : @(iStatus),
                               @"frameRate" : @(dRealFrameRate),
                               @"bitrate" : @(dRealBitrate),
                               @"lostFrameRate" : @(dLostVFrameRate),
                               @"reconnectCnt" : @(iReconnectCnt),
                               @"connected" : @(bConnected)};
        [[NSNotificationCenter defaultCenter] postNotificationName:LiveSteamNetworkStatus object:nil userInfo:dict];
    }
    
    void Reconnected()
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:LiveSteamReconnected object:nil];
    }
};




@implementation EngineAdaptor{
    
    @private
    CPushStreamInterface* pushStreamInterface;
    
    PushStreamNotify* pushStreamNotify;
}

+ (EngineAdaptor *)shareInstance
{
    static id instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init
{
    if (self = [super init]) {
        pushStreamInterface = new CPushStreamInterface();
		
		// 获取沙盒路径
		NSString* tmp = NSTemporaryDirectory();
		
		// 初始化推流接口
		const char* pLogDirectories = [tmp UTF8String];
		
		// 设置日志文件存放的文件夹位置,以及日志等级,日志等级0~5,0级最低,5级最高
		pushStreamInterface->Init(pLogDirectories, 5);
        
        pushStreamNotify = new PushStreamNotify();
        
    }
    return self;
}

- (bool)setupEngine:(char *)adr
{
	bool bReturn = false;

    
	if (pushStreamInterface != nil)
	{
		// 设置推流URL
		pushStreamInterface->SetPushStreamURL(adr);
		// 设置视频推流分辨率
		pushStreamInterface->SetVideoPushStreamResolution(368, 640);
		// 设置视频帧率
		pushStreamInterface->SetVideoFrameRate(CameraFPS);
		// 设置视频I帧的帧间距
		pushStreamInterface->SetVideoFrameSpacing(CameraFPS);
		// 设置视频推流码率
        pushStreamInterface->SetVideoBitRate(600000, 600000, 800000);
		// 设置视频质量(0~50)
		pushStreamInterface->SetVideoQuality(23);
		// 设置视频编码预设值(0~8),3对应faster
		pushStreamInterface->SetVideoEncoderPreset(2);
		// 设置音频采样率
		pushStreamInterface->SetAudioSampleRate(44100);
		// 设置音频通道数
		pushStreamInterface->SetAudioChannels(1);
		// 设置音频码率
		pushStreamInterface->SetAudioBitRate(96000);
		// 设置重新连接服务的时间间隔(单位:毫秒)
		pushStreamInterface->SetReconnectTime(3000);
        pushStreamInterface->SetHardEncode(true);
        
		// 开启推流
		bReturn = pushStreamInterface->Open(pushStreamNotify);
	}
	
	return bReturn;
}



- (void)setPause:(BOOL)isPause
{
	if (pushStreamInterface != nil)
	{
		pushStreamInterface->SetPause(isPause);
	}
}

- (void)setMute:(BOOL)isMute
{
	if (pushStreamInterface != nil)
	{
		pushStreamInterface->SetMute(isMute);
	}
}

- (bool)encodeWith:(uint8_t *)buffer bufferSize:(int)bufferSize bufferType:(int)bufferType
{
	bool bReturn = false;
	
	if (pushStreamInterface != nil)
	{
		if (bufferType == 0)
		{
			// 推送视频,2表示视频格式为RGBA
			bReturn = pushStreamInterface->VideoCaptureData(368, 640, 2, (const char*)buffer, bufferSize);
		}
		else if (bufferType == 1)
		{
			// 推送音频
			bReturn = pushStreamInterface->AudioCaptureData((const char*)buffer, bufferSize);
		}
	}
	
	return bReturn;
}

- (void)closeEngine;
{
	if (pushStreamInterface != nil)
	{
		pushStreamInterface->Close();
	}
}





@end
