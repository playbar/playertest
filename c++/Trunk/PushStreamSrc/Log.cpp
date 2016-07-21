#include <sys/timeb.h>
#include <time.h>
#include <iostream>
#include <string.h>
#include <stdio.h>
#ifdef	ANDROID	// Android平台
#include <android/log.h>
#endif
#include "Log.h"

// Log(unsigned int aiLogLevel, const char* apFormat, ...)中apFormat的缓存最大长度
#define DEF_FORMAT_MAX_LEN	3072
////////////////////////////////////////////////////////////
// CLog
CLog::CLog()
{
	m_iLogLevel = enum_Log_Level0;
	memset(m_szLogFileName, 0, sizeof(m_szLogFileName));
	m_iLogOption = enum_Log_Option_Timestamp | enum_Log_Option_PrintToConsole;

	memset(m_szLogBuff, 0 , sizeof(m_szLogBuff));
	m_iLogBuffLen = 0;
}

CLog::~CLog()
{
}

// 取得CLog对象
CLog& CLog::GetInstance()
{
	static CLog oLog;
	return oLog;
}

// 设置日志等级
void CLog::SetLogLevel(unsigned int aiLogLevel)
{
	m_iLogLevel = aiLogLevel;
}

// 设置日志文件名
void CLog::SetLogFileName(char* apLogFileName, unsigned int aiLogFileNameLen)
{
	if (apLogFileName == NULL || aiLogFileNameLen == 0)
	{
		return;
	}

	// 长度过长
	if (aiLogFileNameLen >= DEF_LOG_FILENAME_LEN)
	{
		return;
	}

	memcpy(m_szLogFileName, apLogFileName, aiLogFileNameLen);
}

// 设置日志选项
void CLog::SetLogOption(unsigned int aiLogOption)
{
	m_iLogOption = aiLogOption;
}

// 记录日志
void CLog::Log(unsigned int aiLogLevel, const char* apFormat, ...)
{
	// 不能输出空字符串
	if (apFormat == NULL)
	{
		return;
	}

	// 考虑多线程的问题这里需要加锁
	CAutoLock oAutoLock(m_oLock);

	// 添加时间戳
	AddTimestamp();
	
	// 是否打印到文件中
	bool bPrintToFile = false;

	// 添加日志级别
	AddLogLevel(aiLogLevel, bPrintToFile);
	
	// 添加文件名和行号
	AddFileNameAndLine();
	
	// 添加日志记录
	va_list argptr;
	va_start(argptr, apFormat);
	AddLog(apFormat, argptr);
	va_end(argptr);
	
	// 打印到控制台/文件
	PrintOut(bPrintToFile);
	
	memset(m_szLogBuff, 0, sizeof(m_szLogBuff));
	m_iLogBuffLen = 0;
}

// 添加时间戳
void CLog::AddTimestamp()
{
	if (m_iLogOption & enum_Log_Option_Timestamp) // 打印时间戳
	{
		struct timeb oTime;
		ftime(&oTime);

		struct tm* pLocalTime = localtime(&oTime.time);
		if (pLocalTime != NULL)
		{
			// 将数据写入缓存
			char* pTemp = m_szLogBuff + m_iLogBuffLen;
			m_iLogBuffLen += sprintf(pTemp, "%02d:%02d:%02d:%03d ", pLocalTime->tm_hour, pLocalTime->tm_min, pLocalTime->tm_sec, oTime.millitm);
		}
	}
}

// 添加日志级别
void CLog::AddLogLevel(unsigned int aiLogLevel, bool& abPrintToFile)
{
	if (m_iLogOption & enum_Log_Option_LogLevel)	// 打印日志级别
	{
		// 将数据写入缓存
		char* pTemp = m_szLogBuff + m_iLogBuffLen;
		m_iLogBuffLen += sprintf(pTemp, "LogLevel:%u ", aiLogLevel);
	}

	if (m_iLogLevel < aiLogLevel)
	{
		abPrintToFile = true;
	}
}

// 添加文件名和行号
void CLog::AddFileNameAndLine()
{
	if (m_iLogOption & enum_Log_Option_FilenameAndLine)	// 打印文件名和行号
	{
		// 将数据写入缓存
		char* pTemp = m_szLogBuff + m_iLogBuffLen;
		m_iLogBuffLen += sprintf(pTemp, "%s(%d) ", __FILE__, __LINE__);
	}
}

// 添加日志记录
void CLog::AddLog(const char* apFormat, va_list& argptr)
{
#ifndef WIN32
	// 把%I64d替换成%lld
	const char* pPtr1 = apFormat;
	char szFormat[DEF_FORMAT_MAX_LEN] = {0};
	
	const char* pPtr2 = strstr(pPtr1, "%I64d");
	if (pPtr2 != NULL)
	{
		int iCopyLen = 0;
		while (pPtr2 != NULL)
		{
			memcpy(szFormat + iCopyLen, pPtr1, pPtr2 - pPtr1);
			iCopyLen += (pPtr2 - pPtr1);

			strcpy(szFormat + iCopyLen, "%lld");
			iCopyLen += strlen("%lld");

			pPtr1 = pPtr2 + strlen("%I64d");
			pPtr2 = strstr(pPtr1, "%I64d");
		}
		strcpy(szFormat + iCopyLen, pPtr1);
		pPtr1 = szFormat;
	}
	
	// 把szFormat追加到m_szLogBuff中
	char* pTemp = m_szLogBuff + m_iLogBuffLen;
	m_iLogBuffLen += vsprintf(pTemp, pPtr1, argptr);
#else
	// 把szFormat追加到m_szLogBuff中
	char* pTemp = m_szLogBuff + m_iLogBuffLen;
	m_iLogBuffLen += vsprintf(pTemp, apFormat, argptr);	
#endif
}

// 打印到控制台/文件
void CLog::PrintOut(bool abPrintToFile)
{
	if (m_iLogOption & enum_Log_Option_PrintToConsole)	// 打印到控制台
	{
#ifdef	ANDROID	// Android平台
		__android_log_print(ANDROID_LOG_INFO, "Android Platform", "%s/n", m_szLogBuff);
#else
		printf("%s\n", m_szLogBuff);
#endif
	}
	
	if (m_iLogOption & enum_Log_Option_PrintToFile)	// 打印到文件
	{
		if (abPrintToFile && strlen(m_szLogFileName) > 0)	// 有文件名,并且有可打印标志,则打印到文件
		{
			FILE* pFile = NULL;
			pFile = fopen(m_szLogFileName, "a");
			if (pFile != NULL)
			{
				fprintf(pFile, "%s\n", m_szLogBuff);
				fclose(pFile);
			}
		}
	}
}