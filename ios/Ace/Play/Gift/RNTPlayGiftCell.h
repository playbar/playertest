//
//  RNTPlayGiftCell.h
//  Ace
//
//  Created by 于传峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
@class RNTPlayGiftModel;

@interface RNTPlayGiftCell : UIView
@property (nonatomic, assign, getter=isSelected) BOOL selected;
- (void)addTarget:(nullable id)target action:(nullable SEL)action;
@property (nonatomic, strong, nonnull) RNTPlayGiftModel *model;
@end
