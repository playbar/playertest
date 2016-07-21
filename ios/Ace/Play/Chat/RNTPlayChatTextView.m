//
//  RNTPlayChatTextView.m
//  Ace
//
//  Created by 于传峰 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayChatTextView.h"
#import "RNTMessageAttachment.h"

@interface RNTPlayChatTextView()<UITextViewDelegate>

@end

@implementation RNTPlayChatTextView


- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.scrollEnabled = NO;
        self.editable = NO;
        self.textContainerInset = UIEdgeInsetsZero;
        self.delegate = self;
        [self setupGesture];
    }
    return self;
}
- (void)setupGesture
{
    UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapAction:)];
    //    tap.delaysTouchesBegan = YES;
    [self addGestureRecognizer:tap];
}

- (void)tapAction:(UITapGestureRecognizer*)tap
{
    self.selectedRange = NSMakeRange(0, 0);
    CGPoint location = [tap locationInView:self];
    
    [self setTouchsWithPoint:location];
    RNTLog(@"getsture - action");
}
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    self.selectedRange = NSMakeRange(0, 0);
    [super touchesBegan:touches withEvent:event];
    RNTLog(@"textView - touchs");
    UITouch* touch = [touches anyObject];
    CGPoint location = [touch locationInView:touch.view];
    
    [self setTouchsWithPoint:location];

}

- (void)setTouchsWithPoint:(CGPoint)location
{
    RNTMessageAttachment* attachment = [self.attributedText attribute:@"range" atIndex:0 effectiveRange:nil];
    
    self.selectedRange = attachment.range;
    NSArray* rects = [self selectionRectsForRange:self.selectedTextRange];
    self.selectedRange = NSMakeRange(0, 0);
    
    BOOL contain = NO;
    for (UITextSelectionRect* textRect in rects) {
        if (CGRectContainsPoint(textRect.rect, location)) {
            contain = YES;
            break;
        }
    }
    
    NSString* userID = (contain && attachment.userID) ? attachment.userID : @"-1";
//    if ([self.delegate respondsToSelector:@selector(playChatTextView:selectedUser:)]) {
//        [self.delegate playChatTextView:self selectedUser:userID];
//    }
    NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
    [center postNotificationName:RNTPlayChatShowInfoNotification object:self userInfo:@{RNTPlayChatShowInfoUserIDKey : userID}];
}

#pragma mark - UITextViewDelegate
- (BOOL)textView:(UITextView *)textView shouldInteractWithURL:(NSURL *)URL inRange:(NSRange)characterRange{
    NSString* urlStr = URL.absoluteString;
    if (urlStr) {
        NSNotificationCenter* center = [NSNotificationCenter defaultCenter];
        [center postNotificationName:RNTPlayChatClickLinkNotification object:self userInfo:@{RNTPlayChatClickLinkUrlStrKey : urlStr}];
    }
    return  NO;
}

@end
