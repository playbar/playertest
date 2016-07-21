//
//  RNTPlayMessageModel.h
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, PlayMessageModelType) {
    PlayMessageModelTypePlay = 0,
    PlayMessageModelTypeShoot,
};

@class RNTSocketSummary, RNTPlaySendGiftInfo;
#define cellMaxWidth     ([UIScreen mainScreen].bounds.size.width * 0.7)
#define cellHeightMargin 10
#define cellLeftMargin 5
#define cellRightMargin 10
#define textMaxWidth (cellMaxWidth - (cellLeftMargin+cellRightMargin))
@interface RNTPlayMessageModel : NSObject

@property (nonatomic, copy) NSString *senderName;
@property (nonatomic, copy) NSString *senderUserID;
@property (nonatomic, copy) NSString *contentStr;
@property (nonatomic, copy) NSMutableAttributedString *attributedStr;

+ (void)changeMessageModelTypeTo:(PlayMessageModelType)type;

// 聊天
+ (instancetype)chatMessageModelWithDict:(NSDictionary *)dict summary:(RNTSocketSummary *)sum;
// 进房间
+ (instancetype)enterRoomMessageModelWithDict:(NSDictionary *)dict summary:(RNTSocketSummary *)sum;
// 送礼物
+ (instancetype)sendGiftMessageModelWithInfo:(RNTPlaySendGiftInfo *)info;
// 直播提醒
+ (instancetype)playTipsMessageModelNeedLink:(BOOL)need;
// 系统提示
+ (instancetype)systemTipsMessageModelWithCotent:(NSString *)content;
// 点赞 / 关注 、分享、系统提示
+ (instancetype)allTipsMessageModelWithDict:(NSDictionary *)dict summary:(RNTSocketSummary *)sum;

@property (nonatomic, assign) CGSize textSize;

@end
