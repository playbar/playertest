//
//  NSString+Extension.h
//  Weibo
//
//  Created by 周兵 on 15/12/5.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (Extension)
+ (BOOL)isNull:(id)obj;
+ (NSString *)conbertWithString:(NSString *)str;
+ (NSString *)stringTurnToTransverselineTimeSting:(NSString *)string;
+ (NSString *)currentTime;

/** 将string中的空格和换行去掉 */
+ (NSString *)stringByReplaceSpaceAndEnterKey:(NSString *)string;
/**
 *  检测用户名是否符合规则
 */
+ (BOOL)checkName:(NSString *)name;
/**
 *  检测手机号时候符合规则
 */
//+ (BOOL)checkPhoneNumber:(NSString *)PhoneNumber;
/**
 *  检测密码是否符合格式
 */
+ (BOOL)checkCode:(NSString *)code;

/** 将普通文本转换为带有表情的富文本 */
+ (NSAttributedString *)convertToAttributedStringHaveEmotionWithString:(NSString *)text font:(UIFont *)font;
/**
 *  返回错误描述
 */
+(NSString *)getErrorMessageWIthErrorCode:(NSString *)code;
///** 动态表情 */
//+ (NSAttributedString *)convertToGifEmotionAttributedStringWithString:(NSString *)text;
//- (BOOL)containedGifEmotion;
//+ (NSString *)stringByDelateGiftEmotionText:(NSString *)text;

/** 将普通文本转换为带有颜色的富文本 */
//+ (NSAttributedString *)convertToAttributedStringWith:(NSString *)text color:(UIColor *)color range:(NSRange)range;
//
//+ (NSString *)getPhoneBindCodeStrWihtPhoneNum:(NSString *)phoneNum;
//
//+ (NSString *)getPhoneBindCommitStrrWihtPhoneNum:(NSString *)code;
//
//+ (NSString *)getActionWordsFromCarId:(NSString *)car;
//
//+ (NSString *)getCarNameWithId:(NSString *)_id;
//
//+ (NSString *)calAgeWithString:(NSString *)ageStr;
//
//+ (NSString *)stringRestriction:(NSString *)string;

/** base64加密 */
+ (NSString *)base64EncoderWithString:(NSString *)inputStr;
/** base64解密 */
+ (NSString *)base64DecoderWithString:(NSString *)inputStr;
/** md5加密 */
+ (NSString *)md5EncoderWithString:(NSString *)inputStr;
/** 根据时间字符串yyyy-MM-dd HH:mm:ss转换成时间显示规则 */
//+ (NSString *)stringWithTimeStr:(NSString *)timeStr;
/** 根据时间戳转换成时间yyyy-MM-dd HH:mm:ss */
//+ (NSString *)dateStringFromTimestamp:(NSString *)timestamp;
/**
 *   内部版本号 获取版本号1.xx 格式  用于加密登录 
 *
 */
//+ (NSString *)getVersionStr;
/**
 *  字典转json串
 *
 */
//+ (NSString*)dictionaryToJson:(NSDictionary *)dic;
/**
 *  json串转字典
 *
 */
//+ (NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString;


@end
