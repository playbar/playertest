//
//  RNTPlayHostDownLineView.h
//  Ace
//
//  Created by 于传峰 on 16/3/16.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTPlayHostDownLineView : UIImageView

+ (instancetype)showDownLineViewWithUseID:(NSString *)userID selectedClosed:(void(^)())closed selectedAttention:(void(^)())attentioned;

@end
