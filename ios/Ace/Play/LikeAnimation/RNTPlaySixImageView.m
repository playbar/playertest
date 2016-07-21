//
//  RNTPlaySixImageView.m
//  Ace
//
//  Created by 于传峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlaySixImageView.h"

@implementation RNTPlaySixImageView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.layer.anchorPoint = CGPointMake(0.25, 0.5);
        self.backgroundColor = [UIColor clearColor];
        // rotation
        static int i = 1;
        CGFloat angle = (arc4random() % 1000) * 0.001 * M_PI_4 * 0.7;
        angle = angle * i;
        i = -i;
        self.transform = CGAffineTransformMakeRotation(angle);
    }
    return self;
}

- (void)setType:(RNTPlaySixImageViewType)type {
    if (_type == type) return;
    _type = type;
    
    NSString *imageName;
    switch (type) {
        case RNTPlaySixImageViewRed: {
            imageName = @"play_6_red";
            break;
        }
        case RNTPlaySixImageViewBlue: {
            imageName = @"play_6_blue";
            break;
        }
        case RNTPlaySixImageViewPurple: {
            imageName = @"play_6_purple";
            break;
        }
        default: {
            imageName = nil;
            break;
        }
    }
    self.image = [UIImage imageNamed:imageName];
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag {
    self.opaque = YES;
    self.isAnimating = NO;
    if (self.animationFinished) {
        self.animationFinished();
    }
    [self removeFromSuperview];
}

- (void)animationDidStart:(CAAnimation *)anim {
    self.opaque = YES;
    self.isAnimating = YES;
}

//- (UIColor *) randomColor
//{
//    CGFloat hue = ( arc4random() % 256 / 256.0 ); //0.0 to 1.0
//    CGFloat saturation = ( arc4random() % 128 / 256.0 ) + 0.5; // 0.5 to 1.0,away from white
//    CGFloat brightness = ( arc4random() % 128 / 256.0 ) + 0.5; //0.5 to 1.0,away from black
//    return [UIColor colorWithHue:hue saturation:saturation brightness:brightness alpha:1];
//}

- (void)dealloc
{
    RNTLog(@"--");
}
@end
