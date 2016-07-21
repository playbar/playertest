//
//  RNTNoNetBtn.h
//  Ace
//
//  Created by 靳峰 on 16/3/8.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNTNoNetBtn : UIButton
/**
 *  返回提示按钮
 *
 *  @param imageName 图片名字
 *  @param title     提示文字
 *  @param frame     大小
 *
 *  @return 返回按钮
 */
+(RNTNoNetBtn *)propmptWithImageName:(NSString *)imageName text:(NSString *)title size:(CGRect)frame;
@end
