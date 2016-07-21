// $_FILEHEADER_BEGIN *********************************************************
// 文件名称：NormalLock.h
// 创建日期：2012-01-31
// 创建人：罗俊杰
// 文件说明：普通锁对象(windows下是临界区,linux下是互斥量)
// $_FILEHEADER_END ***********************************************************

#ifndef NORMAL_LOCK_H
#define NORMAL_LOCK_H

#ifdef WIN32
#include <Windows.h>
#else
#include <pthread.h>
#endif

class CNormalLock
{
public:
	CNormalLock();
	~CNormalLock();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Lock
	// 函数参数：
	// 返 回 值：
	// 函数说明：加锁
	// $_FUNCTION_END *********************************************************
	void Lock();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：UnLock
	// 函数参数：
	// 返 回 值：
	// 函数说明：解锁
	// $_FUNCTION_END *********************************************************
	void UnLock();

private:
#ifdef WIN32
	// 临界区
	CRITICAL_SECTION	m_oCriticalSection;
#else
	// 初始化互斥量是否成功
	bool				m_bMutex;
	
	// 互斥量
	pthread_mutex_t		m_oMutex;
	
	// 初始化互斥量属性是否成功
	bool				m_bMutexAttr;
	
	// 互斥量属性
	pthread_mutexattr_t	m_oMutexAttr;
#endif
};

class CAutoLock
{
public:
	CAutoLock(CNormalLock& aNormalLock);
	~CAutoLock();

private:
	// 普通锁对象
	CNormalLock& m_oNormalLock;
};

#endif