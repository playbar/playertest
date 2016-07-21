//
//  RNTCaptureWarnMessageView.m
//  Ace
//
//  Created by Ranger on 16/5/10.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTCaptureWarnMessageView.h"
#import "UIImage+RNT.h"

#define Margin 12
#define TextFont [UIFont systemFontOfSize:18]
#define TextMaxW kScreenWidth -16 -24 -42
#define TextColor RNTColor_16(0xffd200)

@interface RNTCaptureWarnMessageView ()

@property (nonatomic, weak) UILabel *messageLabel;

@end

@implementation RNTCaptureWarnMessageView

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        
        self.backgroundColor = RNTAlphaColor_16(0x000000, 0.5);
        
        self.layer.cornerRadius = 3;
        self.layer.masksToBounds = YES;
        
        [self setupSubview];
        
    }
    return self;
}

- (void)setupSubview
{
    
    // 消息
    UILabel *messageLabel = [[UILabel alloc] init];
    messageLabel.text = @"警告：请注意直播间尺度，不要涉及黄色内容";
    messageLabel.textColor = TextColor;
    messageLabel.font = TextFont;
    messageLabel.numberOfLines = 0;
    
//    messageLabel.layer.shadowOffset = CGSizeMake(1, 1);
//    messageLabel.layer.shadowColor = RNTColor_16(0xff0000).CGColor;
    
    [self addSubview:messageLabel];
    self.messageLabel = messageLabel;
    
    
    UIButton *closeBtn = [[UIButton alloc] init];
    [closeBtn setImage:[UIImage imageNamed:@"capture_warn_close"] forState:UIControlStateNormal];
    [closeBtn setBackgroundColor:RNTAlphaColor_16(0x000000, 0.3)];
    [closeBtn addTarget:self action:@selector(closeClick) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:closeBtn];

    [closeBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.top.bottom.equalTo(self);
        make.width.equalTo(@(41));
    }];
    
    [messageLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.top).offset(Margin);
        make.left.equalTo(self.left).offset(Margin);
        make.right.equalTo(closeBtn.left).offset(-Margin);

    }];
    

}


- (void)setWarnMessage:(NSString *)warnMessage
{
    _warnMessage = warnMessage;
    self.messageLabel.text = warnMessage;
    CGSize messageSize = [warnMessage boundingRectWithSize:CGSizeMake(TextMaxW, CGFLOAT_MAX) options:NSStringDrawingUsesLineFragmentOrigin| NSStringDrawingUsesFontLeading attributes:@{NSFontAttributeName : TextFont} context:nil].size;
    self.viewHeight = messageSize.height + 2* Margin;
}

- (void)closeClick
{
    [self removeFromSuperview];
}

@end
