//
//  RNTIntoPlayModel.m
//  Ace
//
//  Created by 靳峰 on 16/3/15.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTIntoPlayModel.h"

@implementation RNTIntoPlayModel
+(instancetype)getModelWithUerMode:(RNTUser *)user andShowModel:(RNTShowListModel *)show
{
    RNTIntoPlayModel *model = [[RNTIntoPlayModel alloc] init];
    
    model.userId = user.userId;
    model.profile = user.profile;
    model.nickName = user.nickName;
    model.signature = user.signature;
    model.title = show.title;
    model.showImg = user.showImg;
    if (show != nil) {
        
        model.showId = show.showId;
        model.downStreanUrl = show.downStreamUrl;
    }else{
        model.showId = user.showId;
        model.downStreanUrl = user.downStreanUrl;
    }
    model.rank = user.rank;

    return model;
}
@end
