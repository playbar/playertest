#include <assert.h>
#include "SendDeal.h"
#include "Log.h"

extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/imgutils.h"
#include "libavutil/mathematics.h"
#include "libavutil/time.h"
}

// 超时
#define RW_TIMEOUT					10000000
// 重连次数最大值
#define	RECONNECT_NUM_MAX			5
// 重连间隔时间ms
#define RECONNECT_INTERVAL			1000
// 重连间隔时间最大值
#define RECONNECT_INTERVAL_MAX		10000

typedef struct URLContext {
	const AVClass *av_class;    /**< information for av_log(). Set by url_open(). */
	struct URLProtocol *prot;
	void *priv_data;
	char *filename;             /**< specified URL */
	int flags;
	int max_packet_size;        /**< if non zero, the stream is packetized with this max packet size */
	int is_streamed;            /**< true if streamed (no seek possible), default = false */
	int is_connected;
	AVIOInterruptCB interrupt_callback;
	int64_t rw_timeout;         /**< maximum time to wait for (network) read/write operation completion, in mcs */
} URLContext;

typedef struct TrackedMethod {
	char *name;
	int id;
} TrackedMethod;

/** RTMP protocol handler state */
typedef enum {
	STATE_START,      ///< client has not done anything yet
	STATE_HANDSHAKED, ///< client has performed handshake
	STATE_FCPUBLISH,  ///< client FCPublishing stream (for output)
	STATE_PLAYING,    ///< client has started receiving multimedia data from server
	STATE_SEEKING,    ///< client has started the seek operation. Back on STATE_PLAYING when the time comes
	STATE_PUBLISHING, ///< client has started sending multimedia data to server (for output)
	STATE_RECEIVING,  ///< received a publish command (for input)
	STATE_SENDING,    ///< received a play command (for output)
	STATE_STOPPED,    ///< the broadcast has been stopped
} ClientState;

typedef enum RTMPPacketType {
	RTMP_PT_CHUNK_SIZE = 1,  ///< chunk size change
	RTMP_PT_BYTES_READ = 3,  ///< number of bytes read
	RTMP_PT_PING,               ///< ping
	RTMP_PT_SERVER_BW,          ///< server bandwidth
	RTMP_PT_CLIENT_BW,          ///< client bandwidth
	RTMP_PT_AUDIO = 8,  ///< audio packet
	RTMP_PT_VIDEO,              ///< video packet
	RTMP_PT_FLEX_STREAM = 15,  ///< Flex shared stream
	RTMP_PT_FLEX_OBJECT,        ///< Flex shared object
	RTMP_PT_FLEX_MESSAGE,       ///< Flex shared message
	RTMP_PT_NOTIFY,             ///< some notification
	RTMP_PT_SHARED_OBJ,         ///< shared object
	RTMP_PT_INVOKE,             ///< invoke some stream action
	RTMP_PT_METADATA = 22,  ///< FLV metadata
} RTMPPacketType;

typedef struct RTMPPacket {
	int            channel_id; ///< RTMP channel ID (nothing to do with audio/video channels though)
	RTMPPacketType type;       ///< packet payload type
	uint32_t       timestamp;  ///< packet full timestamp
	uint32_t       ts_field;   ///< 24-bit timestamp or increment to the previous one, in milliseconds (latter only for media packets). Clipped to a maximum of 0xFFFFFF, indicating an extended timestamp field.
	uint32_t       extra;      ///< probably an additional channel ID used during streaming data
	uint8_t        *data;      ///< packet payload
	int            size;       ///< packet payload size
	int            offset;     ///< amount of data read so far
	int            read;       ///< amount read, including headers
} RTMPPacket;

#define RTMP_HEADER 11

/** protocol handler context */
typedef struct RTMPContext {
	const AVClass *class1;
	URLContext*   stream;                     ///< TCP stream used in interactions with RTMP server
	RTMPPacket    *prev_pkt[2];               ///< packet history used when reading and sending packets ([0] for reading, [1] for writing)
	int           nb_prev_pkt[2];             ///< number of elements in prev_pkt
	int           in_chunk_size;              ///< size of the chunks incoming RTMP packets are divided into
	int           out_chunk_size;             ///< size of the chunks outgoing RTMP packets are divided into
	int           is_input;                   ///< input/output flag
	char          *playpath;                  ///< stream identifier to play (with possible "mp4:" prefix)
	int           live;                       ///< 0: recorded, -1: live, -2: both
	char          *app;                       ///< name of application
	char          *conn;                      ///< append arbitrary AMF data to the Connect message
	ClientState   state;                      ///< current state
	int           stream_id;                  ///< ID assigned by the server for the stream
	uint8_t*      flv_data;                   ///< buffer with data for demuxer
	int           flv_size;                   ///< current buffer size
	int           flv_off;                    ///< number of bytes read from current buffer
	int           flv_nb_packets;             ///< number of flv packets published
	RTMPPacket    out_pkt;                    ///< rtmp packet, created from flv a/v or metadata (for output)
	uint32_t      client_report_size;         ///< number of bytes after which client should report to server
	uint32_t      bytes_read;                 ///< number of bytes read from server
	uint32_t      last_bytes_read;            ///< number of bytes read last reported to server
	int           skip_bytes;                 ///< number of bytes to skip from the input FLV stream in the next write call
	int           has_audio;                  ///< presence of audio data
	int           has_video;                  ///< presence of video data
	int           received_metadata;          ///< Indicates if we have received metadata about the streams
	uint8_t       flv_header[RTMP_HEADER];    ///< partial incoming flv packet header
	int           flv_header_bytes;           ///< number of initialized bytes in flv_header
	int           nb_invokes;                 ///< keeps track of invoke messages
	char*         tcurl;                      ///< url of the target stream
	char*         flashver;                   ///< version of the flash plugin
	char*         swfhash;                    ///< SHA256 hash of the decompressed SWF file (32 bytes)
	int           swfhash_len;                ///< length of the SHA256 hash
	int           swfsize;                    ///< size of the decompressed SWF file
	char*         swfurl;                     ///< url of the swf player
	char*         swfverify;                  ///< URL to player swf file, compute hash/size automatically
	char          swfverification[42];        ///< hash of the SWF verification
	char*         pageurl;                    ///< url of the web page
	char*         subscribe;                  ///< name of live stream to subscribe
	int           server_bw;                  ///< server bandwidth
	int           client_buffer_time;         ///< client buffer time in ms
	int           flush_interval;             ///< number of packets flushed in the same request (RTMPT only)
	int           encrypted;                  ///< use an encrypted connection (RTMPE only)
	TrackedMethod*tracked_methods;            ///< tracked methods buffer
	int           nb_tracked_methods;         ///< number of tracked methods
	int           tracked_methods_size;       ///< size of the tracked methods buffer
	int           listen;                     ///< listen mode flag
	int           listen_timeout;             ///< listen timeout to wait for new connections
	int           nb_streamid;                ///< The next stream id to return on createStream calls
	char          username[50];
	char          password[50];
	char          auth_params[500];
	int           do_reconnect;
	int           auth_tried;
} RTMPContext;


typedef struct InterruptCbOpaque {
    int64_t timeout;
    int64_t start_time;
} InterruptCbOpaque;


static int interrupt_cb(void* pOpaque)
{
    InterruptCbOpaque* pICOpaque = (InterruptCbOpaque*)pOpaque;
    
    if (0 == pICOpaque->start_time)
    {
        return 0;
    }
    
    int64_t iUsedTime = av_gettime() - pICOpaque->start_time;
    if (iUsedTime > pICOpaque->timeout)
    {
        return 1;
    }
    
    return 0;
}


CSendDeal::CSendDeal()
{
	m_bConnect = false;
	m_pFormatCtx = nullptr;
	m_pVideoStream = nullptr;
	m_pAudioStream = nullptr;
	m_pCodecCtx = nullptr;
	m_pAacBitStreamFilterCtx = nullptr;
    m_pICbOpaque = nullptr;
}

CSendDeal::~CSendDeal()
{
	assert(m_pFormatCtx == nullptr);
	assert(m_pVideoStream == nullptr);
	assert(m_pAudioStream == nullptr);
	assert(m_pCodecCtx == nullptr);
	assert(m_pAacBitStreamFilterCtx == nullptr);
}

// 打开
bool CSendDeal::Open(const STRUCT_PUSH_STREAM_PARAM& aPushStreamParam)
{
	// 保存推流参数
	memcpy(&m_PushStreamParam, &aPushStreamParam, sizeof(aPushStreamParam));
	
	// 连接服务
	if (!Connect())
	{
		// 关闭
		Close();
        return false;
	}

	// aac过滤器
	m_pAacBitStreamFilterCtx = av_bitstream_filter_init("aac_adtstoasc");
	if (m_pAacBitStreamFilterCtx == nullptr)
	{
		// 关闭
		Close();

		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Open av_bitstream_filter_init Failed!");
		assert(false);
		return false;
	}

	return true;
}

// 关闭
void CSendDeal::Close()
{
	// 清除资源
	Clear();
	
	if (m_pAacBitStreamFilterCtx != nullptr)
	{
		av_bitstream_filter_close(m_pAacBitStreamFilterCtx);
		m_pAacBitStreamFilterCtx = nullptr;
	}

	m_bConnect = false;
	m_pCodecCtx = nullptr;
}

// 设置音频编码上下文
void CSendDeal::SetCodecContext(AVCodecContext* apCodecCtx)
{
	if (apCodecCtx == nullptr)
	{
		assert(false);
		return;
	}

	m_pCodecCtx = apCodecCtx;
}

// 获取视频流ID
int CSendDeal::GetVideoStreamID()
{
	if (m_pVideoStream == nullptr)
	{
		assert(false);
		return -1;
	}

	return m_pVideoStream->index;
}

// 获取音频流ID
int CSendDeal::GetAudioStreamID()
{
	if (m_pAudioStream == nullptr)
	{
		assert(false);
		return -1;
	}

	return m_pAudioStream->index;
}

// 视频发送
int CSendDeal::VideoOutput(AVPacket* apPacket)
{
	// 返回值
	int iResult = -1;

	lock_guard<mutex> AutoLock(m_Lock);
	
	if (!m_bConnect)
	{
		return -1;
	}

	if (m_pFormatCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::VideoOutput m_pFormatCtx == nullptr!");
		assert(false);
		return -1;
	}

	apPacket->stream_index = m_pVideoStream->index;
    
    iResult = WriteFrame(apPacket);

    return iResult;
}

// 音频发送
int CSendDeal::AudioOutput(AVPacket* apPacket)
{
	// 返回值
	int iResult = -1;

	lock_guard<mutex> AutoLock(m_Lock);
	
	if (!m_bConnect)
	{
		return -1;
	}

	if (m_pFormatCtx == nullptr || m_pCodecCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AudioOutput m_pFormatCtx == nullptr || m_pCodecCtx == nullptr!");
		assert(false);
		return -1;
	}

	apPacket->stream_index = m_pAudioStream->index;
	av_bitstream_filter_filter(m_pAacBitStreamFilterCtx, m_pCodecCtx, nullptr, &apPacket->data, &apPacket->size, apPacket->data, apPacket->size, 0);

    iResult = WriteFrame(apPacket);
    
    return iResult;
}

int CSendDeal::WriteFrame(AVPacket* apPacket)
{
    m_pICbOpaque->start_time = av_gettime();
    int iResult = av_write_frame(m_pFormatCtx, apPacket);
    if (iResult < 0)
    {
        /*
         if (-10054 == iResult || -10053 == iResult || -138 == iResult || AVERROR_EOF == iResult)
         {
         iResult = -2;
         return iResult;
         }
         */
        
//        if(iResult == AVERROR_EXIT)
//        {
//            return 0;
//        }
        
        m_bConnect = false;
        iResult = -1;
        return iResult;
    }
    
    return 0;
}

void CSendDeal::Stop()
{
    m_pICbOpaque->timeout = 100000;
}


// 是否连接
bool CSendDeal::IsConnect()
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	return m_bConnect;
}

// 重新连接
void CSendDeal::Reconnect()
{
	lock_guard<mutex> AutoLock(m_Lock);
	
	// 清除资源
	Clear();
	
	if (!Connect())
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Reconnect Failed!");
	}
	else
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Reconnect Success!");
	}
}

// 清除资源
void CSendDeal::Clear()
{
    int ret = 0;
    
	if (m_pVideoStream != nullptr)
	{
		avcodec_close(m_pVideoStream->codec);
		m_pVideoStream = nullptr;
	}

	if (m_pAudioStream != nullptr)
	{
		avcodec_close(m_pAudioStream->codec);
		m_pAudioStream = nullptr;
	}

	if (m_pFormatCtx != nullptr)
	{
        m_pICbOpaque->timeout = RW_TIMEOUT;
		//Write file trailer 写文件尾
		if (m_pFormatCtx->pb != nullptr)
		{
			ret = av_write_trailer(m_pFormatCtx);
		}
        
		avio_close(m_pFormatCtx->pb);
		avformat_free_context(m_pFormatCtx);
		m_pFormatCtx = nullptr;
        
        if(nullptr != m_pICbOpaque)
        {
            av_free(m_pICbOpaque);
            m_pICbOpaque = nullptr;
        }
	}
}

// 连接服务
bool CSendDeal::Connect()
{
	if (m_pFormatCtx != nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect m_pFormatCtx != nullptr!");
		assert(false);
		return false;
	}

	int iResult = -1;

	// 分配格式上下文
	iResult = avformat_alloc_output_context2(&m_pFormatCtx, nullptr, "flv", m_PushStreamParam.szPushStreamURL);
	if (iResult < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect avformat_alloc_output_context2 Failed!");
		assert(false);
		return false;
	}

	AVOutputFormat* pOutputFormt = m_pFormatCtx->oformat;
	pOutputFormt->video_codec = CODEC_ID_H264;
	pOutputFormt->audio_codec = CODEC_ID_AAC;

	m_pVideoStream = AddVideoStream(m_pFormatCtx, pOutputFormt->video_codec);
	if (m_pVideoStream == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect AddVideoStream Failed!");
		assert(false);
		return false;
	}

	if (!OpenVideo(m_pVideoStream))
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect OpenVideo Failed!");
		assert(false);
		return false;
	}

	m_pAudioStream = AddAudioStream(m_pFormatCtx, pOutputFormt->audio_codec);
	if (m_pAudioStream == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect AddAudioStream Failed!");
		assert(false);
		return false;
	}

	if (!OpenAudio(m_pAudioStream))
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect OpenAudio Failed!");
		assert(false);
		return false;
	}

	
    AVDictionary* pOptions = nullptr;
    
    m_pICbOpaque = (InterruptCbOpaque*)av_mallocz(sizeof(InterruptCbOpaque));
    if (m_pICbOpaque == nullptr)
    {
        CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect av_mallocz Failed!");
        assert(false);
        return false;
    }
    m_pICbOpaque->timeout = RW_TIMEOUT;
    m_pICbOpaque->start_time = 0;
    m_pFormatCtx->interrupt_callback.callback = interrupt_cb;
    m_pFormatCtx->interrupt_callback.opaque = m_pICbOpaque;
    
	// open file.
	iResult = avio_open2(&m_pFormatCtx->pb, m_PushStreamParam.szPushStreamURL, AVIO_FLAG_WRITE, &m_pFormatCtx->interrupt_callback, &pOptions);
	if (iResult < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect avio_open2 Failed!");
		return false;
	}

	// 设置读写超时
	if (nullptr != m_pFormatCtx->pb && nullptr != m_pFormatCtx->pb->opaque)
	{
		URLContext* p = (URLContext*)m_pFormatCtx->pb->opaque;
		p->rw_timeout = RW_TIMEOUT;
	}
    
	// 建立rtmp会话
	iResult = avformat_write_header(m_pFormatCtx, nullptr);
	if (iResult < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::Connect avformat_write_header Failed!");
		return false;
	}

	m_bConnect = true;
	
	return m_bConnect;
}

// 添加视频流
AVStream* CSendDeal::AddVideoStream(AVFormatContext* apFmtCtx, int aiCodecId)
{
	if (apFmtCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AddVideoStream apFmtCtx == nullptr!");
		assert(false);
		return nullptr;
	}

	AVCodecContext* pCodecCtx = nullptr;
	AVStream* pStream = nullptr;
	AVCodec* pCodec = nullptr;

	// 添加一个新流
	pStream = avformat_new_stream(apFmtCtx, nullptr);
	if (pStream == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AddVideoStream avformat_new_stream Failed!");
		assert(false);
		return nullptr;
	}

	pCodecCtx = pStream->codec;
	/* find the video encoder */
	pCodec = avcodec_find_encoder((AVCodecID)aiCodecId);
	if (pCodec == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AddVideoStream avcodec_find_encoder Failed!");
		assert(false);
		return nullptr;
	}

	avcodec_get_context_defaults3(pCodecCtx, pCodec);

	pCodecCtx->codec_id = (AVCodecID)aiCodecId;
	pCodecCtx->bit_rate = m_PushStreamParam.iVideoBitRate;

	/* resolution must be a multiple of two */
	pCodecCtx->width = m_PushStreamParam.iVideoPushStreamWidth;
	pCodecCtx->height = m_PushStreamParam.iVideoPushStreamHeight;
	/* time base: this is the fundamental unit of time (in seconds) in terms
	of which frame timestamps are represented. for fixed-fps content,
	timebase should be 1/framerate and timestamp increments should be
	identically 1. */
	pCodecCtx->time_base.den = m_PushStreamParam.iVideoFrameRate;
	pCodecCtx->time_base.num = 1;
	pCodecCtx->gop_size = m_PushStreamParam.iVideoFrameSpacing; /* emit one intra frame every twelve frames at most */
	pCodecCtx->pix_fmt = PIX_FMT_YUV420P;
	// some formats want stream headers to be separate
	if (apFmtCtx->oformat->flags & AVFMT_GLOBALHEADER)
	{
		pCodecCtx->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}
    
    pStream->time_base = AV_TIME_BASE_Q;

	return pStream;
}

// 打开视频流
bool CSendDeal::OpenVideo(AVStream* apVideoStream)
{
	if (apVideoStream == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::OpenVideo apVideoStream == nullptr!");
		assert(false);
		return false;
	}

	AVCodec* pCodec = nullptr;
	AVCodecContext* pCodecCtx = nullptr;

	pCodecCtx = apVideoStream->codec;
	/* find the video encoder */
	pCodec = avcodec_find_encoder(pCodecCtx->codec_id);
	if (pCodec == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::OpenVideo avcodec_find_encoder Failed!");
		assert(false);
		return false;
	}

	AVDictionary* pOptions = nullptr;

	/* open the codec */
	if (avcodec_open2(pCodecCtx, pCodec, &pOptions) < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::OpenVideo avcodec_open2 Failed!");
		assert(false);
		return false;
	}

	return true;
}

// 添加音频流
AVStream* CSendDeal::AddAudioStream(AVFormatContext* apFmtCtx, int aiCodecId)
{
	if (apFmtCtx == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AddAudioStream apFmtCtx == nullptr!");
		assert(false);
		return nullptr;
	}

	AVCodecContext* pCodecCtx = nullptr;
	AVStream* pStream = nullptr;
	AVCodec* pCodec = nullptr;

	// 添加一个新流
	pStream = avformat_new_stream(apFmtCtx, nullptr);
	if (pStream == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AddAudioStream avformat_new_stream Failed!");
		assert(false);
		return nullptr;
	}

	pCodecCtx = pStream->codec;
	pCodec = avcodec_find_encoder((AVCodecID)aiCodecId);
	if (pCodec == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::AddAudioStream avcodec_find_encoder Failed!");
		assert(false);
		return nullptr;
	}

	avcodec_get_context_defaults3(pCodecCtx, pCodec);

	pStream->time_base.num = 1;
	pStream->time_base.den = m_PushStreamParam.iAudioSampleRate;
	pCodecCtx->codec_id = (AVCodecID)aiCodecId;
	pCodecCtx->bit_rate = m_PushStreamParam.iAudioBitRate;
	pCodecCtx->sample_fmt = AV_SAMPLE_FMT_S16;
	pCodecCtx->sample_rate = m_PushStreamParam.iAudioSampleRate;
	pCodecCtx->frame_size = 1024;
	pCodecCtx->channels = m_PushStreamParam.iAudioChannels;
	pCodecCtx->channel_layout = av_get_default_channel_layout(m_PushStreamParam.iAudioChannels);
	// some formats want stream headers to be separate
	if (apFmtCtx->oformat->flags & AVFMT_GLOBALHEADER)
	{
		pCodecCtx->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

    pStream->time_base = AV_TIME_BASE_Q;
    
	return pStream;
}

// 打开音频流
bool CSendDeal::OpenAudio(AVStream* apAudioStream)
{
	if (apAudioStream == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::OpenAudio apAudioStream == nullptr!");
		assert(false);
		return false;
	}

	AVCodec* pCodec = nullptr;
	AVCodecContext* pCodecCtx = nullptr;

	pCodecCtx = apAudioStream->codec;
	/* find the audio encoder */
	pCodec = avcodec_find_encoder(pCodecCtx->codec_id);
	if (pCodec == nullptr)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::OpenAudio avcodec_find_encoder Failed!");
		assert(false);
		return false;
	}

	/* open the codec */
	if (avcodec_open2(pCodecCtx, pCodec, nullptr) < 0)
	{
		CLog::GetInstance().Log(ENUM_LOG_LEVEL::enum_Log_Level5, "CSendDeal::OpenAudio avcodec_open2 Failed!");
		assert(false);
		return false;
	}

	return true;
}