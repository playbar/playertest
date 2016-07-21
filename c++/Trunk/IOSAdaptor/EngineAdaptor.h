//
//  EngineAdaptor.h
//  t_allLibrary
//
//  Created by Ranger on 16/3/7.
//  Copyright © 2016年 RedNovo. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface EngineAdaptor : NSObject


+ (EngineAdaptor *)shareInstance;

/**
 *  @author Ranger, 16-03-10 21:03
 *  初始化engine
 *  @param adr 推流地址
 */
- (bool)setupEngine:(char *)adr;

/**
 *  @author Ranger, 16-03-19 14:03
 *  设置暂停
 *  @param isPause 暂停
 */
 - (void)setPause:(BOOL)isPause;
 
/**
 *  @author Ranger, 16-03-19 14:03
 *  设置静音
 *  @param isMute 静音
 */
- (void)setMute:(BOOL)isMute;

/**
 *  @author Ranger, 16-03-19 14:03
 *  推送数据
 *  @param buffer 数据 bufferSize 数据大小 bufferType 数据类型
 */
- (bool)encodeWith:(uint8_t *)buffer bufferSize:(int)bufferSize bufferType:(int)bufferType;

/**
 *  @author Ranger, 16-03-19 14:03
 *  停止推流
 */
- (void)closeEngine;

@end
