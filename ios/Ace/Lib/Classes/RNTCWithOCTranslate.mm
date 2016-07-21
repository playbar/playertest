//
//  RNTCWithOCTranslate.m
//  Ace
//
//  Created by 靳峰 on 16/5/31.
//  Copyright © 2016年 RNT. All rights reserved.
//

#include "RNTCWithOCTranslate.h"
#import <Foundation/Foundation.h>

const char * RNTCWithOCTranslate::cancelSpace(char *strName)
{
    NSString *strNameOC = [NSString  stringWithUTF8String:strName];
    strNameOC = [strNameOC  stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    strNameOC = [strNameOC  stringByReplacingOccurrencesOfString:@" " withString:@""];
    strNameOC = [strNameOC  stringByReplacingOccurrencesOfString:@"\r" withString:@""];
    
    const char *strNameC = [strNameOC UTF8String];
    return  strNameC;

}


void RNTCWithOCTranslate::CallBackBySkeletonLayer(std::string strResPath)
{
    // 在此添加处理代码
    NSNotificationCenter* notiCenter = [NSNotificationCenter defaultCenter];
    [notiCenter postNotificationName:RNTGIftAnimationEnd object:nil];
    
    RNTLog(@"big man--- , CallBackBySkeletonLayer...");
}




