#ifndef PUSH_STREAM_NOTIFY_H
#define PUSH_STREAM_NOTIFY_H



enum ENUM_PUSH_STREAM_NETWORK_STATUS : unsigned short
{
    ENUM_PUSH_STREAM_NETWORK_STATUS_BAD = 0,
    ENUM_PUSH_STREAM_NETWORK_STATUS_NORMAL,
    ENUM_PUSH_STREAM_NETWORK_STATUS_GOOD,
};

class CPushStreamNotify
{
public:
	CPushStreamNotify() {};
	virtual ~CPushStreamNotify() {};
	
public:
	virtual void NetworkStauts(int iStatus, double dRealFrameRate, double dRealBitrate, double dLostVFrameRate, int iReconnectCnt, bool bConnected) = 0;
	
	virtual void Reconnected() = 0;
};

#endif