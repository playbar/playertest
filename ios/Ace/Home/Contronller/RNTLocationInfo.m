//
//  RNTLocationInfo.m
//  Ace
//
//  Created by 靳峰 on 16/3/12.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTLocationInfo.h"
#import <CoreLocation/CoreLocation.h>
#import "Singleton.h"

@interface RNTLocationInfo()<CLLocationManagerDelegate>
//定位管理器
@property(nonatomic,strong) CLLocationManager *locationManager;
//位置
@property(nonatomic,copy) NSString *location;
//回调
@property(nonatomic,copy) void(^locaInfo)(NSString *);
@end

@implementation RNTLocationInfo

//外部接口
+(void)getLoacationInfo:(void(^)(NSString *location)) localInfo
{

    RNTLocationInfo *info = [RNTLocationInfo sharedLocal];
    [info getInfo];
    info.locaInfo = localInfo;
}

-(void)getInfo
{
    self.locationManager.delegate = self;
    //定位管理器
    self.locationManager=[[CLLocationManager alloc]init];
    
    self.locationManager.delegate = self;
    //定位服务未打开
    if (![CLLocationManager locationServicesEnabled]) {
        RNTLog(@"定位服务当前可能尚未打开，请设置打开");
    }
    
    //如果没有授权则请求用户授权
    if([self.locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)]) {
        
        [self.locationManager requestWhenInUseAuthorization]; //使用中授权
        
    }
    
    
    //启动跟踪定位
    [self.locationManager startUpdatingLocation];
}


#pragma mark - CLLocationManagerDelegate

-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations{
    //取出第一个位置
    CLLocation *location=[locations firstObject];
    
    CLGeocoder *geocoder=[[CLGeocoder alloc]init];
    //地理反编码
    [geocoder reverseGeocodeLocation:location completionHandler:^(NSArray<CLPlacemark *> * _Nullable placemarks, NSError * _Nullable error) {
        CLPlacemark *placemark=[placemarks firstObject];
        
        NSString *city = placemark.addressDictionary[@"City"];
//        NSString *sublocality = placemark.addressDictionary[@"SubLocality"];
//        
//        if (sublocality.length>0) {
//            
//            self.location = [NSString stringWithFormat:@"%@ - %@",placemark.addressDictionary[@"City"],placemark.addressDictionary[@"SubLocality"]];
//            
//        }else{
            self.location =  [NSString stringWithFormat:@"%@",city];
//        }
        
        if (error) {
            self.location = @"error";
        }
    }];
    
    //    如果不需要实时定位，使用完即使关闭定位服务
    [self.locationManager stopUpdatingLocation];
}

#pragma mark - set方法
-(void)setLocation:(NSString *)location
{
    _location = location;
    if (self.locaInfo) {
        self.locaInfo(location);
    }
}

#pragma mark - 懒加载
-(CLLocationManager *)locationManager
{
    if (!_locationManager) {
        _locationManager = [[CLLocationManager alloc]init];
        //设置定位精度
        _locationManager.desiredAccuracy=kCLLocationAccuracyHundredMeters;
        //设置代理
        _locationManager.delegate=self;
    }
    return _locationManager;
}
SingletonM(Local)


@end
