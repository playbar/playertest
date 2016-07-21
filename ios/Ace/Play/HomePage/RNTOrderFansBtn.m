//
//  RNTOrderFansBtn.m
//  Ace
//
//  Created by 靳峰 on 16/2/28.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTOrderFansBtn.h"

@interface RNTOrderFansBtn ()

//关注
@property(nonatomic,strong) UILabel *orderLab;

//选中的黄色view
@property(nonatomic,strong) UIView *yellowView;
//选中的白色三角
@property(nonatomic,strong) UIImageView *triangle;

@end

@implementation RNTOrderFansBtn

- (instancetype)init
{
    self = [super init];
    if (self) {
        [self setSubviews];
    }
    return self;
}

//重写被选中的方法
-(void)setSelected:(BOOL)selected
{
    [super setSelected:selected];
    self.triangle.hidden = !selected;
    if (selected) {
        self.count.textColor = RNTMainColor;
        self.orderLab.textColor = RNTMainColor;
    }else{
        self.count.textColor = RNTColor_16(0xffffff);
        self.orderLab.textColor = RNTColor_16(0xffffff);
    }
}


#pragma mark - 布局子控件
-(void)setSubviews
{
    
    //关注数
    UILabel *orderCount = [[UILabel alloc] init];
    orderCount.font = [UIFont systemFontOfSize:18];
    orderCount.text = @"0";
    self.count = orderCount;
    [orderCount sizeToFit];
    [self addSubview:orderCount];
    [self.count  mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self);
        make.top.mas_equalTo(self).offset(10);
    }];
    
    //关注lab
    UILabel *orderLab = [[UILabel alloc] init];
    orderLab.font = [UIFont systemFontOfSize:15];
    orderLab.text = @"关注";
    [orderLab sizeToFit];
    self.orderLab = orderLab;
    [self addSubview:orderLab];
    [self.orderLab mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self.count );
        make.top.mas_equalTo(self.count.mas_bottom);
    }];

    //白色三角
    UIImageView *triangle = [[UIImageView alloc] init];
    triangle.image = [UIImage imageNamed:@"homePage_back"];
    [triangle sizeToFit];
    self.triangle = triangle;
    [self addSubview:triangle];
    [self.triangle mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(self );
        make.bottom.mas_equalTo(self);
    }];
    
//    //选中黄色
//    UIView *yellowView = [[UIView alloc] init];
//    yellowView.backgroundColor = RNTMainColor;
//    [self addSubview:yellowView];
//    [yellowView mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.width.mas_equalTo(self);
//        make.height.mas_equalTo(4);
//        make.bottom.left.mas_equalTo(self);
//    }];
//    self.yellowView = yellowView;
}

- (void)setTitleText:(NSString *)titleText
{
    _titleText = [titleText copy];
    self.orderLab.text = titleText;
}
@end
