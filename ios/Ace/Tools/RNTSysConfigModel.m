//
//  RNTSysConfigModel.m
//  Ace
//
//  Created by 靳峰 on 16/5/19.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSysConfigModel.h"

#define RNTSysConfigModelPath [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject] stringByAppendingPathComponent:@"sysConfig.data"]

@implementation RNTSysConfigModel

+ (BOOL)saveSysConfigModel:(RNTSysConfigModel *)model
{
    return [NSKeyedArchiver archiveRootObject:model toFile:RNTSysConfigModelPath];
}


+ (RNTSysConfigModel *)getSysConfigModel
{
    return [NSKeyedUnarchiver unarchiveObjectWithFile:RNTSysConfigModelPath];
}


- (void)encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeObject:self.ad forKey:@"ad"];
    [aCoder encodeObject:self.bootPic forKey:@"bootPic"];
    [aCoder encodeObject:self.giftVer forKey:@"giftVer"];
    [aCoder encodeObject:self.iosVer forKey:@"iosVer"];
    [aCoder encodeObject:self.isVerify forKey:@"isVerify"];
    [aCoder encodeObject:self.goodVer forKey:@"goodVer"];
    [aCoder encodeObject:self.showTips forKey:@"showTips"];
    [aCoder encodeObject:self.liveShowPushTitle forKey:@"liveShowPushTitle"];
    [aCoder encodeObject:self.shareTitle forKey:@"shareTitle"];
    [aCoder encodeObject:self.shareSummary forKey:@"shareSummary"];

    
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init]) {
        self.ad = [aDecoder decodeObjectForKey:@"ad"];
        self.bootPic = [aDecoder decodeObjectForKey:@"bootPic"];
        self.giftVer = [aDecoder decodeObjectForKey:@"giftVer"];
        self.iosVer = [aDecoder decodeObjectForKey:@"iosVer"];
        self.isVerify = [aDecoder decodeObjectForKey:@"isVerify"];
        self.goodVer = [aDecoder decodeObjectForKey:@"goodVer"];
        self.showTips = [aDecoder decodeObjectForKey:@"showTips"];
        self.liveShowPushTitle =  [aDecoder decodeObjectForKey:@"liveShowPushTitle"];
        self.shareTitle = [aDecoder decodeObjectForKey:@"shareTitle"];
        self.shareSummary = [aDecoder decodeObjectForKey:@"shareSummary"];
    }
    return self;
}
@end
