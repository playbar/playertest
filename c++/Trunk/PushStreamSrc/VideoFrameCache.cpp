#include <assert.h>
#include "VideoFrameCache.h"
#include "Log.h"

extern "C"
{
#include "libavformat/avformat.h"
}

CVideoFrameCache::CVideoFrameCache()
{
	m_bHasNewFrame = false;
	m_iPixelFormat = 0;
	m_iFrameWidth = 0;
	m_iFrameHeight = 0;

	for (int i = 0; i < DEF_VIDEO_FRAME_NUM; ++i)
	{
		m_pFrame[i] = nullptr;
		m_pFrameData[i] = nullptr;
	}
}

CVideoFrameCache::~CVideoFrameCache()
{
	for (int i = 0; i < DEF_VIDEO_FRAME_NUM; ++i)
	{
		assert(m_pFrame[i] == nullptr);
		assert(m_pFrameData[i] == nullptr);
	}
}

// 打开
bool CVideoFrameCache::Open(int aiPixelFormat, int aiFrameWidth, int aiFrameHeight)
{
	AVPixelFormat PixelFormat;
	switch (aiPixelFormat)
	{
	case 1:
		PixelFormat = AV_PIX_FMT_NV21;
		break;
	case 2:
		PixelFormat = AV_PIX_FMT_RGBA;
		break;
	default:
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFrameCache::Open aiPixelFormat == %d!", aiPixelFormat);
		assert(false);
		return false;
	}

	if (PixelFormat == m_iPixelFormat && aiFrameWidth == m_iFrameWidth && aiFrameHeight == m_iFrameHeight)
	{
		return true;
	}

	lock_guard<mutex> AutoLock(m_Lock);

	Free();

	return Alloc(PixelFormat, aiFrameWidth, aiFrameHeight);
}

// 关闭
void CVideoFrameCache::Close()
{
	lock_guard<mutex> AutoLock(m_Lock);

	Free();
}

// 设置帧
bool CVideoFrameCache::SetFrame(const char* apData, int aiDataLen)
{
	if (apData == nullptr || aiDataLen <= 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFrameCache::SetFrame apData == nullptr || aiDataLen <= 0!");
		assert(false);
		return false;
	}

	lock_guard<mutex> AutoLock(m_Lock);

	if (m_pFrame[0] == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFrameCache::SetFrame m_pFrame[0] == nullptr!");

		assert(false);
		return false;
	}

	if (avpicture_fill((AVPicture *)m_pFrame[0], (uint8_t*)apData, (AVPixelFormat)m_iPixelFormat, m_iFrameWidth, m_iFrameHeight) < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFrameCache::SetFrame avpicture_fill Failed!");
		assert(false);
		return false;
	}

	m_bHasNewFrame = true;

	return true;
}

// 获取帧
AVFrame* CVideoFrameCache::GetFrame()
{
	lock_guard<mutex> AutoLock(m_Lock);

	// 暂未开始采集
	if (m_pFrame[0] == nullptr || m_pFrame[1] == nullptr)
	{
		return nullptr;
	}

	if (!m_bHasNewFrame)
	{
		return nullptr;
	}

	AVFrame* pTemp = m_pFrame[0];
	m_pFrame[0] = m_pFrame[1];
	m_pFrame[1] = pTemp;

	m_bHasNewFrame = false;

	return m_pFrame[1];
}

// 分配帧内存
bool CVideoFrameCache::Alloc(int aiPixelFormat, int aiFrameWidth, int aiFrameHeight)
{
	if (aiPixelFormat <= 0 || aiFrameWidth == 0 || aiFrameHeight == 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFrameCache::Alloc aiPixelFormat <= 0 || aiFrameWidth == 0 || aiFrameHeight == 0!");

		assert(false);
		return false;
	}

	// 是否成功
	bool bSuccess = true;

	for (int i = 0; i < DEF_VIDEO_FRAME_NUM; ++i)
	{
		m_pFrame[i] = av_frame_alloc();
		m_pFrameData[i] = (uint8_t *)av_malloc(avpicture_get_size((AVPixelFormat)aiPixelFormat, aiFrameWidth, aiFrameHeight));

		// 用ptr中的内容根据文件格式(YUV…)和分辨率填充picture.这里由于是在初始化阶段,所以填充的可能全是零.
		if (avpicture_fill((AVPicture *)m_pFrame[i], m_pFrameData[i], (AVPixelFormat)aiPixelFormat, aiFrameWidth, aiFrameHeight) < 0)
		{
			bSuccess = false;
			break;
		}
		m_pFrame[i]->format = aiPixelFormat;
		m_pFrame[i]->width = aiFrameWidth;
		m_pFrame[i]->height = aiFrameHeight;
	}

	if (!bSuccess)
	{
		Free();
	}
	else
	{
		m_iPixelFormat = aiPixelFormat;
		m_iFrameWidth = aiFrameWidth;
		m_iFrameHeight = aiFrameHeight;
	}

	return bSuccess;
}

// 释放帧内存
void CVideoFrameCache::Free()
{
	for (int i = 0; i < DEF_VIDEO_FRAME_NUM; ++i)
	{
		av_free(m_pFrameData[i]);
		m_pFrameData[i] = nullptr;

		av_frame_free(&m_pFrame[i]);
		m_pFrame[i] = nullptr;
	}

	m_iPixelFormat = 0;
	m_iFrameWidth = 0;
	m_iFrameHeight = 0;

	m_bHasNewFrame = false;
}