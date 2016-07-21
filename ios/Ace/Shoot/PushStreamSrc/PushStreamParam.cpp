#include <string.h>
#include "PushStreamParam.h"

STRUCT_PUSH_STREAM_PARAM::STRUCT_PUSH_STREAM_PARAM()
{
	Clear();
}

void STRUCT_PUSH_STREAM_PARAM::Clear()
{
	memset(szPushStreamURL, 0, sizeof(szPushStreamURL));
	iVideoPushStreamWidth = 368;
	iVideoPushStreamHeight = 640;
	iVideoFrameRate = 18;
	iVideoFrameSpacing = 18;
	iVideoBitRate = 600000;
	iVideoMinBitRate = 100000;
	iVideoMaxBitRate = 800000;
	iVideoQuality = 23;
	iVideoEncoderPreset = ENUM_VIDEO_ENCODE_PRESET_TYPE_FASTER;

	iAudioSampleRate = 44100;
	iAudioChannels = 1;
	iAudioBitRate = 96000;

	iReconnectTime = 3000;
}