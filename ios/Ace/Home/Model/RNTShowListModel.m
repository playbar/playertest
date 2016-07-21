//
//  RNTShowListModel.m
//  Ace
//
//  Created by 靳峰 on 16/3/6.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTShowListModel.h"
#import "MJExtension.h"

@implementation RNTShowListModel
//更改类型
- (id)mj_newValueFromOldValue:(id)oldValue property:(MJProperty *)property
{
    if (property.type.typeClass == [NSNumber class]) {
        NSString *s =  [NSString stringWithFormat:@"%@", oldValue];
        return s;
    }
    return oldValue;
}

@end


