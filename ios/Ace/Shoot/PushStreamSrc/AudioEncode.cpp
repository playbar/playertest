#include <assert.h>
#include "AudioEncode.h"
#include "Log.h"

extern "C"
{
#include "libavcodec/avcodec.h"
}

CAudioEncode::CAudioEncode()
{
	m_pCodecCtx = nullptr;
}

CAudioEncode::~CAudioEncode()
{
	assert(m_pCodecCtx == nullptr);
}

// 打开
bool CAudioEncode::Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam)
{
	if (m_pCodecCtx != nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Open m_pCodecCtx != nullptr!");

		assert(false);
		return false;
	}

	// 查找编码器
	AVCodec* pCodec = avcodec_find_encoder(AV_CODEC_ID_AAC);
	if (pCodec == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Open pCodec == nullptr!");

		assert(false);
		return false;
	}

	// 创建编码器上下文
	m_pCodecCtx = avcodec_alloc_context3(pCodec);
	if (m_pCodecCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Open m_pCodecCtx == nullptr!");

		assert(false);
		return false;
	}

	// 设置编码器上下文
	if (m_pCodecCtx->codec_id == AV_CODEC_ID_AAC)
	{
		m_pCodecCtx->codec_type = AVMEDIA_TYPE_AUDIO;
		m_pCodecCtx->sample_fmt = AV_SAMPLE_FMT_S16;
		m_pCodecCtx->sample_rate = aPushStreamParam.iAudioSampleRate;

		if (aPushStreamParam.iAudioChannels == 1)
		{
			m_pCodecCtx->channel_layout = AV_CH_LAYOUT_MONO;
		}
		else if (aPushStreamParam.iAudioChannels == 2)
		{
			m_pCodecCtx->channel_layout = AV_CH_LAYOUT_STEREO;
		}
	}
	else
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Open codec_id Not AAC!");
		assert(false);
		return false;
	}

	// 打开编码器
	if (avcodec_open2(m_pCodecCtx, pCodec, nullptr) < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Open avcodec_open2 Failed!");

		assert(false);
		return false;
	}

	CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Open Success!");

	return true;
}

// 关闭
void CAudioEncode::Close()
{
	if (m_pCodecCtx != nullptr)
	{
		// 清空解码buffer和相关状态
		avcodec_flush_buffers(m_pCodecCtx);
		// 关闭解码器上下文
		avcodec_close(m_pCodecCtx);
		// 释放解码器
		av_free(m_pCodecCtx);
		m_pCodecCtx = nullptr;
	}

	CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Close Success!");
}

// 编码
bool CAudioEncode::Encode(AVFrame* apFrame, AVPacket* apPacket)
{
	if (apFrame == nullptr || apPacket == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Encode apFrame == nullptr || apPacket == nullptr!");

		assert(false);
		return false;
	}

	if (m_pCodecCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Encode m_pCodecCtx == nullptr!");

		assert(false);
		return false;
	}

	int iGot = -1;
	if (avcodec_encode_audio2(m_pCodecCtx, apPacket, apFrame, &iGot) < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioEncode::Encode avcodec_encode_audio2 Failed!");

		assert(false);
		return false;
	}

	bool bSuccess = false;
	if (iGot == 1)
	{
		bSuccess = true;
	}

	return bSuccess;
}

// 获取编码器上下文
AVCodecContext* CAudioEncode::GetCodecCtx()
{
	return m_pCodecCtx;
}