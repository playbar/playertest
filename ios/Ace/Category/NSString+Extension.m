//
//  NSString+Extension.m
//  Weibo
//
//  Created by 周兵 on 15/12/5.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import "NSString+Extension.h"
#import "RegexKitLite.h"
#import "RNTAttributedStringContainer.h"
#import <CommonCrypto/CommonDigest.h>
//#import "RNTGifEmotionTextAttachment.h"

static NSDictionary* msgDict;

@implementation NSString (Extension)
+ (BOOL)isNull:(id)obj
{
    if (!obj)
    {
        return YES;
    }
    
    if ([obj isKindOfClass:[NSNull class]])
    {
        return YES;
    }
    
    if ([obj isKindOfClass:[NSString class]])
    {
        if ([obj isEqualToString:@""])
        {
            return YES;
        }
        
        if ([[obj lowercaseString] isEqualToString:@"null"])
        {
            return YES;
        }
        
        if ([[obj lowercaseString] isEqualToString:@"<null>"])
        {
            return YES;
        }
        
        if ([[obj lowercaseString] isEqualToString:@"(null)"])
        {
            return YES;
        }
        
        if ([[obj lowercaseString] isEqualToString:@"[null]"])
        {
            return YES;
        }
    }
    return NO;
}

+(NSString *)conbertWithString:(NSString *)str
{
    if(!str) return str;
    //特殊处理
    if ([str rangeOfString:@"&zeta;ั͡ޓއއއ"].location!=NSNotFound) {
        str=[str stringByReplacingOccurrencesOfString:@"&zeta;ั͡ޓއއއ" withString:@"ζั͡"];
    }
    //获取字典
    NSString* path=[[NSBundle mainBundle] pathForResource:@"convert.plist" ofType:nil];
    
    NSDictionary* dict=[NSDictionary dictionaryWithContentsOfFile:path];
    
    for (NSString* key in dict) {
        
        NSString* vlaue=[dict objectForKey:key];
        
        while([str rangeOfString:key].location !=NSNotFound)
        {
            str=[str stringByReplacingOccurrencesOfString:key withString:vlaue];
        }
    }
    return str;
}

+ (NSString *)stringTurnToTransverselineTimeSting:(NSString *)string
{
    if (string==(NSString*)[NSNull null])
    {
        return @"";
    }
    if ([string isEqualToString:@""]) {
        return @"";
    }
    
    NSDateFormatter *inputFormatter = [[NSDateFormatter alloc] init] ;
    [inputFormatter setLocale:[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"] ];
    [inputFormatter setDateFormat:@"yyyyMMddHHmmss"];
    NSDate* inputDate = [inputFormatter dateFromString:string];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    
    //设定时间格式,这里可以设置成自己需要的格式
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    //用[NSDate date]可以获取系统当前时间
    NSString* returnStr = [dateFormatter stringFromDate:inputDate];
    
    return returnStr;
}

+ (NSString *)stringTurnToObliquelineTimeSting:(NSString *)string
{
    if ([string isEqualToString:@""]) {
        return @"";
    }
    
    NSDateFormatter *inputFormatter = [[NSDateFormatter alloc] init] ;
    [inputFormatter setLocale:[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"] ];
    [inputFormatter setDateFormat:@" "];
    NSDate* inputDate = [inputFormatter dateFromString:string];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    
    //设定时间格式,这里可以设置成自己需要的格式
    [dateFormatter setDateFormat:@"yyyy/MM/dd HH:mm:ss"];
    //用[NSDate date]可以获取系统当前时间
    NSString* returnStr = [dateFormatter stringFromDate:inputDate];
    
    return returnStr;
}

+ (NSString *)currentTime{
    
    NSDate *nowUTC = [NSDate date];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setTimeZone:[NSTimeZone localTimeZone]];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
    [dateFormatter setDateFormat:@"yy/MM/dd HH:mm"];
    
    return [dateFormatter stringFromDate:nowUTC];
}

#pragma mark - 判断各输入框的格式

//密码检测
+ (BOOL)checkCode:(NSString *)code
{
    NSString *pattern = @"\\w{6,18}$";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
    BOOL isMatch = [pred evaluateWithObject:code];
    return isMatch;
}

//用户名检测
+ (BOOL)checkName:(NSString *)name
{
    NSString *pattern = @"^\\D\\w{5,17}$";
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", pattern];
    BOOL isMatch = [pred evaluateWithObject:name];
    return isMatch;
}

+ (NSAttributedString *)convertToAttributedStringHaveEmotionWithString:(NSString *)text font:(UIFont *)font
{
    if (!text) {
        return nil;
    }
    
    NSString* path = [[NSBundle mainBundle] pathForResource:@"emotion" ofType:@"plist"];
    NSDictionary* emotionDic = [[NSDictionary alloc] initWithContentsOfFile:path];
    
    NSMutableAttributedString* contentAttr = [[NSMutableAttributedString alloc] initWithString:text];
    [contentAttr addAttribute:NSForegroundColorAttributeName value:[UIColor blackColor] range:NSMakeRange(0, contentAttr.length)];
    NSMutableArray* parts = [NSMutableArray array];
    
    NSString* pattern = @"\\[\\w{1,3}\\]";
    [text enumerateStringsMatchedByRegex:pattern usingBlock:^(NSInteger captureCount, NSString *const __unsafe_unretained *capturedStrings, const NSRange *capturedRanges, volatile BOOL *const stop) {
        NSString* emotionKey = *capturedStrings;
        NSInteger count = emotionKey.length;
        NSString* emotionValue = emotionDic[emotionKey];
        
        for (int i = 0; !emotionValue && emotionKey.length>1 && i < count; i++) {
            emotionKey = [emotionKey substringWithRange:NSMakeRange(0, emotionKey.length-1)];
            emotionValue = emotionDic[emotionKey];
        }
        
        if (emotionValue) { //_&cap;
            UIImage* emotion = [UIImage imageNamed:emotionValue];
            NSTextAttachment* attachment = [[NSTextAttachment alloc] init];
            attachment.image = emotion;
            attachment.bounds = CGRectMake(0, -font.lineHeight*0.3, font.lineHeight*1.5, font.lineHeight*1.5);
            NSAttributedString* emotionAttr = [NSAttributedString attributedStringWithAttachment:attachment];
            NSRange emotionRange = NSMakeRange((*capturedRanges).location, emotionKey.length);
            
            RNTAttributedStringContainer* part = [[RNTAttributedStringContainer alloc] init];
            part.range = emotionRange;
            part.attrs = emotionAttr;
            [parts addObject:part];
            
            [contentAttr addAttribute:NSForegroundColorAttributeName value:[UIColor whiteColor] range:emotionRange];
        }
        
    }];
    
    [contentAttr enumerateAttribute:NSForegroundColorAttributeName inRange:NSMakeRange(0, contentAttr.length) options:0 usingBlock:^(UIColor* value, NSRange range, BOOL *stop) {
        
        if ([value isEqual:[UIColor blackColor]]) {
            NSAttributedString* ordinaryAttr = [contentAttr attributedSubstringFromRange:range];
            RNTAttributedStringContainer* part = [[RNTAttributedStringContainer alloc] init];
            part.range = range;
            part.attrs = ordinaryAttr;
            [parts addObject:part];
        }
    }];
    
    
    NSArray* textParts = [parts sortedArrayUsingComparator:^NSComparisonResult(RNTAttributedStringContainer* obj1, RNTAttributedStringContainer* obj2) {
        if (obj1.range.location > obj2.range.location) { // 如果顺序不对则返回返向的 它会自动调换位置
            return NSOrderedDescending;
        }
        return NSOrderedAscending;
    }];
    
    NSMutableAttributedString* attributeText = [[NSMutableAttributedString alloc] init];
    for (RNTAttributedStringContainer* part in textParts) {
        [attributeText appendAttributedString:part.attrs];
    }
    [attributeText addAttribute:NSFontAttributeName value:font range:NSMakeRange(0, attributeText.length)];
    [attributeText addAttribute:NSForegroundColorAttributeName value:[UIColor whiteColor] range:NSMakeRange(0, attributeText.length)];
    return attributeText;
}

//+ (NSString *)stringByDelateGiftEmotionText:(NSString *)text
//{
//    if (!text) {
//        return @"";
//    }
//    
//    if (![text containedGifEmotion]) {
//        return text;
//    }
//    
//    NSMutableString* contentText = [text mutableCopy];
//    
//    NSString* pattern1 = @"/[\u4e00-\u9fa5]{4,5}";
//    NSString* pattern2 = @"/666";
//    NSString* pattern = [NSString stringWithFormat:@"%@|%@", pattern1, pattern2];
//    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
//    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
//    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
//    [regx enumerateMatchesInString:contentText options:NSMatchingReportProgress range:NSMakeRange(0, contentText.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
//        NSString* resultString = [contentText substringWithRange:result.range];
//        NSString* gifName = emotionDic[resultString];
//        
//        if (!gifName) {
//            for (int i = 0; resultString.length > 4 && !gifName; i++) {
//                resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
//                gifName = emotionDic[resultString];
//            }
//        }
//        
//        if (gifName) {
//            [contentText replaceCharactersInRange:[contentText rangeOfString:resultString] withString:@""];
//        }
//    }];
//    
//    return contentText;
//}
//
//- (BOOL)containedGifEmotion
//{
//    __block BOOL resultBool = NO;
//    
//    NSString* pattern1 = @"/[\u4e00-\u9fa5]{4,5}";
//    NSString* pattern2 = @"/666";
//    NSString* pattern = [NSString stringWithFormat:@"%@|%@", pattern1, pattern2];
//    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
//    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
//    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
//    [regx enumerateMatchesInString:self options:NSMatchingReportProgress range:NSMakeRange(0, self.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
//        NSString* resultString = [self substringWithRange:result.range];
//        NSString* gifName = emotionDic[resultString];
//        
//        if (!gifName) {
//            for (int i = 0; resultString.length > 4 && !gifName; i++) {
//                resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
//                gifName = emotionDic[resultString];
//            }
//        }
//        
//        if (gifName) {
//            resultBool = YES;
//            *stop = YES;
//        }
//    }];
//    
//    return resultBool;
//}
//
//+ (NSAttributedString *)convertToGifEmotionAttributedStringWithString:(NSString *)contentString
//{
//    if (!contentString) {
//        return nil;
//    }
//    __block BOOL resultBool = NO;
//    
//    NSString* pattern1 = @"/[\u4e00-\u9fa5]{4,5}";
//    NSString* pattern2 = @"/666";
//    NSString* pattern = [NSString stringWithFormat:@"%@|%@", pattern1, pattern2];
//    NSRegularExpression* regx = [[NSRegularExpression alloc] initWithPattern:pattern options:NSRegularExpressionCaseInsensitive error:nil];
//    NSString* path = [[NSBundle mainBundle] pathForResource:@"EmotionGifList" ofType:@"plist"];
//    NSDictionary* emotionDic = [NSDictionary dictionaryWithContentsOfFile:path];
//    NSMutableDictionary* gifEomtionDict = [[NSMutableDictionary alloc] init];
//    [regx enumerateMatchesInString:contentString options:NSMatchingReportProgress range:NSMakeRange(0, contentString.length) usingBlock:^(NSTextCheckingResult * _Nullable result, NSMatchingFlags flags, BOOL * _Nonnull stop) {
//        NSString* resultString = [contentString substringWithRange:result.range];
//        NSString* gifName = emotionDic[resultString];
//        
//        if (!gifName) {
//            for (int i = 0; resultString.length > 4 && !gifName; i++) {
//                resultString = [resultString substringWithRange:NSMakeRange(0, resultString.length - 1)];
//                gifName = emotionDic[resultString];
//            }
//        }
//        
//        if (gifName) {
//            gifEomtionDict[NSStringFromRange(NSMakeRange(result.range.location, resultString.length))] = gifName;
//            resultBool = YES;
//            *stop = YES;
//        }
//    }];
//    
//    if (!resultBool) {
//        return nil;
//    }
//    
//    NSMutableAttributedString* attributedString = [[NSMutableAttributedString alloc] initWithString:contentString];
//    NSMutableArray* ranges = [gifEomtionDict.allKeys mutableCopy];
//    [ranges sortUsingComparator:^NSComparisonResult(NSString* obj1, NSString* obj2) {
//        NSRange range1 = NSRangeFromString(obj1);
//        NSRange range2 = NSRangeFromString(obj2);
//        
//        if (range1.location < range2.location) {
//            return NSOrderedDescending;
//        }
//        
//        return NSOrderedAscending;
//    }];
//    
//    for (NSString* rangeString in ranges) {
//        RNTGifEmotionTextAttachment* attachment = [[RNTGifEmotionTextAttachment alloc] init];
//        attachment.bounds = GifEmotionBounds;
//        attachment.gifName = gifEomtionDict[rangeString];
//        NSAttributedString* attachmentString = [NSAttributedString attributedStringWithAttachment:attachment];
////        [attributedString replaceCharactersInRange:NSRangeFromString(rangeString) withAttributedString:attachmentString];
//        attributedString = [attachmentString mutableCopy];
//    }
//    
////    [attributedString addAttribute:NSForegroundColorAttributeName value:[UIColor redColor] range:NSMakeRange(0, attributedString.length)];
////    [attributedString addAttribute:NSFontAttributeName value:[UIFont boldSystemFontOfSize:14] range:NSMakeRange(0, attributedString.length)];
//    
//    return attributedString;
//    
//}
//
//+ (NSAttributedString *)convertToAttributedStringWith:(NSString *)text color:(UIColor *)color range:(NSRange)range
//{
//    if (!text) {
//        return nil;
//    }
//    NSMutableAttributedString *attStr = [[NSMutableAttributedString alloc] initWithString:text];
//    
//    [attStr addAttribute:NSForegroundColorAttributeName value:color range:range];
//    
//    return attStr;
//}
//
//+ (NSString *)getPhoneBindCodeStrWihtPhoneNum:(NSString *)phoneNum
//{
//    if (!phoneNum) {
//        return nil;
//    }
//    UserManager *mgr = [UserManager shareManager];
//    NSString *str = [NSString stringWithFormat:@"http://%@/getSmsActivateInfo?userName=%@&phone=%@&type=%@&access_token=%@",PASSPORT_51WEIBO_COM,mgr.userInfo.userName,phoneNum,@"0",[UserManager shareManager].userToken];
//    return str;
//}
//
//
//+ (NSString *)getPhoneBindCommitStrrWihtPhoneNum:(NSString *)code
//{
//    if (!code) {
//        return nil;
//    }
//    UserManager *mgr = [UserManager shareManager];
//    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
//    NSString *passWorld = [userDefaults objectForKey:USER_PASSWORD];
//    NSString *str = [NSString stringWithFormat:@"http://%@/confirmSmsgActivateInfo?userName=%@&code=%@&type=%@&pwd=%@&via=%@&qd=%@&access_token=%@",PASSPORT_51WEIBO_COM,mgr.userInfo.userName,code,@"0",passWorld,@"2",@"1000001",[UserManager shareManager].userToken];
//    return str;
//}
//
///**
// *  获取座驾动词
// */
//+ (NSString *)getActionWordsFromCarId:(NSString *)car
//{
//    if (!car) {
//        return nil;
//    }
////    NSString *tempStr = [NSString stringWithFormat:@"%@",car];
//    NSArray *driveArray = @[@"153",@"120",@"154",@"140",@"400001",@"400003",@"400004",@"400006",@"400002",@"400005",@"400007",@"400008",];
//    NSArray *sitArray = @[@"152",@"142",];
//    NSArray *rideArray = @[@"160",@"888",@"110",@"150",@"60",@"111",@"151",@"141",@"101001",];
//    if ([driveArray containsObject:car]) {
//        return @"驾驶着 ";
//    }
//    if ([sitArray containsObject:car]) {
//        return @"坐着 ";
//    }
//    if ([rideArray containsObject:car]) {
//        return @"骑着 ";
//    }
//    return @"乘着 ";
//}
//
///**
// *  根据_id获取座驾名字
// */
//+ (NSString *)getCarNameWithId:(NSString *)_id
//{
//    if (!_id) {
//        return nil;
//    }
//    NSString *carPath = kCarFile(@"Car.plist");
//    NSDictionary *allCarsDic = [[NSDictionary alloc]initWithContentsOfFile:carPath];
//    NSString *carName=[NSString stringWithFormat:@"%@",allCarsDic[_id][@"name"]];
//    return carName;
//}
//
//
//+ (NSString *)calAgeWithString:(NSString *)ageStr
//{
//    if (!ageStr) {
//        return nil;
//    }
//    NSDate *now = [NSDate date];
//    
//    NSCalendar *calendar = [NSCalendar currentCalendar];
//    NSUInteger unitFlags = NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit | NSHourCalendarUnit | NSMinuteCalendarUnit | NSSecondCalendarUnit;
//    NSDateComponents *dateComponent = [calendar components:unitFlags fromDate:now];
//    
//    NSString *year = [ageStr substringToIndex:4];
//    int smallCount = [year intValue];
//    int bigCount = (int)[dateComponent year];
//
//    
//    int delta = bigCount - smallCount;
//    return [NSString stringWithFormat:@"%d",delta];
//}
//
+ (NSString *)base64EncoderWithString:(NSString *)inputStr
{
    if (!inputStr) {
        return nil;
    }
    // 加密
    NSData* baseData = [inputStr dataUsingEncoding:NSUTF8StringEncoding];
    NSString* basedStr = [baseData base64EncodedStringWithOptions:NSDataBase64Encoding64CharacterLineLength];
    return basedStr;
}

+ (NSString *)base64DecoderWithString:(NSString *)inputStr
{
    if (!inputStr) {
        return nil;
    }
    // 解密
    NSData* basedData = [[NSData alloc] initWithBase64EncodedString:inputStr options:NSDataBase64DecodingIgnoreUnknownCharacters];
    NSString* base = [[NSString alloc] initWithData:basedData encoding:NSUTF8StringEncoding];
    return base;
}
//
+ (NSString *)md5EncoderWithString:(NSString *)inputStr
{
    if (!inputStr) {
        return nil;
    }
    // MD5
    const char* str = [inputStr UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(str, strlen(str), result);
    NSMutableString *ret = [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH*2];//
    
    for(int i = 0; i<CC_MD5_DIGEST_LENGTH; i++) {
        [ret appendFormat:@"%02x",result[i]];
    }
    return ret;
}

//+ (NSString *)stringWithTimeStr:(NSString *)timeStr
//{
//    if (!timeStr) {
//        return nil;
//    }
//    //    1.创建时间格式化工具类
//    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
//    
//    // 2.格式时间
//    // 指定时间的格式
//    formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
//    
//    formatter.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"zh"];
//    NSDate *uploadTime = [formatter dateFromString:timeStr];
//    // 利用服务器的和本地时间进行对比
//    NSDateComponents *cmps = [uploadTime deltaWithNow];
//    
//    if ([uploadTime isThisYear]) {
//        // 是今年
//        if ([uploadTime isToday]) {
//            // 是今天
//            if (cmps.hour >= 1) {
//                //其它
//                formatter.dateFormat = @"HH:mm";
//                return [formatter stringFromDate:uploadTime];
//                
//            } else if (cmps.minute <= 1) {
//                // 刚刚
//                return @"刚刚";
//            } else {
//                // 1个小时内
//                return [NSString stringWithFormat:@"%ld分钟前", (long)cmps.minute];
//            }
//        } else if ([uploadTime isYesterday]) {
//            // 是昨天
//            formatter.dateFormat = @"昨天";
//            return [formatter stringFromDate:uploadTime];
//            
//        } else {
//            // 其它天
//            formatter.dateFormat = @"MM-dd";
//            return [formatter stringFromDate:uploadTime];
//        }
//    } else {
//        // 不是今年
//        formatter.dateFormat = @"YY-MM-dd";
//        return [formatter stringFromDate:uploadTime];
//    }
//    return @"1分钟以前";
//}
//
//+ (NSString *)getVersionStr
//{
//    NSString *versionStr = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
//    
//    return [[versionStr substringWithRange:NSMakeRange(0, 3)] stringByAppendingString:[versionStr substringFromIndex:versionStr.length - 1]];
//}
//
//+ (NSString*)dictionaryToJson:(NSDictionary *)dic
//
//{
//    NSError *parseError = nil;
//    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&parseError];
//    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//}
//
////json转字典
//+ (NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString {
//    
//    if (jsonString == nil) {
//        return nil;
//    }
//    
//    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
//    NSError *err;
//    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&err];
//    
//    if(err) {
//        RNTLog(@"json解析失败：%@",err);
//        return nil;
//    }
//    return dic;
//}
//
+ (NSString *)stringByReplaceSpaceAndEnterKey:(NSString *)string
{
    if (!string) {
        return @"";
    }
    string = [string stringByReplacingOccurrencesOfString: @"\r" withString:@""];
    string = [string stringByReplacingOccurrencesOfString: @"\n" withString:@""];
    string = [string stringByReplacingOccurrencesOfString: @" " withString:@""];
    return string;
}

+(NSString *)getErrorMessageWIthErrorCode:(NSString *)code;
{
    if (!msgDict) {
        msgDict = @{
                    @"100":@"该账户不存在",
                    @"101":@"账户余额不足",
                    @"102":@"兑换数据不正确",
                    @"103":@"兑换比率不存在",
                    @"104":@"还未绑定兑点信息",
                    
                    @"200":@"用户名不能为空",
                    @"201":@"密码不能为空",
                    @"202":@"用户名错误",
                    @"203":@"密码错误",
                    @"204":@"该用户不存在",
                    @"205":@"该用户已经被拉黑",
                    @"206":@"已经是关注,不能再添加",
                    @"207":@"两者不是好友,无法删除",
                    @"208":@"用户已经冻结",
                    @"209":@"PID已经被注册",
                    @"210":@"手机号已经被注册",
                    @"211":@"该用户已存在",
                    @"212":@"该用户已经是激活状态",
                    @"213":@"该用户并非黑名单用户,无法从黑名单中移除",
                    @"214":@"验证码已经发送",
                    @"215":@"还未绑定兑点信息",
                    @"216":@"验证码无效",
                    @"217":@"无效的邮件链接",
                    @"218":@"含有非法字符",
                    @"219":@"同一设备注册号码数超出限制",
                    @"220":@"短信发送失败",
                    @"221":@"你已被主播禁言",
                    
                    @"300":@"其他失败",
                    @"301":@"直播已经结束",
                    
                    @"400":@"商品已经存在",
                    @"401":@"商品不存在",
                    @"402":@"商品余额不足",
                    
                    @"500":@"已经关注该用户",
                    
                    @"600":@"参数异常",
                    @"601":@"网络连接超时",
                    @"602":@"数据头异常,请求拒绝",
                    
                    @"700":@"订单不存在",
                    @"701":@"已经兑换过",
                    @"702":@"订单已经开通",
                    @"703":@"订单已经支付",
                    @"704":@"订单状态不满足",
                    @"705":@"订单号不正确",
                    @"706":@"订单充值金额不正确",
                    @"707":@"订单支付方式不正确",
                    @"708":@"订单支付通道不正确",
                    @"709":@"订单创建失败",
                    @"710":@"订单跳转支付失败",
                    @"711":@"订单金额与支付金额不一致",
                    @"712":@"订单充值失败",
                    @"713":@"苹果内购收据验证无效",
                    
                    @"800":@"上传多媒体消息内容失败",
                    @"801":@"下载多媒体消息内容失败,不存在",
                    @"802":@"只有VIP才能给宝贝发消息",
                    };
    }
    
    
    NSString* des = msgDict[code];

    if (!des) {
        des = [NSString stringWithFormat:@"错误码:%@",code];
    }
    
    return des;
}

//
//+ (NSString *)stringRestriction:(NSString *)string
//{
//    if (!string) {
//        return @"";
//    }
//    NSString* textString = [string copy];
//    if (textString.length > 6) {
//        textString = [textString substringToIndex:5];
//        textString = [NSString stringWithFormat:@"%@...", textString];
//    }
//    return textString;
//}
//
//+ (NSString *)dateStringFromTimestamp:(NSString *)timestamp
//{
//    if (!timestamp) {
//        return @"";
//    }
//    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
//    [formatter setDateFormat:@"YYYY-MM-dd HH:mm:ss"];
//    formatter.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"zh"];
//    
//    NSDate *confromTimesp = [NSDate dateWithTimeIntervalSince1970:timestamp.floatValue * 0.001];
//    return [formatter stringFromDate:confromTimesp];
//}
//
///* 根据用户资料获取消费等级图标名称 */
//+ (NSString *)memberLevelImageNameWithMemberLevel:(NSString *)memberLevelStr nobleLevel:(NSString *)nobleLevel
//{
//    NSString *memberLevelImage;
//    
//    int memberLevel;
//    if (memberLevelStr.intValue > maxMemberLevel) {
//        memberLevel = maxMemberLevel;
//    } else {
//        memberLevel = memberLevelStr.intValue;
//    }
//    
//    if ([NSString isNull:nobleLevel]) {
//        memberLevelImage = [NSString stringWithFormat:@"img_user_level_%02zd", memberLevel];
//    } else {
//        switch (nobleLevel.intValue) {
//            case 0:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_warrior_level_%02zd", memberLevel];
//                break;
//            case 1:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_knight_level_%02zd", memberLevel];
//                break;
//            case 2:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_lord_level_%02zd", memberLevel];
//                break;
//            case 3:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_fersen_level_%02zd", memberLevel];
//                break;
//            case 4:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_duke_level_%02zd", memberLevel];
//                break;
//            case 5:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_monarch_level_%02zd", memberLevel];
//                break;
//            case 6:
//                memberLevelImage = [NSString stringWithFormat:@"img_user_emperor_level_%02zd", memberLevel];
//                break;
//                
//            default:
//                break;
//                
//        }
//    }
//    return memberLevelImage;
//}

///* 根据用户资料获取主播等级图标名称 */
//+ (NSString *)hostLevelImageNameWithAnchorLevel:(NSString *)anchorLevel
//{
//    NSString *hostLevel;
//    if (anchorLevel.intValue > maxHostLevel) {
//        hostLevel = [NSString stringWithFormat:@"%d", maxHostLevel];
//    } else {
//        hostLevel = anchorLevel;
//    }
//    return [NSString stringWithFormat:@"h%@", hostLevel];
//}
@end
