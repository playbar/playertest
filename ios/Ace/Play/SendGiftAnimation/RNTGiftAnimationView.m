//
//  GiftAnimationView.m
//  15-ace礼物动画
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 于传峰. All rights reserved.
//

#import "RNTGiftAnimationView.h"
#import "RNTGiftItemView.h"
#import "RNTPlaySendGiftInfo.h"

@interface RNTGiftAnimationView()

@end

@implementation RNTGiftAnimationView

- (NSMutableArray *)items
{
    if (!_items) {
        _items = [[NSMutableArray alloc] init];
    }
    return _items;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        self.userInteractionEnabled = NO;
    }
    return self;
}

- (void)fireItem:(RNTPlaySendGiftInfo *)item
{
    __block BOOL haveSame = NO;
    [self.subviews enumerateObjectsUsingBlock:^(__kindof RNTGiftItemView * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        RNTPlaySendGiftInfo* subInfo = obj.info;
        if ([subInfo.senderName isEqualToString:item.senderName] && subInfo.giftId.intValue == item.giftId.intValue && subInfo.caromCount < item.caromCount) {
            [obj addCaromGiftCount:item.caromCount];
            haveSame = YES;
            *stop = YES;
        }
    }];
    
    if (!haveSame) {
        [self.items addObject:item];
        if (self.subviews.count < 2) {
            [self playAnimation];
        }
    }
}

- (void)playAnimation
{
    if (self.items.count <= 0) {
        return;
    }
    
    RNTPlaySendGiftInfo* info = [self.items objectAtIndex:0];
    __block RNTGiftItemView* itemView;
    
//    __block BOOL haveSame = NO;
//    [self.subviews enumerateObjectsUsingBlock:^(__kindof RNTGiftItemView * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
//        RNTPlaySendGiftInfo* subInfo = obj.info;
//        if ([subInfo.senderName isEqualToString:info.senderName] && subInfo.giftId.intValue == info.giftId.intValue) {
//            [obj addCaromGiftCount:info.caromCount];
//            itemView = obj;
//            haveSame = YES;
//            *stop = YES;
//        }
//    }];
    
//    if (!haveSame) {
        itemView = [self createItemViewWithInfo:info];
        [self addSubview:itemView];
        
        __weak typeof(self) weakSelf = self;
        itemView.dismissBlock = ^{
            if (weakSelf.subviews.count < 2) {
                [weakSelf playAnimation];
            }
        };
        if (self.subviews.count == 1) {
            itemView.topItem = YES;
            itemView.frame = CGRectMake(0, 0, itemWidth, itemHeight);
        }else if (self.subviews.count == 2){
            RNTGiftItemView* lastItemView = [self.subviews firstObject];
            if (lastItemView.isTopItem) {
                itemView.topItem = NO;
                itemView.frame = CGRectMake(0, itemHeight + 5, itemWidth, itemHeight);
                
            }else{
                itemView.topItem = YES;
                itemView.frame = CGRectMake(0, 0, itemWidth, itemHeight);
            }
            
        }
//    }

    [itemView addCaromGiftCount:info.caromCount];
    [self.items removeObjectAtIndex:0];
    [self handleLeftItemsWithItem:info itemView:itemView];
}

- (void)handleLeftItemsWithItem:(RNTPlaySendGiftInfo *)item itemView:(RNTGiftItemView *)itemView{
    if (self.items.count == 0) {
        return;
    }
    RNTPlaySendGiftInfo* info = [self.items objectAtIndex:0];
    if ([info.senderName isEqualToString:item.senderName] && info.giftId.intValue == item.giftId.intValue && info.caromCount > item.caromCount) {
        [itemView addCaromGiftCount:info.caromCount];
        [self.items removeObjectAtIndex:0];
        [self handleLeftItemsWithItem:info itemView:itemView];
    }
    return;
}


- (RNTGiftItemView *)createItemViewWithInfo:(RNTPlaySendGiftInfo *)info
{
    RNTGiftItemView* itemView = [[RNTGiftItemView alloc] init];
    itemView.info = info;
        
    return itemView;
}

- (void)dealloc
{
    RNTLog(@"-----");
}

@end
