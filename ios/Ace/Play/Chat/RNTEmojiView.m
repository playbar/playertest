//
//  RNTEmojiView.m
//  Weibo
//
//  Created by 郭 旭赞 on 15/7/1.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import "RNTEmojiView.h"




#define emojiNumber 141
#define kNumberOfPages 8
#define kImageViewWidth 29
#define numberPerPage 21
#define numberPerRow 7
#define topMargin 17

@interface RNTEmojiView ()<UIScrollViewDelegate>

@property (nonatomic,strong) UIScrollView  *scrollView;
@property (nonatomic,strong) UIPageControl *pageControl;
@property (nonatomic, weak) UIView *gifView;

@property (nonatomic, weak) UIButton *emojiButton;
@property (nonatomic, weak) UIButton *gifButton;
@end

@implementation RNTEmojiView


- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        self.scrollView = [[UIScrollView alloc]init];
        self.scrollView.contentSize = CGSizeMake(kNumberOfPages*kScreenWidth, emojiViewHeight);
        self.scrollView.pagingEnabled = YES;
        self.scrollView.delegate = self;
        self.scrollView.showsVerticalScrollIndicator = NO;
        self.scrollView.showsHorizontalScrollIndicator = NO;
        [self addSubview:self.scrollView];
        [self.scrollView makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.top.bottom.equalTo(self);
        }];
        
        self.pageControl = [[UIPageControl alloc]initWithFrame:CGRectMake(0, 0, 0, 0)];
        self.pageControl.center = CGPointMake(CenterX(self), 160);
        self.pageControl.pageIndicatorTintColor = [UIColor lightGrayColor];
        self.pageControl.currentPageIndicatorTintColor = [UIColor grayColor];
        self.pageControl.numberOfPages = kNumberOfPages;
        [self addSubview:self.pageControl];
        
        [self setSubviews];
    }
    return self;
}



- (void)setSubviews {
    int i;
    int j;
    CGFloat distance = (kScreenWidth - numberPerRow*kImageViewWidth)/(numberPerRow+1);
    for (i=0; i<kNumberOfPages; i++) {
        
        //加表情
        for (j=0; j<numberPerPage; j++) {
            NSInteger rowNumber = j / numberPerRow;
            NSInteger queueNumber = j % numberPerRow;
            UIButton *imageView = [UIButton buttonWithType:UIButtonTypeCustom];
            CGFloat emojiX = i*kScreenWidth + distance + queueNumber*(distance+kImageViewWidth);
            CGFloat emojiY = topMargin+rowNumber*(topMargin+kImageViewWidth);
            imageView.frame = CGRectMake(emojiX, emojiY, kImageViewWidth,  kImageViewWidth);
            if (j == numberPerPage - 1) {//加删除键
                [imageView setImage:[UIImage imageNamed:@"emotion_delete"] forState:UIControlStateNormal];
                [imageView setImage:[UIImage imageNamed:@"emotion_delete_pressed"] forState:UIControlStateHighlighted];
                [imageView addTarget:self action:@selector(deleteButtonClick) forControlEvents:UIControlEventTouchUpInside];
            }else{
                NSString *imageName = [NSString stringWithFormat:@"emotion_%03d.png",(i*20+j+1)];
                [imageView setImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
                imageView.tag = (i*(numberPerPage-1)+j+1);
                [imageView addTarget:self action:@selector(imageViewClick:) forControlEvents:UIControlEventTouchUpInside];
            }
            [self.scrollView addSubview:imageView];        }
    }
}

- (void)imageViewClick:(UIButton *)sender {
    if (sender.tag > emojiNumber) {
        return;
    }
    if ([self.delegate respondsToSelector:@selector(selectEmojiWithName:)]) {
        NSString *imageName = [NSString stringWithFormat:@"emotion_%03zd",sender.tag];
        NSString *path = [[NSBundle mainBundle] pathForResource:@"emotion.plist" ofType:nil];
        NSDictionary *dic = [[NSDictionary alloc]initWithContentsOfFile:path];
        NSString *emojiName = nil;
        for (NSString* key in dic.allKeys) {
            NSString* value = dic[key];
            if ([value isEqualToString:imageName]) {
                emojiName = key;
                break;
            }
        }
        if (emojiName) {
            [self.delegate selectEmojiWithName:emojiName];
        }
    }
}

- (void)deleteButtonClick {
    if ([self.delegate respondsToSelector:@selector(deleteChatWord)]) {
        [self.delegate deleteChatWord];
    }
}

-(void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    self.pageControl.currentPage = scrollView.contentOffset.x/kScreenWidth;
}


@end
