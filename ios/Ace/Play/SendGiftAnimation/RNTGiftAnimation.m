//
//  RNTGiftAnimation.m
//  carAnimation
//
//  Created by 靳峰 on 16/5/4.
//  Copyright © 2016年 靳峰. All rights reserved.
//

#import "RNTGiftAnimation.h"
#import "Masonry.h"

@interface RNTGiftAnimation()

//计数器
@property(nonatomic,assign) int lightCount;
@property(nonatomic,assign) BOOL isCompelet;

@property(nonatomic,strong) UIView *contentView;
//跑车
@property(nonatomic,strong) UIImageView *carBody;
@property(nonatomic,strong) UIImageView *carLight;
@property(nonatomic,strong) UIImageView *carLight2;
@property(nonatomic,strong) UIImageView *carBottom;
@property(nonatomic,strong) UIImageView *track;
//游轮
@property(nonatomic,strong) NSMutableArray *waveArr;
@property(nonatomic,strong) UIImageView *boatBodyImg;
@property(nonatomic,strong) UIImageView *boatShadowImg;
//烟花
@property(nonatomic,strong) CAEmitterLayer  *emitterLayer;

@end

@implementation RNTGiftAnimation

+(RNTGiftAnimation *)giftAnimationWithKind:(RNTGiftKind) gift
{
    RNTGiftAnimation *animationView = [[RNTGiftAnimation alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight)];
    animationView.clipsToBounds = YES;
    
    switch (gift) {
            
        case RNTGiftKindCar:
            [animationView carAnimation];
            break;
            
        case RNTGiftKindFlower:
            [animationView flowerAnimation];
            break;
            
        case RNTGiftKindBoat:
            [animationView boatAnimation];
            break;
            
        case RNTGiftKindFireworkes:
            [animationView flowerWorkesAnimation];
            break;
            
        case RNTGiftKindRocket:
            [animationView rocketAnimation];
            
        default:
            return nil;
            break;
    }
    return animationView;
}

#pragma mark - set方法
-(void)setIsCompelet:(BOOL)isCompelet
{
    _isCompelet = isCompelet;
    
    if (isCompelet) {
        if ([self.delegate respondsToSelector:@selector(animationCompelet)]) {
            [self.delegate performSelector:@selector(animationCompelet)];
        }
    }
}

#pragma mark - 火箭动画
-(void)rocketAnimation
{
    UIImageView *rocketBody = [[UIImageView alloc] init];
    rocketBody.image = [UIImage imageNamed:@"gift_rocket"];
    [rocketBody sizeToFit];
    [self addSubview:rocketBody];
    [rocketBody mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(self);
    }];
    
    UIImageView *fireImgs = [[UIImageView alloc] init];
    //    fireImgs.backgroundColor = [UIColor blackColor];
    NSMutableArray *fireArr = [NSMutableArray array];
    
    for (int i = 0; i<5; i++) {
        NSString *fireName = [NSString stringWithFormat:@"gift_fire_%d",i];
        UIImage *image = [UIImage imageNamed:fireName];
        [fireArr addObject:image];
    }
    fireImgs.animationImages = fireArr;
    fireImgs.animationRepeatCount = 20;
    fireImgs.animationDuration = 2;
    [fireImgs startAnimating];
    [rocketBody addSubview:fireImgs];
    
    [fireImgs mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(rocketBody.mas_bottom).offset(-45);
        make.centerX.mas_equalTo(rocketBody);
        make.size.mas_equalTo(CGSizeMake(208, 1127*0.5));
    }];
    
    
    rocketBody.transform = CGAffineTransformTranslate( rocketBody.transform,0 ,kScreenHeight);
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self flowerWorkesAnimation];
    });
    
    [UIView animateWithDuration:4.8 animations:^{
        rocketBody.transform = CGAffineTransformTranslate( rocketBody.transform,0 , -kScreenHeight*2.0);
    }completion:^(BOOL finished) {
        self.isCompelet = YES;
    }];
    
}

#pragma mark - 烟花效果
-(void)flowerWorkesAnimation
{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(4.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        self.isCompelet = YES;
    });
    
    CAEmitterLayer *fireworksEmitter = [CAEmitterLayer layer];
    CGRect viewBounds = self.layer.bounds;
    fireworksEmitter.emitterPosition = CGPointMake(viewBounds.size.width/2.0, viewBounds.size.height);
    fireworksEmitter.emitterSize	= CGSizeMake(viewBounds.size.width*0.5, 0.0);
    fireworksEmitter.emitterMode	= kCAEmitterLayerOutline;
    fireworksEmitter.emitterShape	= kCAEmitterLayerLine;
    fireworksEmitter.renderMode		= kCAEmitterLayerAdditive;
    fireworksEmitter.seed = (arc4random()%100)+1;
    
    // Create the rocket
    CAEmitterCell* rocket = [CAEmitterCell emitterCell];
    
    rocket.birthRate		= 1;
    rocket.emissionRange	= 0.15 * M_PI;  // some variation in angle
    rocket.velocity			= 580;
    rocket.velocityRange	= 200;
    rocket.yAcceleration	= 75;
    rocket.lifetime			= 1.02;	// we cannot set the birthrate < 1.0 for the burst
    
    //小圆球图片
    rocket.contents			= (id) [[UIImage imageNamed:@"gift_ring"] CGImage];
    rocket.scale			= 0.2;
    rocket.color			= [UIColor yellowColor].CGColor;
    rocket.spinRange		= M_PI;		// slow spin
    
    CAEmitterCell* burst = [CAEmitterCell emitterCell];
    
    burst.birthRate			= 1;		// at the end of travel
    burst.velocity			= 0;        //速度为0
    burst.velocityRange = 100;
    burst.scale				= 2.5;      //大小
    burst.redSpeed			=-1.5;		// shifting
    burst.blueSpeed			=+1.5;		// shifting
    burst.greenSpeed		=+1.0;		// shifting
    burst.lifetime			= 0.55;     //存在时间
    
    
    CAEmitterCell* spark = [CAEmitterCell emitterCell];
    
    spark.birthRate			= 2000;
    spark.velocity			= 155;
    spark.scale = 0.2;
    spark.emissionRange		= 2* M_PI;	// 360 度
    spark.yAcceleration		= 165;		// gravity
    spark.lifetime			= 1;
    spark.contents			= (id) [[UIImage imageNamed:@"gift_ring"] CGImage];
    spark.greenSpeed		=-0.8;
    spark.redSpeed			= 0.4;
    spark.blueSpeed			=-0.8;
    spark.alphaSpeed		=-0.25;
    spark.spin				= 2* M_PI;
    spark.spinRange			= 2* M_PI;
    
    // 3种粒子组合，可以根据顺序，依次烟花弹－烟花弹粒子爆炸－爆炸散开粒子
    fireworksEmitter.emitterCells	= [NSArray arrayWithObject:rocket];
    rocket.emitterCells				= [NSArray arrayWithObject:burst];
    burst.emitterCells				= [NSArray arrayWithObject:spark];
    [self.layer addSublayer:fireworksEmitter];
    self.emitterLayer = fireworksEmitter;

}

#pragma mark - 邮轮效果
-(void)boatAnimation
{
    //海水
    for (int i = 0; i<4; i++) {
        UIImageView *waveImg = [[UIImageView alloc] init];
        waveImg.image = [UIImage imageNamed:[NSString stringWithFormat:@"gift_wave_%d",i]];
        [self addSubview:waveImg];
        waveImg.alpha = 0;
        [waveImg mas_makeConstraints:^(MASConstraintMaker *make) {
            //            make.left.right.mas_equalTo(self);
            make.centerX.mas_equalTo(self);
            make.bottom.mas_equalTo(self);
        }];
        [self.waveArr addObject:waveImg];
    }
    
    //船身
    UIImageView *boatBody = [[UIImageView alloc] init];
//    boatBody.image = [UIImage imageNamed:@"gift_boat_body"];
    NSMutableArray *bodyArr = [NSMutableArray array];
    for (int i = 0; i<3; i++) {
        NSString *bodyName = [NSString stringWithFormat:@"gift_boat_body_%d",i];
        UIImage *image = [UIImage imageNamed:bodyName];
        [bodyArr addObject:image];
    }
    boatBody.animationImages = bodyArr;
    boatBody.animationRepeatCount = 20;
    boatBody.animationDuration = 0.3;
    [boatBody startAnimating];

    
    [self addSubview:boatBody];
    [boatBody mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self);
        make.bottom.mas_equalTo(self).offset(-110);
        if (iPhone5) {
            make.size.mas_equalTo(CGSizeMake(375*0.8, 175*0.8));
        }else if(kScreenWidth > 400){
            
            make.size.mas_equalTo(CGSizeMake(375*1.1, 175*1.1));
        }else{
            
            make.size.mas_equalTo(CGSizeMake(375, 175));
        }
    }];
    self.boatBodyImg = boatBody;
    
    //船影
    UIImageView *boatShadow = [[UIImageView alloc] init];
    boatShadow.image = [UIImage imageNamed:@"gift_boat_shadow"];
    [self addSubview:boatShadow];
    [boatShadow mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(boatBody).offset(60);
        make.bottom.mas_equalTo(boatBody).offset(60);
    }];
    self.boatShadowImg = boatShadow;
    
    [self boatArriveAnimation];
    [self waveAnimation];
}

//船驶入
-(void)boatArriveAnimation
{
    self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, -300, -200);
    self.boatShadowImg.transform = CGAffineTransformTranslate(self.boatShadowImg.transform, -300, -200);
    [UIView animateWithDuration:1.8 delay:0 options:UIViewAnimationOptionCurveEaseOut animations:^{
        if (iPhone5) {
            self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, 300, 220 );
            
        }else{
            
            self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, 300, 220 );
        }
        self.boatShadowImg.transform = CGAffineTransformTranslate(self.boatShadowImg.transform, 300, 220);
    } completion:^(BOOL finished) {
        [self boatStayAnimation];
        [self boatShadowImg];
        
    }];
    
}

//船停住
-(void)boatStayAnimation
{
    //向下
    [UIView animateWithDuration:0.7 delay:0 options:UIViewAnimationOptionCurveLinear animations:^{
        self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, 0, 15);
        self.boatShadowImg.transform = CGAffineTransformTranslate(self.boatShadowImg.transform, 0, 15);
    } completion:^(BOOL finished) {
        //向上
        [UIView animateWithDuration:0.7 animations:^{
            self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, 0, -15);
            self.boatShadowImg.transform = CGAffineTransformTranslate(self.boatShadowImg.transform, 0, -15);
        }completion:^(BOOL finished) {
            //向下
            [UIView animateWithDuration:0.7 animations:^{
                self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, 0, 10);
                self.boatShadowImg.transform = CGAffineTransformTranslate(self.boatShadowImg.transform, 0, -15);
            }completion:^(BOOL finished) {
                NSLog(@"%@",NSStringFromCGPoint(self.carBody.layer.anchorPoint));
                [self boatLeaveAnimation];
            }];
        }];
    }];
}

//船离开
-(void)boatLeaveAnimation
{
    
    [UIView animateWithDuration:1.8 animations:^{
        //        NSLog(@"%@",NSStringFromCGPoint(self.carBody.layer.anchorPoint));
        
        self.boatBodyImg.transform = CGAffineTransformTranslate(self.boatBodyImg.transform, 300, 200);
        self.boatShadowImg.transform = CGAffineTransformTranslate(self.boatShadowImg.transform, 300, 200);
    }];
}
//影子动画
-(void)boatShadowAnimation
{
    CAKeyframeAnimation *scaleAnimation = [CAKeyframeAnimation animationWithKeyPath:@"transform.scale"];
    scaleAnimation.values = @[@0.8, @1.5,@0.8,@1.3];
    scaleAnimation.keyTimes = @[@0, @0.3,@0.6,@1];
    scaleAnimation.duration = 2.1;
    
    [self.boatShadowImg.layer addAnimation:scaleAnimation forKey:nil];
}

//海水动画
-(void)waveAnimation
{
    // 透明度
    CAKeyframeAnimation *opacityAnimation = [CAKeyframeAnimation animationWithKeyPath:@"opacity"];
    
    opacityAnimation.keyTimes = @[@0,@0.35,@0.7,@1];
    opacityAnimation.values = @[@0,@1,@1,@0];
    opacityAnimation.duration =5.7;
    
    CGFloat distance = 20;
    if (kScreenWidth >400) {
        distance = 15;
    }
    
    //位移X1
    CGFloat width = kScreenWidth*0.5;
    CAKeyframeAnimation *positionXAnimation = [CAKeyframeAnimation animationWithKeyPath:@"position.x"];
    positionXAnimation.keyTimes = @[@0,@0.5,@1];
    positionXAnimation.values = @[@(-distance+width),@(distance+width),@(-distance+width)];
    positionXAnimation.duration =1;
    positionXAnimation.repeatCount = 30;
    
    //位移X2
    CAKeyframeAnimation *positionXAnimation2 = [CAKeyframeAnimation animationWithKeyPath:@"position.x"];
    positionXAnimation2.keyTimes = @[@0,@0.5,@1];
    positionXAnimation2.values = @[@(distance+width),@(-distance+width),@(distance+width)];
    positionXAnimation2.duration =1;
    positionXAnimation2.repeatCount = 30;
    
    for (int i = 0; i<4; i++) {
        
        UIImageView *waveImg = self.waveArr[i];
        
        [waveImg.layer addAnimation:opacityAnimation forKey:nil];
        
        if (i == 0 || i==2) {
            [waveImg.layer addAnimation:positionXAnimation forKey:nil];
        }else{
            [waveImg.layer addAnimation:positionXAnimation2 forKey:nil];
        }
    }
    
    //位移Y
    [UIView animateWithDuration:1.8 delay:3.9 options:UIViewAnimationOptionCurveLinear animations:^{
        for (UIImageView *waveImg in self.waveArr) {
            waveImg.transform = CGAffineTransformTranslate(waveImg.transform, 0, 80);
        }
    } completion:^(BOOL finished) {
        //        [self removeFromSuperview];
        self.isCompelet = YES;
    }];
    
}


#pragma mark - 花束动画
//花束出现
-(void)flowerAnimation
{
    UIImageView *flowerImg = [[UIImageView alloc] init];
    flowerImg.image = [UIImage imageNamed:@"gift_flower"];
    [self addSubview:flowerImg];
    [flowerImg mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.bottom.right.mas_equalTo(self);
        if (iPhone5) {
            make.height.mas_equalTo(300);
        }else{
            
            make.height.mas_equalTo(335);
        }
    }];
    
    self.lightCount = 0;
    
    
    flowerImg.transform = CGAffineTransformTranslate(flowerImg.transform, 0,100);
    flowerImg.alpha = 0.6;
    [UIView  animateWithDuration:1.6 animations:^{
        flowerImg.transform = CGAffineTransformTranslate(flowerImg.transform, 0, -100);
        flowerImg.alpha = 1;
    } completion:^(BOOL finished) {
        [self starAnimation:flowerImg];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self flowerDismiss:flowerImg];
        });
        
    }];
    
    
}
//星星闪光
-(void)starAnimation:(UIImageView *)flowerImg
{
    
    
    NSArray *positionX  = @[@60.1,@100.1,@170.1,@240.1,@240.1,@300.1];
    NSArray *positionY  = @[@40.1,@90.1,@160.1,@60.1,@120.1,@180.1];
    NSArray *scaleArray = @[@0.6,@0.9,@0.8,@0.6,@1,@0.8 ];
    NSArray *timeOffsetArray = @[@0.4,@0.3,@0.2,@0.2,@0,@0.1];
    
    UIImageView *starImg = [[UIImageView alloc] init];
    starImg.image = [UIImage imageNamed:@"gift_star"];
    [starImg sizeToFit];
    CGSize size = starImg.frame.size;
    [flowerImg addSubview:starImg];
    
    CGFloat alpha = arc4random_uniform(8)*0.1+0.2;
    CGFloat radius = arc4random_uniform(M_PI*2);
    CGFloat timeOffset =  [timeOffsetArray[self.lightCount] floatValue];
    CGFloat x = [positionX[self.lightCount] floatValue];
    CGFloat y =[positionY[self.lightCount] floatValue];
    CGFloat scale = [scaleArray[self.lightCount] floatValue];
    
    self.lightCount++;
    
    starImg.frame = CGRectMake(0, 0,size.width*scale , size.height*scale);
    starImg.center = CGPointMake(x, y);
    starImg.transform = CGAffineTransformMakeRotation(radius);
    starImg.alpha = alpha;
    
    CAKeyframeAnimation *scaleAnimation = [CAKeyframeAnimation animationWithKeyPath:@"transform.scale"];
    scaleAnimation.values = @[@1, @2,@1];
    scaleAnimation.keyTimes = @[@0, @0.5,@1];
    
    CAKeyframeAnimation *alphaAnimation = [CAKeyframeAnimation animationWithKeyPath:@"opacity"];
    alphaAnimation.values = @[@0, @1,@0];
    alphaAnimation.keyTimes = @[@0, @0.5,@1];
    
    CAAnimationGroup *animationGroup = [CAAnimationGroup animation];
    animationGroup.fillMode = kCAFillModeRemoved;
    animationGroup.removedOnCompletion = YES;
    animationGroup.duration = 0.8;
    animationGroup.timeOffset = timeOffset;
    animationGroup.repeatCount =10;
    animationGroup.animations = @[scaleAnimation,alphaAnimation];
    [starImg.layer addAnimation:animationGroup forKey:nil];
    
    if (self.lightCount<=5) {
        
        [self starAnimation:flowerImg];
    }
}

//花束消失
-(void)flowerDismiss:(UIImageView *)flowerImg
{
    [UIView animateWithDuration:0.8 animations:^{
        flowerImg.alpha = 0;
    } completion:^(BOOL finished) {
        //        [self removeFromSuperview];
        self.isCompelet = YES;
    }];
}

#pragma mark - 跑车动画
-(void)carAnimation
{
    self.lightCount = 0;
    
    UIView *contentView  = [[UIView alloc] initWithFrame:self.bounds];
    self.contentView = contentView;
    [self addSubview:contentView];
    
    //车身
    UIImageView *carBody = [[UIImageView alloc] init];
    carBody.image = [UIImage imageNamed:@"gift_carBody"];
    [carBody sizeToFit];
    [contentView addSubview:carBody];
    self.carBody = carBody;
    if (kScreenWidth>=400) {
        self.carBody.transform = CGAffineTransformMakeScale(1.4, 1.4);
    }
    self.carBody.layer.anchorPoint = CGPointMake(0.8, 0.5);
    carBody.center = self.center;
    
    NSMutableArray *lightArr = [NSMutableArray array];
    for (int i = 0; i<3; i++) {
        UIImage *image = [UIImage imageNamed:[NSString stringWithFormat:@"gift_carLight_%d",i]];
        UIImage *imagePlaceHolder = [UIImage imageNamed:@"gift_carBody"];
        [lightArr addObject:image];
        [lightArr addObject:imagePlaceHolder];
    }
    carBody.animationImages = lightArr;
    carBody.animationDuration = 0.5;
    carBody.animationRepeatCount = 2;
    
    //车轮
    UIImageView *carWheel = [[UIImageView alloc] init];
    carWheel.image = [UIImage imageNamed:@"gift_carBottom"];
    [carWheel sizeToFit];
    self.carBottom = carWheel;
    [contentView addSubview:carWheel];
    if (kScreenWidth>=400) {
        self.carBottom.transform = CGAffineTransformMakeScale(1.4, 1.4);
    }
    [carWheel mas_makeConstraints:^(MASConstraintMaker *make) {
        if (kScreenWidth>=400) {
            make.bottom.mas_equalTo(carBody.mas_bottom).offset(-18);
            make.left.mas_equalTo(carBody.mas_left).offset(57);
        }else{
            make.bottom.mas_equalTo(carBody.mas_bottom).offset(2);
            make.left.mas_equalTo(carBody.mas_left).offset(7);
        }
    }];
    [contentView bringSubviewToFront:carBody];
    
    //车灯
    UIImageView *carLight = [[UIImageView alloc] init];
    carLight.image = [UIImage imageNamed:@"gift_carLight"];
    [contentView addSubview:carLight];
    [carLight mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(carBody.mas_left).offset(15);
        make.bottom.mas_equalTo(carBody.mas_bottom).offset(20);
    }];
    carLight.hidden = YES;
    [contentView bringSubviewToFront:carBody];
    self.carLight = carLight;
    
    UIImageView *carLight2 = [[UIImageView alloc] init];
    carLight2.image = [UIImage imageNamed:@"gift_carLight"];
    [contentView addSubview:carLight2];
    [carLight2 mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(carBody.mas_left).offset(60);
        make.bottom.mas_equalTo(carBody.mas_bottom).offset(34);
    }];
    carLight2.hidden = YES;
    [contentView bringSubviewToFront:carLight2];
    self.carLight2 = carLight2;
    
    self.contentView.center = CGPointMake(kScreenWidth*1.5, 0);
    
    [self carArrive];
    [self elasticity];
}

//驶入
-(void)carArrive
{
    
    [UIView animateWithDuration:1.2 delay:0 options:UIViewAnimationOptionCurveEaseOut animations:^{
        self.contentView.transform = CGAffineTransformTranslate(self.contentView.transform, -kScreenWidth+100, 200);
    } completion:^(BOOL finished) {
        [self carTrackResult];
    }];
}

//弹性
-(void)elasticity
{
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.9 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        
        CAKeyframeAnimation* rotationAnimation =
        [CAKeyframeAnimation animationWithKeyPath:@"transform.rotation"];
        rotationAnimation.keyTimes = @[@0,@0.2, @0.4,@0.5,@0.8,@1];
        rotationAnimation.values = @[@0,@(-0.02),@(0) ,@(-0.01),@(-0.005),@0];
        rotationAnimation.duration = 0.7;
        [self.carBody.layer addAnimation:rotationAnimation forKey:nil];
    });
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.8 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self carLightResult];
        [self.carBody startAnimating];
    });
}

//灯光
-(void)carLightResult
{
    self.lightCount++;
    
    if (self.lightCount>=4) {
        [self  carLeave];
        return;
    }
    
    self.carLight.hidden = NO;
    self.carLight2.hidden = NO;
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        self.carLight.hidden = YES;
        self.carLight2.hidden = YES;
    });
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.4 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self carLightResult];
    });
}

//跑道
-(void)carTrackResult
{
    UIImageView *track = [[UIImageView alloc] init];
    track.image = [UIImage imageNamed:@"gift_carTrack"];
    [track sizeToFit];
    self.track = track;
    track.transform = CGAffineTransformScale(track.transform , 1.2, 1.2);
    [self addSubview:track];
    if (kScreenWidth>=400){
        track.transform = CGAffineTransformScale(track.transform , 1.4, 1.4);
    }
    [track mas_makeConstraints:^(MASConstraintMaker *make) {
        if (kScreenWidth>=400) {
            make.bottom.mas_equalTo(self.carBody).offset(-28);
            make.right.mas_equalTo(self.carBody).offset(-36);
        }else{
            make.bottom.mas_equalTo(self.carBody).offset(-10);
            make.right.mas_equalTo(self.carBody).offset(-10);
        }
    }];
    [self bringSubviewToFront:self.contentView];
    
}

//车离开
-(void)carLeave
{
    [UIView animateWithDuration:1.2 delay:0 options:UIViewAnimationOptionCurveEaseOut animations:^{
        self.contentView.transform = CGAffineTransformTranslate(self.contentView.transform, -kScreenWidth, 200);
        self.track.alpha = 0;
    } completion:^(BOOL finished) {
        //        [self removeFromSuperview];
        self.isCompelet = YES;
    }];
    
}

#pragma mark - 懒加载
-(NSMutableArray *)waveArr
{
    if (!_waveArr) {
        _waveArr = [NSMutableArray array];
    }
    return _waveArr;
}
@end
