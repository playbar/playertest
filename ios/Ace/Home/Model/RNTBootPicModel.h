//
//  RNTBootPicModel.h
//  Ace
//
//  Created by 靳峰 on 16/5/18.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTBootPicModel : NSObject
//标题
@property(nonatomic,copy) NSString *title;
//图片
@property(nonatomic,copy) NSString *picUrl;
//跳转
@property(nonatomic,copy) NSString *linkUrl;
//0 不挑 1 跳网页 2 跳房间
@property(nonatomic,copy) NSString *type;
//房间Id
@property(nonatomic,copy) NSString *showId;

@end
