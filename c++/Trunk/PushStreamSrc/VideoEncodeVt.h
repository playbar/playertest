#ifndef VIDEO_ENCODE_VT_H
#define VIDEO_ENCODE_VT_H

#include "IVideoEncode.h"
#include <VideoToolbox/VideoToolbox.h>
#include <mutex>


struct EncBuffNode
{
    CMSampleBufferRef buf;
    EncBuffNode* next;
};

struct EncBuffQueue
{
    EncBuffNode* head;
    EncBuffNode* tail;
    int cnt;

    std::mutex lock;
};


class CVideoEncodeVt : public IVideoEncode
{
public:
	CVideoEncodeVt();
	~CVideoEncodeVt();

public:
	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Open
	// 函数参数：aPushStreamParam		[输入]	推流参数
	// 返 回 值：调用是否成功
	// 函数说明：打开
	// $_FUNCTION_END *********************************************************
	bool Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam);

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Close
	// 函数参数：
	// 返 回 值：
	// 函数说明：关闭
	// $_FUNCTION_END *********************************************************
	void Close();

	// $_FUNCTION_BEGIN *******************************************************
	// 函数名称：Encode
	// 函数参数：apFrame				[输入]		视频帧
	//			 apPacket				[输入|输出]	包数据
	// 返 回 值：调用是否成功
	// 函数说明：编码
	// $_FUNCTION_END *********************************************************
	bool Encode(AVFrame* apFrame, AVPacket* apPacket);
    
    
private:
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetProfileLevel
    // 函数参数：abHasBFrames				[输入]		是否使用B帧
    //		   aProfileLevel			[输出]       videotoolbox支持的ProfileLevel
    // 返 回 值：调用是否成功
    // 函数说明：取得videotoolbox支持的profile_level
    // $_FUNCTION_END *********************************************************
    int GetProfileLevel(bool abHasBFrames, int aiProfile, int aiLevel, CFStringRef* aProfileLevel);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetCVPixelBufferInfo
    // 函数参数：
    //		   apPixelBufferInfo		[输出]       像素缓冲信息
    // 返 回 值：调用是否成功
    // 函数说明：取得像素缓冲信息，用于创建像素缓冲
    // $_FUNCTION_END *********************************************************
    int GetCVPixelBufferInfo(int aiWidth, int aiHeight, CFMutableDictionaryRef* apPixelBufferInfo);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetCVPixelInfo
    // 函数参数：apFrame                 [输入]		原始视频帧
    //		   apColor                 [输出]        像素格式
    //         apContiguousBufSize     [输出]        连续的buffer大小
    // 返 回 值：调用是否成功
    // 函数说明：取得像素格式信息
    // $_FUNCTION_END *********************************************************
    int GetCVPixelInfo(const AVFrame* apFrame, int* apColor, int* apPlaneCnt,
                       int* apWidths, int* apHeights, int* apStrides, int* apContiguousBufSize);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetExtraData
    // 函数参数：aeCodecType              [输入]        编码类型，这里只支持h264
    //		   aProfileLevel            [输入]        编码使用的profile_level
    //		   aPixelBufferInfo         [输入]        像素缓冲信息
    // 返 回 值：调用是否成功
    // 函数说明：取得附加数据
    // $_FUNCTION_END *********************************************************
    int GetExtraData(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam, CMVideoCodecType aeCodecType,
                     CFStringRef aProfileLevel, CFDictionaryRef aPixelBufferInfo);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetParamSize
    // 函数参数：avideoFmtDesc            [输入]        视频格式描述
    //		   aiSize                   [输出]        参数集大小
    // 返 回 值：调用是否成功
    // 函数说明：取得参数集的大小
    // $_FUNCTION_END *********************************************************
    int GetParamSize(CMVideoFormatDescriptionRef avideoFmtDesc, int* aiSize);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：CopyParamSets
    // 函数参数：avideoFmtDesc            [输入]        视频格式描述
    //		   apBuf                    [输出]        保存参数集缓冲
    // 返 回 值：调用是否成功
    // 函数说明：拷贝参数集到缓冲
    // $_FUNCTION_END *********************************************************
    int CopyParamSets(CMVideoFormatDescriptionRef avideoFmtDesc, uint8_t* apBuf, int aiBufSize);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：CreateEncoder
    // 函数参数：aeCodecType              [输入]        编码类型，这里只支持h264
    //		   aProfileLevel            [输入]        编码使用的profile_level
    //		   aPixelBufferInfo         [输入]        像素缓冲信息
    //		   apSession                [输出]        编码会话
    // 返 回 值：调用是否成功
    // 函数说明：拷贝参数集到缓冲
    // $_FUNCTION_END *********************************************************
    int CreateEncoder(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam, CMVideoCodecType aeCodecType,
                      CFStringRef aProfileLevel, CFDictionaryRef aPixelBufferInfo, VTCompressionSessionRef* apSession);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：CreateCVPixelBuffer
    // 函数参数：apFrame                  [输入]        原图像帧
    //		   apPixelBuffer            [输出]        像素缓冲
    //		   aSession                 [输入]        编码会话
    // 返 回 值：调用是否成功
    // 函数说明：创建用于编码的像素缓冲
    // $_FUNCTION_END *********************************************************
    int CreateCVPixelBuffer(const AVFrame* apFrame, CVPixelBufferRef* apPixelBuffer, VTCompressionSessionRef aSession);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：CopyFrameToPixelBuffer
    // 函数参数：apFrame                  [输入]        原图像帧
    //		   aPixelBuffer             [输入]        像素缓冲
    //		   aSession                 [输入]        编码会话
    //		   apStrides                [输入]        行字节数
    //		   apRows                   [输入]        行数
    // 返 回 值：调用是否成功
    // 函数说明：拷贝原图像到像素缓冲
    // $_FUNCTION_END *********************************************************
    int CopyFrameToPixelBuffer(const AVFrame* pFrame, CVPixelBufferRef aPixelBuffer,
                               const int* apStrides, const int* apRows);
  
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：SetExtraData
    // 函数参数：sampleBuffer             [输入]        编码后的数据、
    // 返 回 值：调用是否成功
    // 函数说明：保存参数集到附加数据缓存
    int SetExtraData(CMSampleBufferRef sampleBuffer);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：EncodeFrame
    // 函数参数：apFrame                  [输入]        原图像帧
    //		   aSession                 [输入]        编码会话
    // 返 回 值：调用是否成功
    // 函数说明：编码一帧视频
    // $_FUNCTION_END *********************************************************
    int EncodeFrame(const AVFrame* apFrame, VTCompressionSessionRef aSession);

    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：EncodeFrame
    // 返 回 值：拷贝附加数据缓存的数据到avpacket
    // 函数说明：编码一帧视频
    // $_FUNCTION_END *********************************************************
    int CopyExtradataToAVPacket(AVPacket* apPacket);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：CopySampleBufferToAVPakcet
    // 函数参数：sampleBuffer                  [输入]       编码后的数据
    // 返 回 值：调用是否成功
    // 函数说明：编码一帧视频
    // $_FUNCTION_END *********************************************************
    int CopySampleBufferToAVPakcet(CMSampleBufferRef sampleBuffer, AVPacket* apPacket);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：IsKeyFrame
    // 函数参数：sampleBuffer                  [输入]       编码后的数据
    // 返 回 值：调用是否成功
    // 函数说明：是否关键帧
    // $_FUNCTION_END *********************************************************
    bool IsKeyFrame(CMSampleBufferRef sampleBuffer);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetLengthCodeSize
    // 函数参数：sampleBuffer                 [输入]       编码后的数据
    //		   aiSize                       [输出]        头大小
    // 返 回 值：调用是否成功
    // 函数说明：取得nalu的头大小
    // $_FUNCTION_END *********************************************************
    int GetLengthCodeSize(CMSampleBufferRef sampleBuffer, int* aiSize);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：GetNaluCnt
    // 函数参数：sampleBuffer                 [输入]       编码后的数据
    //		   aiLengthCodeSize             [输入]        头大小
    //		   aiNaluCnt                    [输出]        nalu的数量
    // 返 回 值：调用是否成功
    // 函数说明：取得nalu的数量
    // $_FUNCTION_END *********************************************************
    int GetNaluCnt(CMSampleBufferRef sampleBuffer, int aiLengthCodeSize, int* aiNaluCnt);
    
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：CopyNalus
    // 函数参数：sampleBuffer                 [输入]       编码后的数据
    //		   apData                       [输出]        缓存
    //		   aiDataSize                   [输入]        缓存大小
    // 返 回 值：调用是否成功
    // 函数说明：拷贝nalu到缓冲
    // $_FUNCTION_END *********************************************************
    int CopyNalus(CMSampleBufferRef sampleBuffer, int aiLengthCodeSize, uint8_t* apData, int aiDataSize);
    
    
    
    
private:
    // $_FUNCTION_BEGIN *******************************************************
    // 函数名称：EncodeOutputCallback
    // 函数参数：outputCallbackRefCon         [输入]       编码会话关联的数据
    //		   status                   [输入]            编码状态
    //         sampleBuffer             [输入]            编码后的数据
    // 返 回 值：调用是否成功
    // 函数说明：编码回调函数
    // $_FUNCTION_END *********************************************************
    static void EncodeOutputCallback(void* outputCallbackRefCon,
                                     void* sourceFrameRefCon,
                                     OSStatus status,
                                     VTEncodeInfoFlags infoFlags,
                                     CMSampleBufferRef sampleBuffer);
    
   
    
private:
    int PopBuff(CMSampleBufferRef* apBuf);
    
    void PushBuff(CMSampleBufferRef aBuf);
    
    void ClearEncodeBuffQueue();
    
private:
    //打开标志
    bool m_bOpen;
      
    //编码失败标志
    bool m_bCompressErr;
    
    //编码会话
    VTCompressionSessionRef m_EncodeSession;
    
    //编码数据队列
    EncBuffQueue m_EncBuffQueue;
     
    //附加数据缓冲
    uint8_t* m_pExtraData;
    
    //附加数据缓冲大小
    int m_iExtraDataSize;
       
    //编码相关的参数
    int m_iFrameRate;
    int m_iProfile;
    int m_iLevel;
    int m_iEntropy;
    bool m_bHasBFrames;
    
    //每一帧的时间戳
    int64_t m_iFirstPts;
    
    int64_t m_iDtsDelta;
    
};



#endif