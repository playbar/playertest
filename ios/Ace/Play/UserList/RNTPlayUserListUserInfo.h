//
//  RNTPlayUserListUserInfo.h
//  Ace
//
//  Created by 于传峰 on 16/3/4.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTUser.h"



@interface RNTPlayUserListUserInfo : RNTUser

///* 头像 */
//@property (nonatomic, copy) NSString *iconUrl;
///* 名字 */
//@property (nonatomic, copy) NSString *userName;
///* 签名 */
//@property (nonatomic, copy) NSString *signature;
///* id */
//@property (nonatomic, copy) NSString *userID;
//// 0:女 1:男
//@property (nonatomic, assign) int sexType;


+ (instancetype)userInfoWithDict:(NSDictionary *)dict;

+ (NSArray *)userInfoWithDictArray:(NSArray *)dictArray;

@end
