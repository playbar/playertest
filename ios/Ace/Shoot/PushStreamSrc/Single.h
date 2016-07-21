// $_FILEHEADER_BEGIN *********************************************************
// 文件名称：Single.h
// 创建日期：2012-08-01
// 创建人：罗俊杰
// 文件说明：单例模板类
// $_FILEHEADER_END ***********************************************************

#ifndef SINGLE_H
#define SINGLE_H

template <typename T>
class CSingle
{
public:
	// $_FUNCTION_BEGIN **************************************************
	// 函数名称：GetInstance
	// 函数参数：
	// 返 回 值：单例模板对象
	// 函数说明：取得单例模板对象
	// $_FUNCTION_END ****************************************************
	static T& GetInstance();

private:
	CSingle() = default;
	CSingle(const CSingle& aSingle) = default;
	~CSingle() = default;
	CSingle& operator =(const CSingle& aSingle) = default;
};

#include "Single.inl"

#endif