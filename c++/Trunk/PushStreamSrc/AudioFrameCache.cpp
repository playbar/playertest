#include <assert.h>
#include "AudioFrameCache.h"
#include "Log.h"

extern "C"
{
#include "libavformat/avformat.h"
}

CAudioFrameCache::CAudioFrameCache()
{
	m_bHasNewFrame = false;

	for (int i = 0; i < DEF_AUDIO_FRAME_NUM; ++i)
	{
		m_pData[i] = nullptr;
		m_iDataLen[i] = 0;
	}
	m_iBufferLen = 0;
}

CAudioFrameCache::~CAudioFrameCache()
{
	for (int i = 0; i < DEF_AUDIO_FRAME_NUM; ++i)
	{
		assert(m_pData[i] == nullptr);
		assert(m_iDataLen[i] == 0);
	}
}

// 打开
bool CAudioFrameCache::Open()
{
	// 目前无处理
	return true;
}

// 关闭
void CAudioFrameCache::Close()
{
	lock_guard<mutex> AutoLock(m_Lock);

	Free();
}

// 清空缓存
void CAudioFrameCache::Clear()
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	m_bHasNewFrame = false;
}

// 设置帧
bool CAudioFrameCache::SetFrame(const uint8_t* apData, unsigned int aiDataLen)
{
	lock_guard<mutex> AutoLock(m_Lock);

	if (m_pData[0] == nullptr || m_pData[1] == nullptr)
	{
		if (!Alloc(aiDataLen))
		{
			CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioFrameCache::SetFrame Alloc Failed!");

			assert(false);
			return false;
		}
	}
	else
	{
		if (m_iBufferLen < aiDataLen)
		{
			Free();

			if (!Alloc(aiDataLen))
			{
				CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioFrameCache::SetFrame Alloc Failed!");

				assert(false);
				return false;
			}
		}
	}

	memcpy(m_pData[0], apData, aiDataLen);
	m_iDataLen[0] = aiDataLen;

	m_bHasNewFrame = true;

	return true;
}

// 获取帧
bool CAudioFrameCache::GetFrame(uint8_t*& apData, unsigned int& aiDataLen)
{
	lock_guard<mutex> AutoLock(m_Lock);

	if (!m_bHasNewFrame)
	{
		return false;
	}

	uint8_t* pTmp = m_pData[0];
	m_pData[0] = m_pData[1];
	m_pData[1] = pTmp;

	unsigned int iTmp = m_iDataLen[0];
	m_iDataLen[0] = m_iDataLen[1];
	m_iDataLen[1] = iTmp;

	m_bHasNewFrame = false;

	apData = m_pData[1];
	aiDataLen = m_iDataLen[1];

	return true;
}

// 分配帧内存
bool CAudioFrameCache::Alloc(unsigned int aiBufferLen)
{
	if (aiBufferLen == 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioFrameCache::Alloc aiBufferLen != 0!");

		assert(false);
		return false;
	}

	for (int i = 0; i < DEF_AUDIO_FRAME_NUM; ++i)
	{
		m_pData[i] = (uint8_t *)av_malloc(aiBufferLen);
	}
	m_iBufferLen = aiBufferLen;

	return true;
}

// 释放帧内存
void CAudioFrameCache::Free()
{
	m_bHasNewFrame = false;

	for (int i = 0; i < DEF_AUDIO_FRAME_NUM; ++i)
	{
		av_free(m_pData[i]);
		m_pData[i] = nullptr;
		m_iDataLen[i] = 0;
	}
	m_iBufferLen = 0;
}