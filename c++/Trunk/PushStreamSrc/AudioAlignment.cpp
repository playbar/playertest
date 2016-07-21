#include <assert.h>
#include "AudioAlignment.h"
#include "Log.h"

extern "C"
{
#include "libavformat/avformat.h"
#include "libavutil/audio_fifo.h"
}

// 每秒采样(AAC为1024)
#define DEF_NB_SAMPLE		1024 * 4

CAudioAlignment::CAudioAlignment()
{
	m_bInit = false;
	m_pAVAudioFifo = nullptr;
}

CAudioAlignment::~CAudioAlignment()
{
	assert(m_pAVAudioFifo == nullptr);
}

// 打开
bool CAudioAlignment::Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam)
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	if (m_bInit)
	{
		return true;
	}

	if (m_pAVAudioFifo != nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::Open m_pAVAudioFifo != nullptr!");
		assert(false);
		return false;
	}
	
	m_pAVAudioFifo = av_audio_fifo_alloc(AV_SAMPLE_FMT_S16, aPushStreamParam.iAudioChannels, DEF_NB_SAMPLE);
	if (m_pAVAudioFifo == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::Open av_audio_fifo_alloc Failed!");
		assert(false);
		return false;
	}
	
	m_bInit = true;
	
	return true;
}

// 关闭
void CAudioAlignment::Close()
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	if (m_pAVAudioFifo != nullptr)
	{
		av_audio_fifo_free(m_pAVAudioFifo);
		m_pAVAudioFifo = nullptr;
	}
	
	m_bInit = false;
}

// 设置数据
bool CAudioAlignment::SetData(uint8_t* apData, int aiNbSample)
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	if (apData == nullptr || aiNbSample <= 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::SetData apData == nullptr || aiNbSample <= 0!");
		assert(false);
		return false;
	}
	
	if (!m_bInit)
	{
		return false;
	}
	
	if (m_pAVAudioFifo == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::SetData m_pAVAudioFifo == nullptr!");
		assert(false);
		return false;
	}
	
	if (av_audio_fifo_write(m_pAVAudioFifo, (void**)&apData, aiNbSample) < aiNbSample)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::SetData av_audio_fifo_write Failed!");
		assert(false);
		return false;
	}
	
	return true;
}

// 获取数据
bool CAudioAlignment::GetData(uint8_t* apData, int aiNbSample)
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	if (!m_bInit)
	{
		return false;
	}
	
	if (apData == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::GetData apData == nullptr!");
		assert(false);
		return false;
	}
	
	if (m_pAVAudioFifo == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::GetData m_pAVAudioFifo == nullptr!");
		assert(false);
		return false;
	}
	
	int iFifoSize = av_audio_fifo_size(m_pAVAudioFifo);
	if (iFifoSize < aiNbSample)
	{
		return false;
	}
	
	if(iFifoSize > 1024 * 10)
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level4, "audio fifo size =%d", iFifoSize);
	
	if (av_audio_fifo_read(m_pAVAudioFifo, (void**)&apData, aiNbSample) != aiNbSample)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioAlignment::GetData av_audio_fifo_read != %d", aiNbSample);
		assert(false);
		return false;
	}
	
	return true;
}