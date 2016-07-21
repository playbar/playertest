//
//  RNTAttributedStringContainer.h
//  Weibo
//
//  Created by 于 传峰 on 15/6/16.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNTAttributedStringContainer : NSObject

@property( nonatomic, assign) NSRange range;
@property( nonatomic, copy) NSAttributedString* attrs;

@end
