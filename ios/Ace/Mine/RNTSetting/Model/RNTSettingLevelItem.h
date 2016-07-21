//
//  RNTSettingLevelItem.h
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingLevelItem : RNTSettingItem

@property (nonatomic, copy) NSString *level;
@property (nonatomic, copy) NSString *EXP;

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title level:(NSString *)level EXP:(NSString *)EXP;
@end
