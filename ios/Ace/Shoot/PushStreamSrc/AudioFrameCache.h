#ifndef AUDIO_FRAME_CACHE_H
#define AUDIO_FRAME_CACHE_H

#include <mutex>
using namespace std;

// 帧缓存个数
#define DEF_AUDIO_FRAME_NUM		2

class CAudioFrameCache
{
public:
	CAudioFrameCache();
	~CAudioFrameCache();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：
	// 返 回 值：调用是否成功
	// 函数说明：打开
	// $_FUNCTION_END *********************************************************
	bool Open();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Close
	// 函数参数：
	// 返 回 值：
	// 函数说明：关闭
	// $_FUNCTION_END *********************************************************
	void Close();
	
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Clear
	// 函数参数：
	// 返 回 值：
	// 函数说明：清空缓存
	// $_FUNCTION_END *********************************************************
	void Clear();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetFrame
	// 函数参数：apData				[输入]		数据
	//			 aiDataLen			[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：设置帧
	// $_FUNCTION_END *********************************************************
	bool SetFrame(const uint8_t* apData, unsigned int aiDataLen);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：GetFrame
	// 函数参数：apData				[输入]		源帧数据
	//			 aiDataLen			[输入]		源帧数据大小
	// 返 回 值：调用是否成功
	// 函数说明：获取帧
	// $_FUNCTION_END *********************************************************
	bool GetFrame(uint8_t*& apData, unsigned int& aiDataLen);

private:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Alloc
	// 函数参数：aiBufferLen		[输入]		缓存大小
	// 返 回 值：调用是否成功
	// 函数说明：分配帧内存
	// $_FUNCTION_END *********************************************************
	bool Alloc(unsigned int aiBufferLen);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Free
	// 函数参数：
	// 返 回 值：
	// 函数说明：释放帧内存
	// $_FUNCTION_END *********************************************************
	void Free();

private:
	// 锁
	mutex			m_Lock;

	// 是否有新数据
	bool			m_bHasNewFrame;

	// 帧缓存,0:最新的数据,1:操作的数据
	uint8_t*		m_pData[DEF_AUDIO_FRAME_NUM];

	// 数据的长度
	unsigned int	m_iDataLen[DEF_AUDIO_FRAME_NUM];

	// 缓存大小
	unsigned int	m_iBufferLen;
};

#endif