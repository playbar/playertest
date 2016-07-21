#include <assert.h>
#include <errno.h>
#include <thread>
#include "PushStreamEngine.h"
#include "Log.h"

extern "C"
{
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libavutil/time.h"
#include "libavutil/channel_layout.h"
}

////////////////////////////////////////////////////////////////////////////////////////////////////
CPushStreamEngine::CPushStreamEngine()
{
    m_bInit = false;
    m_bPausePushStream = false;
    m_bMute = false;
    memset(m_szSilenceData, 0, 4096);
    m_iAudioPTS = -1;
    m_iVideoStreamID = -1;
    m_iAudioStreamID = -1;
    m_bVideoEncodeThreadExit = false;
    m_VideoEncodeThreadID = 0;
    m_bAudioEncodeThreadExit = false;
    m_AudioEncodeThreadID = 0;
    m_bSendThreadExit = false;
    m_SendThreadID = 0;
}

CPushStreamEngine::~CPushStreamEngine()
{
    assert(m_VideoEncodeThreadID == 0);
    assert(m_AudioEncodeThreadID == 0);
    assert(m_SendThreadID == 0);
}

// 打开推流
bool CPushStreamEngine::Open()
{
    av_register_all();
    if (avformat_network_init() < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open avformat_network_init Failed!");
        assert(false);
        return false;
    }
    
    // 打开发送处理
    if (!m_SendDeal.Open(m_PushStreamParam))
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open SendDeal Open Failed!");
        assert(false);
        return false;
    }
    
    // 打开视频编码
    if (!m_VideoEncode.Open(m_PushStreamParam))
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open VideoEncode Open Failed!");
        assert(false);
        return false;
    }
    
    // 打开音频编码
    if (!m_AudioEncode.Open(m_PushStreamParam))
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open AudioEncode Open Failed!");
        assert(false);
        return false;
    }
    
    // 保存视频流ID和音频流ID
    m_iVideoStreamID = m_SendDeal.GetVideoStreamID();
    m_iAudioStreamID = m_SendDeal.GetAudioStreamID();
    
    m_SendDeal.SetCodecContext(m_AudioEncode.GetCodecCtx());
    
    // 启动视频编码线程
    int iResult = -1;
    iResult = pthread_create(&m_VideoEncodeThreadID, nullptr, VideoEncodeThread, (void*)this);
    if (iResult != 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open VideoEncodeThread Failed! error_number:%d, error:%s", errno, strerror(errno));
        assert(false);
        return false;
    }
    
    // 启动音频编码线程
    iResult = pthread_create(&m_AudioEncodeThreadID, nullptr, AudioEncodeThread, (void*)this);
    if (iResult != 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open AudioEncodeThread Failed! error_number:%d, error:%s", errno, strerror(errno));
        assert(false);
        return false;
    }
    
    // 启动发送线程
    iResult = pthread_create(&m_SendThreadID, nullptr, SendThread, (void*)this);
    if (iResult != 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Open SendThread Failed! error_number:%d, error:%s", errno, strerror(errno));
        assert(false);
        return false;
    }
    
    m_bInit = true;
    
    return true;
}

// 关闭推流
void CPushStreamEngine::Close()
{
    // 关闭线程
    m_bVideoEncodeThreadExit = true;
    m_bAudioEncodeThreadExit = true;
    m_bSendThreadExit = true;
    
    // 等待发送线程
    int iResult = -1;
    if (m_SendThreadID != 0)
    {
        iResult = pthread_join(m_SendThreadID, nullptr);
        m_SendThreadID = 0;
        if (iResult != 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Close pthread_join SendThread Failed! error_number:%d, error:%s", errno, strerror(errno));
        }
    }
    
    // 等待视频编码线程
    if (m_VideoEncodeThreadID != 0)
    {
        iResult = pthread_join(m_VideoEncodeThreadID, nullptr);
        m_VideoEncodeThreadID = 0;
        if (iResult != 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Close pthread_join VideoEncodeThread Failed! error_number:%d, error:%s", errno, strerror(errno));
        }
    }
    
    // 等待音频编码线程
    if (m_AudioEncodeThreadID != 0)
    {
        iResult = pthread_join(m_AudioEncodeThreadID, nullptr);
        m_AudioEncodeThreadID = 0;
        if (iResult != 0)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::Close pthread_join AudioEncodeThread Failed! error_number:%d, error:%s", errno, strerror(errno));
        }
    }
    
    // 关闭发送处理
    m_SendDeal.Close();
    
    // 关闭视频编码
    m_VideoEncode.Close();
    
    // 关闭音频编码
    m_AudioEncode.Close();
    
    // 关闭视频转换
    m_VideoFormatConvert.Close();
    
    // 清除资源
    m_AVPacketQueue.Clear();
    m_CaptureVideoFrameCache.Close();
    m_AudioAlignment.Close();
    
    m_PushStreamParam.Clear();
    m_bInit = false;
    m_bPausePushStream = false;
    m_bMute = false;
    m_iAudioPTS = -1;
    m_iVideoStreamID = -1;
    m_iAudioStreamID = -1;
    m_bVideoEncodeThreadExit = false;
    m_bAudioEncodeThreadExit = false;
    m_bSendThreadExit = false;
    
    avformat_network_deinit();
}

// 设置日志目录
bool CPushStreamEngine::SetLogDirectories(const char* apLogDirectories, int aiLogLevel)
{
    if (!InitLog(apLogDirectories, aiLogLevel))
    {
        assert(false);
        return false;
    }
    
    // 开始打印日值
    CLog::GetInstance().Log(enum_Log_Level5, "**************************************************!");
    CLog::GetInstance().Log(enum_Log_Level5, "Log Start!");
    CLog::GetInstance().Log(enum_Log_Level5, "**************************************************!");
    
    return true;
}

// 设置推流URL
void CPushStreamEngine::SetPushStreamURL(const char* apURL)
{
    if (apURL == nullptr)
    {
        assert(false);
        return;
    }
    
    memcpy(m_PushStreamParam.szPushStreamURL, apURL, strlen(apURL));
}

// 设置视频推流分辨率
void CPushStreamEngine::SetVideoPushStreamResolution(int aiVideoPushStreamWidth, int aiVideoPushStreamHeight)
{
    m_PushStreamParam.iVideoPushStreamWidth = aiVideoPushStreamWidth;
    m_PushStreamParam.iVideoPushStreamHeight = aiVideoPushStreamHeight;
}

// 设置视频帧率
void CPushStreamEngine::SetVideoFrameRate(int aiVideoFrameRate)
{
    m_PushStreamParam.iVideoFrameRate = aiVideoFrameRate;
}

// 设置视频I帧的帧间距
void CPushStreamEngine::SetVideoFrameSpacing(int aiVideoFrameSpacing)
{
    m_PushStreamParam.iVideoFrameSpacing = aiVideoFrameSpacing;
}

// 设置视频推流码率
void CPushStreamEngine::SetVideoBitRate(int aiVideoBitRate, int aiVideoMinBitRate, int aiVideoMaxBitRate)
{
    m_PushStreamParam.iVideoBitRate = aiVideoBitRate;
    m_PushStreamParam.iVideoMinBitRate = aiVideoMinBitRate;
    m_PushStreamParam.iVideoMaxBitRate = aiVideoMaxBitRate;
}

// 设置视频质量(数值为0~50)
void CPushStreamEngine::SetVideoQuality(int aiVideoQuality)
{
    if (aiVideoQuality >= 0 && aiVideoQuality <= 50)
    {
        m_PushStreamParam.iVideoQuality = aiVideoQuality;
    }
}

// 设置视频编码预设值(数值为0~8)
void CPushStreamEngine::SetVideoEncoderPreset(int aiVideoEncoderPreset)
{
    if (aiVideoEncoderPreset >= 0 && aiVideoEncoderPreset <= 8)
    {
        m_PushStreamParam.iVideoEncoderPreset = aiVideoEncoderPreset;
    }
}

// 设置音频采样率
void CPushStreamEngine::SetAudioSampleRate(int aiAudioSampleRate)
{
    m_PushStreamParam.iAudioSampleRate = aiAudioSampleRate;
}

// 设置音频通道数(数值为1或者2)
void CPushStreamEngine::SetAudioChannels(int aiAudioChannels)
{
    m_PushStreamParam.iAudioChannels = aiAudioChannels;
}

// 设置音频码率
void CPushStreamEngine::SetAudioBitRate(int aiAudioBitRate)
{
    m_PushStreamParam.iAudioBitRate = aiAudioBitRate;
}

// 设置重新连接服务的时间间隔(单位:毫秒)
void CPushStreamEngine::SetReconnectTime(int aiReconnectTime)
{
    m_PushStreamParam.iReconnectTime = aiReconnectTime;
}

// 设置是否暂停
void CPushStreamEngine::SetPause(bool abPause)
{
    m_bPausePushStream = abPause;
}

// 设置是否静音
void CPushStreamEngine::SetMute(bool abMute)
{
    m_bMute = abMute;
}

// 视频采集数据
bool CPushStreamEngine::VideoCaptureData(int aiFrameWidth, int aiFrameHeight, int aiVideoFmt, const char* apData, int aiDataLen)
{
    if (apData == nullptr || aiDataLen <= 0)
    {
        CLog::GetInstance().Log(enum_Log_Level5, "CPushStreamEngine::VideoCaptureData apData == nullptr || aiDataLen <= 0!");
        assert(false);
        return false;
    }
    
    if (!m_bInit)
    {
        return false;
    }
    
    if (!m_CaptureVideoFrameCache.Open(aiVideoFmt, aiFrameWidth, aiFrameHeight))
    {
        CLog::GetInstance().Log(enum_Log_Level5, "CPushStreamEngine::VideoCaptureData VideoFrameCache Open Failed!");
        assert(false);
        return false;
    }
    
    // 如果暂停
    if (m_bPausePushStream)
    {
        return true;
    }
    
    return m_CaptureVideoFrameCache.SetFrame(apData, aiDataLen);
}

// 音频采集数据
bool CPushStreamEngine::AudioCaptureData(const char* apData, int aiDataLen)
{
    if (apData == nullptr || aiDataLen <= 0)
    {
        CLog::GetInstance().Log(enum_Log_Level5, "CPushStreamEngine::AudioCaptureData apData == nullptr || aiDataLen <= 0!");
        assert(false);
        return false;
    }
    
    if (!m_bInit)
    {
        return false;
    }
    
    if (!m_AudioAlignment.Open(m_PushStreamParam))
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::AudioCaptureData AudioAligment Open Failed!");
        assert(false);
        return false;
    }
    
    int iNbSamples = aiDataLen / (av_get_bytes_per_sample(AV_SAMPLE_FMT_S16) * m_PushStreamParam.iAudioChannels);
    uint8_t* pData = (uint8_t*)apData;
    
    if(m_bMute)
    {
        pData = m_szSilenceData;
    }
    
    if (!m_AudioAlignment.SetData(pData, iNbSamples))
    {
        CLog::GetInstance().Log(enum_Log_Level5, "CPushStreamEngine::AudioCaptureData SetData Failed!");
        assert(false);
        return false;
    }
    
    return true;
}

// 音频背景数据
bool CPushStreamEngine::AudioBackgroundData(const char* apData, int aiDataLen)
{
    if (!m_bInit)
    {
        return false;
    }
    
    return true;
}

// 视频编码线程
void* CPushStreamEngine::VideoEncodeThread(void* apParam)
{
    CPushStreamEngine* pPushStreamEngine = (CPushStreamEngine*)apParam;
    if (pPushStreamEngine != nullptr)
    {
        pPushStreamEngine->VideoEncodeFunc();
        return nullptr;
    }
    
    return nullptr;
}

// 视频编码函数
int CPushStreamEngine::VideoEncodeFunc()
{
    int64_t iLastTime = av_gettime();
    
    // 计算每帧时间
    int64_t iDuration = 1000 / m_PushStreamParam.iVideoFrameRate;
    
    // 帧缓存
    AVFrame* pOutputFrame = av_frame_alloc();
    
    // AVFrame中保存数据的buf
    uint8_t* pOutputFrameData = (uint8_t *)av_malloc(avpicture_get_size(AV_PIX_FMT_YUV420P, m_PushStreamParam.iVideoPushStreamWidth, m_PushStreamParam.iVideoPushStreamHeight));
    if (pOutputFrame == nullptr || pOutputFrameData == nullptr)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::VideoEncodeFunc alloc Failed!");
        assert(false);
        return 0;
    }
    
    if (avpicture_fill((AVPicture *)pOutputFrame, pOutputFrameData, AV_PIX_FMT_YUV420P, m_PushStreamParam.iVideoPushStreamWidth, m_PushStreamParam.iVideoPushStreamHeight) < 0)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::VideoEncodeFunc avpicture_fill Failed!");
        assert(false);
        return 0;
    }
    
    bool bKey = false;
    
    int64_t iGetTime = av_gettime();
    
    while (!m_bVideoEncodeThreadExit)
    {
        try
        {
            // 取得原视频帧
            AVFrame* pFrame = m_CaptureVideoFrameCache.GetFrame();
            if (pFrame == nullptr)
            {
                if(m_bPausePushStream)
                    bKey = true;
                
                int64_t iGetTime2 = av_gettime();
                if((iGetTime2 - iGetTime)/1000 > 110)
                    CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level4, "video GetFrame wait =%d", (int)((iGetTime2 - iGetTime)/1000));
                
                this_thread::sleep_for(chrono::milliseconds(3));
                continue;
            }
            iGetTime = av_gettime();
            
            // 获取音频时间戳
            if (m_iAudioPTS == -1)
            {
                this_thread::sleep_for(chrono::milliseconds(3));
                continue;
            }
            
            
            // 检测是否需要转换格式
            if (!m_VideoFormatConvert.Open((AVPixelFormat)pFrame->format, pFrame->width, pFrame->height, AV_PIX_FMT_YUV420P, m_PushStreamParam.iVideoPushStreamWidth, m_PushStreamParam.iVideoPushStreamHeight))
            {
                CLog::GetInstance().Log(enum_Log_Level5, "CPushStreamEngine::VideoEncodeFunc VideoFormatConvert Open Failed!");
                this_thread::sleep_for(chrono::milliseconds(3));
                continue;
            }
            
            AVFrame tmpFrame = *pFrame;
            if(bKey)
            {
                tmpFrame.key_frame = 1;
                tmpFrame.pict_type = AV_PICTURE_TYPE_I;
                bKey = false;
            }
            
            // 转换格式
            if (!m_VideoFormatConvert.Convert(&tmpFrame, pOutputFrame))
            {
                CLog::GetInstance().Log(enum_Log_Level5, "CPushStreamEngine::VideoEncodeFunc VideoFormatConvert Convert Failed!");
                this_thread::sleep_for(chrono::milliseconds(3));
                continue;
            }
            
            // 编码
            AVPacket Packet;
            av_init_packet(&Packet);
            Packet.data = nullptr;	// packet data will be allocated by the encoder
            Packet.size = 0;
            
            if (m_VideoEncode.Encode(pOutputFrame, &Packet))
            {
                Packet.pts = m_iAudioPTS;
                Packet.pos = -1;
                Packet.dts = Packet.pts;
                Packet.stream_index = m_iVideoStreamID;
                
                // 加入发送队列
                m_AVPacketQueue.PutPacket(&Packet);
            }
            else
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::VideoEncodeFunc Encode Failed!");
            }
            
            int64_t iVideoEncodeTime = (av_gettime() - iLastTime)/ 1000;
            
            // 计算时间
            int64_t iUseTime = int64_t((av_gettime() - iLastTime) / 1000);
            if (iUseTime < (iDuration - 1))
            {
                this_thread::sleep_for(chrono::milliseconds(iDuration - 1 - iUseTime));
            }
            
            int64_t iVideoUsedTime = (av_gettime() - iLastTime)/ 1000;
            iLastTime = av_gettime();
            
            if(iVideoEncodeTime >= iDuration)
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level4, "video encode=%d use=%d", (int)iVideoEncodeTime, (int)iVideoUsedTime);
        }
        catch (...)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::VideoEncodeFunc Exception!");
        }
    }
    
    // 释放资源
    av_free(pOutputFrameData);
    pOutputFrameData = nullptr;
    
    av_frame_free(&pOutputFrame);
    pOutputFrame = nullptr;
    
    return 0;
}

// 音频编码线程
void* CPushStreamEngine::AudioEncodeThread(void* apParam)
{
    CPushStreamEngine* pPushStreamEngine = (CPushStreamEngine*)apParam;
    if (pPushStreamEngine != nullptr)
    {
        pPushStreamEngine->AudioEncodeFunc();
        return nullptr;
    }
    
    return nullptr;
}

// 音频编码函数
int CPushStreamEngine::AudioEncodeFunc()
{
    AVCodecContext* pCodecCtx = m_AudioEncode.GetCodecCtx();
    if (pCodecCtx == nullptr)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::AudioEncodeFunc pCodecCtx == nullptr!");
        
        assert(false);
        return 0;
    }
    
    // 申请用于编码的frame
    AVFrame* pFrame = av_frame_alloc();
    if (pFrame == nullptr)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::AudioEncodeFunc pFrame == nullptr!");
        
        assert(false);
        return 0;
    }
    pFrame->nb_samples = pCodecCtx->frame_size;
    
    uint64_t iChannelLayout = AV_CH_LAYOUT_STEREO;
    if (pCodecCtx->channels == 1)
    {
        iChannelLayout = AV_CH_LAYOUT_MONO;
    }
    else if (pCodecCtx->channels == 2)
    {
        iChannelLayout = AV_CH_LAYOUT_STEREO;
    }
    
    AVSampleFormat OutSampleFmt = pCodecCtx->sample_fmt;
    int OutSampleRate = m_PushStreamParam.iAudioSampleRate;
    int OutChannels = av_get_channel_layout_nb_channels(iChannelLayout);
    int OutBufferSize = av_samples_get_buffer_size(nullptr, OutChannels, OutSampleRate, OutSampleFmt, 1);
    
    // 计算每个frame的data大小(aac是4096)
    int iFrameDataSize = av_samples_get_buffer_size(nullptr, pCodecCtx->channels, pCodecCtx->frame_size, OutSampleFmt, 1);
    uint8_t* pFrameData = (uint8_t*)av_malloc(iFrameDataSize);
    if (pFrameData == nullptr)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::AudioEncodeFunc pFrameData == nullptr!");
        assert(false);
        
        // 释放资源
        av_frame_free(&pFrame);
        
        return 0;
    }
    
    // 计算每帧时间
    int iDuration = 1000 * iFrameDataSize / OutBufferSize;
    
    // 帧索引
    int iFrameIndex = 0;
    
    int64_t iLastTime = av_gettime();
    
    while (!m_bAudioEncodeThreadExit)
    {
        try
        {
            bool bHasAudioData = false;
            //取音频数据
            bHasAudioData = m_AudioAlignment.GetData(pFrameData, pFrame->nb_samples);
            if (!bHasAudioData)
            {
                // 当前不是暂停状态
                if (!m_bPausePushStream)
                {
                    this_thread::sleep_for(chrono::milliseconds(3));
                    continue;
                }
                else
                {
                    m_AudioAlignment.Close();
                }
            }
            
            // 当前是暂停状态
            if (m_bPausePushStream)
            {
                // 有未发完的完整数据
                if (bHasAudioData)
                {
                    if (avcodec_fill_audio_frame(pFrame, OutChannels, OutSampleFmt, (const uint8_t*)pFrameData, iFrameDataSize, 1) < 0)
                    {
                        continue;
                    }
                }
                else
                {
                    // 没有未发完的完整数据,发静音数据
                    if (avcodec_fill_audio_frame(pFrame, OutChannels, OutSampleFmt, (const uint8_t*)m_szSilenceData, iFrameDataSize, 1) < 0)
                    {
                        continue;
                    }
                }
            }
            else
            {
                if (avcodec_fill_audio_frame(pFrame, OutChannels, OutSampleFmt, (const uint8_t*)pFrameData, iFrameDataSize, 1) < 0)
                {
                    continue;
                }
            }
            
            // 编码
            AVPacket Packet;
            av_init_packet(&Packet);
            Packet.data = nullptr;	// packet data will be allocated by the encoder
            Packet.size = 0;
            
            if (m_AudioEncode.Encode(pFrame, &Packet))
            {
                Packet.pts = iFrameIndex * iDuration;
                Packet.pos = -1;
                Packet.dts = Packet.pts;
                Packet.duration = iDuration;
                Packet.stream_index = m_iAudioStreamID;
                
                // 加入发送队列
                m_AVPacketQueue.PutPacket(&Packet);
                
                ++iFrameIndex;
                
                m_iAudioPTS = Packet.pts;
            }
            else
            {
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CAudioDeal::EncodeFunc Encode Failed!");
            }
            
            int64_t iAudioEncodeTime = (av_gettime() - iLastTime)/ 1000;
            
            if (m_bPausePushStream && !bHasAudioData)
            {
                // 计算时间
                int64_t iUseTime = int64_t((av_gettime() - iLastTime) / 1000);
                if (iUseTime < (iDuration - 1))
                {
                    this_thread::sleep_for(chrono::milliseconds(iDuration - 1 - iUseTime));
                }
            }
            
            int64_t iAudioUsedTime = (av_gettime() - iLastTime)/ 1000;
            
            iLastTime = av_gettime();
            
            if(iAudioEncodeTime >= iDuration)
                CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level4, "audio encode=%d use=%d", (int)iAudioEncodeTime, (int)iAudioUsedTime);
        }
        catch (...)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::AudioEncodeFunc Exception!");
        }
    }
    
    // 释放资源
    av_frame_free(&pFrame);
    pFrame = nullptr;
    
    av_free(pFrameData);
    pFrameData = nullptr;
    
    return 0;
}

// 发送线程
void* CPushStreamEngine::SendThread(void* apParam)
{
    CPushStreamEngine* pPushStreamEngine = (CPushStreamEngine*)apParam;
    if (pPushStreamEngine != nullptr)
    {
        pPushStreamEngine->SendFunc();
        return nullptr;
    }
    
    return nullptr;
}

// 发送函数
int CPushStreamEngine::SendFunc()
{
    // 是否丢包
    bool bLostPacket = false;
    
    while (!m_bSendThreadExit)
    {
        try
        {
            // 检测当前是否连接
            if (!m_SendDeal.IsConnect())
            {
                // 1秒重连一次
                this_thread::sleep_for(chrono::milliseconds(m_PushStreamParam.iReconnectTime));
                
                m_SendDeal.Reconnect();
                continue;
            }
            
            AVPacket Packet;
            if (!m_AVPacketQueue.GetPacket(&Packet))
            {
                this_thread::sleep_for(chrono::milliseconds(3));
                continue;
            }
            
            int iResult = -1;
            if (Packet.stream_index == m_iVideoStreamID)
            {
                // 丢包策略:如果当前视频发送失败,则后面的包都丢弃,直到遇到下一个I帧
                // 检测当前帧是否为I帧
                bool bKeyFrame = ((Packet.flags & AV_PKT_FLAG_KEY) == AV_PKT_FLAG_KEY) ? true : false;
                if (bKeyFrame)
                {
                    // 当前为需要丢包状态
                    if (bLostPacket)
                    {
                        // 修改状态
                        bLostPacket = false;
                    }
                }
                else
                {
                    // 当前为需要丢包状态
                    if (bLostPacket)
                    {
                        // 释放包
                        av_free_packet(&Packet);
                        continue;
                    }
                }
                
                // 视频包
                iResult = m_SendDeal.VideoOutput(&Packet);
                if (iResult == -1)		// 断开连接
                {
                    CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::SendFunc Video Disconnect!");
                    
                    // 清空队列
                    m_AVPacketQueue.Clear();
                }
                /*
                 else if (iResult == -1)	// 发送失败
                 {
                 CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::SendFunc Video Failed!");
                 
                 // 修改丢包状态
                 bLostPacket = true;
                 }
                 */
            }
            else if (Packet.stream_index == m_iAudioStreamID)
            {
                // 音频包
                iResult = m_SendDeal.AudioOutput(&Packet);
                if (iResult == -1)		// 断开连接
                {
                    CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::SendFunc Audio Disconnect!");
                    
                    // 清空队列
                    m_AVPacketQueue.Clear();
                }
                /*
                 else if (iResult == -1)	// 发送失败
                 {
                 CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::SendFunc Audio Failed!");
                 }
                 */
            }
            
            // 释放包
            av_free_packet(&Packet);
        }
        catch (...)
        {
            CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CPushStreamEngine::SendFunc Exception!");
        }
    }
    
    return 0;
}

// 初始化日志
bool CPushStreamEngine::InitLog(const char* apLogOutputPath, int aiLogLevel)
{
    if (apLogOutputPath == nullptr)
    {
        assert(false);
        return false;
    }
    
    // 取得当前时间
    time_t oTime;
    time(&oTime);
    struct tm* pTime = localtime(&oTime);
    if (pTime == nullptr)
    {
        assert(false);
        return false;
    }
    
    // 生成日志文件名
    char szLogFileName[1024 + 1] = { 0 };
    sprintf(szLogFileName, "%s%s%4d%02d%02d%2d.log", apLogOutputPath, "PushStreamEngine_", (1900 + pTime->tm_year), (1 + pTime->tm_mon), pTime->tm_mday, pTime->tm_min);
    
    CLog::GetInstance().SetLogLevel(aiLogLevel);
    CLog::GetInstance().SetLogFileName(szLogFileName, (unsigned int)strlen(szLogFileName));
    CLog::GetInstance().SetLogOption(enum_Log_Option_Timestamp | enum_Log_Option_PrintToFile);
    
    return true;
}