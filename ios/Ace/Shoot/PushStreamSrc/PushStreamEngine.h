#ifndef PUSH_STREAM_ENGINE_H
#define PUSH_STREAM_ENGINE_H

#include <pthread.h>
#include "PushStreamParam.h"
#include "VideoFrameCache.h"
#include "AudioFrameCache.h"
#include "VideoFormatConvert.h"
#include "VideoEncode.h"
#include "AudioEncode.h"
#include "AVPacketQueue.h"
#include "AudioAlignment.h"
#include "SendDeal.h"

class CPushStreamEngine
{
public:
	CPushStreamEngine();
	~CPushStreamEngine();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：
	// 返 回 值：调用是否成功
	// 函数说明：打开推流
	// $_FUNCTION_END *********************************************************
	bool Open();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Close
	// 函数参数：
	// 返 回 值：
	// 函数说明：关闭推流
	// $_FUNCTION_END *********************************************************
	void Close();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetLogDirectories
	// 函数参数：apLogDirectories			[输入]		日志目录
	//			 aiLogLevel					[输入]		日志等级(0~5级别,0级最低,5级最高)
	// 返 回 值：调用是否成功
	// 函数说明：设置日志目录
	// $_FUNCTION_END *********************************************************
	bool SetLogDirectories(const char* apLogDirectories, int aiLogLevel);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetPushStreamURL
	// 函数参数：apURL						[输入]		推流地址
	// 返 回 值：
	// 函数说明：设置推流URL
	// $_FUNCTION_END *********************************************************
	void SetPushStreamURL(const char* apURL);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetVideoPushStreamResolution
	// 函数参数：aiVideoPushStreamWidth		[输入]		视频推流分辨率的宽
	//			 aiVideoPushStreamHeight	[输入]		视频推流分辨率的高
	// 返 回 值：
	// 函数说明：设置视频推流分辨率
	// $_FUNCTION_END *********************************************************
	void SetVideoPushStreamResolution(int aiVideoPushStreamWidth, int aiVideoPushStreamHeight);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetVideoFrameRate
	// 函数参数：aiVideoFrameRate			[输入]		视频帧率
	// 返 回 值：
	// 函数说明：设置视频帧率
	// $_FUNCTION_END *********************************************************
	void SetVideoFrameRate(int aiVideoFrameRate);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetVideoFrameSpacing
	// 函数参数：aiVideoFrameSpacing		[输入]		I帧的帧间距
	// 返 回 值：
	// 函数说明：设置视频I帧的帧间距
	// $_FUNCTION_END *********************************************************
	void SetVideoFrameSpacing(int aiVideoFrameSpacing);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetVideoBitRate
	// 函数参数：aiVideoBitRate				[输入]		视频推流码率
	//			 aiVideoMinBitRate			[输入]		视频推流最小码率
	//			 aiVideoMaxBitRate			[输入]		视频推流最大码率
	// 返 回 值：
	// 函数说明：设置视频推流码率
	// $_FUNCTION_END *********************************************************
	void SetVideoBitRate(int aiVideoBitRate, int aiVideoMinBitRate, int aiVideoMaxBitRate);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetVideoQuality
	// 函数参数：aiVideoQuality				[输入]		视频质量
	// 返 回 值：
	// 函数说明：设置视频质量(数值为0~50)
	// $_FUNCTION_END *********************************************************
	void SetVideoQuality(int aiVideoQuality);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetVideoEncoderPreset
	// 函数参数：aiVideoEncoderPreset		[输入]		视频编码预设值
	// 返 回 值：
	// 函数说明：设置视频编码预设值(数值为0~8)
	// $_FUNCTION_END *********************************************************
	void SetVideoEncoderPreset(int aiVideoEncoderPreset);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetAudioSampleRate
	// 函数参数：aiAudioSampleRate			[输入]		音频采样率
	// 返 回 值：
	// 函数说明：设置音频采样率
	// $_FUNCTION_END *********************************************************
	void SetAudioSampleRate(int aiAudioSampleRate);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetAudioChannels
	// 函数参数：aiAudioChannels			[输入]		音频通道数
	// 返 回 值：
	// 函数说明：设置音频通道数(数值为1或者2)
	// $_FUNCTION_END *********************************************************
	void SetAudioChannels(int aiAudioChannels);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetAudioBitRate
	// 函数参数：aiAudioBitRate				[输入]		音频码率
	// 返 回 值：
	// 函数说明：设置音频码率
	// $_FUNCTION_END *********************************************************
	void SetAudioBitRate(int aiAudioBitRate);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetReconnectTime
	// 函数参数：aiReconnectTime			[输入]		重新连接服务的时间间隔(单位:毫秒)
	// 返 回 值：
	// 函数说明：设置重新连接服务的时间间隔(单位:毫秒)
	// $_FUNCTION_END *********************************************************
	void SetReconnectTime(int aiReconnectTime);

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetPause
	// 函数参数：abPause				[输入]		是否暂停
	// 返 回 值：
	// 函数说明：设置是否暂停
	// $_FUNCTION_END *********************************************************
	void SetPause(bool abPause);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetMute
	// 函数参数：abMute					[输入]		是否静音
	// 返 回 值：
	// 函数说明：设置是否静音
	// $_FUNCTION_END *********************************************************
	void SetMute(bool abMute);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：VideoCaptureData
	// 函数参数：aiFrameWidth			[输入]		帧宽
	//			 aiFrameHeight			[输入]		帧高
	//			 aiVideoFmt				[输入]		视频格式(1:NV21 2:RGBA)
	//			 apData					[输入]		数据
	//			 aiDataLen				[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：视频采集数据
	// $_FUNCTION_END *********************************************************
	bool VideoCaptureData(int aiFrameWidth, int aiFrameHeight, int aiVideoFmt, const char* apData, int aiDataLen);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AudioCaptureData
	// 函数参数：apData					[输入]		数据
	//			 aiDataLen				[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：音频采集数据
	// $_FUNCTION_END *********************************************************
	bool AudioCaptureData(const char* apData, int aiDataLen);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AudioBackgroundData
	// 函数参数：apData					[输入]		数据
	//			 aiDataLen				[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：音频背景数据
	// $_FUNCTION_END *********************************************************
	bool AudioBackgroundData(const char* apData, int aiDataLen);

private:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：VideoEncodeThread
	// 函数参数：apParam			[输入]	参数
	// 返 回 值：线程的返回代码
	// 函数说明：视频编码线程
	// $_FUNCTION_END *********************************************************
	static void* VideoEncodeThread(void* apParam);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：VideoEncodeFunc
	// 函数参数：
	// 返 回 值：线程的返回代码
	// 函数说明：视频编码函数
	// $_FUNCTION_END *********************************************************
	int VideoEncodeFunc();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AudioEncodeThread
	// 函数参数：apParam			[输入]	参数
	// 返 回 值：线程的返回代码
	// 函数说明：音频编码线程
	// $_FUNCTION_END *********************************************************
	static void* AudioEncodeThread(void* apParam);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AudioEncodeFunc
	// 函数参数：
	// 返 回 值：线程的返回代码
	// 函数说明：音频编码函数
	// $_FUNCTION_END *********************************************************
	int AudioEncodeFunc();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SendThread
	// 函数参数：apParam			[输入]	参数
	// 返 回 值：线程的返回代码
	// 函数说明：发送线程
	// $_FUNCTION_END *********************************************************
	static void* SendThread(void* apParam);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SendFunc
	// 函数参数：
	// 返 回 值：线程的返回代码
	// 函数说明：发送函数
	// $_FUNCTION_END *********************************************************
	int SendFunc();

private:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：InitLog
	// 函数参数：apLogOutputPath		[输入]	日志输出路径
	//			 aiLogLevel				[输入]	日志等级
	// 返 回 值：调用是否成功
	// 函数说明：初始化日志
	// $_FUNCTION_END *********************************************************
	bool InitLog(const char* apLogOutputPath, int aiLogLevel);

private:
	// 推流参数
	STRUCT_PUSH_STREAM_PARAM			m_PushStreamParam;

	// 是否初始化
	bool								m_bInit;

	// 是否暂停推流
	bool								m_bPausePushStream;

	// 是否静音
	bool								m_bMute;

    // 静音数据
	uint8_t								m_szSilenceData[4096];
    
	// 音频PTS
	int64_t								m_iAudioPTS;

	// 采集视频帧缓存
	CVideoFrameCache					m_CaptureVideoFrameCache;
	
	// 音频字节对齐
	CAudioAlignment						m_AudioAlignment;
	
	// 视频格式转换
	CVideoFormatConvert					m_VideoFormatConvert;

	// 视频编码
	CVideoEncode						m_VideoEncode;

	// 音频编码
	CAudioEncode						m_AudioEncode;

	// 发送队列
	CAVPacketQueue						m_AVPacketQueue;

	// 发送处理
	CSendDeal							m_SendDeal;

	// 视频流ID
	int									m_iVideoStreamID;

	// 音频流ID
	int									m_iAudioStreamID;

	// 视频编码线程退出标志
	volatile bool						m_bVideoEncodeThreadExit;

	// 视频编码线程
	pthread_t							m_VideoEncodeThreadID;

	// 音频编码线程退出标志
	volatile bool						m_bAudioEncodeThreadExit;

	// 音频编码线程
	pthread_t							m_AudioEncodeThreadID;

	// 发送线程退出标志
	volatile bool						m_bSendThreadExit;

	// 发送线程
	pthread_t							m_SendThreadID;
};

#endif