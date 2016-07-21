//
//  RNTSocketTool.m
//  Ace
//
//  Created by Ranger on 16/3/3.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTSocketTool.h"
#import "GCDAsyncSocket.h"
#import "RNTSocketSummary.h"
#import "MJExtension.h"
#import "AFNetworkReachabilityManager.h"
#import "AppDelegate.h"
#import "RNTNetTool.h"
#import "UIDevice+RNTDeviceType.h"
#import "OpenUDID.h"

#define SocketIP @"172.16.150.21"
#define SocketPORT 9999

@interface RNTSocketTool ()

@property (nonatomic, strong) GCDAsyncSocket *socket;

@property (nonatomic, strong) NSMutableData *buffer;

@property (nonatomic, copy) SocketConnectBlock success;

@property (nonatomic, copy) SocketConnectBlock fail;

@property (nonatomic, strong) NSTimer *heartTimer;// 心跳
@property (nonatomic, strong) NSDate *lastTime; // 上次收到消息时间
@property (nonatomic, strong) NSTimer *monitorTimer;// 监测

@property (nonatomic, assign) NSInteger reconnectCount;// 重联次数
@property (nonatomic, assign) NSInteger addressCount;// 获取地址次数

@property (nonatomic, copy) NSString *ip;
@property (nonatomic, copy) NSString *port;

@end

@implementation RNTSocketTool

+ (RNTSocketTool *)shareInstance
{
    static id shareInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        shareInstance = [[self alloc] init];
    });
    
    return shareInstance;
}


#pragma mark - socket连接
- (void)socketSetup
{
    if (self.socket == nil) {
        self.buffer = [[NSMutableData alloc] init];
        self.reconnectCount = 0;
        self.addressCount = 0;
        dispatch_queue_t socketQueue = dispatch_queue_create("SocketQueue", NULL);
        self.socket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:socketQueue];
                
        // 添加监听
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(socketAutoReconnect) name:RNTNetworkConnectedNotification object:nil];

    }
}

// 获取socket地址
- (void)getSocketAdress:(void (^)())success
{
    if (self.addressCount < 3) {
        [RNTNetTool postJSONWithURL:@"008-001" params:nil success:^(NSDictionary *responseDic) {
            self.ip = responseDic[@"server"][@"ip"];
            self.port = responseDic[@"server"][@"port"];
            if (success) {
                success();
            }
        } failure:^(NSError *error) {
            [self getSocketAdress:success];
            self.addressCount++;
        }];
    }else {
        [MBProgressHUD showError:@"网络异常"];
    }
}


- (void)socketConnectSuccess:(SocketConnectBlock)success fail:(SocketConnectBlock)fail
{
    self.success = success;
    self.fail = fail;
    if ([self.socket isDisconnected]) {
        
        WEAK(self);
        [self getSocketAdress:^{
            NSError *error = nil;
            [weakself.socket connectToHost:weakself.ip onPort:weakself.port.intValue error:&error];
            if (error) {
                RNTLog(@"connectError = %@", error);
            }
        }];
    }else {
        return;
    }
}

- (void)socketDisconnect
{
//    self.buffer = nil;
//    if ([self.socket isConnected]) {
//        [self.socket setDelegate:nil];
//        [self.socket disconnectAfterReadingAndWriting];
//        self.socket = nil;
//    }
    [self.heartTimer invalidate];
    self.heartTimer = nil;
    
    [self.monitorTimer invalidate];
    self.monitorTimer = nil;
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    [self.socket setDelegate:nil];
    [self.socket disconnectAfterReadingAndWriting];
    self.socket = nil;
}

- (void)socketAutoReconnect
{
    if ([self.socket isConnected]) return;
    if (self.reconnectCount > 20) {
        self.reconnectCount = 0;
        return;
    };
    
    NSError *error = nil;
    [self.socket connectToHost:self.ip onPort:self.port.intValue error:&error];
    
    
    if (error) {
        RNTLog(@"reconnectError = %@", error);
    }
    
    // 递归重联
    self.reconnectCount++;
    [self socketAutoReconnect];
}

- (BOOL)isConnected
{
    if (self.socket) {
        return self.socket.isConnected;
    }else {
        return NO;
    }
}

#pragma mark - socket数据
/*
 发送房间用户信息： 201
 发送消息 : 202
 房间信息： 203
 送礼：204
 点赞：205
 
 进房间：301
 退房间：302
 踢人：303
 暂停：304
 */
#pragma mark 发送数据
- (void)writeMessage:(NSMutableDictionary *)parameter andSummary:(RNTSocketSummary *)summary withTag:(long)tag
{
    
    RNTLog(@"*************************************WriteMessage**********************************");
//    parameter[@"chatMode"] = @"1";
    // 拼basicData
    [parameter addEntriesFromDictionary:[self BasicData]];
    // body
    NSError *bodyErr;
    NSData *bodyData = [NSJSONSerialization dataWithJSONObject:parameter options:NSJSONWritingPrettyPrinted error:&bodyErr];
    int bodyLen = (int)bodyData.length;
    
    // summary
//    NSError *summaryErrDict;
//    NSDictionary *summaryDict = [summary keyValuesWithError:&summaryErrDict];
    NSDictionary *summaryDict = summary.mj_keyValues;
    NSError *summaryErrData;
    NSData *summaryData = [NSJSONSerialization dataWithJSONObject:summaryDict options:NSJSONWritingPrettyPrinted error:&summaryErrData];
    int summaryLength = (int)summaryData.length;
    
    // ok
    NSData *okData = [@"ok" dataUsingEncoding:NSUTF8StringEncoding];
    
    // header
    int totalLength = 10 + summaryLength + bodyLen;
    
    // data
    NSMutableData *data = [[NSMutableData alloc] init];
    
    uint32_t totalLen = CFSwapInt32HostToBig(totalLength);
    uint32_t summaryLen = CFSwapInt32HostToBig(summaryLength);
    
    [data appendBytes:&totalLen length:4];
    [data appendBytes:&summaryLen length:4];
    [data appendData:summaryData];
    [data appendData:bodyData];
    [data appendData:okData];
    
    RNTLog(@"messageTag = %@", @(tag));
    RNTLog(@"body = %@ err = %@ len = %d", parameter, bodyErr, bodyLen);
    RNTLog(@"summary = %@ len = %d errData = %@", summaryDict, summaryLength, summaryErrData);
    RNTLog(@"totallen = %d", totalLength);
    
    // 发送
    [self.socket writeData:data withTimeout:-1 tag:tag];
    
    RNTLog(@"************************************************************************************");
}

- (void)writeData:(NSMutableData *)data andSummary:(RNTSocketSummary *)summary withTag:(long)tag
{
    RNTLog(@"*************************************WriteData**************************************");
    // 拼basicData
    // body
    int bodyLen = (int)data.length;
    
    // summary
//    NSError *summaryErrDict;
//    NSDictionary *summaryDict = [summary keyValuesWithError:&summaryErrDict];
    NSDictionary *summaryDict = summary.mj_keyValues;
    NSError *summaryErrData;
    NSData *summaryData = [NSJSONSerialization dataWithJSONObject:summaryDict options:NSJSONWritingPrettyPrinted error:&summaryErrData];
    int summaryLength = (int)summaryData.length;
    
    // ok
    NSData *okData = [@"ok" dataUsingEncoding:NSUTF8StringEncoding];
    
    // header
    int totalLength = 10 + summaryLength + bodyLen;
    
    // data
    NSMutableData *writeData = [[NSMutableData alloc] init];
    
    uint32_t totalLen = CFSwapInt32HostToBig(totalLength);
    uint32_t summaryLen = CFSwapInt32HostToBig(summaryLength);
    
    [writeData appendBytes:&totalLen length:4];
    [writeData appendBytes:&summaryLen length:4];
    [writeData appendData:summaryData];
    [writeData appendData:data];
    [writeData appendData:okData];
    
    RNTLog(@"dataTag = %@", @(tag));
    RNTLog(@"body len = %d",  bodyLen);
    RNTLog(@"summary = %@ len = %d errData = %@", summaryDict, summaryLength, summaryErrData);
    RNTLog(@"totallen = %d", totalLength);
    
    // 发送
    [self.socket writeData:writeData withTimeout:-1 tag:tag];
    RNTLog(@"*************************************************************************************");
}


// 发送心跳
- (void)sendHeartbeat
{
    RNTSocketSummary *heartSum = [[RNTSocketSummary alloc] init];
    heartSum.requestKey = @"009-004";
    
    NSMutableDictionary *para = [NSMutableDictionary dictionary];
    para[@"body"] = @"hello";
    [self writeMessage:para andSummary:heartSum withTag:999];

}

#pragma mark 接收数据
- (void)readData:(NSData *)data withTag:(long)tag
{
//    RNTLog(@"read thread = %@", [NSThread currentThread]);
    
    if (data.length > 0) {
        
        if(data.length < 4){//由于粘包导致的数据包长度不够获取total长度(4个字节),所以要重读一次数据
            
            [self.buffer appendData:data];
            [self.socket readDataWithTimeout:-1 tag:tag];
            return;
        }
        
        // 获取总长
        if (self.buffer.length == 0) {
            
            self.buffer = nil;
            self.buffer = [[NSMutableData alloc] init];
            RNTLog(@"####  NOBUFFER");
            int totalLength;
            [data getBytes:&totalLength length:4];
            uint32_t totalLen =  CFSwapInt32BigToHost(totalLength);
            
            int dataLen = (int)data.length;
            RNTLog(@"totalL = %d  dataL = %d", totalLen, dataLen);
            
            if (totalLen == dataLen || totalLen < dataLen) {// 接收到的是一个完整的数据包可以正常解析
//                RNTLog(@"正常包");
                // 获取summary长
                int summaryLength;
                [data getBytes:&summaryLength range:NSMakeRange(4, 4)];
                uint32_t summaryLen =  CFSwapInt32BigToHost(summaryLength);
                
                // 获取summary
                NSData *summaryData = [data subdataWithRange:NSMakeRange(8, summaryLen)];
//                RNTSocketSummary *summary = [RNTSocketSummary objectWithJSONData:summaryData];
                RNTSocketSummary *summary = [RNTSocketSummary mj_objectWithKeyValues:summaryData];
                //            NSDictionary *summaryDict = [NSJSONSerialization JSONObjectWithData:summaryData options:NSJSONReadingMutableContainers error:nil];
                
                //            RNTLog(@" summaryL = %d *** summary = %@",  summaryLen, summaryDict );
                
                // 获取body
                int bodyLen = totalLen - summaryLen - 10;
                int bodyLoc = 8 + summaryLen;
                
                NSData *bodyData = [data subdataWithRange:NSMakeRange(bodyLoc, bodyLen)];
                //            NSDictionary *bodyDict = [NSJSONSerialization JSONObjectWithData:bodyData options:NSJSONReadingMutableContainers error:&bodyErr];
                
//                RNTLog(@"bodylen = %d *** loc = %d \n ", bodyLen, bodyLoc);
                
                // 获取ok
                int okLen = 2;
                int okLoc = totalLen - 2;
                NSData *okData = [data subdataWithRange:NSMakeRange(okLoc, okLen)];
                NSString *ok = [[NSString alloc] initWithData:okData encoding:NSUTF8StringEncoding];
//                RNTLog(@"OK = %@ Loc = %d", ok, okLoc);
                
                if ([ok isEqualToString:@"ok"]) {
                    
                    //                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:bodyData options:NSJSONReadingMutableContainers error:nil];
                    //                RNTLog(@"readDict sum =%@ = %@ mode=  %@",summary.requestKey, dict, summary.interactMode);
                    self.lastTime = [NSDate date];
                    if ([summary.interactMode isEqualToString:@"1"] && self.socketResponse) {
                       
                        
                        // 回调
                        dispatch_async(dispatch_get_main_queue(), ^{
                            self.socketResponse(bodyData, summary);
                        });
                    }
                    
//                    if ([summary.interactMode isEqualToString:@"0"] && self.socketRequest) { // 回调
//                        dispatch_async(dispatch_get_main_queue(), ^{
//                            self.socketRequest(bodyData, summary);
//                        });
//                    }
                    
                    if ([summary.interactMode isEqualToString:@"0"]) { //广播事件
                        
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self socketEventWithSummary:summary andData:bodyData];
                        });
                    }
//                    RNTLog(@"*********************************NO BUFFER 数据包解析正常*********************************");
                }else {
                    RNTLog(@"NO BUFFER 数据包解析异常");
                    // 解析异常 继续读后续数据
                    [self.socket readDataWithTimeout:-1 tag:10];
                }
                
                // 判断data是否还有内容
                if (dataLen > totalLen ) {// data还有数据 出现粘包
//                    RNTLog(@"出现粘包");
                    int subDataLen = dataLen - totalLen;
                    int subDataLoc = totalLen;
                    NSData *subData = [data subdataWithRange:NSMakeRange(subDataLoc, subDataLen)];
                    [self readData:subData withTag:tag];
//                    RNTLog(@"subLen = %d loc = %d", subDataLen, subDataLoc);
                }
                
            }else if(totalLen > dataLen){// 获取的是半包,把获到的数据存到缓存,再调用本身
                
//                RNTLog(@"出现半包");
                self.buffer = nil;
                self.buffer = [[NSMutableData alloc] init];
                [self.buffer appendData:data];
                [self.socket readDataWithTimeout:-1 tag:tag];
                
            }
        } else {// 缓冲区有数据 解析缓冲区数据
//            RNTLog(@"####  BUFFER");
            // 拼接传进来的data
            [self.buffer appendData:data];
            
            int totalLength;
            [self.buffer getBytes:&totalLength length:4];
            uint32_t totalLen =  CFSwapInt32BigToHost(totalLength);
            
            int bufferLen = (int)self.buffer.length;
//            RNTLog(@" buffer  totalL = %d  dataL = %d", totalLen, bufferLen);
            
            if (totalLen == bufferLen || totalLen < bufferLen) {// 接收到的是一个完整的数据包可以正常解析
//                RNTLog(@"buffer正常包");
                // 获取summary长
                int summaryLength;
                [self.buffer getBytes:&summaryLength range:NSMakeRange(4, 4)];
                uint32_t summaryLen =  CFSwapInt32BigToHost(summaryLength);
                
                // 获取summary
                NSData *summaryData = [self.buffer subdataWithRange:NSMakeRange(8, summaryLen)];
                RNTSocketSummary *summary = [RNTSocketSummary mj_objectWithKeyValues:summaryData];
                //            NSDictionary *summaryDict = [NSJSONSerialization JSONObjectWithData:summaryData options:NSJSONReadingMutableContainers error:nil];
                
                //            RNTLog(@"buffer summaryL = %d *** summary = %@",  summaryLen, summaryDict );
                
                // 获取body
                int bodyLen = totalLen - summaryLen - 10;
                int bodyLoc = 8 + summaryLen;
                
                NSData *bodyData = [self.buffer subdataWithRange:NSMakeRange(bodyLoc, bodyLen)];
                //            NSError *bodyErr;
                //            NSDictionary *bodyDict = [NSJSONSerialization JSONObjectWithData:bodyData options:NSJSONReadingMutableContainers error:&bodyErr];
                
//                RNTLog(@"buffer bodylen = %d *** loc = %d", bodyLen, bodyLoc);
                
                // 获取ok
                int okLen = 2;
                int okLoc = totalLen - 2;
                NSData *okData = [self.buffer subdataWithRange:NSMakeRange(okLoc, okLen)];
                NSString *ok = [[NSString alloc] initWithData:okData encoding:NSUTF8StringEncoding];
//                RNTLog(@"buffer OK = %@ Loc = %d", ok, okLoc);
                
                if ([ok isEqualToString:@"ok"]) {
                    //                NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:bodyData options:NSJSONReadingMutableContainers error:nil];
                    //                  RNTLog(@"readDict sum =%@ = %@ mode=  %@",summary.requestKey, dict, summary.interactMode);
                    
                    self.lastTime = [NSDate date];
                    if ([summary.interactMode isEqualToString:@"1"] && self.socketResponse) {
                        
                        // 回调
                        dispatch_async(dispatch_get_main_queue(), ^{
                            self.socketResponse(bodyData, summary);
                        });
                    }
                    
//                    if ([summary.interactMode isEqualToString:@"0"] && self.socketRequest) { // 回调
//                        dispatch_async(dispatch_get_main_queue(), ^{
//                            self.socketRequest(bodyData, summary);
//                        });
//                    }
                    if ([summary.interactMode isEqualToString:@"0"]) {

                        //广播事件
                        dispatch_async(dispatch_get_main_queue(), ^{
                            [self socketEventWithSummary:summary andData:bodyData];
                        });
                    }

                    
//                    RNTLog(@"*********************************NO BUFFER 数据包解析正常*********************************");
                    
                }else {
                    RNTLog(@"buffer 数据包解析异常");
                    
                    self.buffer = nil;
                    self.buffer = [NSMutableData data];
                    // 解析异常 继续读后续数据
                    [self.socket readDataWithTimeout:-1 tag:10];
                }
                
                // 判断data是否还有内容
                if (bufferLen > totalLen ) {// data还有数据 出现粘包
//                    RNTLog(@"buffer 出现粘包");
                    int subDataLen = bufferLen - totalLen;
                    int subDataLoc = totalLen;
                    NSData *subData = [self.buffer subdataWithRange:NSMakeRange(subDataLoc, subDataLen)];
                    
                    // 清除已经解析过的数据
                    self.buffer = nil;
                    self.buffer = [[NSMutableData alloc] init];
                    
                    [self readData:subData withTag:tag];
//                    RNTLog(@"buffer subLen = %d loc = %d  bufferLen = %@" , subDataLen, subDataLoc, @(self.buffer.length));
                    
                }else if (bufferLen == totalLen) {
                    // buffer没有多余数据  清空buffer
                    self.buffer = nil;
                    self.buffer = [[NSMutableData alloc] init];
                }
                
            }else if(totalLen > bufferLen){// 获取的是半包,再读数据
                
//                RNTLog(@"buffer 出现半包");
                [self.socket readDataWithTimeout:-1 tag:tag];
            }
        }
        // 解析完毕 等待数据
        [self.socket readDataWithTimeout:-1 tag:0];
    }
}


#pragma mark - socket事件
- (void)socketEventWithSummary:(RNTSocketSummary *)summary andData:(NSData *)data
{
    RNTLog(@"resuestKey == %@", summary.requestKey);
    if ([summary.requestKey isEqualToString:@"002-009"]) {// 聊天
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveChatMessage:summary:)]) {
            [self.delegate socketDidReceiveChatMessage:data summary:summary];
        }
        
    }else if ([summary.requestKey isEqualToString:@"002-018"]) {// 分享，礼物，点赞数据
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveBroadcast:summary:)]) {
            [self.delegate socketDidReceiveBroadcast:data summary:summary];
        }
        
    }else if ([summary.requestKey isEqualToString:@"002-010"]) {// 礼物数据
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveGift:summary:)]) {
            [self.delegate socketDidReceiveGift:data summary:summary];
        }
        
    }else if ([summary.requestKey isEqualToString:@"004-003"]) {// 直播间动态
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveRoomState:summary:)]) {
            [self.delegate socketDidReceiveRoomState:data summary:summary];
        }
        
    }else if ([summary.requestKey isEqualToString:@"002-001"]) {// 进出房间
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveUserInOutRoom:summary:)]) {
            [self.delegate socketDidReceiveUserInOutRoom:data summary:summary];
        }
        
    } else if ([summary.requestKey isEqualToString:@"004-004"]){// 直播结束
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveEnd:summary:)]) {
            [self.delegate socketDidReceiveEnd:data summary:summary];
        }
        
    }
//    else if ([summary.requestKey isEqualToString:@"004-005"]){// 直播暂停
//        if ([self.delegate respondsToSelector:@selector(socketDidReceivePause:summary:)]) {
//            [self.delegate socketDidReceivePause:data summary:summary];
//        }
//        
//    }else if ([summary.requestKey isEqualToString:@"002-021"]){// 封号
//        if ([self.delegate respondsToSelector:@selector(socketDidReceiveBannedAccount:summary:)]) {
//            [self.delegate socketDidReceiveBannedAccount:data summary:summary];
//        }
//        
//    }else if ([summary.requestKey isEqualToString:@"002-020"]){// 下播
//        if ([self.delegate respondsToSelector:@selector(socketDidReceiveDownLive:summary:)]) {
//            [self.delegate socketDidReceiveDownLive:data summary:summary];
//        }
//        
//    }
    else if ([summary.requestKey isEqualToString:@"002-007"]){// 踢人
        if ([self.delegate respondsToSelector:@selector(socketDidReceiveKickOut:summary:)]) {
            [self.delegate socketDidReceiveKickOut:data summary:summary];
        }
        
    }
}


#pragma mark - socket代理
// 联接成功
- (void)socket:(GCDAsyncSocket *)sock didConnectToHost:(NSString *)host port:(uint16_t)port
{
    self.buffer = [[NSMutableData alloc] init];
    
    if (self.success) {
        self.success();
    }

    dispatch_async(dispatch_get_main_queue(), ^{
        //发送心跳包
        //开启定时器
        self.heartTimer = [NSTimer scheduledTimerWithTimeInterval:10 target:self selector:@selector(sendHeartbeat) userInfo:nil repeats:YES];
        
        // 告诉事件循环分一点事件来处理定时器
        [[NSRunLoop mainRunLoop] addTimer:self.heartTimer forMode:NSRunLoopCommonModes];
        
        
        self.monitorTimer = [NSTimer scheduledTimerWithTimeInterval:20 target:self selector:@selector(monitorSocket) userInfo:nil repeats:YES];
        
        [[NSRunLoop mainRunLoop] addTimer:self.monitorTimer forMode:NSRunLoopCommonModes];
    });
}

// 连接失败
- (void)socketDidDisconnect:(GCDAsyncSocket *)sock withError:(NSError *)err
{
    RNTLog(@"断开连接");
    RNTLog(@" %@", err.description);
    self.buffer = nil;
    
    if (self.fail) {
        self.fail();
    }
    
    // 主动断开 不重联
    if (!err) return;
    
    
    NSError *error = nil;
    
    //根据网络状态判断是否重连
    AppDelegate *app = (AppDelegate *)[UIApplication sharedApplication].delegate;
    if (app.hasNetwork) {
        
        [self.socket connectToHost:self.ip onPort:self.port.intValue withTimeout:-1 error:&error];
        
        if (error) {
            RNTLog(@"链接失败重联 = %@",error.description);
        }
    }
}


// 写完会被调用
-(void)socket:(GCDAsyncSocket *)sock didWriteDataWithTag:(long)tag
{
    RNTLog(@"数据发送成功");
    RNTLog(@"writeTag = %@", @(tag));
    //发送完 读取下数据
    [sock readDataWithTimeout:-1 tag:tag];
}


-(void)socket:(GCDAsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag
{
//    RNTLog(@"有消息可读");
//    RNTLog(@"readTag = %@", @(tag));
    [self readData:data withTag:tag];
}

#pragma mark - 监测
- (void)monitorSocket
{
    NSDate *currentTime = [NSDate date];
    
    NSTimeInterval time = [currentTime timeIntervalSinceDate:self.lastTime];
    
    if (time > 20) {// 认定网络异常 socket断开
        RNTLog(@"长时间无心跳数据 断开连接");
        [self socketDisconnect];
        
        // 延时重联
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        
            [self socketSetup];
            [self socketAutoReconnect];
        });
    }
}

#pragma mark - BasicData
- (NSDictionary *)BasicData
{
    //设备型号
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *platform = [defaults valueForKey:@"platform"];
    if (!platform) {
        platform = [UIDevice getDevicePlatform];
        [defaults setValue:platform forKey:@"platform"];
        [defaults synchronize];
    }
    //系统版本
    NSString *systemVersion = [UIDevice currentDevice].systemVersion;
    //应用版本
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary]objectForKey:@"CFBundleShortVersionString"];
    //udid
    NSString *udid = [OpenUDID value];
    //时间戳
    NSString *timestamp = [NSString stringWithFormat:@"%.f", [[NSDate date] timeIntervalSince1970] * 1000];
    
    return @{@"platform" : platform, @"systemVersion" : systemVersion, @"appVersion" : appVersion, @"udid" : udid, @"timestamp" : timestamp};
}


@end
