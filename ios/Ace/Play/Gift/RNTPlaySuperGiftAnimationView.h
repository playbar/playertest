//
//  RNTPlaySuperGiftAnimationView.h
//  Ace
//
//  Created by 于传峰 on 16/5/12.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTPlaySuperGiftAnimationView : UIView

-(void)remove;
- (void)fireSuperAnimationWithGiftID:(NSString *)giftID;

SingletonH(playSuperGiftAnimationView)

@end
