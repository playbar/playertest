//
//  RNTSettingRightLabelAndArrowItem.h
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingRightLabelAndArrowItem : RNTSettingItem

@property (nonatomic, assign) Class destVC;
@property (nonatomic,copy)NSString *rightLabelText;
- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title rightLabelText:(NSString *)rightLabelText  destClass:(Class)destVc;

@end
