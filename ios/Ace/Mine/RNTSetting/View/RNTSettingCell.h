//
//  RNTSettingCell.h
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RNTSettingItem.h"
#import "RNTSettingArrowItem.h"
#import "RNTSettingLevelItem.h"
#import "RNTSettingRightButtonAndLabelItem.h"
#import "RNTSettingRightLabelItem.h"
#import "RNTSettingPhotoItem.h"
#import "RNTSettingRightLabelAndArrowItem.h"
#import "RNTSettingSinatureItem.h"

@interface RNTSettingCell : UITableViewCell

@property (nonatomic,strong)RNTSettingItem *item;

@end
