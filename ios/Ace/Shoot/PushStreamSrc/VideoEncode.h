#ifndef VIDEO_ENCODE_H
#define VIDEO_ENCODE_H

#include "PushStreamParam.h"

struct AVCodecContext;
struct AVFrame;
struct AVPacket;
class CVideoEncode
{
public:
	CVideoEncode();
	~CVideoEncode();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：aPushStreamParam		[输入]	推流参数
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
	// 函数名称：Encode
	// 函数参数：apFrame				[输入]		视频帧
	//			 apPacket				[输入|输出]	包数据
	// 返 回 值：调用是否成功
	// 函数说明：编码
	// $_FUNCTION_END *********************************************************
	bool Encode(AVFrame* apFrame, AVPacket* apPacket);
private:
	// 编码器上下文
	AVCodecContext*	m_pCodecCtx;
};

#endif