//
//  RNTSettingItem.h
//  Ace
//
//  Created by 周兵 on 16/2/24.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef  void (^ActionBlcok)();

@interface RNTSettingItem : NSObject

@property (nonatomic, copy) ActionBlcok action;

@property (nonatomic, copy) NSString *subTitle;

@property (nonatomic, copy) NSString *icon;

@property (nonatomic, copy) NSString *tilte;

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title;

@end
