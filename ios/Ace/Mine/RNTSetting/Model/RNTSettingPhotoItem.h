//
//  RNTSettingPhotoItem.h
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingPhotoItem : RNTSettingItem

@property (nonatomic,copy)NSString *imageUrl;
@property (nonatomic, strong) UIImage *image;
- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title imageUrl:(NSString *)imageUrl;

@end
