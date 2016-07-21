//
//  RNTPlaySixImageView.h
//  Ace
//
//  Created by 于传峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, RNTPlaySixImageViewType) {
    RNTPlaySixImageViewRed = 1,
    RNTPlaySixImageViewBlue,
    RNTPlaySixImageViewPurple,
};


@interface RNTPlaySixImageView : UIImageView

@property (assign, nonatomic) BOOL isAnimating;
@property (assign, nonatomic) RNTPlaySixImageViewType type;
@property (nonatomic, copy)  void(^animationFinished)();
@end
