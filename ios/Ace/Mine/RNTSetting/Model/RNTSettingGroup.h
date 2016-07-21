//
//  RNTSettingGroup.h
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTSettingGroup : NSObject
/**
 *   头部标题
 */
@property (nonatomic, copy) NSString *headerTitle;
/**
 *  底部标题
 */
@property (nonatomic, copy) NSString *footerTitle;
/**
 *  当前分组中所有行的数据
 */
@property (nonatomic, strong) NSMutableArray *items;
@end
