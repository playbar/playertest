//
//  RNTCaptureStartModel.h
//  Ace
//
//  Created by Ranger on 16/3/12.
//  Copyright © 2016年 RNT. All rights reserved.
//  开播模型

#import <Foundation/Foundation.h>

@interface RNTCaptureStartModel : NSObject

/**  不传 返回用户id */
@property (nonatomic, copy) NSString *userId;
/**  可传 可nil */
@property (nonatomic, copy) NSString *title;
/**  可传 可nil */
@property (nonatomic, copy) NSString *position;
/**  传image对象 返回image data  可为nil*/
@property (nonatomic, strong) id image;// 传image对象 返回image data

@end
