//
//  RNTSettingSinatureItem.h
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingItem.h"

@interface RNTSettingSinatureItem : RNTSettingItem

@property (nonatomic,copy)NSString *sinature;

@property (nonatomic, assign) Class destVC;

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title sinature:(NSString *)sinature destClass:(Class)destVc;

+ (float)signatureHeight;

@end
