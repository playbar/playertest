//
//  RNTGiftCaromCountLabel.m
//  Ace
//
//  Created by 于传峰 on 16/4/7.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTGiftCaromCountLabel.h"

@implementation RNTGiftCaromCountLabel

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

- (void)drawTextInRect:(CGRect)rect {
     CGSize shadowOffset = self.shadowOffset;
     UIColor *textColor = self.textColor;

     CGContextRef c = UIGraphicsGetCurrentContext();
     CGContextSetLineWidth(c, 1);
     CGContextSetLineJoin(c, kCGLineJoinRound);

     CGContextSetTextDrawingMode(c, kCGTextStroke);
     self.textColor = [UIColor whiteColor];
     [super drawTextInRect:rect];

     CGContextSetTextDrawingMode(c, kCGTextFill);
     self.textColor = textColor;
     self.shadowOffset = CGSizeMake(0, 0);
     [super drawTextInRect:rect];

     self.shadowOffset = shadowOffset;
}

@end
