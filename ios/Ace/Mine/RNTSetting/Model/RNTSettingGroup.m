//
//  RNTSettingGroup.m
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingGroup.h"

@implementation RNTSettingGroup
- (instancetype)init
{
    self = [super init];
    if (self) {
        self.items = [NSMutableArray array];
    }
    return self;
}
@end
