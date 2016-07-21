//
//  RNTPlayLikeView.m
//  Ace
//
//  Created by 于传峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayLikeView.h"
#import "RNTPlaySixImageView.h"

@interface RNTPlayLikeView()

@property (strong, nonatomic) NSMutableArray *heartViewArray;
@property (nonatomic, assign) NSInteger leftCount;
@end

@implementation RNTPlayLikeView

- (NSMutableArray *)heartViewArray
{
    if (!_heartViewArray) {
        _heartViewArray = [[NSMutableArray alloc] init];
    }
    return _heartViewArray;
}

#pragma mark - 初始化

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self setup];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setup];
    }
    return self;
}

// Initialization
- (void)setup {
    self.backgroundColor = [UIColor clearColor];
}

- (RNTPlaySixImageView *)createSixView {
    RNTPlaySixImageView *sixView = [[RNTPlaySixImageView alloc] initWithFrame:CGRectMake(0, 0, 1, 1)];
    [self addSubview:sixView];

    [self.heartViewArray addObject:sixView];
    __weak typeof(self) weakSelf = self;
    __weak typeof(sixView) weakSixView = sixView;
    sixView.animationFinished = ^{
        [weakSelf.heartViewArray removeObject:weakSixView];
    };
    return sixView;
}

#pragma mark - 创建

- (CAAnimationGroup *)likeAnimationGroup {
    CAAnimationGroup *animationGroup = [[CAAnimationGroup alloc] init];
    animationGroup.fillMode = kCAFillModeRemoved;
    animationGroup.removedOnCompletion = YES;
    animationGroup.beginTime = CACurrentMediaTime();
    animationGroup.duration = arc4random() % 2 + 1.5;
    animationGroup.timeOffset = 0;
    
    // Position X
    CAKeyframeAnimation *positionXAnimation = [CAKeyframeAnimation animationWithKeyPath:@"position.x"];
    NSMutableArray *values = [NSMutableArray arrayWithObject:@(WIDTH / 2.0)];
    int width = WIDTH - 15;
    for (int i = 0; i < 3; i++) {
        [values addObject:@(arc4random() % width)];
    }
    positionXAnimation.values = values;
    // Positon Y
    CABasicAnimation *positionYAnimation = [CABasicAnimation animationWithKeyPath:@"position.y"];
    positionYAnimation.fromValue = @(HEIGHT);
    positionYAnimation.toValue = @0;
    positionYAnimation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseIn];
    
    // Opacity
    CAKeyframeAnimation *opacityAnimation = [CAKeyframeAnimation animationWithKeyPath:@"opacity"];
    opacityAnimation.values = @[@1, @0.2, @0];
    opacityAnimation.keyTimes = @[@0, @0.8, @1];
    
    // Transform
    CAKeyframeAnimation *scaleAnimation = [CAKeyframeAnimation animationWithKeyPath:@"transform.scale"];
    scaleAnimation.values = @[@1, @35];
    scaleAnimation.keyTimes = @[@0, @0.3];
    
    animationGroup.animations = @[positionXAnimation, positionYAnimation, opacityAnimation, scaleAnimation];
    return animationGroup;
}

#pragma mark - 控制

- (void)fireAnimation
{
    self.leftCount ++;
    if (self.leftCount == 1) {
        [self likeAninating];
    }
}

- (void)stopAnimation {
    if (self.subviews.count <= 0) {
        return;
    }
    __weak typeof(self) weakSelf = self;
    [self.heartViewArray enumerateObjectsUsingBlock:^(RNTPlaySixImageView *sixView, NSUInteger idx, BOOL * _Nonnull stop) {
        [sixView.layer removeAllAnimations];
        [sixView removeFromSuperview];
        [weakSelf.heartViewArray removeAllObjects];
    }];
    self.leftCount = -1;
}

- (void)likeAninating {
    if (self.leftCount <= 0) {
        return;
    }
    if (self.leftCount > 500) {
        self.leftCount = 500;
    }
    RNTPlaySixImageView *sixView = [self createSixView];
//    sixView.type = RNTPlaySixImageViewBlue;
    sixView.type = (RNTPlaySixImageViewType)(arc4random() % 4);

    
    CAAnimationGroup *animationGroup = [self likeAnimationGroup];
    animationGroup.delegate = sixView;
    [sixView.layer addAnimation:animationGroup forKey:nil];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        self.leftCount -- ;
        [self likeAninating];
    });
}

- (void)dealloc
{
    RNTLog(@"--");
}

@end
