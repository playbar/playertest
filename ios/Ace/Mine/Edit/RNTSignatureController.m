//
//  RNTSignatureController.m
//  Ace
//
//  Created by 周兵 on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSignatureController.h"
#import "MBProgressHUD+RNT.h"
#import "RNTAccountManagerButton.h"
#import "RNTMineNetTool.h"
#import "NSString+Extension.h"

#define MaxLength 20
#define TextColor RNTColor_16(0xa8a8a9)
#define TextFont [UIFont systemFontOfSize:14]

@interface RNTSignatureController ()
@property (nonatomic, strong)UITextView *signature;
@property (nonatomic, strong)UILabel *placeholderLabel;
@property (nonatomic, strong) RNTAccountManagerButton *submitBtn;
@property (nonatomic, strong) UIScrollView *scrollView; // 用于弹出键盘时滚动
@end

@implementation RNTSignatureController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"个性签名";
    self.view.backgroundColor = RNTBackgroundColor;
    
    [self setupSubviews];
    
    // 注册一个通知, 当textview文本发生变化的时候就会发送通知
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(textViewEditChanged:) name:UITextViewTextDidChangeNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

- (void)setupSubviews
{
    UILabel *limitLabel = [[UILabel alloc] init];
    limitLabel.text = @"请不要超过20个字";
    limitLabel.font = TextFont;
    limitLabel.textColor = TextColor;
    
    [self.scrollView addSubview:limitLabel];
    [self.scrollView addSubview:self.signature];
    [self.scrollView addSubview:self.placeholderLabel];
    [self.scrollView addSubview:self.submitBtn];
    [self.view addSubview:self.scrollView];
    
    [self.signature makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(15);
        make.left.equalTo(12);
        make.right.equalTo(self.view).offset(-12);
        make.height.equalTo(100);
    }];
    
    [self.placeholderLabel makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.equalTo(self.signature).offset(10);
        make.right.equalTo(self.signature).offset(-10);
        make.height.equalTo(14);
    }];
    
    [limitLabel makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(16);
        make.right.equalTo(self.view).offset(-16);
        make.top.equalTo(self.signature.bottom).offset(10);
        make.height.equalTo(14);
    }];
    
    [self.submitBtn makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self.signature);
        make.top.equalTo(limitLabel.bottom).offset(52);
        make.height.equalTo(44);
    }];
}

//退出编辑
- (void)endEdit
{
    [self.view endEditing:YES];
}

- (void)submitBtnClick
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    NSString *signature = [NSString stringByReplaceSpaceAndEnterKey:self.signature.text];

    WEAK(self);
    [RNTMineNetTool modifySignatureWithUserId:userM.user.userId signature:signature success:^(NSDictionary *dict) {
        if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
            userM.user.signature = signature;
            [[NSNotificationCenter defaultCenter] postNotificationName:USER_INFO_CHANGE object:nil];
            [weakself.navigationController popViewControllerAnimated:YES];
        } else {
            [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
        }
    } failure:^(NSError *error) {
        
    }];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - 通知
-(void)textViewEditChanged:(NSNotification *)obj{
    
    UITextView *textView = (UITextView *)obj.object;
    
    self.placeholderLabel.hidden = textView.text.length;
    
    NSString *toBeString = textView.text;
    ;
    NSString *lang = [self.signature textInputMode].primaryLanguage; // 键盘输入模式
    
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

#pragma mark - 懒加载
- (UITextView *)signature
{
    if (_signature == nil) {
        _signature = [[UITextView alloc] init];
        _signature.bounces = NO;
        _signature.showsHorizontalScrollIndicator = NO;
        _signature.showsVerticalScrollIndicator = NO;
        _signature.font = TextFont;
        _signature.textColor = TextColor;
        _signature.textContainerInset = UIEdgeInsetsMake(9, 5, 9, 5);
        _signature.layer.cornerRadius = 3.0;
        _signature.layer.masksToBounds = YES;
        _signature.layer.borderColor = RNTSeparatorColor.CGColor;
        _signature.layer.borderWidth = 0.5;
        _signature.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
    }
    return _signature;
}

- (UILabel *)placeholderLabel
{
    if (_placeholderLabel == nil) {
        _placeholderLabel = [[UILabel alloc] init];
        
        RNTUserManager *userM = [RNTUserManager sharedManager];
        
        NSString *placeholder;
        if (userM.user.signature.length > 0) {
            placeholder = userM.user.signature;
        } else {
            placeholder =  @"请输入个性签名";
        }
        _placeholderLabel.text = placeholder;
        _placeholderLabel.font = TextFont;
        _placeholderLabel.textColor = TextColor;
    }
    return _placeholderLabel;
}

- (RNTAccountManagerButton *)submitBtn
{
    if (_submitBtn == nil) {
        _submitBtn = [[RNTAccountManagerButton alloc] init];
        [_submitBtn setTitle:@"提交" forState:UIControlStateNormal];
        [_submitBtn addTarget:self action:@selector(submitBtnClick) forControlEvents:UIControlEventTouchUpInside];
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
