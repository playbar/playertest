//
//  RNTFeedBackController.m
//  Ace
//
//  Created by 周兵 on 16/2/27.
//  Copyright © 2016年 RNT. All rights reserved.
//

#define TextFont [UIFont systemFontOfSize:14]
#define TextColor RNTColor_16(0x9a9a9a)

#define ContactMaxLength 30
#define MaxLength 200

@interface RNTFeedBackView : UIView <UITextViewDelegate, UITextFieldDelegate>
@property (nonatomic, strong) UITextView *opinionView; //意见框
@property (nonatomic, strong) UITextField *contactView; //联系方式框
@property (nonatomic, strong) UILabel *placeholderLabel; //意见框提示文字

@end

@implementation RNTFeedBackView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.cornerRadius = 4.0;
        self.layer.masksToBounds =YES;
        self.layer.borderWidth = 0.5;
        self.layer.borderColor = RNTSeparatorColor.CGColor;
        [self setupSubview];
    }
    return self;
}

- (void)setupSubview
{
    [self addSubview:self.opinionView];
    [self addSubview:self.contactView];
    [self addSubview:self.placeholderLabel];
    
    [self.contactView makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.height.equalTo(36);
        make.left.equalTo(12);
        make.right.equalTo(self).offset(-12);
    }];
    
    UIView *line = [[UIView alloc] init];
    line.backgroundColor = RNTSeparatorColor;
    [self addSubview:line];
    [line makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self);
        make.bottom.equalTo(self.contactView.top);
        make.height.equalTo(0.5);
    }];

    [self.opinionView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self);
        make.left.right.equalTo(self.contactView);
        make.bottom.equalTo(line.top);
    }];
    
    [self.placeholderLabel makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.right.equalTo(self.opinionView);
        make.height.equalTo(33);
    }];
}

#pragma mark - UITextViewDelegate
- (void)textViewDidChange:(UITextView *)textView
{
    self.placeholderLabel.hidden = textView.text.length;
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    if (textField.text.length >= ContactMaxLength && string.length > 0) {
        return NO;
    }
    return YES;
}

#pragma mark -
- (UITextView *)opinionView
{
    if (_opinionView == nil) {
        _opinionView = [[UITextView alloc] init];
        _opinionView.font = TextFont;
        _opinionView.textColor = TextColor;
        _opinionView.backgroundColor = [UIColor clearColor];
        _opinionView.delegate = self;
        _opinionView.showsVerticalScrollIndicator = NO;
    }
    return _opinionView;
}

- (UITextField *)contactView
{
    if (_contactView == nil) {
        _contactView = [[UITextField alloc] init];
        _contactView.placeholder = @"您的联系方式（QQ/微信/手机号）";
        _contactView.clearButtonMode = UITextFieldViewModeWhileEditing;
        _contactView.font = TextFont;
        _contactView.textColor = TextColor;
        _contactView.backgroundColor = [UIColor clearColor];
        _contactView.keyboardType = UIKeyboardTypeASCIICapable;
        _contactView.delegate = self;
    }
    return _contactView;
}

- (UILabel*)placeholderLabel
{
    if (_placeholderLabel == nil) {
        _placeholderLabel = [[UILabel alloc] init];
        _placeholderLabel.text = @" 欢迎反馈，您的反馈是我们进步的动力！";
        _placeholderLabel.font = TextFont;
        _placeholderLabel.textColor = TextColor;
    }
    return _placeholderLabel;
}

@end

#import "RNTFeedBackController.h"
#import "RNTMineNetTool.h"
#import "RNTAccountManagerButton.h"

@interface RNTFeedBackController ()

@property (nonatomic, strong) RNTFeedBackView *feedBackView;
@property (nonatomic, strong) RNTAccountManagerButton *submitBtn;

@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@end

@implementation RNTFeedBackController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"意见反馈";
    
    [self setupSubview];
    
    [self.feedBackView.opinionView becomeFirstResponder];
    
    // 注册一个通知, 当textview文本发生变化的时候就会发送通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textViewEditChanged:) name:UITextViewTextDidChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

#pragma mark - 通知
- (void)keyboardWillShow:(NSNotification *)notification
{
    NSDictionary *userInfo = [notification userInfo];
    
    NSValue* aValue = [userInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
    
    CGRect keyboardRect = [aValue CGRectValue];
    self.scrollView.mj_h = keyboardRect.origin.y;
}

- (void)keyboardWillHide:(NSNotification *)notification
{
    self.scrollView.mj_h = kScreenHeight;
}

-(void)textViewEditChanged:(NSNotification *)obj{
    
    UITextView *textView = (UITextView *)obj.object;
    
    
    NSString *toBeString = textView.text;
    ;
    NSString *lang = [self.feedBackView.opinionView textInputMode].primaryLanguage; // 键盘输入模式
    
    if ([lang isEqualToString:@"zh-Hans"]) { // 简体中文输入，包括简体拼音，健体五笔，简体手写
        UITextRange *selectedRange = [textView markedTextRange];
        //获取高亮部分
        UITextPosition *position = [textView positionFromPosition:selectedRange.start offset:0];
        // 没有高亮选择的字，则对已输入的文字进行字数统计和限制
        if (!position) {
            if (toBeString.length > MaxLength) {
                textView.text = [toBeString substringToIndex:MaxLength];
            }
        }
        // 有高亮选择的字符串，则暂不对文字进行统计和限制
        else{
            
        }
    }
    // 中文输入法以外的直接对其统计限制即可，不考虑其他语种情况
    else{
        if (toBeString.length > MaxLength) {
            textView.text = [toBeString substringToIndex:MaxLength];
        }
    }
}

- (void)setupSubview
{
    [self.scrollView addSubview:self.feedBackView];
    [self.scrollView addSubview:self.submitBtn];
    [self.view addSubview:self.scrollView];
    
    [self.feedBackView makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(16);
        make.left.equalTo(10);
        make.right.equalTo(self.view).offset(-10);
        make.height.equalTo(164);
    }];
    
    [self.submitBtn makeConstraints:^(MASConstraintMaker *make) {
        make.width.centerX.equalTo(self.feedBackView);
        make.top.equalTo(self.feedBackView.bottom).offset(28);
        make.height.equalTo(44);
    }];
}

- (void)submit
{
    if (self.feedBackView.opinionView.text.length > 0) {
        WEAK(self);
        [RNTMineNetTool feedbackWithUserId:@"" contactInfo:self.feedBackView.contactView.text contact:self.feedBackView.opinionView.text success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                [MBProgressHUD showSuccess:@"提交成功！"];
                [weakself.navigationController popViewControllerAnimated:YES];
            } else {
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            RNTLog(@"%@", error.localizedDescription);
        }];
    } else {
        [MBProgressHUD showError:@"反馈意见不能为空"];
    }
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 
- (RNTFeedBackView *)feedBackView
{
    if (_feedBackView == nil) {
        _feedBackView = [[RNTFeedBackView alloc] init];
    }
    return _feedBackView;
}

- (RNTAccountManagerButton *)submitBtn
{
    if (_submitBtn == nil) {
        _submitBtn = [[RNTAccountManagerButton alloc] init];
        [_submitBtn setTitle:@"提交" forState:UIControlStateNormal];
        [_submitBtn addTarget:self action:@selector(submit) forControlEvents:UIControlEventTouchUpInside];
    }
    return _submitBtn;
}

- (UIScrollView *)scrollView
{
    if (_scrollView == nil) {
        _scrollView = [[UIScrollView alloc] initWithFrame:self.view.bounds];
        _scrollView.backgroundColor = [UIColor clearColor];
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.showsVerticalScrollIndicator = YES;
        _scrollView.bounces = YES;
        _scrollView.contentSize = CGSizeMake(kScreenWidth, 320);
        //点击手势
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self
                                       action:@selector(endEdit)];
        
        [_scrollView addGestureRecognizer:tap];
    }
    return _scrollView;
}
@end
