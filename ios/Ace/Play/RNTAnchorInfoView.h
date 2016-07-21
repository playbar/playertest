//
//  RNTAnchorInfoView.h
//  Ace
//
//  Created by 靳峰 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//  主播信息界面

#import <UIKit/UIKit.h>
@class RNTUser;

typedef NS_ENUM(NSInteger, RNTAnchorInfoType) {
    RNTAnchorInfoTypeClickAnchor  = 0,//用户点用户
    RNTAnchorInfoTypeClickUser ,//主播点用户
    RNTAnchorInfoTypeClickSelf,//用户点自己
    RNTAnchorInfoTypeAnchorClickSelf //主播点自己
};

@interface RNTAnchorInfoView : UIView
/**
 *  展示信息卡
 *
 *  @param userID            用户的ID
 *  @param type              点击类型
 *  @param attentionBtnClick 关注按钮点击
 *  @param homePageVC        主页按钮点击
 *  @param reportBtnClick    举报按钮点击
 */
+ (void)showAnchorViewWithUserID:(NSString*)userID clickType:(RNTAnchorInfoType)type attention:(void(^)()) attentionBtnClick homePage : (void(^)( )) homePageVC report:(void(^)()) reportBtnClick;
+ (void)dismiss;

@end
