#include <assert.h>
#include "VideoEncodeVt.h"
#include "Log.h"

extern "C"
{
#include "libavcodec/avcodec.h"
}




enum VTH264Profile
{
    H264_PROF_AUTO,
    H264_PROF_BASELINE,
    H264_PROF_MAIN,
    H264_PROF_HIGH
};

enum VTH264Entropy
{
    H264_ENTROPY_NOT_SET,
    H264_CAVLC,
    H264_CABAC
};



static const uint8_t StartCode[] = {0, 0, 0, 1};


static void NumRelease(CFNumberRef* pRef)
{
    if(NULL != *pRef)
    {
        CFRelease(*pRef);
        *pRef = NULL;
    }
}



CVideoEncodeVt::CVideoEncodeVt()
{
    m_bOpen = false;
    m_bCompressErr = false;
    m_EncodeSession = NULL;
    m_EncBuffQueue.head = m_EncBuffQueue.tail = NULL;
    m_EncBuffQueue.cnt = 0;
    
    
    m_pExtraData = NULL;
    m_iExtraDataSize = 0;
    
    m_iFirstPts = AV_NOPTS_VALUE;
    m_iDtsDelta = AV_NOPTS_VALUE;

}

CVideoEncodeVt::~CVideoEncodeVt()
{
	
}

// 打开
bool CVideoEncodeVt::Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam)
{
    if(m_bOpen)
        return true;
    
    
    int ret = 0;
    CFMutableDictionaryRef pixelBufferInfo = NULL;
    CMVideoCodecType codecType = kCMVideoCodecType_H264;
    CFStringRef profileLevel = NULL;
    
    
    do
    {
        m_bCompressErr = false;
        m_bHasBFrames = false;
        m_iEntropy = H264_ENTROPY_NOT_SET;
        m_iProfile = H264_PROF_HIGH;
        m_iLevel = 0;
        m_iFrameRate = aPushStreamParam.iVideoFrameRate;
        
        //参数检查
        if(m_iProfile == H264_PROF_BASELINE)
        {
            m_bHasBFrames = false;
            
            if(H264_CABAC == m_iEntropy)
            {
                m_iEntropy = H264_ENTROPY_NOT_SET;
            }
        }
        
        m_iDtsDelta = m_bHasBFrames ? -1 : 0;
        
        //取得profile level
        ret = GetProfileLevel(m_bHasBFrames, m_iProfile, m_iLevel, &profileLevel);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "GetProfileLevel failed!");
            
            assert(false);
            break;
        }
        
        //取得pixel buffer info
        ret = GetCVPixelBufferInfo(aPushStreamParam.iVideoPushStreamWidth,
                                   aPushStreamParam.iVideoPushStreamHeight,
                                   &pixelBufferInfo);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CreateCVPixelBufferInfo failed!");
            
            assert(false);
            break;
        }
        
        //清空编码缓存队列
        ClearEncodeBuffQueue();
        
        //获得附加数据
        ret = GetExtraData(aPushStreamParam, codecType, profileLevel, pixelBufferInfo);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "EncExtraData failed!");
            
            assert(false);
            break;
        }
        
        //创建编码器
        ret = CreateEncoder(aPushStreamParam, codecType, profileLevel, pixelBufferInfo, &m_EncodeSession);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CreateEncoder failed!");
            assert(false);
            break;
        }
        
        //是否使用B帧
        CFBooleanRef hasBFrames;
        ret = VTSessionCopyProperty(m_EncodeSession,
                                                kVTCompressionPropertyKey_AllowFrameReordering,
                                                kCFAllocatorDefault,
                                                &hasBFrames);
        if (0 == ret)
        {
            m_bHasBFrames = CFBooleanGetValue(hasBFrames);
            CFRelease(hasBFrames);
        }
        
    }while(0);
    
    
    if (NULL != pixelBufferInfo)
    {
        CFRelease(pixelBufferInfo);
    }
    
    
    m_bOpen = ret == 0 ? true : false;
  
    return m_bOpen;
}

// 关闭
void CVideoEncodeVt::Close()
{
    if(NULL != m_EncodeSession)
    {
        VTCompressionSessionCompleteFrames(m_EncodeSession, kCMTimeIndefinite);
        
        CFRelease(m_EncodeSession);
        m_EncodeSession = NULL;
    }
 
    ClearEncodeBuffQueue();
 
    if(NULL != m_pExtraData)
    {
        av_free(m_pExtraData);
        m_pExtraData = NULL;
        m_iExtraDataSize = 0;
    }
    
    m_bOpen = false;
}

// 编码
bool CVideoEncodeVt::Encode(AVFrame* apFrame, AVPacket* apPacket)
{
    if(NULL == apFrame || NULL == apPacket)
    {
        return false;
    }
    
    //编码
    int ret = EncodeFrame(apFrame, m_EncodeSession);
    if (ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "EncodeFrame failed!");
        //assert(false);
        return false;
    }
    
    if(m_iFirstPts == AV_NOPTS_VALUE)
    {
        m_iFirstPts = apFrame->pts;
    }
    else if(m_iDtsDelta == AV_NOPTS_VALUE && m_bHasBFrames)
    {
        m_iDtsDelta = apFrame->pts - m_iFirstPts;
    }
    
    //取得编码后的数据
    CMSampleBufferRef sampeBuffer;
    ret = PopBuff(&sampeBuffer);
    if( ret < 0)
    {
        return false;
    }
    
    //拷贝为avpacket
    ret = CopySampleBufferToAVPakcet(sampeBuffer, apPacket);
    CFRelease(sampeBuffer);
    sampeBuffer = NULL;
    
    if(ret < 0)
    {
        return false;
    }
    
    return true;
}


int CVideoEncodeVt::GetProfileLevel(bool abHasBFrames, int aiProfile, int aiLevel, CFStringRef* aProfileLevel)
{
    int ret = 0;
    
    if(aiProfile == H264_PROF_AUTO)
    {
        aiProfile = abHasBFrames ? H264_PROF_MAIN : H264_PROF_BASELINE;
    }
    
    switch (aiProfile)
    {
        case H264_PROF_BASELINE:
            switch (aiLevel)
            {
                case  0: *aProfileLevel = kVTProfileLevel_H264_Baseline_AutoLevel; break;
                case 13: *aProfileLevel = kVTProfileLevel_H264_Baseline_1_3;       break;
                case 30: *aProfileLevel = kVTProfileLevel_H264_Baseline_3_0;       break;
                case 31: *aProfileLevel = kVTProfileLevel_H264_Baseline_3_1;       break;
                case 32: *aProfileLevel = kVTProfileLevel_H264_Baseline_3_2;       break;
                case 40: *aProfileLevel = kVTProfileLevel_H264_Baseline_4_0;       break;
                case 41: *aProfileLevel = kVTProfileLevel_H264_Baseline_4_1;       break;
                case 42: *aProfileLevel = kVTProfileLevel_H264_Baseline_4_2;       break;
                case 50: *aProfileLevel = kVTProfileLevel_H264_Baseline_5_0;       break;
                case 51: *aProfileLevel = kVTProfileLevel_H264_Baseline_5_1;       break;
                case 52: *aProfileLevel = kVTProfileLevel_H264_Baseline_5_2;       break;
            }
            break;
            
        case H264_PROF_MAIN:
            switch (aiLevel)
            {
                case  0: *aProfileLevel = kVTProfileLevel_H264_Main_AutoLevel; break;
                case 30: *aProfileLevel = kVTProfileLevel_H264_Main_3_0;       break;
                case 31: *aProfileLevel = kVTProfileLevel_H264_Main_3_1;       break;
                case 32: *aProfileLevel = kVTProfileLevel_H264_Main_3_2;       break;
                case 40: *aProfileLevel = kVTProfileLevel_H264_Main_4_0;       break;
                case 41: *aProfileLevel = kVTProfileLevel_H264_Main_4_1;       break;
                case 42: *aProfileLevel = kVTProfileLevel_H264_Main_4_2;       break;
                case 50: *aProfileLevel = kVTProfileLevel_H264_Main_5_0;       break;
                case 51: *aProfileLevel = kVTProfileLevel_H264_Main_5_1;       break;
                case 52: *aProfileLevel = kVTProfileLevel_H264_Main_5_2;       break;
            }
            break;
            
        case H264_PROF_HIGH:
            switch (aiLevel)
            {
                case  0: *aProfileLevel = kVTProfileLevel_H264_High_AutoLevel; break;
                case 30: *aProfileLevel = kVTProfileLevel_H264_High_3_0;       break;
                case 31: *aProfileLevel = kVTProfileLevel_H264_High_3_1;       break;
                case 32: *aProfileLevel = kVTProfileLevel_H264_High_3_2;       break;
                case 40: *aProfileLevel = kVTProfileLevel_H264_High_4_0;       break;
                case 41: *aProfileLevel = kVTProfileLevel_H264_High_4_1;       break;
                case 42: *aProfileLevel = kVTProfileLevel_H264_High_4_2;       break;
                case 50: *aProfileLevel = kVTProfileLevel_H264_High_5_0;       break;
                case 51: *aProfileLevel = kVTProfileLevel_H264_High_5_1;       break;
                case 52: *aProfileLevel = kVTProfileLevel_H264_High_5_2;       break;
            }
            break;
        default:
            ret = -1;
            break;
    }
  
    return ret;
}


int CVideoEncodeVt::GetCVPixelBufferInfo(int aiWidth, int aiHeight, CFMutableDictionaryRef* apPixelBufferInfo)
{
    CFMutableDictionaryRef pixelBufInfo = NULL;
    CFNumberRef pixFmtNum = NULL;
    CFNumberRef widthNum = NULL;
    CFNumberRef heightNum = NULL;
    
    
    do
    {
        //创建像素缓冲需要的属性字典
        pixelBufInfo = CFDictionaryCreateMutable(kCFAllocatorDefault,
                                                 20,
                                                 &kCFCopyStringDictionaryKeyCallBacks,
                                                 &kCFTypeDictionaryValueCallBacks);
        if(NULL == pixelBufInfo)
        {
            assert(false);
            break;
        }
        
        int cvPixFmt = kCVPixelFormatType_420YpCbCr8Planar;
        pixFmtNum = CFNumberCreate(kCFAllocatorDefault, kCFNumberSInt32Type, &cvPixFmt);
        if (NULL == pixFmtNum)
        {
            assert(false);
            break;
        }
        
        widthNum = CFNumberCreate(kCFAllocatorDefault, kCFNumberSInt32Type, &aiWidth);
        if (NULL == widthNum)
        {
            assert(false);
            break;
        }
        
        heightNum = CFNumberCreate(kCFAllocatorDefault, kCFNumberSInt32Type, &aiHeight);
        if (NULL == heightNum)
        {
            assert(false);
            break;
        }
        
        CFDictionarySetValue(pixelBufInfo, kCVPixelBufferPixelFormatTypeKey, pixFmtNum);
        CFDictionarySetValue(pixelBufInfo, kCVPixelBufferWidthKey, widthNum);
        CFDictionarySetValue(pixelBufInfo, kCVPixelBufferHeightKey, heightNum);
        NumRelease(&pixFmtNum);
        NumRelease(&widthNum);
        NumRelease(&heightNum);
        
        *apPixelBufferInfo = pixelBufInfo;
        
        return 0;
    }while(0);
    
    
    if(pixelBufInfo)
    {
        CFRelease(pixelBufInfo);
    }
    
    NumRelease(&pixFmtNum);
    NumRelease(&widthNum);
    NumRelease(&heightNum);
    
    return -1;
}

int CVideoEncodeVt::GetCVPixelInfo(const AVFrame* apFrame, int* apColor, int* apPlaneCnt,
                                   int* apWidths, int* apHeights, int* apStrides, int* apContiguousBufSize)
{
    int iPixFmt = apFrame->format;
    
    //只支持yuv420p
    if(iPixFmt != AV_PIX_FMT_YUV420P)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "only AV_PIX_FMT_YUV420P permitted	!");
        assert(false);
        return -1;
    }
    
    //计算缓冲需要的参数
    *apPlaneCnt = 3;
    
    apWidths [0] = apFrame->width;
    apHeights[0] = apFrame->height;
    apStrides[0] = apFrame->linesize[0];
    
    apWidths [1] = (apFrame->width  + 1) / 2;
    apHeights[1] = (apFrame->height + 1) / 2;
    apStrides[1] = apFrame->linesize[1];
    
    apWidths [2] = (apFrame->width  + 1) / 2;
    apHeights[2] = (apFrame->height + 1) / 2;
    apStrides[2] = apFrame->linesize[2];
    
    
    //计算一个连续的缓冲大小
    for (int i = 0; i < *apPlaneCnt; i++)
    {
        if (i < *apPlaneCnt - 1 && apFrame->data[i] + apStrides[i] * apHeights[i] != apFrame->data[i + 1])
        {
            *apContiguousBufSize = 0;
            break;
        }
        
        *apContiguousBufSize += apStrides[i] * apHeights[i];
    }
    
    
    return 0;
}

int CVideoEncodeVt::GetParamSize(CMVideoFormatDescriptionRef avideoFmtDesc, int* aiSize)
{
    if(NULL == avideoFmtDesc)
    {
        return -1;
    }
    
    size_t nTotalSize = 0;
    size_t nParamCnt = 0;
    bool bCannotGetCnt = false;
    
    //取得参数集的数量
    int ret = CMVideoFormatDescriptionGetH264ParameterSetAtIndex(avideoFmtDesc, 0, NULL, NULL, &nParamCnt, NULL);
    if(0 != ret)
    {
        bCannotGetCnt = true;
        nParamCnt = 0;
    }
    
    //计算参数集的大小
    for(int i = 0; i < nParamCnt || bCannotGetCnt; i++)
    {
        size_t nParamSize = 0;
        ret = CMVideoFormatDescriptionGetH264ParameterSetAtIndex(avideoFmtDesc, i, NULL, &nParamSize, NULL, NULL);
        if(0 != ret)
        {
            if(i > 0 && bCannotGetCnt)
            {
                ret = 0;
            }
            
            break;
        }
        
        nTotalSize += nParamSize + sizeof(StartCode);
    }
    
    if(0 != ret)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "VTCompressionSessionCreate failed!");
        
        assert(false);
        return -1;
    }
        
    *aiSize = (int)nTotalSize;
    
    return 0;
}

int CVideoEncodeVt::CopyParamSets(CMVideoFormatDescriptionRef avideoFmtDesc, uint8_t* apBuf, int aiBufSize)
{
    if(NULL == avideoFmtDesc || NULL == apBuf || aiBufSize <= 0)
    {
        return -1;
    }
    
    size_t nTotalSize = 0;
    size_t nParamCnt = 0;
    bool bCannotGetCnt = false;
    
    //取得参数集的数量
    int ret = CMVideoFormatDescriptionGetH264ParameterSetAtIndex(avideoFmtDesc, 0, NULL, NULL, &nParamCnt, NULL);
    if(0 != ret)
    {
        bCannotGetCnt = true;
        nParamCnt = 0;
    }
    
    //取得参数集
    for(int i = 0; i < nParamCnt || bCannotGetCnt; i++)
    {
        const uint8_t* pParam = NULL;
        size_t nParamSize = 0;
        ret = CMVideoFormatDescriptionGetH264ParameterSetAtIndex(avideoFmtDesc, i, &pParam, &nParamSize, NULL, NULL);
        if(0 != ret)
        {
            if(i > 0 && bCannotGetCnt)
            {
                ret = 0;
            }
            
            break;
        }

        if (aiBufSize < nTotalSize + sizeof(StartCode) + nParamSize)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "bufsize too small! %d < %d", aiBufSize, nTotalSize + sizeof(StartCode) + nParamSize);
            
            assert(false);
            return -1;
        }
        
        memcpy(apBuf + nTotalSize, StartCode, sizeof(StartCode));
        nTotalSize += sizeof(StartCode);
        
        memcpy(apBuf + nTotalSize, pParam, nParamSize);
        nTotalSize += nParamSize;
    }

    if(0 != ret)
    {
        return -1;
    }
    
    return (int)nTotalSize;
}



int CVideoEncodeVt::CreateEncoder(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam,
                                  CMVideoCodecType aeCodecType,
                                  CFStringRef aProfileLevel,
                                  CFDictionaryRef aPixelBufferInfo,
                                  VTCompressionSessionRef* apSession)
{
    VTCompressionSessionRef session;
    int ret = 0;
    
    
    do
    {
        //创建编码会话
        ret = VTCompressionSessionCreate(kCFAllocatorDefault,
                                         aPushStreamParam.iVideoPushStreamWidth,
                                         aPushStreamParam.iVideoPushStreamHeight,
                                         aeCodecType,
                                         NULL,
                                         aPixelBufferInfo,
                                         kCFAllocatorDefault,
                                         EncodeOutputCallback,
                                         (void*)this,
                                         &session);
        if (0 != ret)
        {
            
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "VTCompressionSessionCreate failed!");
            
            assert(false);
            break;
        }
        
        //realtime
        ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_RealTime, kCFBooleanTrue);
        if (0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_RealTime failed!");
            break;
        }
        
        //profile level
        ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_ProfileLevel, aProfileLevel);
        if (0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_ProfileLevel failed!");
            break;
        }
        
        //bit rate
        int iBitRate = aPushStreamParam.iVideoMaxBitRate;
        CFNumberRef bitRate = CFNumberCreate(kCFAllocatorDefault, kCFNumberSInt32Type, &iBitRate);
        ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_AverageBitRate, bitRate);
        CFRelease(bitRate);
        if (0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_AverageBitRate failed!");
            break;
        }
        
        CFNumberRef keyFrameInterval = CFNumberCreate(kCFAllocatorDefault, kCFNumberSInt32Type, &aPushStreamParam.iVideoFrameRate);
        ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_MaxKeyFrameInterval, keyFrameInterval);
        CFRelease(keyFrameInterval);
        if (0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_MaxKeyFrameInterval failed!");
            break;
        }
        
        //fame rate
        CFNumberRef frameRate = CFNumberCreate(kCFAllocatorDefault, kCFNumberSInt32Type, &aPushStreamParam.iVideoFrameRate);
        ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_ExpectedFrameRate, frameRate);
        CFRelease(frameRate);
        if (0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_ExpectedFrameRate failed!");
            break;
        }
        
        //是否使用b帧
        if (!m_bHasBFrames)
        {
            ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_AllowFrameReordering, kCFBooleanFalse);
            if (0 != ret)
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_AllowFrameReordering failed!");
                break;
            }
        }
        
        //entropy
        if (m_iEntropy != H264_ENTROPY_NOT_SET)
        {
            CFStringRef entropy = m_iEntropy == H264_CABAC ? kVTH264EntropyMode_CABAC : kVTH264EntropyMode_CAVLC;
            
            ret = VTSessionSetProperty(session, kVTCompressionPropertyKey_H264EntropyMode, entropy);
            if (0 != ret)
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "kVTCompressionPropertyKey_H264EntropyMode failed!");
                break;
            }
        }
        
        //准备编码
        ret = VTCompressionSessionPrepareToEncodeFrames(session);
        if (0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "prepare to encode frames failed!");
            assert(false);
            break;
        }
        
        *apSession = session;

        return 0;
    }while(0);
    
    
    if(session)
    {
        CFRelease(session);
        session = NULL;
    }
    
    return -1;
}


int CVideoEncodeVt::GetExtraData(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam, CMVideoCodecType aeCodecType,
                                 CFStringRef aProfileLevel, CFDictionaryRef aPixelBufferInfo)
{
    int ret = 0;
    AVFrame* pFrame = NULL;
    VTCompressionSessionRef session = NULL;
    CMSampleBufferRef sampleBuf = NULL;
    
    do
    {
        //创建编码器
        ret = CreateEncoder(aPushStreamParam, aeCodecType, aProfileLevel, aPixelBufferInfo, &session);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CreateEncoder failed!");
            assert(false);
            break;
        }
        
        //创建一个avframe，并分配缓存，设置参数
        pFrame = av_frame_alloc();
        if(NULL == pFrame)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "av_frame_alloc failed!");
            assert(false);
            break;
        }
        
        int iWidth = aPushStreamParam.iVideoPushStreamWidth;
        int iHeight = aPushStreamParam.iVideoPushStreamHeight;
        int iYSize = iWidth * iHeight;
        int iUVSize = (iWidth / 2) * (iHeight / 2);
        
        pFrame->buf[0] = av_buffer_alloc(iYSize + iUVSize * 2);
        if(NULL == pFrame->buf[0])
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "av_buffer_alloc failed!");
            assert(false);
            break;
        }
        pFrame->data[0] = pFrame->buf[0]->data;
        pFrame->data[1] = pFrame->buf[0]->data + iYSize;
        pFrame->data[2] = pFrame->buf[0]->data + iYSize + iUVSize;
        memset(pFrame->data[0], 0, iYSize);
        memset(pFrame->data[1], 128, iUVSize);
        memset(pFrame->data[2], 128, iUVSize);
        pFrame->linesize[0] = iWidth;
        pFrame->linesize[1] = (iWidth + 1) / 2;
        pFrame->linesize[2] = (iWidth + 1) / 2;
        pFrame->format = AV_PIX_FMT_YUV420P;
        pFrame->width = iWidth;
        pFrame->height = iHeight;
        pFrame->pts = 0;

        //编码
        ret = EncodeFrame(pFrame, session);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "EncodeFrame failed!");
            assert(false);
            break;
        }
        
        //等待编码结束
        ret = VTCompressionSessionCompleteFrames(session, kCMTimeIndefinite);
        if(0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "VTCompressionSessionCompleteFrames failed!");
            break;
        }
        
        //取得编码后的数据
        ret = PopBuff(&sampleBuf);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "PopBuff failed!");
            assert(false);
            break;
        }
        
    }while(0);
    
    if(NULL != sampleBuf)
    {
        CFRelease(sampleBuf);
        sampleBuf = NULL;
    }

    if(NULL != pFrame)
    {
        av_frame_unref(pFrame);
        av_frame_free(&pFrame);
    }
    
    
    if(NULL != session)
    {
        CFRelease(session);
        session = NULL;
    }
    
    assert(0 == ret && m_pExtraData && m_iExtraDataSize > 0);
    
    return ret;
}


int CVideoEncodeVt::CreateCVPixelBuffer(const AVFrame* apFrame, CVPixelBufferRef* apPixelBuffer,
                                        VTCompressionSessionRef aSession)
{
    CVPixelBufferPoolRef pixBufPoll;
    
    int iPlaneCnt = 0;
    int iColor = 0;
    int iWidths [AV_NUM_DATA_POINTERS] = {0};
    int iHeights[AV_NUM_DATA_POINTERS] = {0};
    int iStrides[AV_NUM_DATA_POINTERS] = {0};
    int iContiguousBufSize = 0;
    
    //取得像素信息
    int ret = GetCVPixelInfo(apFrame, &iColor, &iPlaneCnt, iWidths, iHeights, iStrides, &iContiguousBufSize);
    if (ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "GetCVPixelInfo failed!");
        return -1;
    }
    
    //取得像素缓冲池
    pixBufPoll = VTCompressionSessionGetPixelBufferPool(aSession);
    if (NULL == pixBufPoll)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "VTCompressionSessionGetPixelBufferPool failed!");
        return -1;
    }
    
    //分配一个像素缓冲
    ret = CVPixelBufferPoolCreatePixelBuffer(NULL, pixBufPoll, apPixelBuffer);
    if (0 != ret)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVPixelBufferPoolCreatePixelBuffer failed!");
        return -1;
    }

    //将图像拷贝到像素缓冲
    ret = CopyFrameToPixelBuffer(apFrame, *apPixelBuffer, iStrides, iHeights);
    if(ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CopyFrameToPixelBuffer failed!");
        
        CFRelease(*apPixelBuffer);
        apPixelBuffer = NULL;
        return -1;
    }
    
    return 0;
}

int CVideoEncodeVt::CopyFrameToPixelBuffer(const AVFrame* pFrame, CVPixelBufferRef aPixelBuffer,
                                           const int* apStrides, const int* apRows)
{
    if(NULL == aPixelBuffer)
    {
        return -1;
    }
    
    
    int iPlaneCnt = 0;

    int ret = CVPixelBufferLockBaseAddress(aPixelBuffer, 0);
    if(0 != ret)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CVPixelBufferPoolCreatePixelBuffer failed!");
        return -1;
    }
    
    
    if(CVPixelBufferIsPlanar(aPixelBuffer))
    {
        iPlaneCnt = (int)CVPixelBufferGetPlaneCount(aPixelBuffer);
        
        for(int i = 0; pFrame->data[i]; i++)
        {
            if(i == iPlaneCnt)
            {
                CVPixelBufferUnlockBaseAddress(aPixelBuffer, 0);
                
                return -1;
            }
            
            uint8_t* pSrc = pFrame->data[i];
            uint8_t* pDst = (uint8_t*)CVPixelBufferGetBaseAddressOfPlane(aPixelBuffer, i);
            int iSrcStride = apStrides[i];
            int iDstStride = (int)CVPixelBufferGetBytesPerRowOfPlane(aPixelBuffer, i);
            
            if(iSrcStride == iDstStride)
            {
                memcpy(pDst, pSrc, iSrcStride * apRows[i]);
            }
            else
            {
                int iCopyBytes = iDstStride < iSrcStride ? iDstStride : iSrcStride;
                for(int j = 0; j < apRows[i]; j++)
                {
                    memcpy(pDst + j * iDstStride, pSrc + j * iSrcStride, iCopyBytes);
                }
            }
        }
    }
    else
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "aPixelBuffer muse be yuv420p!");
        
        CVPixelBufferUnlockBaseAddress(aPixelBuffer, 0);
        return -1;
    }
    
    CVPixelBufferUnlockBaseAddress(aPixelBuffer, 0);
    
    
    return 0;
}


int CVideoEncodeVt::SetExtraData(CMSampleBufferRef sampleBuffer)
{
    CMVideoFormatDescriptionRef videoFmtDesc;
    
    //取得sample buffer中的格式描述信息
    videoFmtDesc = CMSampleBufferGetFormatDescription(sampleBuffer);
    if(NULL == videoFmtDesc)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMSampleBufferGetFormatDescription failed!");
        
        assert(false);
        return -1;
    }
    
    //取得参数集
    int iParamsSize = 0;
    int ret = GetParamSize(videoFmtDesc, &iParamsSize);
    if(ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "GetParamSize failed!");
        
        assert(false);
        return -1;
    }
    
    //分配缓冲用于保存参数集
    m_pExtraData = (uint8_t*)av_mallocz(iParamsSize);
    if(NULL == m_pExtraData)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "m_pExtraData alloc failed!");
        
        assert(false);
        return -1;
    }
    m_iExtraDataSize = iParamsSize;
    
    //拷贝参数集
    ret = CopyParamSets(videoFmtDesc, m_pExtraData, m_iExtraDataSize);
    if(ret <= 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CopyParamSets failed!");
        
        assert(false);
        return -1;
    }
    
    return 0;
}

int CVideoEncodeVt::EncodeFrame(const AVFrame* apFrame, VTCompressionSessionRef aSession)
{
    int ret = 0;
    CVPixelBufferRef pixelBuffer = NULL;
    CFDictionaryRef frameDict = NULL;
    
    do
    {
        //创建pixel buffer
        ret = CreateCVPixelBuffer(apFrame, &pixelBuffer, aSession);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CreateCVPixelBuffer failed!");
            //assert(false);
            break;
        }
        
        //判断是否关键帧
        if (apFrame->pict_type == AV_PICTURE_TYPE_I)
        {
            const void *keys[] = { kVTEncodeFrameOptionKey_ForceKeyFrame };
            const void *vals[] = { kCFBooleanTrue };
            
            frameDict = CFDictionaryCreate(NULL, keys, vals, 1, NULL, NULL);
            if(NULL == frameDict)
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CFDictionaryCreate failed!");
                assert(false);
                break;
            }
        }
        
        //时间戳
        CMTime time = CMTimeMake(apFrame->pts, m_iFrameRate);
        
        //编码
        ret = VTCompressionSessionEncodeFrame(aSession, pixelBuffer, time, kCMTimeInvalid, frameDict, NULL, NULL);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "VTCompressionSessionEncodeFrame failed!");
            assert(false);
            break;
        }
    }while(0);
    
    
    if(NULL != pixelBuffer)
    {
        CFRelease(pixelBuffer);
        pixelBuffer = NULL;
    }
    
    if(NULL != frameDict)
    {
        CFRelease(frameDict);
        frameDict = NULL;
    }
    
    
    return ret;
}

int CVideoEncodeVt::CopyExtradataToAVPacket(AVPacket* apPacket)
{
    if(NULL == m_pExtraData || m_iExtraDataSize <= 0)
    {
        return -1;
    }
    
    av_free_packet(apPacket);
    int ret = av_new_packet(apPacket, m_iExtraDataSize);
    if(ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "av_new_packet failed!");
        assert(false);
        return -1;
    }
    
    memcpy(apPacket->data, m_pExtraData, m_iExtraDataSize);
    apPacket->pts = 0;
    apPacket->dts = 0;
    apPacket->size = m_iExtraDataSize;
    
    return 0;
}


int CVideoEncodeVt::CopySampleBufferToAVPakcet(CMSampleBufferRef sampleBuffer, AVPacket* apPacket)
{
    //取得头大小
    int iLengthCodeSize = 0;
    int ret = GetLengthCodeSize(sampleBuffer, &iLengthCodeSize);
    if(ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "GetLengthCodeSize failed!");
        assert(false);
        return -1;
    }
    
    int iHeaderSize = 0;
    bool bIsKeyFrame = IsKeyFrame(sampleBuffer);
    bool bAddHeader = bIsKeyFrame;
    CMVideoFormatDescriptionRef videoFmtDesc = NULL;
    
    //需要添加头
    if (bAddHeader)
    {
        videoFmtDesc = CMSampleBufferGetFormatDescription(sampleBuffer);
        if (NULL == videoFmtDesc)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMSampleBufferGetFormatDescription failed!");
            assert(false);
            return -1;
        }
      
        //取得头大小
        ret = GetParamSize(videoFmtDesc, &iHeaderSize);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "GetParamSize failed!");
            assert(false);
            return -1;
        }
    }
    
    //nalu的数量
    int iNaluCnt = 0;
    ret = GetNaluCnt(sampleBuffer, iLengthCodeSize, &iNaluCnt);
    if(ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "GetNaluCnt failed!");
        assert(false);
        return -1;
    }
    int iSampleSize = (int)CMSampleBufferGetTotalSampleSize(sampleBuffer);
    int iPacketSize = iHeaderSize + iSampleSize + iNaluCnt * ((int)sizeof(StartCode) - (int)iLengthCodeSize);
    
    
    av_free_packet(apPacket);
    ret = av_new_packet(apPacket, iPacketSize);
    if(ret < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "av_new_packet failed!");
        assert(false);
        return -1;
    }
    
    do
    {
        //添加头
        if(bAddHeader)
        {
            //参数集拷贝到头
            ret = CopyParamSets(videoFmtDesc, apPacket->data, iPacketSize);
            if(ret < 0)
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CopyParamSets failed!");
                assert(false);
                break;
            }
        }
        
        //拷贝每个nalu
        ret = CopyNalus(sampleBuffer, iLengthCodeSize, apPacket->data + iHeaderSize, apPacket->size - iHeaderSize);
        if(ret < 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CopyNalus failed!");
            assert(false);
            break;
        }
        
        if (bIsKeyFrame)
        {
            apPacket->flags |= AV_PKT_FLAG_KEY;
        }
        
        //设置时间戳
        CMTime pts = CMSampleBufferGetPresentationTimeStamp(sampleBuffer);
        CMTime dts = CMSampleBufferGetDecodeTimeStamp(sampleBuffer);
        
        if (CMTIME_IS_INVALID(dts))
        {
            if (!m_bHasBFrames)
            {
                dts = pts;
            }
            else
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "dts is invalid!");
                assert(false);
                break;
            }
        }
        
        int64_t iDtsDelta = m_iDtsDelta >= 0 ? m_iDtsDelta : 0;
        
        apPacket->pts = pts.value / m_iFrameRate;
        apPacket->dts = dts.value / m_iFrameRate - iDtsDelta;
        apPacket->size = iPacketSize;
        
        return 0;
        
    }while(0);
    
    
    av_free_packet(apPacket);
    return -1;
}

bool CVideoEncodeVt::IsKeyFrame(CMSampleBufferRef sampleBuffer)
{
    CFArrayRef      attachments;
    CFDictionaryRef attachment;
    CFBooleanRef    notSync;
    CFIndex         len;
    
    attachments = CMSampleBufferGetSampleAttachmentsArray(sampleBuffer, false);
    len = !attachments ? 0 : CFArrayGetCount(attachments);
    
    if (!len)
    {
        return true;
    }
    
    attachment = (CFDictionaryRef)CFArrayGetValueAtIndex(attachments, 0);
    if (CFDictionaryGetValueIfPresent(attachment, kCMSampleAttachmentKey_NotSync, (const void **)&notSync))
    {
        bool ret = !CFBooleanGetValue(notSync);
        return ret;
    }
    
    return true;
}

int CVideoEncodeVt::GetLengthCodeSize(CMSampleBufferRef sampleBuffer, int* aiSize)
{
    if(NULL == sampleBuffer)
    {
        return -1;
    }
    
    int iSize = 0;
    CMVideoFormatDescriptionRef videoFmtDesc = NULL;
    
    videoFmtDesc = CMSampleBufferGetFormatDescription(sampleBuffer);
    if (NULL == videoFmtDesc)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMSampleBufferGetFormatDescription failed!");
        return -1;
    }
    
    //取得nlau头长度
    int ret = CMVideoFormatDescriptionGetH264ParameterSetAtIndex(videoFmtDesc, 0, NULL, NULL, NULL, &iSize);
    if (0 != ret)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMVideoFormatDescriptionGetH264ParameterSetAtIndex failed!");
        assert(false);
        return -1;
    }
    
    *aiSize = iSize;
    
    return 0;
}

int CVideoEncodeVt::GetNaluCnt(CMSampleBufferRef sampleBuffer, int aiLengthCodeSize, int* aiNaluCnt)
{
    if(NULL == sampleBuffer || NULL == aiNaluCnt || aiLengthCodeSize > 4)
    {
        assert(false);
        return -1;
    }
    
    size_t nSampleSize = CMSampleBufferGetTotalSampleSize(sampleBuffer);
    if((int)nSampleSize < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMSampleBufferGetTotalSampleSize failed!");
        assert(false);
        return -1;
    }
    
    
    CMBlockBufferRef blockBuffer = CMSampleBufferGetDataBuffer(sampleBuffer);
    if(NULL == blockBuffer)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMSampleBufferGetDataBuffer failed!");
        assert(false);
        return -1;
    }
    
    int iNaluCnt = 0;
    size_t nOffset = 0;
    uint8_t pBuf[4] = {0};
    
    //遍历整个数据，取得每个nalu的大小
    while(nOffset < nSampleSize)
    {
        size_t nLength = 0;
        int ret = CMBlockBufferCopyDataBytes(blockBuffer, nOffset, aiLengthCodeSize, pBuf);
        if(0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMBlockBufferCopyDataBytes failed!");
            assert(false);
            return -1;
        }

        
        for(int i = 0; i < aiLengthCodeSize; i++)
        {
            nLength <<= 8;
            nLength |= pBuf[i];
        }
        
        nOffset += (nLength + aiLengthCodeSize);
        
        iNaluCnt++;
    }
    
    *aiNaluCnt = iNaluCnt;
    
    return 0;
}

int CVideoEncodeVt::CopyNalus(CMSampleBufferRef sampleBuffer, int aiLengthCodeSize, uint8_t* apData, int aiDataSize)
{
    if(NULL == sampleBuffer || aiLengthCodeSize <= 0 || aiLengthCodeSize > 4 || NULL == apData || aiDataSize < 0)
    {
        return -1;
    }
    
    CMBlockBufferRef blockBuffer = CMSampleBufferGetDataBuffer(sampleBuffer);
    if(NULL == blockBuffer)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMSampleBufferGetDataBuffer failed!");
        assert(false);
        return -1;
    }
    
    int ret = 0;
    size_t nSrcOffset = 0;
    size_t nSrcSize = CMSampleBufferGetTotalSampleSize(sampleBuffer);
    size_t nSrcRemainSize = nSrcSize;
    
    
    
    size_t nDstRemainSize = aiDataSize;
    uint8_t pSizeBuf[4] = {0};
    uint8_t* pDst = apData;
    
    while (nSrcRemainSize > 0)
    {
        ret = CMBlockBufferCopyDataBytes(blockBuffer, nSrcOffset, aiLengthCodeSize, pSizeBuf);
        if(0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMBlockBufferCopyDataBytes failed!");
            assert(false);
            return -1;
        }
        
        
        size_t nLength = 0;
        for(int i = 0; i < aiLengthCodeSize; i++)
        {
            nLength <<= 8;
            nLength |= pSizeBuf[i];
        }
        
        size_t nCurrSrcLen = nLength + aiLengthCodeSize;
        size_t nCurrDstLen = nLength + sizeof(StartCode);
        
        if(nCurrSrcLen > nSrcRemainSize)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "src data bad!");
            assert(false);
            return -1;
        }
        
        if(nCurrDstLen > nDstRemainSize)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "dst data too small!");
            assert(false);
            return -1;
        }

        memcpy(pDst, StartCode, sizeof(StartCode));
        ret = CMBlockBufferCopyDataBytes(blockBuffer, nSrcOffset + aiLengthCodeSize, nLength, pDst + sizeof(StartCode));
        if(0 != ret)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CMBlockBufferCopyDataBytes failed!");
            assert(false);
            return -1;
        }
        
        nSrcOffset += nCurrSrcLen;
        pDst += nCurrDstLen;
        
        nSrcRemainSize -= nCurrSrcLen;
        nDstRemainSize -= nCurrDstLen;
    }
    
    return 0;
}




void CVideoEncodeVt::EncodeOutputCallback(void * CM_NULLABLE outputCallbackRefCon,
                                          void * CM_NULLABLE sourceFrameRefCon,
                                          OSStatus status,
                                          VTEncodeInfoFlags infoFlags,
                                          CM_NULLABLE CMSampleBufferRef sampleBuffer )
{
    CVideoEncodeVt* pThis = (CVideoEncodeVt*)outputCallbackRefCon;
    
    //编码错误	
    if(pThis->m_bCompressErr)
    {
        if(NULL != sampleBuffer)
        {
            CFRelease(sampleBuffer);
        }
        
        return;
    }
    
    if(status || NULL == sampleBuffer)
    {
        pThis->m_bCompressErr = true;
        return;
    }
    
    //保存参数集
    if(NULL == pThis->m_pExtraData)
    {
        int ret = pThis->SetExtraData(sampleBuffer);
        if(ret < 0)
        {
            pThis->m_bCompressErr = true;
            return;
        }
    }
    
    //保存到队列
    pThis->PushBuff(sampleBuffer);
}


int CVideoEncodeVt::PopBuff(CMSampleBufferRef* apBuf)
{
    if(NULL == apBuf)
    {
        return -1;
    }
    

    
    EncBuffNode* pNode = NULL;
    
    std::lock_guard<std::mutex> AutoLock(m_EncBuffQueue.lock);
   
    if(NULL == m_EncBuffQueue.head)
    {
        return -1;
    }
    
    pNode = m_EncBuffQueue.head;
    m_EncBuffQueue.head = m_EncBuffQueue.head->next;
    if(NULL == m_EncBuffQueue.head)
    {
        m_EncBuffQueue.tail = NULL;
    }
    m_EncBuffQueue.cnt--;
    
    
    *apBuf = pNode->buf;
    av_free(pNode);
    
    
    return 0;
}

void CVideoEncodeVt::PushBuff(CMSampleBufferRef aBuf)
{
    EncBuffNode* pNode = (EncBuffNode*)av_malloc(sizeof(EncBuffNode));
    if(NULL == pNode)
    {
        return;
    }
    
    CFRetain(aBuf);
    pNode->buf = aBuf;
    pNode->next = NULL;
    
    std::lock_guard<std::mutex> AutoLock(m_EncBuffQueue.lock);
 
    if(NULL == m_EncBuffQueue.head)
    {
        m_EncBuffQueue.head = pNode;
    }
    else
    {
        m_EncBuffQueue.tail->next = pNode;
    }
    
    m_EncBuffQueue.tail = pNode;
    m_EncBuffQueue.cnt++;
}

void CVideoEncodeVt::ClearEncodeBuffQueue()
{
    std::lock_guard<std::mutex> AutoLock(m_EncBuffQueue.lock);
    
    EncBuffNode* pNode = m_EncBuffQueue.head;
    while(pNode)
    {
        EncBuffNode* pNext = pNode->next;
        CFRelease(pNode->buf);
        av_free(pNode);
        pNode = pNext;
    }
    
    
    m_EncBuffQueue.head = m_EncBuffQueue.tail = NULL;
    m_EncBuffQueue.cnt = 0;
}



