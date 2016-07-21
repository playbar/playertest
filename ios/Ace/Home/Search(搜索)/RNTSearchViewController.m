//
//  RNTSearchViewController.m
//  Ace
//
//  Created by 靳峰 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSearchViewController.h"
#import "RNTSearchTableViewCell.h"
#import "RNTHomePageViewController.h"
#import "MBProgressHUD+RNT.h"
#import "RNTHomeNetTool.h"


@interface RNTSearchViewController ()<UITextFieldDelegate,UITableViewDelegate,UITableViewDataSource>
//搜索框
@property(nonatomic,strong) UITextField *textField;
//取消按钮
@property(nonatomic,strong) UIButton *cancelBtn;
//列表
@property(nonatomic,strong) UITableView *table;
//模型数组
@property(nonatomic,strong) NSArray  *modelArr;

@end

@implementation RNTSearchViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = RNTBackgroundColor;
    [self setSubviews];
}

-(void)viewWillAppear:(BOOL)animated
{
    [self.navigationController setNavigationBarHidden:NO animated:YES];
    self.navigationItem.leftBarButtonItem = nil;
    self.navigationItem.hidesBackButton = YES;
    [super viewWillAppear:animated];

}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self.textField endEditing:YES];
}


#pragma mark - 添加子控件
-(void)setSubviews
{
    UIView *contentView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kScreenWidth, 28)];
    //搜索框
    self.textField = [[UITextField alloc] initWithFrame:CGRectMake(0, 0, 319*kScreenWidth/375, 28)];
    self.textField.backgroundColor = RNTColor_16(0xe0b900);
    self.textField.placeholder = @"搜索主播名或者用户id";
    self.textField.font = [UIFont systemFontOfSize:13];
    self.textField.returnKeyType = UIReturnKeySearch;
    self.textField.layer.cornerRadius = 3;
    self.textField.clipsToBounds = YES;
    self.textField.clearButtonMode = UITextFieldViewModeWhileEditing;
//    self.textField.layer.borderWidth =1;
//    self.textField.layer.borderColor =RNTColor_16(0xd6a907).CGColor;
    self.textField.delegate = self;
    self.textField.layer.masksToBounds = YES;
    UIView *paddingView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 32, 28)];
    UIImageView *image = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, 15, 15)];
    [paddingView addSubview:image];
    image.image = [UIImage imageNamed:@"home_find"];
    image.center = paddingView.center;
    self.textField.leftView = paddingView;
    self.textField.leftViewMode = UITextFieldViewModeAlways;
    [self.textField becomeFirstResponder];
    [contentView addSubview:self.textField];
    
    //取消
    self.cancelBtn = [[UIButton alloc] init];
    [self.cancelBtn setTitle:@"取消" forState:UIControlStateNormal];
    [self.cancelBtn setTitleColor:RNTColor_16(0x4a3c17) forState:UIControlStateNormal];
    self.cancelBtn.titleLabel.font = [UIFont systemFontOfSize:15];
    [self.cancelBtn addTarget:self action:@selector(backNav) forControlEvents:UIControlEventTouchUpInside];
    [contentView addSubview:self.cancelBtn];
    [self.cancelBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        if (iPhone6P || iPhone6) {
            make.right.mas_equalTo(contentView).offset(-1);
        }else{
            make.right.mas_equalTo(contentView).offset(2);
        }
        make.top.mas_equalTo(contentView).offset(-2);
    }];
    
    self.navigationItem.titleView = contentView;

    //列表
    self.table = [[UITableView alloc] initWithFrame:self.view.bounds];
    self.table.rowHeight = 67;
    self.table.delegate = self;
    self.table.dataSource = self;
    self.table.tableFooterView = [[UIView alloc] init];
    [self.view addSubview:self.table];

}


#pragma mark - 按钮点击

-(void)backNav
{
    [self.textField endEditing:YES];
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - UITextFieldDelegate
- (BOOL)textFieldShouldReturn:(UITextField *)textField;
{
    if (self.textField.text.length == 0 || self.textField.text == nil) {
        [MBProgressHUD showError:@"请输入搜索内容"];
        return YES;
    }
    
    [RNTHomeNetTool getSearchListWithText:self.textField.text pageNum:1 pageSize:100 getSuccess:^(NSArray *searchArr) {
        self.modelArr = searchArr;
        if (self.modelArr.count == 0) {
            [MBProgressHUD showError:@"没有相应的搜索内容QAQ"];
        }
        [self.table reloadData];
    } getFail:^{
        
    }];
    [self.textField resignFirstResponder];
    
    return YES;
}


#pragma mark - UITableViewDataSource

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    

    return self.modelArr.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    RNTSearchTableViewCell *cell = [RNTSearchTableViewCell cellWithTableView:tableView];
    RNTUser *userModel = self.modelArr[indexPath.row];
    cell.user = userModel;
    return cell;
}

#pragma mark - UITableViewDelegate
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RNTUser *userModel = self.modelArr[indexPath.row];
    RNTHomePageViewController *homePage = [[RNTHomePageViewController alloc] init];
    homePage.userId = userModel.userId;
    [self.navigationController pushViewController:homePage animated:YES];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];

}

-(NSArray *)modelArr
{
    if (!_modelArr) {
        _modelArr = [NSArray array];
    }
    return _modelArr;
}
@end
