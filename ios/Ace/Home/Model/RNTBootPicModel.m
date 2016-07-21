//
//  RNTBootPicModel.m
//  Ace
//
//  Created by 靳峰 on 16/5/18.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTBootPicModel.h"

@implementation RNTBootPicModel

- (void)encodeWithCoder:(NSCoder *)aCoder
{
    [aCoder encodeObject:self.title forKey:@"title"];
    [aCoder encodeObject:self.type forKey:@"type"];
    [aCoder encodeObject:self.picUrl forKey:@"picUrl"];
    [aCoder encodeObject:self.linkUrl forKey:@"linkUrl"];
    [aCoder encodeObject:self.showId forKey:@"showId"];
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super init]) {
        self.title = [aDecoder decodeObjectForKey:@"title"];
        self.type = [aDecoder decodeObjectForKey:@"type"];
        self.picUrl = [aDecoder decodeObjectForKey:@"picUrl"];
        self.linkUrl = [aDecoder decodeObjectForKey:@"linkUrl"];
        self.showId = [aDecoder decodeObjectForKey:@"showId"];
    }
    return self;
}

@end
