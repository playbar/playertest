//
//  RNTIntoPlayModel.h
//  Ace
//
//  Created by 靳峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RNTShowListModel.h"
#import "RNTUser.h"


@interface RNTIntoPlayModel : RNTUser

@property(nonatomic,copy) NSString *title;

+(instancetype)getModelWithUerMode:(RNTUser *)user andShowModel:(RNTShowListModel *)show;

@end
