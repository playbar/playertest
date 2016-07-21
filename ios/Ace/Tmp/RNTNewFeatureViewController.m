//
//  RNTNewFeatureViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTNewFeatureViewController.h"
#import "RNTTabBarContronller.h"

#define UserDefaults [NSUserDefaults standardUserDefaults]
#define VersionKey @"version"
#define kimagesCount 3

#define ButtonW 400
#define ButtonH  70

@interface RNTNewFeatureViewController ()<UICollectionViewDelegate,UICollectionViewDataSource>

@property(nonatomic,strong) UICollectionView *collectionView;
//@property(nonatomic,strong) NSMutableArray *imagesArr;
@property (nonatomic,strong)NSMutableArray *countArray;

@end

@implementation RNTNewFeatureViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [UserDefaults setBool:YES forKey:@"CanShowComments"];
    [UserDefaults setBool:NO forKey:@"IsWatched"];
    [UserDefaults synchronize];
    
    self.view.backgroundColor  = RNTBackgroundColor;
    
    UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
    layout.itemSize = self.view.bounds.size;
    layout.minimumLineSpacing = 0 ;
    layout.minimumInteritemSpacing = 0;
    layout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
    
    self.collectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, kScreenHeight) collectionViewLayout:layout];
    self.collectionView.delegate = self;
    self.collectionView.dataSource = self;
    self.collectionView.pagingEnabled = YES;
    self.collectionView.bounces = NO;
    self.collectionView.showsHorizontalScrollIndicator = NO;
    [self.collectionView registerClass:[UICollectionViewCell class] forCellWithReuseIdentifier:@"newFeatureCell"];
    [self.view addSubview:self.collectionView];
    
    [self setUpData];
    
//    //图片数组
//    for (int i = 0; i< kimagesCount; i++) {
//        NSString *imagesName = self.countArray[i];
//        [self.imagesArr addObject:imagesName];
//    }
    [self.collectionView reloadData];

}

/**
 *  设置图片
 */
- (void)setUpData
{
    if (kScreenWidth == 320 && kScreenHeight == 480) {
        self.countArray = [NSMutableArray arrayWithObjects:@"newFeature_iPhone4_0",@"newFeature_iPhone4_1",@"newFeature_iPhone4_2", nil];
    }else if (kScreenWidth == 320 && kScreenHeight == 568){
        self.countArray = [NSMutableArray arrayWithObjects:@"newFeature_iPhone5_0",@"newFeature_iPhone5_1",@"newFeature_iPhone5_2", nil];
    }else{
        self.countArray = [NSMutableArray arrayWithObjects:@"newFeature_iPhone6_0",@"newFeature_iPhone6_1",@"newFeature_iPhone6_2", nil];
    }
    
}

/**
 *  更换根控制器
 */
- (void)changeRootViewController
{
    RNTTabBarContronller *tabBarVC = [[RNTTabBarContronller alloc]init];
    
    [UIApplication sharedApplication].keyWindow.rootViewController = tabBarVC;
    
    // 获取Info.plist
    NSDictionary *infoDict = [NSBundle mainBundle].infoDictionary;
    // 获取当前用户的软件版本
    NSString *currentVersion = infoDict[@"CFBundleShortVersionString"];
    
    // 存储最新版本号
    [UserDefaults setObject:currentVersion forKey:VersionKey];
    // 同步
    [UserDefaults synchronize];
    
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.countArray.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"newFeatureCell" forIndexPath:indexPath];

    UIImageView *imageView = [[UIImageView alloc] initWithFrame:cell.bounds];
    imageView.image = [UIImage imageNamed:self.countArray[indexPath.item]];
    imageView.backgroundColor = kRandomColor;
    [cell.contentView addSubview:imageView];
    
    //是否为最后一页
    if (indexPath.item == kimagesCount -1) {
        UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, ButtonW, ButtonH)];
        btn.center = CGPointMake(kScreenWidth*0.5, kScreenHeight-50-ButtonH*0.5);
        [cell addSubview:btn];
        [btn addTarget:self action:@selector(changeRootViewController) forControlEvents:UIControlEventTouchUpInside];
    }
    
    return cell;
}
@end
