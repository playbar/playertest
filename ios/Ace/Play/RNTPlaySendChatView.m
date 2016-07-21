//
//  RNTPlaySendChatView.m
//  Ace
//
//  Created by 于传峰 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlaySendChatView.h"
#import "RNTEmojiView.h"
#import "UIImage+RNT.h"
#import "NSString+Extension.h"

@interface RNTPlaySendChatView()<RNTEmojiViewDelegate, UITextFieldDelegate>
@property (nonatomic, weak) UITextField *textField;
@property (nonatomic, weak) UIButton *sendButton;
@property (nonatomic, strong) RNTEmojiView *emotionView;
@property (nonatomic,strong)NSMutableArray *emojiNames;
@end

@implementation RNTPlaySendChatView

- (RNTEmojiView *)emotionView
{
    if (!_emotionView) {
        _emotionView = [[RNTEmojiView alloc]initWithFrame:CGRectMake(0, Height(self), kScreenWidth, emotionKeyBoardHeight)];
        _emotionView.delegate = self;
    }
    return _emotionView;
}
- (NSMutableArray *)emojiNames
{
    if (!_emojiNames) {
        _emojiNames = [[NSMutableArray alloc] init];
    }
    return _emojiNames;
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        self.backgroundColor = [UIColor clearColor];
        [self setupSubViews];
    }
    return self;
}

- (void)setupSubViews
{
    UIButton* emojiButton = [[UIButton alloc] init];
    emojiButton.backgroundColor = [UIColor clearColor];
    [emojiButton setContentHuggingPriority:1000 forAxis:UILayoutConstraintAxisHorizontal];
    [self addSubview:emojiButton];
    [emojiButton addTarget:self action:@selector(switchKeyboardType:) forControlEvents:UIControlEventTouchUpInside];
    [emojiButton setImage:[UIImage imageNamed:@"play_chat_emoji"] forState:UIControlStateNormal];
    [emojiButton sizeToFit];
    [emojiButton makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self);
        make.left.equalTo(self).offset(8);
    }];
    
    // send
    UIButton* sendButton = [[UIButton alloc] init];
    [sendButton addTarget:self action:@selector(sendChat:) forControlEvents:UIControlEventTouchUpInside];
    [sendButton setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0x0090ff)] forState:UIControlStateNormal];
    [sendButton setBackgroundImage:[UIImage imageWithColor:RNTColor_16(0x646464)] forState:UIControlStateDisabled];
    [self addSubview:sendButton];
    sendButton.enabled = NO;
    self.sendButton = sendButton;
    sendButton.titleLabel.font = [UIFont systemFontOfSize:15];
    [sendButton setTitle:@"发送" forState:UIControlStateNormal];
    sendButton.layer.cornerRadius = 6;
    sendButton.clipsToBounds = YES;
    [sendButton makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self);
        make.right.equalTo(self).offset(-8);
        make.width.equalTo(62);
    }];
    
    // textFiled
    UITextField* textField = [[UITextField alloc] init];
    [self addSubview:textField];
    self.textField = textField;
    textField.returnKeyType = UIReturnKeySend;
    textField.enablesReturnKeyAutomatically = YES;
    [self.textField addTarget:self action:@selector(textFieldTouched:) forControlEvents:UIControlEventTouchDown];
    self.textField.delegate = self;
    textField.placeholder = @"和大家说点什么";
    textField.textColor = [UIColor grayColor];
    textField.borderStyle = UITextBorderStyleRoundedRect;
    [textField makeConstraints:^(MASConstraintMaker *make) {
        make.top.bottom.equalTo(self);
        make.left.equalTo(emojiButton.right).offset(8);
        make.right.equalTo(sendButton.left).offset(-8);
    }];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textFieldChange) name:UITextFieldTextDidChangeNotification object:textField];
}

- (void)switchKeyboardType:(UIButton *)button
{
    [self.textField resignFirstResponder];
    if (self.textField.inputView) {
        self.textField.inputView = nil;
    }else{
        self.textField.inputView = self.emotionView;
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.25 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        
        if ([self.textField canBecomeFirstResponder]) {
            [self.textField becomeFirstResponder];
        }
    });
}

- (void)sendChat:(UIButton *)button
{
    NSString* text = [NSString stringByReplaceSpaceAndEnterKey:self.textField.text];
    if (text.length == 0) {
        self.textField.text = @"";
        [self setSendButtonState];
        return;
    }
//    [self dismiss];
    
    
    RNTUserManager* manager = [RNTUserManager sharedManager];
    if (!manager.isLogged) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.25 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            if ([self.delegate respondsToSelector:@selector(playSendChatViewShowLoginView:)]) {
                [self.delegate playSendChatViewShowLoginView:self];
            }
        });
        return;
    }
    if (text.length) {
        if ([self.delegate respondsToSelector:@selector(playSendChatView:sendMessage:)]) {
            [self.delegate playSendChatView:self sendMessage:text];
        }
        self.textField.text = @"";
        [self setSendButtonState];
    }
}

- (void)pop
{
    if ([self.textField canBecomeFirstResponder]) {
        [self.textField becomeFirstResponder];
    }
}
- (void)dismiss
{
    [self endEditing:YES];
}

#pragma mark - RNTEmojiViewDelegate
-(void)selectEmojiWithName:(NSString *)emojiName {
    self.textField.text = [self.textField.text stringByAppendingString:emojiName];
    [self.emojiNames addObject:emojiName];
    [self setSendButtonState];
}

- (void)deleteChatWord {
    if (self.textField.text.length) {
        if( [self textField:self.textField shouldChangeCharactersInRange:NSMakeRange(0, 1) replacementString:@""]){
            [self.textField deleteBackward];
        }
        
        [self setSendButtonState];
    }
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    
    if(range.length == 1) {
        RNTLog(@"这是删除键--");
        if (self.emojiNames.count) {
            NSUInteger location = [self selectedRangeInTextField:textField].location;
            __block NSMutableString* frontText = [[textField.text substringToIndex:location] mutableCopy];
            NSString* backText = [textField.text substringFromIndex:location];
            __block BOOL haveEmoji = NO;
            WEAK(self)
            [self.emojiNames enumerateObjectsUsingBlock:^(NSString* emojiName, NSUInteger idx, BOOL * _Nonnull stop) {
                if ([frontText hasSuffix:emojiName]) {
                    [frontText deleteCharactersInRange:[frontText rangeOfString:emojiName]];
                    [frontText appendString:backText];
                    textField.text = frontText;
                    UITextPosition* start = [textField positionFromPosition:textField.selectedTextRange.start inDirection:UITextLayoutDirectionLeft offset:backText.length];
                    textField.selectedTextRange = [textField textRangeFromPosition:start toPosition:start];
                    [weakself.emojiNames removeObjectAtIndex:idx];
                    [weakself setSendButtonState];
                    haveEmoji = YES;
                    RNTLog(@"%@--%zd", textField.selectedTextRange, textField.text.length);
                    *stop = YES;
                }
            }];
            
            
            if (haveEmoji) {
                return NO;
            }else if (self.textField.text.length){
//                [self.textField deleteBackward];
//                [self setSendButtonState];
                return YES;
            }
        }else if (self.textField.text.length){
//            [self.textField deleteBackward];
//            [self setSendButtonState];
            return YES;
        }
    }
    

    return YES;
    
}
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    [self setSendButtonState];
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [self sendChat:nil];
    return YES;
}

- (void)textFieldTouched:(UITextField *)textField
{
    if (textField.inputView) {
        [self switchKeyboardType:nil];
    }
}

- (void)textFieldChange
{
    [self setSendButtonState];
}

- (void)setSendButtonState
{
    NSString* text = [NSString stringByReplaceSpaceAndEnterKey:self.textField.text];
    self.sendButton.enabled = text.length > 0;
    if (!self.sendButton.isEnabled) {
        [self.emojiNames removeAllObjects];
    }
    [self handleTextFieldText:text];
}

- (void)handleTextFieldText:(NSString *)text
{
    if (text.length > 50) {
        text = [text substringToIndex:50];
        self.textField.text = text;
    }
    if (text.length > 0) {
        if (text.UTF8String == NULL) {
            text = [text substringToIndex:text.length - 1];
            RNTLog(@"null string %zd - %@", text.length, text);
            [self handleTextFieldText:text];
            self.textField.text = text;
        }
    }
    
}


- (NSRange) selectedRangeInTextField:(UITextField*)textFiled
{
    UITextPosition* beginning = textFiled.beginningOfDocument;
    
    UITextRange* selectedRange = textFiled.selectedTextRange;
    UITextPosition* selectionStart = selectedRange.start;
    UITextPosition* selectionEnd = selectedRange.end;
    
    const NSInteger location = [textFiled offsetFromPosition:beginning toPosition:selectionStart];
    const NSInteger length = [textFiled offsetFromPosition:selectionStart toPosition:selectionEnd];
    
    return NSMakeRange(location, length);
}

- (void)dealloc
{
    RNTLog(@"--");
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
