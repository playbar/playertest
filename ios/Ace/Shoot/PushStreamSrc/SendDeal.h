#ifndef SEND_DEAL_H
#define SEND_DEAL_H

#include "PushStreamParam.h"
#include <string>
#include <mutex>
using namespace std;

struct AVPacket;
struct AVFormatContext;
struct AVStream;
struct AVBitStreamFilterContext;
struct AVCodecContext;
class CSendDeal
{
public:
	CSendDeal();
	~CSendDeal();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：aPushStreamParam			[输入]		推流参数
	// 返 回 值：调用是否成功
	// 函数说明：打开
	// $_FUNCTION_END *********************************************************
	bool Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Close
	// 函数参数：
	// 返 回 值：
	// 函数说明：关闭
	// $_FUNCTION_END *********************************************************
	void Close();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetCodecContext
	// 函数参数：apCodecCtx					[输入]		音频编码上下文
	// 返 回 值：
	// 函数说明：设置音频编码上下文
	// $_FUNCTION_END *********************************************************
	void SetCodecContext(AVCodecContext* apCodecCtx);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：GetVideoStreamID
	// 函数参数：
	// 返 回 值：视频流ID
	// 函数说明：获取视频流ID
	// $_FUNCTION_END *********************************************************
	int GetVideoStreamID();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：GetAudioStreamID
	// 函数参数：
	// 返 回 值：音频流ID
	// 函数说明：获取音频流ID
	// $_FUNCTION_END *********************************************************
	int GetAudioStreamID();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：VideoOutput
	// 函数参数：apPacket					[输入]		包
	// 返 回 值：0:成功,-1:失败,-2:连接断开
	// 函数说明：视频发送
	// $_FUNCTION_END *********************************************************
	int VideoOutput(AVPacket* apPacket);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AudioOutput
	// 函数参数：apPacket					[输入]		包
	// 返 回 值：0:成功,-1:失败,-2:连接断开
	// 函数说明：音频发送
	// $_FUNCTION_END *********************************************************
	int AudioOutput(AVPacket* apPacket);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：IsConnect
	// 函数参数：
	// 返 回 值：是否连接
	// 函数说明：是否连接
	// $_FUNCTION_END *********************************************************
	bool IsConnect();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Reconnect
	// 函数参数：
	// 返 回 值：
	// 函数说明：重新连接
	// $_FUNCTION_END *********************************************************
	void Reconnect();

private:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Clear
	// 函数参数：
	// 返 回 值：
	// 函数说明：清除资源
	// $_FUNCTION_END *********************************************************
	void Clear();
	
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Connect
	// 函数参数：
	// 返 回 值：调用是否成功
	// 函数说明：连接服务
	// $_FUNCTION_END *********************************************************
	bool Connect();
	
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AddVideoStream
	// 函数参数：apFmtCtx					[输入]		格式上下文
	//			 aiCodecId					[输入]		格式ID
	// 返 回 值：输出视频流
	// 函数说明：添加视频流
	// $_FUNCTION_END *********************************************************
	AVStream* AddVideoStream(AVFormatContext* apFmtCtx, int aiCodecId);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：OpenVideo
	// 函数参数：apVideoStream				[输入]		输出视频流
	// 返 回 值：调用是否成功
	// 函数说明：打开视频流
	// $_FUNCTION_END *********************************************************
	bool OpenVideo(AVStream* apVideoStream);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：AddAudioStream
	// 函数参数：apFmtCtx					[输入]		格式上下文
	//			 aiCodecId					[输入]		格式ID
	// 返 回 值：输出音频流
	// 函数说明：添加音频流
	// $_FUNCTION_END *********************************************************
	AVStream* AddAudioStream(AVFormatContext* apFmtCtx, int aiCodecId);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：OpenAudio
	// 函数参数：apAudioStream				[输入]		输出音频流
	// 返 回 值：调用是否成功
	// 函数说明：打开音频流
	// $_FUNCTION_END *********************************************************
	bool OpenAudio(AVStream* apAudioStream);

private:
	// 锁
	mutex						m_Lock;

	// 是否连接
	bool						m_bConnect;

	// 推流参数
	STRUCT_PUSH_STREAM_PARAM	m_PushStreamParam;

	// 格式上下文
	AVFormatContext*			m_pFormatCtx;

	// 输出视频流
	AVStream*					m_pVideoStream;

	// 输出音频流
	AVStream*					m_pAudioStream;

	// 音频编码上下文
	AVCodecContext*				m_pCodecCtx;

	// ADTS处理
	AVBitStreamFilterContext*	m_pAacBitStreamFilterCtx;
};

#endif