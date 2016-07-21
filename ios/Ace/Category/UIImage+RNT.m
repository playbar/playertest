//
//  UIImage+RNT.m
//  Weibo
//
//  Created by 杜 维欣 on 15/7/7.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import "UIImage+RNT.h"
#import "SDWebImageManager.h"

@implementation UIImage (RNT)
- (NSData*)imageDataWithImage
{
    CGSize newSize = CGSizeMake(300, 300);
    UIGraphicsBeginImageContext(newSize);
    [self drawInRect:CGRectMake(0,0,newSize.width,newSize.height)];
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    NSData *imageData = UIImageJPEGRepresentation(newImage, 0.5);
    return imageData;
}

+ (UIImage *)circleImageWithImage:(UIImage *)image borderWidth:(CGFloat)width borderColor:(UIColor *)color
{
//    CGFloat imageW = image.size.width + 2 * width;
//    CGFloat imageH = image.size.width + 2 * width;
//    if (imageW > imageH) {
//        imageW = imageH;
//    }else
//    {
//        imageH = imageW;
//    }
//    CGSize imageSize = CGSizeMake(imageW, imageH);
//    UIGraphicsBeginImageContextWithOptions(imageSize, NO, 0.0);
//    
//    CGContextRef ctx = UIGraphicsGetCurrentContext();
//    [color set];
//    
//    CGFloat bigRadius = imageW * 0.5;
//    CGFloat centerX = bigRadius;
//    CGFloat centerY = bigRadius;
//    CGContextAddArc(ctx, centerX, centerY, bigRadius, 0, M_PI * 2, 0);
//    CGContextFillPath(ctx);
//    
//    CGFloat smallRadius = bigRadius - width;
//    CGContextAddArc(ctx, centerX, centerY, smallRadius, 0, M_PI * 2, 0);
//    CGContextClip(ctx);
//    
//    [image drawInRect:CGRectMake(width, width, image.size.width, image.size.height)];
//    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
//    UIGraphicsEndImageContext();
//    
//    return newImage;
    UIImage* oldImage = image;
    CGFloat minLength  = MIN(oldImage.size.width, oldImage.size.height);
    CGFloat length = minLength + width * 2;
    CGFloat centerX = length * 0.5;
    CGFloat centerY = length * 0.5;
    CGFloat bigRadius = length * 0.5;
    
    CGContextRef ctx = nil;
    
    UIGraphicsBeginImageContextWithOptions(CGSizeMake(length, length), NO, 0.0);
    ctx = UIGraphicsGetCurrentContext();
    
    if (!ctx) {
        CGRect newImageRect = CGRectMake((oldImage.size.width - minLength) * 0.5, (oldImage.size.height - minLength) * 0.5, minLength, minLength);
        CGImageRef newCGImage = CGImageCreateWithImageInRect(oldImage.CGImage, newImageRect);
        UIImage* newImage = [UIImage imageWithCGImage:newCGImage];
        CFRelease(newCGImage);
        return newImage;
    }
    
    // 画大圆
    [color set];
    CGContextAddArc(ctx, centerX, centerY, bigRadius, 0, M_PI * 2, 0);
    
    CGContextFillPath(ctx);
    
    // 画小圆
    CGFloat smallRadius = minLength * 0.5;
    CGContextAddArc(ctx, centerX, centerY, smallRadius, 0, M_PI * 2, 0);
    CGContextClip(ctx);
    
    CGFloat imageX = centerX - oldImage.size.width * 0.5;
    CGFloat imageY = centerY - oldImage.size.height * 0.5;
    [oldImage drawInRect:CGRectMake(imageX, imageY, oldImage.size.width, oldImage.size.height)];
    
    UIImage* newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return newImage;

}

- (UIImage*)grayscaleWithType:(int)type {
    
    if (type == 0) {
        return self;
    }
    
    CGImageRef imageRef = self.CGImage;
    
    size_t width  = CGImageGetWidth(imageRef);
    size_t height = CGImageGetHeight(imageRef);
    
    size_t bitsPerComponent = CGImageGetBitsPerComponent(imageRef);
    size_t bitsPerPixel = CGImageGetBitsPerPixel(imageRef);
    
    size_t bytesPerRow = CGImageGetBytesPerRow(imageRef);
    
    CGColorSpaceRef colorSpace = CGImageGetColorSpace(imageRef);
    
    CGBitmapInfo bitmapInfo = CGImageGetBitmapInfo(imageRef);
    
    
    bool shouldInterpolate = CGImageGetShouldInterpolate(imageRef);
    
    CGColorRenderingIntent intent = CGImageGetRenderingIntent(imageRef);
    
    CGDataProviderRef dataProvider = CGImageGetDataProvider(imageRef);
    
    CFDataRef data = CGDataProviderCopyData(dataProvider);
    
    UInt8 *buffer = (UInt8*)CFDataGetBytePtr(data);
    
    NSUInteger  x, y;
    for (y = 0; y < height; y++) {
        for (x = 0; x < width; x++) {
            UInt8 *tmp;
            tmp = buffer + y * bytesPerRow + x * 4;
            
            UInt8 red,green,blue;
            red = *(tmp + 0);
            green = *(tmp + 1);
            blue = *(tmp + 2);
            
            UInt8 brightness;
            switch (type) {
                case 1:
                    brightness = (77 * red + 28 * green + 151 * blue) / 256;
                    *(tmp + 0) = brightness;
                    *(tmp + 1) = brightness;
                    *(tmp + 2) = brightness;
                    break;
                case 2:
                    *(tmp + 0) = red;
                    *(tmp + 1) = green * 0.7;
                    *(tmp + 2) = blue * 0.4;
                    break;
                case 3:
                    *(tmp + 0) = 255 - red;
                    *(tmp + 1) = 255 - green;
                    *(tmp + 2) = 255 - blue;
                    break;
                default:
                    *(tmp + 0) = red;
                    *(tmp + 1) = green;
                    *(tmp + 2) = blue;
                    break;
            }
        }
    }
    
    
    CFDataRef effectedData = CFDataCreate(NULL, buffer, CFDataGetLength(data));
    
    CGDataProviderRef effectedDataProvider = CGDataProviderCreateWithCFData(effectedData);
    
    CGImageRef effectedCgImage = CGImageCreate(
                                               width, height,
                                               bitsPerComponent, bitsPerPixel, bytesPerRow,
                                               colorSpace, bitmapInfo, effectedDataProvider,
                                               NULL, shouldInterpolate, intent);
    
    UIImage *effectedImage = [[UIImage alloc] initWithCGImage:effectedCgImage];
    
    CGImageRelease(effectedCgImage);
    
    CFRelease(effectedDataProvider);
    
    CFRelease(effectedData);
    
    CFRelease(data);
    
    return effectedImage;
    
}


static NSData *kPNGSignatureData = nil;
BOOL ImageDataHasPNGPreffixWithData(NSData *data);

BOOL ImageDataHasPNGPreffixWithData(NSData *data) {
    NSUInteger pngSignatureLength = [kPNGSignatureData length];
    if ([data length] >= pngSignatureLength) {
        if ([[data subdataWithRange:NSMakeRange(0, pngSignatureLength)] isEqualToData:kPNGSignatureData]) {
            return YES;
        }
    }
    
    return NO;
}

+ (void)grayImageWithUrl:(NSURL *)imageUrl type:(int)type finish:(void (^)(UIImage *))finishBlock
{
    [[SDWebImageManager sharedManager] downloadImageWithURL:imageUrl options:SDWebImageRetryFailed progress:nil completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
        
        NSString* key = [[SDWebImageManager sharedManager] cacheKeyForURL:imageURL];
        
        BOOL result = [[SDImageCache sharedImageCache] diskImageExistsWithKey:key];
        
        NSString* imagePath = [[SDImageCache sharedImageCache] defaultCachePathForKey:key];
        
        NSData* newData = [NSData dataWithContentsOfFile:imagePath];
        if (!result || !newData) {
            BOOL imageIsPng = ImageDataHasPNGPreffixWithData(nil);
            
            NSData* imageData = nil;
            
            if (imageIsPng) {
                imageData = UIImagePNGRepresentation(image);
            }
            
            else {
                imageData = UIImageJPEGRepresentation(image, (CGFloat)1.0);
            }
            
            NSFileManager* _fileManager = [NSFileManager defaultManager];
            
            if (imageData) {
                [_fileManager removeItemAtPath:imagePath error:nil];
//                if (![_fileManager fileExistsAtPath:imagePath]) {
//                    
//                    [_fileManager createDirectoryAtPath:imagePath withIntermediateDirectories:YES attributes:nil error:NULL];
//                    
//                }
                
                [_fileManager createFileAtPath:imagePath contents:imageData attributes:nil];
                
            }
            
        }
        
        newData = [NSData dataWithContentsOfFile:imagePath];
        
        UIImage* grayImage = nil;
        
        if (type == 0) {
            grayImage = [UIImage imageWithData:newData];
        }else{
            UIImage* newImage = [UIImage imageWithData:newData];
            
            grayImage = [newImage grayscaleWithType:type];
        }
        
        
        if (finishBlock) {
            finishBlock(grayImage);
        }
        
    }];
}

+ (UIImage *)imageWithColor:(UIColor *)color {
    CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    return image;
}

+ (instancetype)resizableImageWithName:(NSString *)imageName
{
    UIImage *image = [self imageNamed:imageName];
    
    CGFloat left = image.size.width * 0.5;
    CGFloat top = image.size.height * 0.5;
    
    image = [image stretchableImageWithLeftCapWidth:left topCapHeight:top];
    
    return image;
}

+ (instancetype)imageWithName:(NSString *)imageName
{
    UIImage *image = nil;
    if (iPhone4) {
        NSString *newImageName = [imageName stringByAppendingString:@"_480"];
        image = [UIImage imageNamed:newImageName];
    }
    
    if (image == nil) {
        // 有的图片并没有_750 或 _568结尾的图片
        image = [UIImage imageNamed:imageName];
    }
    
    return image;
}
@end
