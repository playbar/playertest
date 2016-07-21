#ifndef PUSH_STREAM_PARAM_H
#define PUSH_STREAM_PARAM_H

// 推流URL缓存最大长度
#define DEF_PUSH_STREAM_URL_MAX_LEN		1024

// 视频编码预设值,编码等级由高到低,ENUM_VIDEO_ENCODE_PRESET_TYPE_ULTRAFAST最高
enum ENUM_VIDEO_ENCODE_PRESET_TYPE : unsigned short
{
	ENUM_VIDEO_ENCODE_PRESET_TYPE_ULTRAFAST = 0,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_SUPERFAST = 1,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_VERYFAST = 2,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_FASTER = 3,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_FAST = 4,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_MEDIUM = 5,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_SLOW = 6,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_SLOWER = 7,
	ENUM_VIDEO_ENCODE_PRESET_TYPE_VERYSLOW = 8,
};

// 推流参数
struct STRUCT_PUSH_STREAM_PARAM
{
	char		szPushStreamURL[DEF_PUSH_STREAM_URL_MAX_LEN];		// 推流URL地址

	int			iVideoPushStreamWidth;								// 视频推流分辨率的宽
	int			iVideoPushStreamHeight;								// 视频推流分辨率的高
	int			iVideoFrameRate;									// 视频推流帧率
	int			iVideoFrameSpacing;									// I帧的帧间距
	int			iVideoBitRate;										// 视频推流码率
	int			iVideoMinBitRate;									// 视频推流最小码率
	int			iVideoMaxBitRate;									// 视频推流最大码率
	int			iVideoQuality;										// 视频质量(数值为0~50)
	int			iVideoEncoderPreset;								// 视频编码预设值,参见ENUM_VIDEO_ENCODE_PRESET_TYPE

	int			iAudioSampleRate;									// 音频采样率
	int			iAudioChannels;										// 音频通道数(数值为1或者2)
	int			iAudioBitRate;										// 音频码率

	int			iReconnectTime;										// 重新连接服务的时间间隔(单位:毫秒)

	STRUCT_PUSH_STREAM_PARAM();
	void Clear();
};

#endif