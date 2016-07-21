//
//  RNTPlayMessageModel.m
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayMessageModel.h"
#import "NSString+Extension.h"
#import "RNTMessageAttachment.h"
#import "RNTSocketSummary.h"
#import "RNTUserManager.h"
#import "RNTPlaySendGiftInfo.h"
#import "RNTSysConfigModel.h"

static int _textFontSize;
#define senderNameColor RNTColor_16(0xfff100)
@implementation RNTPlayMessageModel

+ (void)changeMessageModelTypeTo:(PlayMessageModelType)type
{
    if (type == PlayMessageModelTypePlay) {
        _textFontSize = 17;
    }else if (type == PlayMessageModelTypeShoot){
        _textFontSize = 18;
    }
}

+ (instancetype)chatMessageModelWithDict:(NSDictionary *)dict summary:(RNTSocketSummary *)sum
{
    RNTPlayMessageModel* model = [[self alloc] init];
    model.senderName = sum.senderName;
    model.senderUserID = dict[@"senderId"];
    model.contentStr = [NSString stringByReplaceSpaceAndEnterKey:dict[@"txt"]];
    [model setChatAttributedStr];
    
    return model;
    
}

+ (instancetype)enterRoomMessageModelWithDict:(NSDictionary *)dict summary:(RNTSocketSummary *)sum
{
    RNTPlayMessageModel* model = [[self alloc] init];
    model.senderName = sum.senderName;
    model.senderUserID = dict[@"userId"];
    [model setEnterRoomAttributedStr];
    
    return model;
}

+ (instancetype)playTipsMessageModelNeedLink:(BOOL)need{
    RNTPlayMessageModel* model = [[self alloc] init];
    model.senderName = @"<直播提醒>";
    model.senderUserID = @"-1";
    // 请保持文明聊天，拒绝黄赌毒，禁止在房间内直播违规违法内容，违规者直接<a href='http://www.baidu.com'>封号</a>
    
//    NSString* content = [[NSUserDefaults standardUserDefaults] objectForKey:SHOW_TIPS_KEY];
    NSString *content = [RNTUserManager sharedManager].sysConfigModel.showTips;
    if (!content.length) {
        return nil;
    }
    
    
    // 处理url
    NSString* pattern = @"<a.*</a>";
    NSRegularExpression* regular = [[NSRegularExpression alloc] initWithPattern:pattern options:0 error:nil];
    NSRange resultsRnage = [regular rangeOfFirstMatchInString:content options:0 range:NSMakeRange(0, content.length)];
    NSString* urlStr = [content substringWithRange:resultsRnage];
    NSString* textStr = [content substringToIndex:resultsRnage.location];
    // <a href='http://www.baidu.com'>封号</a>
    NSString* urlPattern = @"'.*'";
    regular = [[NSRegularExpression alloc] initWithPattern:urlPattern options:0 error:nil];
    resultsRnage = [regular rangeOfFirstMatchInString:urlStr options:0 range:NSMakeRange(0, urlStr.length)];
    resultsRnage = NSMakeRange(resultsRnage.location+1, resultsRnage.length - 2);
    NSString* urlText = [urlStr substringWithRange:resultsRnage];
    
    NSString* titlePattern = @">.*<";
    regular = [[NSRegularExpression alloc] initWithPattern:titlePattern options:0 error:nil];
    resultsRnage = [regular rangeOfFirstMatchInString:urlStr options:0 range:NSMakeRange(0, urlStr.length)];
    resultsRnage = NSMakeRange(resultsRnage.location+1, resultsRnage.length - 2);
    NSString* titleText = [urlStr substringWithRange:resultsRnage];
    
    content = [NSString stringWithFormat:@"%@%@", textStr, titleText];
    
    content = [NSString stringWithFormat:@"%@%@", model.senderName, content];
    
    NSRange titleRange = [content rangeOfString:titleText];
    
    NSMutableAttributedString* attributedStr = [[NSMutableAttributedString alloc] initWithString:content];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0xff2a2a) range:NSMakeRange(0, attributedStr.length)];
//    [attributedStr addAttribute:NSForegroundColorAttributeName value:RNTMainColor range:[content rangeOfString:model.senderName]];
    [attributedStr addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:_textFontSize] range:NSMakeRange(0, attributedStr.length)];
    if (need) {
        [attributedStr addAttribute:NSUnderlineStyleAttributeName value:@1 range:titleRange];
        [attributedStr addAttribute:NSLinkAttributeName value:urlText range:titleRange];
    }
    
    model.attributedStr = attributedStr;
    
    return model;
}

+ (instancetype)systemTipsMessageModelWithCotent:(NSString *)content{
    RNTPlayMessageModel* model = [[self alloc] init];
    model.senderName = @"<系统消息>";
    model.senderUserID = @"-1";
    
    content = [NSString stringWithFormat:@"%@%@", model.senderName, content];
    
    NSMutableAttributedString* attributedStr = [[NSMutableAttributedString alloc] initWithString:content];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0xff1edf) range:NSMakeRange(0, attributedStr.length)];
    [attributedStr addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:_textFontSize] range:NSMakeRange(0, attributedStr.length)];
    
    model.attributedStr = attributedStr;
    
    return model;
}
+ (instancetype)allTipsMessageModelWithDict:(NSDictionary *)dict summary:(RNTSocketSummary *)sum{
    NSInteger type = [dict[@"type"] integerValue];
    NSString* content = dict[@"msg"];
    if (type == 4) {
        return [self systemTipsMessageModelWithCotent:content];
    }
    
    if (!dict[@"nickName"]) {
        return nil;
    }
    
    RNTPlayMessageModel* model = [[self alloc] init];
    NSString* senderName = [NSString stringWithFormat:@"%@ ", dict[@"nickName"]];
    
    //    content = [NSString stringWithFormat:@"%@我%@", senderName, content];
    UIColor* textColor = [UIColor whiteColor];
    switch (type) {
        case 2: // share
            content = [NSString stringWithFormat:@"%@分享了主播", senderName];
            textColor = RNTColor_16(0x3affd3);
            break;
        case 3: // support
            content = [NSString stringWithFormat:@"%@点了%@个赞", senderName, dict[@"cnt"]];
            textColor = RNTColor_16(0xc85dff);
            break;
        case 5: // attention
            textColor = RNTColor_16(0xffd201);
            content = [NSString stringWithFormat:@"%@关注了主播", senderName];
            break;
            
        default:
            content = nil;
            break;
    }
    
    if (!content) {
        return nil;
    }
    
    
    NSMutableAttributedString* attributedStr = [[NSMutableAttributedString alloc] initWithString:content];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:textColor range:NSMakeRange(0, attributedStr.length)];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:senderNameColor range:[content rangeOfString:senderName]];
    [attributedStr addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:_textFontSize] range:NSMakeRange(0, attributedStr.length)];
    
    [model setNameRangeWithAttri:attributedStr nameColor:senderNameColor senderID:nil];
    model.attributedStr = attributedStr;
    
    return model;
}

+ (instancetype)sendGiftMessageModelWithInfo:(RNTPlaySendGiftInfo *)info
{
    RNTPlayMessageModel* model = [[self alloc] init];
    model.senderName = info.senderName;
    model.senderUserID = info.sendId;
    [model setSendGiftAttributedStr:info];
    
    return model;
}

- (void)setSendGiftAttributedStr:(RNTPlaySendGiftInfo *)info
{
    NSString* senderName = [NSString stringWithFormat:@"%@", info.senderName];
    NSString* content = [NSString stringWithFormat:@"%@ 送出了%zd个%@", senderName, 1, info.giftName];
    NSMutableAttributedString* attributedStr = [[NSMutableAttributedString alloc] initWithString:content];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0xedff59) range:NSMakeRange(0, attributedStr.length)];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:senderNameColor range:[content rangeOfString:senderName]];
//    / RNTColor_16(0xfff100)
    [attributedStr addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:_textFontSize] range:NSMakeRange(0, attributedStr.length)];
    
    
    [self setNameRangeWithAttri:attributedStr nameColor:senderNameColor senderID:info.sendId];
    
    self.attributedStr = attributedStr;
}

- (void)setEnterRoomAttributedStr
{
    NSString* content = [NSString stringWithFormat:@"%@进入房间", self.senderName];
    NSMutableAttributedString* attributedStr = [[NSMutableAttributedString alloc] initWithString:content];
    [attributedStr addAttribute:NSForegroundColorAttributeName value:RNTColor_16(0x4df5ff) range:NSMakeRange(0, attributedStr.length)];
    [attributedStr addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:_textFontSize] range:NSMakeRange(0, attributedStr.length)];
    
    self.attributedStr = attributedStr;
}

- (void)setChatAttributedStr
{
    UIColor* nameColor;
    RNTUserManager* manager = [RNTUserManager sharedManager];
    BOOL isSelfSend = (manager.isLogged && manager.user.userId.integerValue == self.senderUserID.integerValue);
    if(isSelfSend) {
        nameColor = RNTColor_16(0xff6e4c);
    }else{
        nameColor = senderNameColor;
    }
    
    NSString* sendName = [NSString stringWithFormat:@"%@:", self.senderName];
    
    NSMutableAttributedString* contentAttri = (NSMutableAttributedString*)[NSString convertToAttributedStringHaveEmotionWithString:self.contentStr font:[UIFont systemFontOfSize:_textFontSize]];
    
    NSMutableAttributedString* nameAttri = [[NSMutableAttributedString alloc] initWithString:sendName];
    [nameAttri addAttribute:NSForegroundColorAttributeName value:nameColor range:NSMakeRange(0, nameAttri.length)];
    [nameAttri addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:_textFontSize] range:NSMakeRange(0, nameAttri.length)];
    
    NSMutableAttributedString* allAttri = [[NSMutableAttributedString alloc] initWithAttributedString:nameAttri];
    [allAttri appendAttributedString:contentAttri];
//    if (isSelfSend) {
//        [allAttri addAttribute:NSForegroundColorAttributeName value:nameColor range:NSMakeRange(0, allAttri.length)];
//    }
    
//    NSShadow* shadow = [[NSShadow alloc] init];
//    shadow.shadowColor = [UIColor blackColor];
//    shadow.shadowOffset = CGSizeMake(1, 1);
//    [allAttri addAttribute:NSShadowAttributeName value:shadow range:NSMakeRange(0, allAttri.length)];
//
//    NSMutableParagraphStyle* style = [[NSMutableParagraphStyle alloc] init];
//    style.lineBreakMode = NSLineBreakByCharWrapping;
//    [allAttri addAttribute:NSParagraphStyleAttributeName value:style range:NSMakeRange(0, allAttri.length)];
    
//    __block NSRange nameRange;
//    [allAttri enumerateAttribute:NSForegroundColorAttributeName inRange:NSMakeRange(0, allAttri.length) options:0 usingBlock:^(UIColor* value, NSRange range, BOOL * _Nonnull stop) {
//        if (CGColorEqualToColor(value.CGColor, nameColor.CGColor)) {
//            nameRange = range;
//            *stop = YES;
//        }
//    }];
//    
//    RNTMessageAttachment* attach = [[RNTMessageAttachment alloc] init];
//    attach.userID = self.senderUserID;
//    attach.range = nameRange;
//    
//    [allAttri addAttribute:@"range" value:attach range:NSMakeRange(0, 1)];
    [self setNameRangeWithAttri:allAttri nameColor:nameColor senderID:self.senderUserID];
    
    
    self.attributedStr = allAttri;
}

- (void)setAttributedStr:(NSMutableAttributedString *)attributedStr
{
    _attributedStr = attributedStr;
    NSShadow* shadow = [[NSShadow alloc] init];
    shadow.shadowColor = [UIColor blackColor];
    shadow.shadowOffset = CGSizeMake(1, 1);
    [_attributedStr addAttribute:NSShadowAttributeName value:shadow range:NSMakeRange(0, _attributedStr.length)];
    
    NSMutableParagraphStyle* style = [[NSMutableParagraphStyle alloc] init];
    style.lineBreakMode = NSLineBreakByCharWrapping;
    [_attributedStr addAttribute:NSParagraphStyleAttributeName value:style range:NSMakeRange(0, _attributedStr.length)];
    [self setContentSize];
}

- (void)setContentSize
{
    if (!self.attributedStr) {
        return;
    }
    CGSize size = [self.attributedStr boundingRectWithSize:CGSizeMake(textMaxWidth, CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading context:nil].size;
    self.textSize = size;
}

- (void)setNameRangeWithAttri:(NSMutableAttributedString *)attri nameColor:(UIColor*)color senderID:(NSString *)senderID{
    
    if (!senderID) {
        return;
    }
    
    __block NSRange nameRange;
    [attri enumerateAttribute:NSForegroundColorAttributeName inRange:NSMakeRange(0, attri.length) options:0 usingBlock:^(UIColor* value, NSRange range, BOOL * _Nonnull stop) {
        if (CGColorEqualToColor(value.CGColor, color.CGColor)) {
            nameRange = range;
            *stop = YES;
        }
    }];
    
    RNTMessageAttachment* attach = [[RNTMessageAttachment alloc] init];
    attach.userID = senderID;
    attach.range = nameRange;
    
    [attri addAttribute:@"range" value:attach range:NSMakeRange(0, 1)];
}


@end
