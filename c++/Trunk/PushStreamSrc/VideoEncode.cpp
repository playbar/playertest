#include <assert.h>
#include "VideoEncode.h"
#include "Log.h"

extern "C"
{
#include "libavcodec/avcodec.h"
}

CVideoEncode::CVideoEncode()
{
	m_pCodecCtx = nullptr;
}

CVideoEncode::~CVideoEncode()
{
	assert(m_pCodecCtx == nullptr);
}

// 打开
bool CVideoEncode::Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam)
{
	if (m_pCodecCtx != nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open m_pCodecCtx != nullptr!");

		assert(false);
		return false;
	}

	// 查找编码器
	AVCodec* pCodec = avcodec_find_encoder(AV_CODEC_ID_H264);
	if (pCodec == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open pCodec == nullptr!");

		assert(false);
		return false;
	}

	// 创建编码器上下文
	m_pCodecCtx = avcodec_alloc_context3(pCodec);
	if (m_pCodecCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open m_pCodecCtx == nullptr!");

		assert(false);
		return false;
	}

	// 设置编码器上下文
	m_pCodecCtx->codec_type = AVMEDIA_TYPE_VIDEO;
	m_pCodecCtx->pix_fmt = PIX_FMT_YUV420P;
	m_pCodecCtx->width = aPushStreamParam.iVideoPushStreamWidth;
	m_pCodecCtx->height = aPushStreamParam.iVideoPushStreamHeight;
	m_pCodecCtx->time_base.num = 1;
	m_pCodecCtx->time_base.den = aPushStreamParam.iVideoFrameRate;
	m_pCodecCtx->gop_size = aPushStreamParam.iVideoFrameSpacing;	// 两个I帧之间的帧数
	m_pCodecCtx->bit_rate = aPushStreamParam.iVideoBitRate;
	m_pCodecCtx->rc_min_rate = aPushStreamParam.iVideoMinBitRate;
	m_pCodecCtx->rc_max_rate = aPushStreamParam.iVideoMaxBitRate;

	char szValue[256] {};
	switch (aPushStreamParam.iVideoEncoderPreset)
	{
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_ULTRAFAST:
		strcpy(szValue, "ultrafast");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_SUPERFAST:
		strcpy(szValue, "superfase");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_VERYFAST:
		strcpy(szValue, "veryfast");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_FASTER:
		strcpy(szValue, "faster");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_FAST:
		strcpy(szValue, "fast");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_MEDIUM:
		strcpy(szValue, "medium");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_SLOW:
		strcpy(szValue, "slow");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_SLOWER:
		strcpy(szValue, "slower");
		break;
	case ENUM_VIDEO_ENCODE_PRESET_TYPE_VERYSLOW:
		strcpy(szValue, "veryslow");
		break;
	default:
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open VideoEncoderPreset Error!");
		assert(false);
		return false;
	}

	bool bSuccess = false;
	AVDictionary* pOption = nullptr;

	do
	{
		if (m_pCodecCtx->codec_id == AV_CODEC_ID_H264)		// H.264
		{
			// 为了实时编码而设置的指令
			if (av_dict_set(&pOption, "preset", szValue, 0) < 0)
			{
				CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open av_dict_set 'preset' Failed!");

				assert(false);
				break;
			}

			if (av_dict_set(&pOption, "tune", "zerolatency", 0) < 0)
			{
				CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open av_dict_set 'tune' 'zerolatency' Failed!");

				assert(false);
				break;
			}
		}
		else
		{
			// 目前只支持H.264
			break;
		}

		// 打开编码器
		if (avcodec_open2(m_pCodecCtx, pCodec, &pOption) < 0)
		{
			CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Open avcodec_open2 Failed!");

			assert(false);
			break;
		}

		bSuccess = true;

	} while (false);

	// 释放资源
	if (pOption != nullptr)
	{
		av_dict_free(&pOption);
		pOption = nullptr;
	}

	return bSuccess;
}

// 关闭
void CVideoEncode::Close()
{
	if (m_pCodecCtx != nullptr)
	{
		// 清空编码buffer和相关状态
		avcodec_flush_buffers(m_pCodecCtx);
		// 关闭编码器上下文
		avcodec_close(m_pCodecCtx);
		// 释放编码器
		av_free(m_pCodecCtx);
		m_pCodecCtx = nullptr;
	}
}

// 编码
bool CVideoEncode::Encode(AVFrame* apFrame, AVPacket* apPacket)
{
	if (apFrame == nullptr || apPacket == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Encode apFrame == nullptr || apPacket == nullptr!");

		assert(false);
		return false;
	}

	if (m_pCodecCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Encode m_pCodecCtx == nullptr!");

		assert(false);
		return false;
	}

	int iGot = -1;
	if (avcodec_encode_video2(m_pCodecCtx, apPacket, apFrame, &iGot) < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoEncode::Encode avcodec_encode_video2 Failed!");

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
