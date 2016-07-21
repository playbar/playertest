//
//  RNTPlayShareInfo.h
//  Ace
//
//  Created by 于传峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTUser.h"

@class RNTIntoPlayModel;
@interface RNTPlayShareInfo : RNTUser

//@property (nonatomic, copy) NSString *showID;
@property (nonatomic, strong) UIImage *image;
@property (nonatomic, strong) RNTIntoPlayModel *playInfo;

@end
