//
//  RNTSettingArrowItem.h
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingArrowItem : RNTSettingItem
/**
 *  目标控制器
 */
@property (nonatomic, assign) Class destVC;

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title destClass:(Class)destVc;
@end
