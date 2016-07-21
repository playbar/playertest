//
//  RNTBannerModel.h
//  Ace
//
//  Created by 靳峰 on 16/3/13.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTBannerModel : NSObject
//名称
@property(nonatomic,copy) NSString *title;
//图片地址
@property(nonatomic,copy) NSString *imgUrl;
//跳转地址
@property(nonatomic,copy) NSString *addres;
//跳转类型
@property(nonatomic,copy) NSString *visitType;

@end
