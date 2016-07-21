//
//  RNTSettingRightButtonAndLabelItem.h
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingRightButtonAndLabelItem : RNTSettingItem

@property (nonatomic,copy)NSString *labelText;

@property (nonatomic, assign) Class destVC;

@property (nonatomic,copy)NSString *rightBtnTitle;

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title rightBtnTitle:(NSString *)btnTitle labelText:(NSString *)labelText destClass:(Class)destVc;

@property (nonatomic,copy) void (^handler)(Class aClass);

@end
