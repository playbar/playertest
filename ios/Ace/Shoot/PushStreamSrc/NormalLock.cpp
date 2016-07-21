#include <assert.h>
#include "NormalLock.h"
#include "Log.h"

////////////////////////////////////////////////////////////
// CNormalLock
CNormalLock::CNormalLock()
{	
#ifdef WIN32
	InitializeCriticalSection(&m_oCriticalSection);
#else
	m_bMutex = false;
	m_bMutexAttr = false;
	
	// 初始化互斥量的属性
	if (pthread_mutexattr_init(&m_oMutexAttr) == 0)
	{
		m_bMutexAttr = true;

		// 设置互斥量的属性
		if (pthread_mutexattr_settype(&m_oMutexAttr, PTHREAD_MUTEX_RECURSIVE) == 0)
		{
			// 初始化互斥量
			if (pthread_mutex_init(&m_oMutex, &m_oMutexAttr) == 0)
			{
				m_bMutex = true;
			}
		}
	}
#endif
}

CNormalLock::~CNormalLock()
{
#ifdef WIN32
	DeleteCriticalSection(&m_oCriticalSection);
#else
	if (m_bMutexAttr)
	{
		pthread_mutexattr_destroy(&m_oMutexAttr);
	}
	
	if (m_bMutex)
	{
		pthread_mutex_destroy(&m_oMutex);
	}
#endif
}

// 加锁
void CNormalLock::Lock()
{
#ifdef WIN32
	EnterCriticalSection(&m_oCriticalSection);
#else
	if (m_bMutex)
	{
		if (pthread_mutex_lock(&m_oMutex) != 0)
		{
			assert(false);
			CLog::GetInstance().Log(enum_Log_Level5, "lock error");
		}
	}
	else
	{
		assert(false);
		CLog::GetInstance().Log(enum_Log_Level5, "CNormalLock::Lock lock is not init");
	}
#endif
}

// 解锁
void CNormalLock::UnLock()
{
#ifdef WIN32
	LeaveCriticalSection(&m_oCriticalSection);
#else
	if (m_bMutex)
	{
		if (pthread_mutex_unlock(&m_oMutex) != 0)
		{
			assert(false);
			CLog::GetInstance().Log(enum_Log_Level5, "unlock error");
		}
	}
	else
	{
		assert(false);
		CLog::GetInstance().Log(enum_Log_Level5, "CNormalLock::UnLock lock is not init");
	}
#endif
}

////////////////////////////////////////////////////////////
// CAutoLock
CAutoLock::CAutoLock(CNormalLock& aNormalLock)
	:m_oNormalLock(aNormalLock)
{
	m_oNormalLock.Lock();
}

CAutoLock::~CAutoLock()
{
	m_oNormalLock.UnLock();
}