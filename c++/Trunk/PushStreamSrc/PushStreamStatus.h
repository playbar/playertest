#ifndef PUSH_STREAM_STATUS_H
#define PUSH_STREAM_STATUS_H





class CPushStreamStatus
{
public:
    CPushStreamStatus();
    ~CPushStreamStatus();
    
public:
    
    void SetVideoFrameRate(int iFrameRate);
    
    void AddReconnectCnt(int iCnt = 1);
    
    void AddVideoLostCnt(int iCnt = 1);
    
    void AddVideoSend(int iSize);
    
    void AddAudioSend(int iSize);    
    
    double GetVideoAvgFrameRate();
    
    double GetBitrate();
    
    double GetVideoLostCnt();
    
    int GetReconnectCnt();
    
    int GetNetworkStatus();
    
    int GetVideoCacheMax();

    int GetCalculatePeriod();
    
    int Calculate(int iDuration);
    
    void Reset();
    
    
    
private:
    //视频帧率
    int                                 m_iVideoFrameRate;
    
    //网络状态
    int                                 m_iNetworkStatus;
    
    //视频包最大缓存数量
    int                                 m_iVideoCacheNumMax;
    
    //视频总丢包数
    int                                 m_iVideoTotoalLostCnt;
    
    //视频当前丢包数
    int                                 m_iVideoCurrLostCnt;
    
    //视频平均丢包数
    double                              m_fVideoAvgLostCnt;
    
    //视频平均丢包比
    double                              m_fVideoAvgLostRatio;
    
    //视频当前发送包数
    int                                 m_iVideoCurrSendPktCnt;
    
    //视频当前发送字节数
    int                                 m_iVideoCurrSendSize;
    
    //视频真实的帧率
    double                              m_fVideoRealAvgFrameRate;
    
    //视频真实的码率
    double                              m_fVideoRealAvgBitrate;
    
    //音频当前发送包数
    int                                 m_iAudioCurrSendPktCnt;
    
    //音频当前发送字节数
    int                                 m_iAudioCurrSendSize;
    
    //音频真实的码率
    double                              m_fAudioRealAvgBitrate;
    
    //重连总计数
    int                                 m_iReconnCnt;
        
    //丢包统计周期(s)
    int                                 m_iStaticCalcPeriod;
    
    //网速状态阈值
    double                              m_fNetWorkBadLostRatio;
    double                              m_fNetWorkGoodLostRatio;
    
    int                                 m_iContBadCnt;
    int                                 m_iContGoodCnt;
    
};


#endif