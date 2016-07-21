#ifndef AUDIO_ALIGNMENT_H
#define AUDIO_ALIGNMENT_H

#include "PushStreamParam.h"
#include <mutex>
using namespace std;

struct AVAudioFifo;
struct AVFrame;
class CAudioAlignment
{
public:
	CAudioAlignment();
	~CAudioAlignment();
	
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
	// 函数名称：SetData
	// 函数参数：apData						[输入]		数据
	//			 aiNbSample					[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：设置数据
	// $_FUNCTION_END *********************************************************
	bool SetData(uint8_t* apData, int aiNbSample);
	
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：GetData
	// 函数参数：apData					[输入|输出]	帧
	//			 aiNbSample				[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：获取数据
	// $_FUNCTION_END *********************************************************
	bool GetData(uint8_t* apData, int aiNbSample);
	
private:
	// 锁
	mutex					m_Lock;
	
	// 是否初始化
	bool					m_bInit;
	
	// 音频队列
	AVAudioFifo*			m_pAVAudioFifo;
};

#endif