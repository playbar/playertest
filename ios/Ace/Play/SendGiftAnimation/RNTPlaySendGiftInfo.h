//
//  RNTPlaySendGiftInfo.h
//  Ace
//
//  Created by 于传峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTPlaySendGiftInfo : NSObject

@property (nonatomic, copy) NSString *giftId;
@property (nonatomic, copy) NSString *sendId;
@property (nonatomic, copy) NSString *showID;
@property (nonatomic, copy) NSString *senderName;
@property (nonatomic, copy) NSString *giftName;
@property (nonatomic, strong) UIImage *giftImage;

@property (nonatomic, assign, getter=isCaromSend) BOOL caromSend;
@property (nonatomic, assign) NSUInteger caromCount;

+ (instancetype)giftInfoWithDict:(NSDictionary *)dict;
@end
