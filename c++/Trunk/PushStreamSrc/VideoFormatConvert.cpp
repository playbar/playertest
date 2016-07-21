#include <assert.h>
#include "VideoFormatConvert.h"
#include "Log.h"

extern "C"
{
#include "libavcodec/avcodec.h"
#include "libswscale/swscale.h"

#ifndef USE_FFMPEG_CONVERT
#ifndef ANDROID
#include "libyuv/libyuv.h"
#else
#include "libyuv/include/libyuv.h"
#endif
#endif
    
}

CVideoFormatConvert::CVideoFormatConvert()
{
#ifdef USE_FFMPEG_CONVERT
	m_pImgConvertCtx = nullptr;
#endif
	m_iSrcWidth = 0;
	m_iSrcHeight = 0;
	m_iSrcFmt = AV_PIX_FMT_NONE;
	m_iDestWidth = 0;
	m_iDestHeight = 0;
	m_iDestFmt = AV_PIX_FMT_YUV420P;
}

CVideoFormatConvert::~CVideoFormatConvert()
{
#ifdef USE_FFMPEG_CONVERT
	assert(m_pImgConvertCtx == nullptr);
#endif
}

// 打开(AVPixelFormat)
bool CVideoFormatConvert::Open(int aiSrcFormat, int aiSrcWidth, int aiSrcHeight, int aiDestFormat, int aiDestWidth, int aiDstHeight)
{
	// 判断是否和当前的相同
	if (aiSrcWidth == m_iSrcWidth && aiSrcHeight == m_iSrcHeight && aiSrcFormat == m_iSrcFmt &&
	aiDestWidth == m_iDestWidth && aiDstHeight == m_iDestHeight && aiDestFormat == m_iDestFmt)
	{
		return true;
	}

	Free();

#ifdef USE_FFMPEG_CONVERT
	if (m_pImgConvertCtx == nullptr)
	{
		m_pImgConvertCtx = sws_getContext(aiSrcWidth, aiSrcHeight, (AVPixelFormat)aiSrcFormat, aiDestWidth, aiDstHeight, (AVPixelFormat)aiDestFormat, SWS_BICUBIC, nullptr, nullptr, nullptr);
	}

	if (m_pImgConvertCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFormatConvert::Open m_pImgConvertCtx == nullptr!");

		assert(false);
		return false;
	}
#endif

	m_iSrcWidth = aiSrcWidth;
	m_iSrcHeight = aiSrcHeight;
	m_iSrcFmt = aiSrcFormat;
	m_iDestWidth = aiDestWidth;
	m_iDestHeight = aiDstHeight;
	m_iDestFmt = aiDestFormat;

	return true;
}

// 关闭
void CVideoFormatConvert::Close()
{
	Free();
}

// 将源帧转化为目的帧
bool CVideoFormatConvert::Convert(AVFrame* apSrcFrame, AVFrame* apDestFrame)
{
	if (apSrcFrame == nullptr || apDestFrame == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFormatConvert::Convert apSrcFrame == nullptr || apDestFrame == nullptr!");

		assert(false);
		return false;
	}

	int iResult = -1;
#ifdef USE_FFMPEG_CONVERT
	if (m_pImgConvertCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVideoFormatConvert::Convert m_pImgConvertCtx == nullptr!");

		assert(false);
		return false;
	}

	iResult = sws_scale(m_pImgConvertCtx, (const uint8_t* const*)apSrcFrame->data, apSrcFrame->linesize, 0, m_iSrcHeight, apDestFrame->data, apDestFrame->linesize);

#else
		int dst_y_stride = apSrcFrame->width;
		int dst_u_stride = (apSrcFrame->width + 1) / 2;
		int dst_v_stride = (apSrcFrame->width + 1) / 2;
		int dst_h =(apSrcFrame->height + 1) / 2;
		
		uint8_t* pDstY = apDestFrame->data[0];
		uint8_t* pDstU = apDestFrame->data[1];
		uint8_t* pDstV = apDestFrame->data[2];
		
	if (m_iSrcFmt == AV_PIX_FMT_RGBA)
	{	
		iResult = libyuv::ABGRToI420(apSrcFrame->data[0],
									apSrcFrame->width * 4,
									pDstY, dst_y_stride,
									pDstU, dst_u_stride,
									pDstV, dst_v_stride,
									apSrcFrame->width, apSrcFrame->height);
	}
	
	iResult = iResult ? 0 : 1;
#endif

	if (iResult)
	{
		apDestFrame->width = apSrcFrame->width;
		apDestFrame->height = apSrcFrame->height;
		apDestFrame->format = AV_PIX_FMT_YUV420P;
		apDestFrame->key_frame = apSrcFrame->key_frame;
		apDestFrame->pict_type = apSrcFrame->pict_type;
	}

	return (iResult ? true : false);
}

// 释放资源
void CVideoFormatConvert::Free()
{
#ifdef USE_FFMPEG_CONVERT
	if (m_pImgConvertCtx != nullptr)
	{
		sws_freeContext(m_pImgConvertCtx);
		m_pImgConvertCtx = nullptr;
	}
#endif

	m_iSrcWidth = 0;
	m_iSrcHeight = 0;
	m_iSrcFmt = AV_PIX_FMT_NONE;
	m_iDestWidth = 0;
	m_iDestHeight = 0;
	m_iDestFmt = AV_PIX_FMT_YUV420P;
}