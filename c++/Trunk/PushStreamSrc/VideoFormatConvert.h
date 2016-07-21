#ifndef VIDEO_FORMAT_CONVERT_H
#define VIDEO_FORMAT_CONVERT_H

#ifdef USE_FFMPEG_CONVERT
struct SwsContext;
#endif

struct AVFrame;
class CVideoFormatConvert
{
public:
	CVideoFormatConvert();
	~CVideoFormatConvert();
	
public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：aiSrcFormat			[输入]		源帧格式
	//			 aiSrcWidth				[输入]		源帧宽
	//			 aiSrcHeight			[输入]		源帧高
	//			 aiDestFormat			[输入]		目的帧格式
	//			 aiDestWidth			[输入]		目的帧宽
	//			 aiDstHeight			[输入]		目的帧高
	// 返 回 值：调用是否成功
	// 函数说明：打开
	// $_FUNCTION_END *********************************************************
	bool Open(int aiSrcFormat, int aiSrcWidth, int aiSrcHeight, int aiDestFormat, int aiDestWidth, int aiDstHeight);
	
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Close
	// 函数参数：
	// 返 回 值：
	// 函数说明：关闭
	// $_FUNCTION_END *********************************************************
	void Close();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Convert
	// 函数参数：apSrcFrame				[输入]		源帧
	//			 apDestFrame			[输入]		目的帧
	// 返 回 值：调用是否成功
	// 函数说明：将源帧转化为目的帧
	// $_FUNCTION_END *********************************************************
	bool Convert(AVFrame* apSrcFrame, AVFrame* apDestFrame);
	
private:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Free
	// 函数参数：
	// 返 回 值：
	// 函数说明：释放资源
	// $_FUNCTION_END *********************************************************
	void Free();

private:
	
#ifdef USE_FFMPEG_CONVERT
	// 转换上下文
	struct SwsContext*	m_pImgConvertCtx;
#endif

	// 源帧宽
	int					m_iSrcWidth;

	// 源帧高
	int					m_iSrcHeight;

	// 源帧格式
	int					m_iSrcFmt;

	// 目的帧宽
	int					m_iDestWidth;

	// 目的帧高
	int					m_iDestHeight;

	// 目的帧格式
	int					m_iDestFmt;
};

#endif