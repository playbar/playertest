//
//  RNTEmojiView.h
//  Weibo
//
//  Created by 郭 旭赞 on 15/7/1.
//  Copyright (c) 2015年 Rednovo. All rights reserved.
//

#import <UIKit/UIKit.h>


#define emotionKeyBoardHeight (emojiViewHeight + 15)
#define emojiViewHeight 178

@protocol RNTEmojiViewDelegate <NSObject>
@required
- (void)selectEmojiWithName:(NSString *)emojiName;
- (void)deleteChatWord;

@end

@interface RNTEmojiView : UIView

@property (nonatomic,weak) id<RNTEmojiViewDelegate> delegate;

@end
