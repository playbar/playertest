//
//  RNTAttentionHeaderView.h
//  Ace
//
//  Created by 靳峰 on 16/3/1.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTAttentionHeaderView : UIView
//提示
@property(nonatomic,copy) NSString *tipStr;
//登录
@property(nonatomic,copy) void(^loginBlock)() ;
@end
