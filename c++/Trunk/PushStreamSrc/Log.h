// $_FILEHEADER_BEGIN *********************************************************
// 文件名称：Log.h
// 创建日期：2012-07-06
// 创建人：罗俊杰
// 文件说明：日志类
// $_FILEHEADER_END ***********************************************************

#ifndef LOG_H
#define LOG_H

#include <stdarg.h>
#include "NormalLock.h"

// 日志文件名长度
#define DEF_LOG_FILENAME_LEN	512

// 日志记录的缓存长度
#define DEF_LOG_BUFF_LEN		4096

// 日志级别,从低到高分为0级到5级,0级最低,5级最高,日志默认为0级
enum ENUM_LOG_LEVEL
{
	enum_Log_Level0,	// 0级
	enum_Log_Level1,	// 1级
	enum_Log_Level2,	// 2级
	enum_Log_Level3,	// 3级
	enum_Log_Level4,	// 4级
	enum_Log_Level5,	// 5级
};

// 日志选项
enum ENUM_LOG_OPTION
{
	enum_Log_Option_Timestamp = 1,			// 打印时间戳
	enum_Log_Option_LogLevel = 2,			// 打印日志级别
	enum_Log_Option_FilenameAndLine = 4,	// 打印文件名和行号
	enum_Log_Option_PrintToFile = 8,		// 打印到文件
	enum_Log_Option_PrintToConsole = 16,	// 打印到控制台
};

class CLog
{
public:
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：GetInstance
	// 函数参数：
	// 返 回 值：CLog对象
	// 函数说明：取得CLog对象
	// $_FUNCTION_END ****************************************************
	static CLog& GetInstance();

	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：SetLogLevel
	// 函数参数：aiLogLevel			[输入]	日志等级
	// 返 回 值：
	// 函数说明：设置日志等级
	// $_FUNCTION_END ****************************************************
	void SetLogLevel(unsigned int aiLogLevel);

	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：SetLogFileName
	// 函数参数：apLogFileName		[输入]	日志文件名
	//			 aiLogFileNameLen	[输入]	日志文件名长度
	// 返 回 值：
	// 函数说明：设置日志文件名
	// $_FUNCTION_END ****************************************************
	void SetLogFileName(char* apLogFileName, unsigned int aiLogFileNameLen);

	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：SetLogOption
	// 函数参数：aiLogOption		[输入]	日志选项
	// 返 回 值：
	// 函数说明：设置日志选项
	// $_FUNCTION_END ****************************************************
	void SetLogOption(unsigned int aiLogOption);

	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：Log
	// 函数参数：aiLogLevel			[输入]	每条日志记录的日志等级,如果这个日志等级低于SetLogLevel设置的等级,将不打印到文件中,但可以打印到控制台
	//			 apFormat			[输入]	日志记录的格式化字符串
	//			 ...				[输入]	日志记录格式化参数
	// 返 回 值：
	// 函数说明：记录日志
	// $_FUNCTION_END ****************************************************
	void Log(unsigned int aiLogLevel, const char* apFormat, ...);

private:
	CLog();
	CLog(const CLog& aLog);
	~CLog();
	CLog& operator ==(const CLog& aLog);
	
private:
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：AddTimestamp
	// 函数参数：
	// 返 回 值：
	// 函数说明：添加时间戳
	// $_FUNCTION_END ****************************************************
	void AddTimestamp();
	
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：AddLogLevel
	// 函数参数：aiLogLevel			[输入]	每条日志记录的日志等级
	//			abPrintToFile		[输入]	是否打印到文件
	// 返 回 值：
	// 函数说明：添加日志级别
	// $_FUNCTION_END ****************************************************
	void AddLogLevel(unsigned int aiLogLevel, bool& abPrintToFile);
	
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：AddFileNameAndLine
	// 函数参数：
	// 返 回 值：
	// 函数说明：添加文件名和行号
	// $_FUNCTION_END ****************************************************
	void AddFileNameAndLine();
	
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：AddLog
	// 函数参数：apFormat			[输入]	日志记录的格式化字符串
	//			 argptr				[输入]	日志记录格式化参数
	// 返 回 值：
	// 函数说明：添加日志记录
	// $_FUNCTION_END ****************************************************
	void AddLog(const char* apFormat, va_list& argptr);
	
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：PrintOut
	// 函数参数：abPrintToFile		[输入]	是否打印到文件
	// 返 回 值：
	// 函数说明：打印到控制台/文件
	// $_FUNCTION_END ****************************************************
	void PrintOut(bool abPrintToFile);

private:
	// 日志等级
	unsigned int	m_iLogLevel;

	// 日志文件名
	char			m_szLogFileName[DEF_LOG_FILENAME_LEN];

	// 日志选项(对应ENUM_LOG_OPTION,可通过与操作来添加多个选项)
	unsigned int	m_iLogOption;

	// 一条日志记录的缓存
	char			m_szLogBuff[DEF_LOG_BUFF_LEN];

	// 一条日志记录缓存的实际长度
	unsigned int	m_iLogBuffLen;

	// 锁
	CNormalLock		m_oLock;
};

#endif