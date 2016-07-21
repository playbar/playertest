//
//  RNTCaptureStartModel.m
//  Ace
//
//  Created by Ranger on 16/3/12.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureStartModel.h"
#import "RNTUserManager.h"
#import "RNTLocationInfo.h"

@implementation RNTCaptureStartModel

- (NSString *)userId
{
    if (_userId == nil) {
        return [RNTUserManager sharedManager].user.userId;
    }else {
        return _userId;
    }
}


- (id)image
{
    if (_image) {
//        return UIImagePNGRepresentation((UIImage *)_image);
        return UIImageJPEGRepresentation((UIImage *)_image, 0.5);
    }else {
        return nil;
    }
}

@end
