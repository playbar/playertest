//
//  RNTRegisterViewController.h
//  Ace
//
//  Created by 靳峰 on 16/2/29.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef void(^RNTRegisterControllerBlock)();

@interface RNTRegisterViewController : UIViewController
/**
 *  用于mine控制器直接modal出注册时的返回键点击回调
 */
@property (nonatomic, copy) RNTRegisterControllerBlock gobackBlock;
@end
