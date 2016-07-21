#ifndef AV_PACKET_QUEUE_H
#define AV_PACKET_QUEUE_H

#include <mutex>
using namespace std;

struct AVPacket;
struct AVPacketList;
class CAVPacketQueue
{
public:
	CAVPacketQueue();
	~CAVPacketQueue();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：PutPacket
	// 函数参数：apPacket				[输入]	包
	// 返 回 值：调用是否成功
	// 函数说明：将包压入链表
	// $_FUNCTION_END *********************************************************
	bool PutPacket(AVPacket* apPacket);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：GetPacket
	// 函数参数：apPacket				[输入|输出]	包
	// 返 回 值：调用是否成功
	// 函数说明：从链表获取一个包
	// $_FUNCTION_END *********************************************************
	bool GetPacket(AVPacket* apPacket);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Clear
	// 函数参数：
	// 返 回 值：
	// 函数说明：清空链表
	// $_FUNCTION_END *********************************************************
	void Clear();

private:
	// 锁
	mutex			m_Lock;

	// 链表
	AVPacketList*	m_pFirstPacket;

	// 链表
	AVPacketList*	m_pLastPacket;

	// 链表个数
	int				m_iPacketCount;

	// 链表大小
	int				m_iPacketSize;
};

#endif