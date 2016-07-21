#include "PushStreamInterface.h"
#include "PushStreamEngine.h"
#include "Single.h"

CPushStreamInterface::CPushStreamInterface()
{
}

CPushStreamInterface::~CPushStreamInterface()
{
}

// 推流初始化
bool CPushStreamInterface::Init(const char* apLogDirectories, int aiLogLevel)
{
	// 初始化单例
	CSingle<CPushStreamEngine>::GetInstance();

	// 设置日志目录
	return CSingle<CPushStreamEngine>::GetInstance().SetLogDirectories(apLogDirectories, aiLogLevel);
}

// 打开推流
bool CPushStreamInterface::Open(CPushStreamNotify* pNotify)
{
	return CSingle<CPushStreamEngine>::GetInstance().Open(pNotify);
}

// 关闭推流
void CPushStreamInterface::Close()
{
	CSingle<CPushStreamEngine>::GetInstance().Close();
}

// 设置推流URL
void CPushStreamInterface::SetPushStreamURL(const char* apURL)
{
	CSingle<CPushStreamEngine>::GetInstance().SetPushStreamURL(apURL);
}

// 设置视频推流分辨率
void CPushStreamInterface::SetVideoPushStreamResolution(int aiVideoPushStreamWidth, int aiVideoPushStreamHeight)
{
	CSingle<CPushStreamEngine>::GetInstance().SetVideoPushStreamResolution(aiVideoPushStreamWidth, aiVideoPushStreamHeight);
}

// 设置视频帧率
void CPushStreamInterface::SetVideoFrameRate(int aiVideoFrameRate)
{
	CSingle<CPushStreamEngine>::GetInstance().SetVideoFrameRate(aiVideoFrameRate);
}

// 设置视频I帧的帧间距
void CPushStreamInterface::SetVideoFrameSpacing(int aiVideoFrameSpacing)
{
	CSingle<CPushStreamEngine>::GetInstance().SetVideoFrameSpacing(aiVideoFrameSpacing);
}

// 设置视频推流码率
void CPushStreamInterface::SetVideoBitRate(int aiVideoBitRate, int aiVideoMinBitRate, int aiVideoMaxBitRate)
{
	CSingle<CPushStreamEngine>::GetInstance().SetVideoBitRate(aiVideoBitRate, aiVideoMinBitRate, aiVideoMaxBitRate);
}

// 设置视频质量(数值为0~50)
void CPushStreamInterface::SetVideoQuality(int aiVideoQuality)
{
	CSingle<CPushStreamEngine>::GetInstance().SetVideoQuality(aiVideoQuality);
}

// 设置视频编码预设值(数值为0~8)
void CPushStreamInterface::SetVideoEncoderPreset(int aiVideoEncoderPreset)
{
	CSingle<CPushStreamEngine>::GetInstance().SetVideoEncoderPreset(aiVideoEncoderPreset);
}

// 设置音频采样率
void CPushStreamInterface::SetAudioSampleRate(int aiAudioSampleRate)
{
	CSingle<CPushStreamEngine>::GetInstance().SetAudioSampleRate(aiAudioSampleRate);
}

// 设置音频通道数(数值为1或者2)
void CPushStreamInterface::SetAudioChannels(int aiAudioChannels)
{
	CSingle<CPushStreamEngine>::GetInstance().SetAudioChannels(aiAudioChannels);
}

// 设置音频码率
void CPushStreamInterface::SetAudioBitRate(int aiAudioBitRate)
{
	CSingle<CPushStreamEngine>::GetInstance().SetAudioBitRate(aiAudioBitRate);
}

// 设置重新连接服务的时间间隔(单位:毫秒)
void CPushStreamInterface::SetReconnectTime(int aiReconnectTime)
{
	CSingle<CPushStreamEngine>::GetInstance().SetReconnectTime(aiReconnectTime);
}

void CPushStreamInterface::SetHardEncode(bool abUse)
{
    CSingle<CPushStreamEngine>::GetInstance().SetHardEncode(abUse);
}

// 设置是否暂停
void CPushStreamInterface::SetPause(bool abPause)
{
	CSingle<CPushStreamEngine>::GetInstance().SetPause(abPause);
}

// 设置是否静音
void CPushStreamInterface::SetMute(bool abMute)
{
	CSingle<CPushStreamEngine>::GetInstance().SetMute(abMute);
}

// 视频采集数据
bool CPushStreamInterface::VideoCaptureData(int aiFrameWidth, int aiFrameHeight, int aiVideoFmt, const char* apData, int aiDataLen)
{
	return CSingle<CPushStreamEngine>::GetInstance().VideoCaptureData(aiFrameWidth, aiFrameHeight, aiVideoFmt, apData, aiDataLen);
}

// 音频采集数据
bool CPushStreamInterface::AudioCaptureData(const char* apData, int aiDataLen)
{
	return CSingle<CPushStreamEngine>::GetInstance().AudioCaptureData(apData, aiDataLen);
}

// 音频背景数据
bool CPushStreamInterface::AudioBackgroundData(const char* apData, int aiDataLen)
{
	return CSingle<CPushStreamEngine>::GetInstance().AudioBackgroundData(apData, aiDataLen);
}