//
//  RNTSettingSinatureItem.m
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSettingSinatureItem.h"

static NSString *mySinature;

@implementation RNTSettingSinatureItem

- (instancetype)initWithIcon:(NSString *)icon title:(NSString *)title sinature:(NSString *)sinature destClass:(Class)destVc
{
    self = [super initWithIcon:icon title:title];
    if (self) {
        self.sinature = sinature;
        self.destVC = destVc;
        mySinature = sinature;
    }
    return self;
}

+ (float)signatureHeight
{
    if (mySinature.length == 0) {
        return 60;
    }
    NSDictionary *dict = @{NSFontAttributeName : [UIFont systemFontOfSize:16]};
    CGSize textSize = [mySinature boundingRectWithSize:CGSizeMake(kScreenWidth - 120, CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin attributes:dict context:nil].size;
    return textSize.height + 20 > 60 ? textSize.height + 20 : 60;
}
@end
