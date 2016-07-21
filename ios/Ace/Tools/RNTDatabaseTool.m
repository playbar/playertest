//
//  RNTDatabaseTool.m
//  Ace
//
//  Created by 周兵 on 16/2/26.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTDatabaseTool.h"
#import "FMDB.h"
#import "MJExtension.h"

@implementation RNTDatabaseTool
static FMDatabase *_db;

+ (void)initialize
{
    NSString *docPath = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    NSString *filePath = [docPath stringByAppendingPathComponent:@"Ace.sqlite"];
    
    // 1.创建FMDatabase对象
    _db = [FMDatabase databaseWithPath:filePath];
    
    // 2.打开数据库
    BOOL success = [_db open];
    if (success) {
        
        // 浏览过的主播
        [_db executeUpdate:@"create table if not exists t_BrowsingHistory (ID INTEGER PRIMARY KEY AUTOINCREMENT, anchorInfo blob not null, userID TEXT  not null, anchorID TEXT  not null)"];
        
        //首页数据
        [_db executeUpdate:@"create table if not exists t_HomepageList (ID INTEGER PRIMARY KEY AUTOINCREMENT, userList blob not null, showList blob not null)"];
        
        // 黑名单
        [_db executeUpdate:@"create table if not exists t_BlackList (ID INTEGER PRIMARY KEY AUTOINCREMENT, anchorInfo blob not null, userID TEXT  not null, anchorID TEXT  not null)"];
        
        // 订单
        [_db executeUpdate:@"create table if not exists t_ReceiptList (ID INTEGER PRIMARY KEY AUTOINCREMENT, receiptData blob not null, userID TEXT  not null, timestamp TEXT  not null, version TEXT not null)"];
        
//        // 极光推送registrationID
//        [_db executeUpdate:@"create table if not exists t_JPushRegistrationIDList (ID INTEGER PRIMARY KEY AUTOINCREMENT, userID TEXT  not null, registrationID TEXT not null)"];
        
        [_db close];
    }
}

#pragma mark - 首页
+ (NSDictionary *)getHomepageList
{
//    NSMutableArray *anchorArr = [NSMutableArray array];
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    NSString *sql = [NSString stringWithFormat:@"select * from t_HomepageList"];
    
    if ([_db open]) {
        
        FMResultSet *set = [_db executeQuery:sql];
        
        while ([set next]) {
            
            NSData *showData = [set dataForColumn:@"showList"];
            NSData *userData = [set dataForColumn:@"userList"];
            NSArray *showList = [NSKeyedUnarchiver unarchiveObjectWithData:showData];
            NSArray *userList = [NSKeyedUnarchiver unarchiveObjectWithData:userData];
            [dict setObject:showList forKey:@"showList"];
            [dict setObject:userList forKey:@"userList"];
        }
        [_db close];
    }
    return dict;
}

+ (void)updataHomepageWithShowList:(NSArray *)showList userList:(NSArray *)userList
{
    NSData *showData = [NSKeyedArchiver archivedDataWithRootObject:showList];
    NSData *userData = [NSKeyedArchiver archivedDataWithRootObject:userList];
    
    if ([_db open])
    {
        [_db setShouldCacheStatements:YES];

        //删除操作
        [_db executeUpdate:@"delete from t_HomepageList"];
        
        // 插入数据
        [_db executeUpdate:@"insert into t_HomepageList (showList, userList) values(?, ?)", showData, userData];
        
        [_db close];
    }
}

#pragma mark - 浏览记录
+ (void)saveAnchor:(RNTUser *)anchor
{
    NSString *userID = [RNTUserManager sharedManager].user.userId;
    
    NSDictionary *anchorDict = anchor.mj_keyValues;
    if ([_db open]) {
        //删除之前的数据
        NSString *sql = [NSString stringWithFormat:@"delete from t_BrowsingHistory where userID = '%@' AND anchorID = '%@'", userID, anchor.userId];
        [_db executeUpdate:sql];
        
        // 插入数据
        NSData *anchorData = [NSKeyedArchiver archivedDataWithRootObject:anchorDict];
        [_db executeUpdate:@"insert into t_BrowsingHistory (anchorInfo, userID, anchorID) values(?, ?, ?)", anchorData, userID, anchor.userId];
        
        [_db close];
    }
}

/**
 *  获取浏览记录
 *
 *  @return 主播数组
 */
+ (NSArray *)getAnchorHistory
{
    NSMutableArray *anchorArr = [NSMutableArray array];
    
    //根据ID倒序查询
    NSString *sql = [NSString stringWithFormat:@"select * from t_BrowsingHistory where userID = '%@' order by ID DESC", [RNTUserManager sharedManager].user.userId];
    if ([_db open]) {
        
        FMResultSet *set = [_db executeQuery:sql];
        
        while ([set next]) {
            
            NSData *data = [set dataForColumn:@"anchorInfo"];
            
            NSDictionary *anchorDict = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            [anchorArr addObject:anchorDict];
        }
        [_db close];
    }
    return [RNTUser mj_objectArrayWithKeyValuesArray:anchorArr];
}

#pragma mark - 黑名单
/**
 *  加入黑名单
 *
 */
+ (void)saveBlackList:(RNTUser *)anchor
{
    NSString *userID = [RNTUserManager sharedManager].user.userId;
    
    NSDictionary *anchorDict = anchor.mj_keyValues;
    if ([_db open]) {
        //删除之前的数据
        NSString *sql = [NSString stringWithFormat:@"delete from t_BlackList where userID = '%@' AND anchorID = '%@'", userID, anchor.userId];
        [_db executeUpdate:sql];
        
        // 插入数据
        NSData *anchorData = [NSKeyedArchiver archivedDataWithRootObject:anchorDict];
        [_db executeUpdate:@"insert into t_BlackList (anchorInfo, userID, anchorID) values(?, ?, ?)", anchorData, userID, anchor.userId];
        
        [_db close];
    }
}

/**
 *  从黑名单删除
 *
 */
+ (void)deleteFromBlackList:(RNTUser *)anchor
{
    NSString *userID = [RNTUserManager sharedManager].user.userId;
    
    if ([_db open]) {
        //删除之前的数据
        NSString *sql = [NSString stringWithFormat:@"delete from t_BlackList where userID = '%@' AND anchorID = '%@'", userID, anchor.userId];
        [_db executeUpdate:sql];
        
        [_db close];
    }
}

/**
 *  获取黑名单
 *
 *  @return 用户数组
 */
+ (NSArray *)getBlackList
{
    NSMutableArray *anchorArr = [NSMutableArray array];
    
    //根据ID倒序查询
    NSString *sql = [NSString stringWithFormat:@"select * from t_BlackList where userID = '%@' order by ID DESC", [RNTUserManager sharedManager].user.userId];
    if ([_db open]) {
        
        FMResultSet *set = [_db executeQuery:sql];
        
        while ([set next]) {
            
            NSData *data = [set dataForColumn:@"anchorInfo"];
            
            NSDictionary *anchorDict = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            [anchorArr addObject:anchorDict];
        }
        [_db close];
    }
    return [RNTUser mj_objectArrayWithKeyValuesArray:anchorArr];
}

/**
 *  是否为黑名单用户
 *
 */
+ (BOOL)isBlackList:(RNTUser *)anchor
{
    NSMutableArray *anchorArr = [NSMutableArray array];
    
    //根据ID倒序查询
    NSString *sql = [NSString stringWithFormat:@"select * from t_BlackList where userID = '%@' AND anchorID = '%@'", [RNTUserManager sharedManager].user.userId, anchor.userId];
    if ([_db open]) {
        
        FMResultSet *set = [_db executeQuery:sql];
        
        while ([set next]) {
            
            NSData *data = [set dataForColumn:@"anchorInfo"];
            
            NSDictionary *anchorDict = [NSKeyedUnarchiver unarchiveObjectWithData:data];
            
            [anchorArr addObject:anchorDict];
        }
        [_db close];
    }
    return anchorArr.count > 0;
}

#pragma mark - 订单相关
/**
 *  存储购买凭据
 *
 *  @param receiptData  凭据
 *  @param version      应用版本号
 *  @param timestamp    时间戳
 *  @param userID       用户id
 */
+ (void)saveReceiptDict:(NSDictionary *)receiptDict
{
    if ([_db open]) {
        // 插入数据
        [_db executeUpdate:@"insert into t_ReceiptList (receiptData, userID, timestamp, version) values(?, ?, ?, ?)", receiptDict[@"receiptData"], receiptDict[@"userID"], receiptDict[@"timestamp"], receiptDict[@"version"]];
        
        [_db close];
    }
}
/**
 *  获取未验证凭据
 *
 *  @return 凭据数组
 */
+ (NSArray *)getReceiptArray
{
    NSMutableArray *receiptArr = [NSMutableArray array];
    
    //根据时间戳倒序查询
    NSString *sql = [NSString stringWithFormat:@"select * from t_ReceiptList order by timestamp DESC"];
    if ([_db open]) {
        
        FMResultSet *set = [_db executeQuery:sql];
        
        while ([set next]) {
            
            NSData *receiptData = [set dataForColumn:@"receiptData"];
            NSString *timestamp = [set stringForColumn:@"timestamp"];
            NSString *userID = [set stringForColumn:@"userID"];
            NSString *version = [set stringForColumn:@"version"];
            
            NSDictionary *receiptDict = @{@"receiptData" : receiptData, @"timestamp" : timestamp, @"userID" : userID, @"version": version};
            
            [receiptArr addObject:receiptDict];
        }
        [_db close];
    }
    return receiptArr;
}
/**
 *  删除购买凭据
 *
 *  @param receiptDict
 */
+ (void)deleteReceiptWithReceipt:(NSDictionary *)receiptDict
{
    if ([_db open]) {
        //删除之前的数据
        NSString *sql = [NSString stringWithFormat:@"delete from t_ReceiptList where userID = '%@' AND timestamp = '%@' AND version = '%@'", receiptDict[@"userID"], receiptDict[@"timestamp"], receiptDict[@"version"]];
        [_db executeUpdate:sql];
        
        [_db close];
    }
}

#pragma mark - 极光推送
//+ (void)saveJPushRegistrationID:(NSString *)registrationID userID:(NSString *)userID
//{
//    if ([_db open]) {
//        //删除之前的数据
//        NSString *sql = [NSString stringWithFormat:@"delete from t_JPushRegistrationIDList where userID = '%@'", userID];
//        BOOL A = [_db executeUpdate:sql];
//        
//        // 插入数据
//        BOOL B = [_db executeUpdate:@"insert into t_JPushRegistrationIDList (registrationID, userID) values(?, ?)", registrationID, userID];
//        RNTLog(@"%d  %d  %@  %@", A, B, registrationID, userID);
//        
//        [_db close];
//    }
//}
//
//+ (void)deleteRegistrationIDWithUserID:(NSString *)userID
//{
//    if ([_db open]) {
//        //删除之前的数据
//        NSString *sql = [NSString stringWithFormat:@"delete from t_JPushRegistrationIDList where userID = '%@'", userID];
//        [_db executeUpdate:sql];
//        
//        [_db close];
//    }
//}
//
//+ (NSArray *)getRegistrationIDWithUserID:(NSString *)userID
//{
//    NSMutableArray *registrationIDArr = [NSMutableArray array];
//    
//    NSString *sql = [NSString stringWithFormat:@"select * from t_JPushRegistrationIDList where userID = '%@'", userID];
//    if ([_db open]) {
//        
//        FMResultSet *set = [_db executeQuery:sql];
//        
//        while ([set next]) {
//            
//            NSString *registrationID = [set stringForColumn:@"registrationID"];
//            
//            [registrationIDArr addObject:registrationID];
//        }
//        [_db close];
//    }
//    return registrationIDArr;
//}
@end
