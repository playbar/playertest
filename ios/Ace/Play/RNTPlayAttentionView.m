//
//  RNTPlayAttentionView.m
//  Ace
//
//  Created by 于传峰 on 16/5/11.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayAttentionView.h"
#import "RNTPlayNetWorkTool.h"
#import "RNTUserManager.h"

@interface RNTPlayAttentionView()
@property (nonatomic, weak) UIButton *attentionBtn;
@property (nonatomic, strong) NSTimer *timer;

@property (nonatomic, assign) CGFloat duration;

@property (nonatomic, weak) UIImageView *solidHeart;
@property (nonatomic, weak) UIImageView *hollowHeart1;
@property (nonatomic, weak) UIImageView *hollowHeart2;

@property (nonatomic, assign) BOOL stopBefore;
@property (nonatomic, assign) int type;
@end

@implementation RNTPlayAttentionView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
//        self.backgroundColor = [UIColor redColor];
        self.hidden = YES;
        
        UIImageView* solidHeart = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_attention_yes"]];
        [self addSubview:solidHeart];
        self.solidHeart = solidHeart;
        
        UIImageView* hollowHeart1 = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_attention_no"]];
        [self addSubview:hollowHeart1];
        self.hollowHeart1 = hollowHeart1;
        
        UIImageView* hollowHeart2 = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"play_attention_no"]];
        [self addSubview:hollowHeart2];
        self.hollowHeart2 = hollowHeart2;
        
        self.duration = 0.4;
        
        UIButton* attentionBtn = [[UIButton alloc] init];
        [self addSubview:attentionBtn];
        self.attentionBtn = attentionBtn;
        [attentionBtn makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        
        [attentionBtn addTarget:self action:@selector(attentionBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    }
    return self;
}

- (void)setUserID:(NSString *)userID
{
    _userID = userID;
    
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        self.hidden = YES;
    }else{
        [self getUserData];
    }
}

- (void)attentionBtnClicked:(UIButton *)button
{
//    self.hidden = YES;
    [self clicked];
    RNTUserManager* manager = [RNTUserManager sharedManager];
    [RNTPlayNetWorkTool followUserWithCurrentUserID:manager.user.userId followedUserID:self.userID getSuccess:^(BOOL status) {
        if (status) {
            [MBProgressHUD showSuccess:@"关注成功"];
        }else{
            [MBProgressHUD showError:@"关注失败"];
        }
    }];
}

- (void)setHidden
{
    self.hidden = YES;
    self.stopBefore = YES;
}
- (void)setHidden:(BOOL)hidden
{
    [super setHidden:hidden];
    if (hidden) {
        [self.timer invalidate];
        self.timer = nil;
    }else{
        [self setBeforeHeartWithType:0];
        [self startAnimation];
        NSTimer* timer = [NSTimer scheduledTimerWithTimeInterval:60 target:self selector:@selector(setHidden) userInfo:nil repeats:NO];
        [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
        self.timer = timer;
    }
    
}


- (void)getUserData
{
    if (self.userID.length == 0 || self.userID == nil || self.userID.intValue == -1) return;
    
    [RNTPlayNetWorkTool getUserInfoWithUserID:self.userID getSuccess:^(NSDictionary *dict, BOOL attentionState) {
        if (attentionState) {
            self.hidden = YES;
        }else{
            self.hidden = NO;
        }
    }];
}


- (void)setBeforeHeartWithType:(int)type{
    switch (type) {
        case 0:
            self.solidHeart.transform = CGAffineTransformMakeScale(0.2, 0.2);
            self.solidHeart.alpha = 0.0;
            
            self.hollowHeart1.transform = CGAffineTransformIdentity;
            self.hollowHeart1.alpha = 1.0;
            
            self.hollowHeart2.transform = CGAffineTransformMakeScale(1.1, 1.1);
            self.hollowHeart2.alpha = 0.0;
            
            self.duration = 0.4;
            break;
        case 1:
            self.solidHeart.transform = CGAffineTransformMakeScale(1.0, 1.0);
            self.solidHeart.alpha = 0.6;
            
            self.hollowHeart1.transform = CGAffineTransformIdentity;
            self.hollowHeart1.alpha = 1.0;
            
            self.hollowHeart2.transform = CGAffineTransformMakeScale(1.1, 1.1);
            self.hollowHeart2.alpha = 0.0;
            self.duration = 0.2;
            break;
        case 2:
            self.solidHeart.transform = CGAffineTransformMakeScale(1.1, 1.1);
            self.solidHeart.alpha = 0.6;
            
            self.hollowHeart1.transform = CGAffineTransformMakeScale(1.1, 1.1);;
            self.hollowHeart1.alpha = 1.0;
            
            self.hollowHeart2.transform = CGAffineTransformMakeScale(1.1, 1.1);
            self.hollowHeart2.alpha = 0.5;
            
            self.duration = 0.2;
            break;
        case 3:
            self.solidHeart.transform = CGAffineTransformMakeScale(0.98, 0.98);
            self.solidHeart.alpha = 0.0;
            
            self.hollowHeart1.transform = CGAffineTransformMakeScale(0.98, 0.98);;
            self.hollowHeart1.alpha = 1.0;
            
            self.hollowHeart2.transform = CGAffineTransformMakeScale(1.1, 1.1);
            self.hollowHeart2.alpha = 0.5;
            self.duration = 0.3;
            break;
        case 4:
            self.solidHeart.transform = CGAffineTransformMakeScale(0.98, 0.98);
            self.solidHeart.alpha = 0.0;
            
            self.hollowHeart1.transform = CGAffineTransformMakeScale(1.0, 1.0);;
            self.hollowHeart1.alpha = 1.0;
            
            self.hollowHeart2.transform = CGAffineTransformMakeScale(1.35, 1.35);
            self.hollowHeart2.alpha = 0.0;
            self.duration = 0.2;
            break;
        case -1:
            self.solidHeart.transform = CGAffineTransformMakeScale(0.01, 0.01);
            self.solidHeart.alpha = 1.0;
            
            self.hollowHeart1.transform = CGAffineTransformMakeScale(0.01, 0.01);;
            self.hollowHeart1.alpha = 0.0;
            
            self.hollowHeart2.transform = CGAffineTransformMakeScale(0.01, 0.01);
            self.hollowHeart2.alpha = 0.0;
            self.duration = 0.4;
            break;
            
            
        default:
            break;
    }
}

//- (void)didMoveToSuperview{
//    [super didMoveToSuperview];
//    if (self.superview) {
//        [self setBeforeHeartWithType:0];
//        [self startAnimation];
//    }
//}

- (void)startAnimation{
    
    if (self.stopBefore) {
        return;
    }
    if (_type == 5) {
        _type = 0;
    }
    
    [UIView animateWithDuration:self.duration animations:^{
        RNTLog(@"%zd--", _type);
        [self setBeforeHeartWithType:_type];
    } completion:^(BOOL finished) {
        ++_type;
        [self startAnimation];
    }];
}



- (void)setFrame:(CGRect)frame
{
    [super setFrame:frame];
    CGPoint center = CGPointMake(frame.size.width * 0.5, frame.size.height * 0.5);
    for (UIView* subView in self.subviews) {
        subView.center = center;
    }
}

- (void)clicked
{
    self.stopBefore = YES;
    for (UIView* subView in self.subviews) {
        [subView.layer removeAllAnimations];
    }
    [self setBeforeHeartWithType:1];
    
    [UIView animateWithDuration:0.2 animations:^{
        [self setBeforeHeartWithType:-1];
    } completion:^(BOOL finished) {
        [self startAfterAnimation];
    }];
}

- (void)startAfterAnimation{
    self.solidHeart.transform = CGAffineTransformIdentity;
    
    CAKeyframeAnimation* anima = [CAKeyframeAnimation animation];
    anima.keyPath = @"transform";
    CATransform3D trans0 = CATransform3DMakeRotation(0, 0, 1, 0);
    trans0 = CATransform3DScale(trans0, 0, 0, 1);
    NSValue* value0 = [NSValue valueWithCATransform3D:trans0];
    
    CATransform3D trans1 = CATransform3DMakeRotation(360*2, 0, 1, 0);
    trans1 = CATransform3DScale(trans1, 2, 2, 1);
    NSValue* value1 = [NSValue valueWithCATransform3D:trans1];
    
    CATransform3D trans2 = CATransform3DMakeRotation(0, 0, 1, 0);
    trans2 = CATransform3DScale(trans2, 0, 0, 1);
    NSValue* value2 = [NSValue valueWithCATransform3D:trans2];
    
    anima.values = @[value0, value1, value2];
    anima.repeatCount = 1;
    anima.duration = 1.0;
    anima.fillMode = kCAFillModeForwards;
    anima.removedOnCompletion = NO;
    anima.delegate = self;
    [self.solidHeart.layer addAnimation:anima forKey:nil];
}

- (void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag{
    self.hidden = YES;
    [self removeFromSuperview];
}

- (void)removeFromSuperview
{
    [super removeFromSuperview];
    self.stopBefore = YES;
    [self.timer invalidate];
    self.timer = nil;
    RNTLog(@"removeFromSuperview");
}

- (void)dealloc{
    self.stopBefore = YES;
}

@end
