#ifndef VIDEO_FRAME_CACHE_H
#define VIDEO_FRAME_CACHE_H

#include <mutex>
using namespace std;

// 帧缓存个数
#define DEF_VIDEO_FRAME_NUM		2

struct AVFrame;
class CVideoFrameCache
{
public:
	CVideoFrameCache();
	~CVideoFrameCache();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：aiPixelFormat		[输入]		像素格式
	//			 aiFrameWidth		[输入]		帧宽
	//			 aiFrameHeight		[输入]		帧高
	// 返 回 值：调用是否成功
	// 函数说明：打开
	// $_FUNCTION_END *********************************************************
	bool Open(int aiPixelFormat, int aiFrameWidth, int aiFrameHeight);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Close
	// 函数参数：
	// 返 回 值：
	// 函数说明：关闭
	// $_FUNCTION_END *********************************************************
	void Close();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：SetFrame
	// 函数参数：apData					[输入]		数据
	//			 aiDataLen				[输入]		数据大小
	// 返 回 值：调用是否成功
	// 函数说明：设置帧
	// $_FUNCTION_END *********************************************************
	bool SetFrame(const char* apData, int aiDataLen);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：GetFrame
	// 函数参数：
	// 返 回 值：帧数据
	// 函数说明：获取帧
	// $_FUNCTION_END *********************************************************
	AVFrame* GetFrame();

private:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Alloc
	// 函数参数：aiPixelFormat		[输入]		像素格式
	//			 aiFrameWidth		[输入]		帧宽
	//			 aiFrameHeight		[输入]		帧高
	// 返 回 值：调用是否成功
	// 函数说明：分配帧内存
	// $_FUNCTION_END *********************************************************
	bool Alloc(int aiPixelFormat, int aiFrameWidth, int aiFrameHeight);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Free
	// 函数参数：
	// 返 回 值：
	// 函数说明：释放帧内存
	// $_FUNCTION_END *********************************************************
	void Free();

private:
	// 锁
	mutex						m_Lock;

	// 是否有新数据
	bool						m_bHasNewFrame;

	// 像素格式
	int							m_iPixelFormat;

	// 帧宽
	int							m_iFrameWidth;

	// 帧高
	int							m_iFrameHeight;

	// 帧缓存
	AVFrame*					m_pFrame[DEF_VIDEO_FRAME_NUM];

	// AVFrame中保存数据的buf
	uint8_t*					m_pFrameData[DEF_VIDEO_FRAME_NUM];
};

#endif