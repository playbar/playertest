//
//  RNTHomeSlider.h
//  选择器
//
//  Created by 靳峰 on 16/5/6.
//  Copyright © 2016年 靳峰. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol  RNTHomeSliderClickDelegate<NSObject>

-(void)sliderBtnClickToChangePage:(UIButton *)btn;

@end

@interface RNTHomeSlider : UIView

@property(nonatomic,weak) id<RNTHomeSliderClickDelegate>  delegate;
//slider应该滑动的距离
@property(nonatomic,assign) CGFloat sliderOffset;
//是否是点击滑动
@property(nonatomic,assign) BOOL isClick;
@end
