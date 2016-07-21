//
//  UIImage+RNT.h
//  Weibo
//
//  Created by 杜 维欣 on 15/7/7.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (RNT)
- (NSData*)imageDataWithImage;
+ (UIImage *)circleImageWithImage:(UIImage *)image borderWidth:(CGFloat)borderWidth borderColor:(UIColor *)borderColor;

/**
 *  处理image(本地)
 *  @param type    类型(1是黑白处理, 0不处理)
 *
 *  @return 处理后的image
 */
- (UIImage *)grayscaleWithType:(int)type;

/**
 *  处理image(网络)
 *
 *  @param imageUrl image的地址
 *  @param type    类型(1是黑白处理, 0不处理)
 *
 *
 */
+ (void)grayImageWithUrl:(NSURL *)imageUrl type:(int)type finish:(void (^)(UIImage* image))image;

/**
 *  由颜色生成图片(只能用于纯背景色)
 *
 *  @param color 颜色
 *
 *  @return 图片
 */
+ (UIImage *)imageWithColor:(UIColor *)color;
/**
 *  根据图片名称创建一张拉伸不变形的图片
 *
 *  @param imageName 需要创建的图片名称
 *
 *  @return 拉伸不变形的图片
 */
+ (instancetype)resizableImageWithName:(NSString *)imageName;
/**
 *  根据图片名称创建一张适配后的图片
 *
 *  @param imageName 需要创建的图片名称
 *
 *  @return 适配后的图片
 */
+ (instancetype)imageWithName:(NSString *)imageName;
@end
