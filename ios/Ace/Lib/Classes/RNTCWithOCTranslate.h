//
//  RNTCWithOCTranslate.h
//  Ace
//
//  Created by 靳峰 on 16/5/31.
//  Copyright © 2016年 RNT. All rights reserved.
//

#include <string.h>

class  RNTCWithOCTranslate
{
public:
    static const char * cancelSpace(char *strName);
    // 函数说明
    // 功能：当一个礼物的骨骼动画播放结束，调用此函数
    // 参数：strResPath 礼物动画资源在设备上的存储路径
    static void CallBackBySkeletonLayer(std::string strResPath);
};
