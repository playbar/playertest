//
//  RNTHomeBannerView.h
//  Ace
//
//  Created by 靳峰 on 16/3/5.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTHomeBannerView : UIView
//数据模型
@property(nonatomic,strong) NSArray *bannerModel;
@property(nonatomic,copy) void(^banerClick)(NSString *title,NSString *url);
@end
