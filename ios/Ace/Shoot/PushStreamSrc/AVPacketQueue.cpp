#include <assert.h>
#include "AVPacketQueue.h"
#include "Log.h"

extern "C"
{
#include "libavformat/avformat.h"
}

CAVPacketQueue::CAVPacketQueue()
{
	m_pFirstPacket = nullptr;
	m_pLastPacket = nullptr;
	m_iPacketCount = 0;
	m_iPacketSize = 0;
}

CAVPacketQueue::~CAVPacketQueue()
{
	assert(m_pFirstPacket == nullptr);
	assert(m_pLastPacket == nullptr);
	assert(m_iPacketCount == 0);
	assert(m_iPacketSize == 0);
}

// 将包压入链表
bool CAVPacketQueue::PutPacket(AVPacket* apPacket)
{
	if (apPacket == nullptr)
	{
		assert(false);
		return false;
	}

	// 复制包
	if (av_dup_packet(apPacket) < 0)
	{
		assert(false);
		return false;
	}

	AVPacketList* pPacketList = (AVPacketList*)av_malloc(sizeof(AVPacketList));
	if (pPacketList == nullptr)
	{
		assert(false);
		return false;
	}

	lock_guard<mutex> AutoLock(m_Lock);

	pPacketList->pkt = *apPacket;
	pPacketList->next = nullptr;

	if (m_pLastPacket == nullptr)
	{
		// 第一个
		m_pFirstPacket = pPacketList;
	}
	else
	{
		// 其他情况
		m_pLastPacket->next = pPacketList;
	}
	m_pLastPacket = pPacketList;
	++m_iPacketCount;
	m_iPacketSize += pPacketList->pkt.size + sizeof(*pPacketList);

	return true;
}

// 从链表获取一个包
bool CAVPacketQueue::GetPacket(AVPacket* apPacket)
{
	if (apPacket == nullptr)
	{
		assert(false);
		return false;
	}

	AVPacketList* pPacketList = nullptr;

	// 返回值
	bool bSuccess = false;

	lock_guard<mutex> AutoLock(m_Lock);

	while (true)
	{
		pPacketList = m_pFirstPacket;
		if (pPacketList != nullptr)
		{
			m_pFirstPacket = pPacketList->next;
			if (m_pFirstPacket == nullptr)
				m_pLastPacket = nullptr;
			--m_iPacketCount;
			
			if(m_iPacketCount > 10)
				CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level4, "packet = %d", m_iPacketCount);

			m_iPacketSize -= pPacketList->pkt.size + sizeof(*pPacketList);

			*apPacket = pPacketList->pkt;
			av_free(pPacketList);

			bSuccess = true;

			break;
		}
		else
		{
			break;
		}
	}

	return bSuccess;
}

// 清空链表
void CAVPacketQueue::Clear()
{
	lock_guard<mutex> AutoLock(m_Lock);

	AVPacketList* pPacketList = nullptr;
	AVPacketList* pPacketListTemp = nullptr;

	for (pPacketList = m_pFirstPacket; pPacketList != nullptr; pPacketList = pPacketListTemp)
	{
		pPacketListTemp = pPacketList->next;
		av_free_packet(&pPacketList->pkt);
		av_freep(&pPacketList);
	}

	m_pFirstPacket = nullptr;
	m_pLastPacket = nullptr;
	m_iPacketCount = 0;
	m_iPacketSize = 0;
}