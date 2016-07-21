#include "PushStreamStatus.h"


#include "PushStreamNotify.h"


CPushStreamStatus::CPushStreamStatus()
{
    m_iVideoFrameRate = 0;
    m_iNetworkStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_GOOD;
    m_iVideoTotoalLostCnt = 0;
    m_iVideoCurrLostCnt = 0;
    m_fVideoAvgLostCnt = 0.0;
    m_fVideoAvgLostRatio = 0.0;
    m_iVideoCurrSendPktCnt = 0;
    m_iVideoCurrSendSize = 0;
    m_fVideoRealAvgFrameRate = 0.0;
    m_fVideoRealAvgBitrate = 0.0;
    m_iAudioCurrSendPktCnt = 0;
    m_iAudioCurrSendSize = 0;
    m_fAudioRealAvgBitrate = 0.0;
    m_iReconnCnt = 0;
    m_iStaticCalcPeriod = 0;
    
    m_iVideoCacheNumMax = 0;
    m_fNetWorkBadLostRatio = 7.0 / 18.0;
    m_fNetWorkGoodLostRatio = 0.0 / 18.0;
    m_iStaticCalcPeriod = 30;
}

CPushStreamStatus::~CPushStreamStatus()
{
    
}

void CPushStreamStatus::SetVideoFrameRate(int iFrameRate)
{
    m_iVideoFrameRate = iFrameRate;
    m_iVideoCacheNumMax = m_iVideoFrameRate /2;
}

void CPushStreamStatus::AddReconnectCnt(int iCnt)
{
    m_iReconnCnt++;
}

void CPushStreamStatus::AddVideoLostCnt(int iCnt)
{
    m_iVideoTotoalLostCnt++;
    m_iVideoCurrLostCnt++;
}

void CPushStreamStatus::AddVideoSend(int iSize)
{
    m_iVideoCurrSendSize += iSize;
    m_iVideoCurrSendPktCnt++;
}

void CPushStreamStatus::AddAudioSend(int iSize)
{
    m_iAudioCurrSendSize += iSize;
    m_iAudioCurrSendPktCnt++;
}

double CPushStreamStatus::GetVideoAvgFrameRate()
{
    return m_fVideoRealAvgFrameRate;
}

double CPushStreamStatus::GetBitrate()
{
    return m_fAudioRealAvgBitrate + m_fVideoRealAvgBitrate;
}

double CPushStreamStatus::GetVideoLostCnt()
{
    return m_fVideoAvgLostCnt;
}

int CPushStreamStatus::GetReconnectCnt()
{
    return m_iReconnCnt;
}

int CPushStreamStatus::GetNetworkStatus()
{
    return m_iNetworkStatus;
}

int CPushStreamStatus::GetVideoCacheMax()
{
    return m_iVideoCacheNumMax;
}

int CPushStreamStatus::GetCalculatePeriod()
{
    return m_iStaticCalcPeriod;
}

int CPushStreamStatus::Calculate(int iDuration)
{
    int iCurrStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_GOOD;
    
    m_fVideoAvgLostCnt = (double)m_iVideoCurrLostCnt / (double)iDuration;
    m_fVideoAvgLostRatio = m_fVideoAvgLostCnt / (double)m_iVideoFrameRate;
    m_fVideoRealAvgFrameRate = (double)m_iVideoCurrSendPktCnt / (double)iDuration;
    m_fVideoRealAvgBitrate = (double)m_iVideoCurrSendSize / (double)iDuration;
    m_fAudioRealAvgBitrate = (double)m_iAudioCurrSendSize / (double)iDuration;
    
        
    if(m_fVideoAvgLostRatio > m_fNetWorkBadLostRatio)
    {
        m_iContGoodCnt = 0;
        m_iContBadCnt++;
        
        if(m_iContBadCnt >= 2)
        {
            m_iNetworkStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_BAD;
        }
        
        
        iCurrStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_BAD;
    }
    else if(m_fVideoAvgLostRatio <= m_fNetWorkGoodLostRatio)
    {
        m_iContBadCnt = 0;
        m_iContGoodCnt++;
        
        if(m_iContGoodCnt >= 6)
        {
            m_iNetworkStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_GOOD;
        }
        
        iCurrStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_GOOD;
    }
    else
    {
        m_iContGoodCnt = 0;
        m_iContBadCnt = 0;
        
        
        iCurrStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_NORMAL;
    }
    
    m_iVideoCurrLostCnt = 0;
    m_iVideoCurrSendPktCnt = 0;
    m_iVideoCurrSendSize = 0;
    m_iAudioCurrSendPktCnt = 0;
    m_iAudioCurrSendSize = 0;

    return iCurrStatus;
}

void CPushStreamStatus::Reset()
{    
    m_iNetworkStatus = ENUM_PUSH_STREAM_NETWORK_STATUS_GOOD;
    m_iVideoTotoalLostCnt = 0;
    m_iVideoCurrLostCnt = 0;
    m_fVideoAvgLostCnt = 0.0;
    m_fVideoAvgLostRatio = 0.0;
    m_iVideoCurrSendPktCnt = 0;
    m_iVideoCurrSendSize = 0;
    m_fVideoRealAvgFrameRate = 0.0;
    m_fVideoRealAvgBitrate = 0.0;
    m_iAudioCurrSendPktCnt = 0;
    m_iAudioCurrSendSize = 0;
    m_fAudioRealAvgBitrate = 0.0;
    m_iReconnCnt = 0;
}



