//
//  RNTPlayUserListView.m
//  Ace
//
//  Created by 于传峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTPlayUserListView.h"
#import "UIImageView+WebCache.h"
#import "RNTPlayUserListUserInfo.h"
#import "RNTPlayNetWorkTool.h"

@interface RNTPlayUserListView()<UICollectionViewDataSource, UICollectionViewDelegate>
@property (nonatomic, weak) UICollectionView *collectionView;
@property (nonatomic, assign, getter=isLoading) BOOL loading;
@property (nonatomic, assign) BOOL firstLoad;
@property (nonatomic, strong) NSMutableArray *infos;
@property (nonatomic, strong) NSTimer *timer;

@end

static NSString* const ID = @"userListID";
//static int pageNum;
@implementation RNTPlayUserListView

@synthesize infos = _infos;

- (NSMutableArray *)infos
{
    if (!_infos) {
        _infos = [[NSMutableArray alloc] init];
    }
    return _infos;
}
- (void)setInfos:(NSMutableArray *)infos
{
    _infos = infos;
    RNTPlayUserListUserInfo* lastInfo = [[RNTPlayUserListUserInfo alloc] init];
    lastInfo.userId = @"-1";
    [_infos addObject:lastInfo];
    [self.collectionView reloadData];
}
- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        UICollectionViewFlowLayout* flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.itemSize = CGSizeMake(userListIconWH, userListIconWH);
        flowLayout.minimumLineSpacing = 5;
        flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        
        UICollectionView* collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        collectionView.backgroundColor = [UIColor clearColor];
        collectionView.showsHorizontalScrollIndicator = NO;
        collectionView.showsVerticalScrollIndicator = NO;
        collectionView.dataSource = self;
        collectionView.delegate = self;
        [collectionView registerClass:[UICollectionViewCell class] forCellWithReuseIdentifier:ID];
        [self addSubview:collectionView];
        self.collectionView = collectionView;
        [collectionView makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        
//        pageNum = 1;
    }
    return self;
}

- (void)didMoveToSuperview
{
    [super didMoveToSuperview];
    [self getUserInfosData];
//    if (self.superview) {
//        NSTimer* timer = [NSTimer timerWithTimeInterval:30 target:self selector:@selector(getUserInfosData) userInfo:nil repeats:YES];
//        [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
//        self.timer = timer;
//        [self.timer fire];
//    }
}

//- (void)removeFromSuperview
//{
//    [self.timer invalidate];
//    self.timer = nil;
//    [super removeFromSuperview];
//}

- (void)getUserInfosData
{
    RNTLog(@"重新获取观众列表-----");
    [RNTPlayNetWorkTool getLivingUserListWithPageNum:1 pageSize:1000 showID:self.userID getSuccess:^(NSDictionary *dict) {
        NSArray* infos = [RNTPlayUserListUserInfo userInfoWithDictArray:dict[@"memberList"]];
        self.infos = [[[infos reverseObjectEnumerator] allObjects] mutableCopy];
    }];
}
#pragma mark - UICollectionViewDataSource
//- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
//{
//    return 1;
//}
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    NSInteger count = self.infos.count;
//    RNTLog(@"%@", [NSThread currentThread]);
    return count;
}
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewCell* cell = [collectionView dequeueReusableCellWithReuseIdentifier:ID forIndexPath:indexPath];
    if (!cell.backgroundView) {
        UIImageView* iconView = [[UIImageView  alloc] init];
        iconView.backgroundColor = [UIColor whiteColor];
        iconView.layer.cornerRadius = 0.5 * userListIconWH;
        iconView.clipsToBounds = YES;
        cell.backgroundView = iconView;
    }
    RNTPlayUserListUserInfo* info = self.infos[indexPath.row];
    UIImageView* iconView = (UIImageView *)cell.backgroundView;
    if (indexPath.row == self.infos.count - 1) {
        iconView.image = [UIImage imageNamed:@"play_userList_last"];
    }else{
        [iconView sd_setImageWithURL:[NSURL URLWithString:info.profile] placeholderImage:[UIImage imageNamed:@"PlaceholderIcon"]];
//        if (indexPath.row == 0) {
//            CABasicAnimation *scaleAni = [CABasicAnimation animation];
//            scaleAni.keyPath = @"transform.scale";
//            scaleAni.fromValue = [NSNumber numberWithFloat:0];
//            scaleAni.toValue = [NSNumber numberWithFloat:1];
//            scaleAni.duration = 0.2;
//            [iconView.layer addAnimation:scaleAni forKey:@"scaleAni"];
//        }
    }
    
    return cell;
}

//#pragma mark - getMoreListInfo
//- (void)getMoreUserListInfo
//{
//    if (self.isLoading) {
//        return;
//    }
//    RNTLog(@"=====");
//    self.loading = YES;
//    pageNum ++;
//    [RNTPlayNetWorkTool getLivingUserListWithPageNum:pageNum pageSize:20 showID:self.userID getSuccess:^(NSDictionary *dict) {
//        NSArray* moreIndos = [RNTPlayUserListUserInfo userInfoWithDictArray:dict[@"memberList"]];
//        [self.infos addObjectsFromArray:moreIndos];
//        [self.collectionView reloadData];
//        self.loading = NO;
//    }];
//}

#pragma mark - UICollectionViewDelegate
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    if ([self.delegate respondsToSelector:@selector(userListView:selectedUserWith:)]) {
        [self.delegate userListView:self selectedUserWith:self.infos[indexPath.row]];
    }
}

- (void)collectionView:(UICollectionView *)collectionView willDisplayCell:(UICollectionViewCell *)cell forItemAtIndexPath:(NSIndexPath *)indexPath{
        if (indexPath.item == 0) {
            cell.hidden = YES;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                cell.transform = CGAffineTransformMakeScale(0.1, 0.1);
                [UIView animateWithDuration:0.35 delay:0 usingSpringWithDamping:0.5 initialSpringVelocity:0.6 options:0 animations:^{
                    cell.transform = CGAffineTransformIdentity;
                    cell.hidden = NO;
                } completion:nil];
            });
        }
}
#pragma mark - UIScrollViewDelegate
//- (void)scrollViewDidScroll:(UIScrollView *)scrollView
//{
//    CGFloat contentWidth = scrollView.contentSize.width;
//    CGFloat contentOffsetX = scrollView.contentOffset.x;
//    if (contentWidth - (contentOffsetX + scrollView.bounds.size.width) < 100) {
//        [self getMoreUserListInfo];
//    }
//}

- (void)addUser:(RNTPlayUserListUserInfo *)info
{
    if (!info) {
        return;
    }
    
    if (self.infos.count > 500) {
        [self.infos removeLastObject];
        [self.collectionView deleteItemsAtIndexPaths:@[[NSIndexPath indexPathForItem:self.infos.count-1 inSection:0]]];
    }
    
    [self.infos insertObject:info atIndex:0];
    
    [self.collectionView insertItemsAtIndexPaths:@[[NSIndexPath indexPathForItem:0 inSection:0]]];
//    [self.collectionView reloadData];
    
//    NSArray *indexPaths = @[[NSIndexPath indexPathForItem:0 inSection:0]];
//    dispatch_block_t updates = ^{
//        if (countBeforeInsert < 1) {
////            [self.collectionView insertSections:[NSIndexSet indexSetWithIndex:0]];
//        }
//        [self.collectionView insertItemsAtIndexPaths:indexPaths];
//    };
//    [self.collectionView performBatchUpdates:updates completion:nil];
    
//    [self.collectionView insertItemsAtIndexPaths:@[[NSIndexPath indexPathForItem:0 inSection:0]]];

}

- (void)removeUserID:(NSString *)userID
{
    for (RNTPlayUserListUserInfo *userInfo in self.infos) {
        if ([userID isEqualToString:userInfo.userId]) {
            [self.infos removeObject:userInfo];
            break;
        }
    }
    
    [self.collectionView reloadData];
}

- (void)dealloc
{
    RNTLog(@"--");
}
@end
