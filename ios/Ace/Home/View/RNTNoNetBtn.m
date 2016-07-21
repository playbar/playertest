//
//  RNTNoNetBtn.m
//  Ace
//
//  Created by 靳峰 on 16/3/8.
//  Copyright © 2016年 RNT. All rights reserved.
//


#import "RNTNoNetBtn.h"

@interface RNTNoNetBtn()
@property(nonatomic,copy) NSString *name;
@property(nonatomic,copy) NSString *title;
@end

@implementation RNTNoNetBtn
+(UIButton *)propmptWithImageName:(NSString *)imageName text:(NSString *)title size:(CGRect)frame
{
    RNTNoNetBtn *btn = [[RNTNoNetBtn alloc] initWithFrame:frame];
    btn.name = imageName;
    btn.title = title;
    [btn setSubviews];
    return btn;
}

-(void)setSubviews
{
    UIImageView *noNetImg = [[UIImageView alloc] init];
    noNetImg.image =[UIImage imageNamed:self.name];
    [self addSubview:noNetImg];
    [noNetImg mas_makeConstraints:^(MASConstraintMaker *make) {
        if (Height(self)>500) {
            
            make.top.mas_equalTo(self).offset(184*kScreenWidth/375);
            make.centerX.mas_equalTo(self);
        }else{
            make.centerX.mas_equalTo(self);
            make.centerY.mas_equalTo(self).offset(-20);
           
        }
    }];
    
    UILabel *label = [[UILabel alloc] init];
    label.text = self.title;
    label.font = [UIFont systemFontOfSize:15];
    label.textColor = RNTColor_16(0xa8a8a9);
    [self addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(noNetImg.mas_bottom).offset(27*kScreenWidth/375);
        make.centerX.mas_equalTo(noNetImg);
    }];
}

@end
