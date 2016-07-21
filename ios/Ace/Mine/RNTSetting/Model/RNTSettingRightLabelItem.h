//
//  RNTSettingRightLabelItem.h
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingRightLabelItem : RNTSettingItem

@property (nonatomic,copy)NSString *labelText;

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title rightLabelText:(NSString *)labelText;

@end
