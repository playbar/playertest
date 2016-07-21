//
//  RNTPlaySendGiftInfo.m
//  Ace
//
//  Created by 于传峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlaySendGiftInfo.h"

@implementation RNTPlaySendGiftInfo

+ (instancetype)giftInfoWithDict:(NSDictionary *)dict
{
    RNTPlaySendGiftInfo* info = [[self alloc] init];
    
    info.giftId = dict[@"giftId"];
    info.showID = dict[@"showId"];
    info.senderName = dict[@"senderName"];
    info.sendId = dict[@"senderId"];
    info.caromCount = [dict[@"cnt"] integerValue];

    return info;
}

- (void)setGiftId:(NSString *)giftId
{
    _giftId = giftId;
    self.giftImage = [UIImage imageWithContentsOfFile:GiftImagePathWithGiftID(giftId)];
    NSArray* dictArray = [NSArray arrayWithContentsOfFile:GiftDictsPath];
    for (NSDictionary* dict in dictArray) {
        if ([dict.allKeys[0] isEqualToString:giftId]) {
            NSDictionary* infoDict = dict.allValues[0];
            self.giftName = infoDict[@"name"];
            break;
        }
    }
}

@end
