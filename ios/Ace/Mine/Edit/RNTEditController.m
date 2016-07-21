//
//  RNTEditController.m
//  Ace
//
//  Created by 周兵 on 16/2/25.
//  Copyright © 2016年 RNT. All rights reserved.
//

#import "RNTEditController.h"
#import "RNTSignatureController.h"
#import <AVFoundation/AVFoundation.h>
#import <Photos/Photos.h>
#import "RNTMineNetTool.h"
#import "NSString+Extension.h"

#define MaxLength 8

typedef enum {
    PickerSourceTypePhotoLibrary, //照片
    PickerSourceTypeCamera //相机
} PickerSourceType;

@interface RNTEditController () <UIImagePickerControllerDelegate, UINavigationControllerDelegate>
@property (nonatomic, strong) UIImage *iconImage;//处理修改头像完成后的延迟问题
@end

@implementation RNTEditController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"编辑个人资料";
    self.view.backgroundColor = RNTBackgroundColor;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setData) name:USER_INFO_CHANGE object:nil];
    // 注册一个通知, 当textField文本发生变化的时候就会发送通知
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(textFieldEditChanged:) name:UITextFieldTextDidChangeNotification object:nil];
    [self setData];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setData
{
    [self.options removeAllObjects];
    RNTUserManager *userM = [RNTUserManager sharedManager];
    
    WEAK(self);
    RNTSettingGroup *group = [[RNTSettingGroup alloc] init];
    RNTSettingPhotoItem *photo = [[RNTSettingPhotoItem alloc] initWithIcon:nil title:@"头像" imageUrl:userM.user.profile];
    if (self.iconImage) {
        photo.image = self.iconImage;
    }
    photo.action = ^{
        [weakself rePhoto];
    };
    
    RNTSettingRightLabelAndArrowItem *name = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:nil title:@"昵称" rightLabelText:userM.user.nickName destClass:nil];
    WEAK(name);
    name.action = ^{
        [weakself reNickname:weakname.rightLabelText];
    };
    
    RNTSettingRightLabelItem *userID = [[RNTSettingRightLabelItem alloc] initWithIcon:nil title:@"ID" rightLabelText:userM.user.userId];
    
    RNTSettingSinatureItem *signature = [[RNTSettingSinatureItem alloc] initWithIcon:nil title:@"个性签名" sinature:userM.user.signature destClass:[RNTSignatureController class]];
    
    NSString *sex;
    if ([userM.user.sex isEqualToString:@"1"]) {
        sex = @"男";
    } else if ([userM.user.sex isEqualToString:@"0"]) {
        sex = @"女";
    }
    
    RNTSettingRightLabelAndArrowItem *gender = [[RNTSettingRightLabelAndArrowItem alloc] initWithIcon:nil title:@"性别" rightLabelText:sex destClass:nil];
    //有性别就不能修改
    if (!sex.length) {
        gender.action = ^{
            [weakself reGender];
        };
    }

    [group.items addObject:photo];
    [group.items addObject:name];
    [group.items addObject:userID];
    [group.items addObject:signature];
    [group.items addObject:gender];
    [self.options addObject:group];
    
    [self.tableView reloadData];
}

- (void)reGender
{
    WEAK(self);
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"请选择" message:nil preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *womanAction = [UIAlertAction actionWithTitle:@"女" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [weakself changeGender:@"0"];
    }];
    
    UIAlertAction *manAction = [UIAlertAction actionWithTitle:@"男" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [weakself changeGender:@"1"];
    }];
    
    [alertVC addAction:womanAction];
    [alertVC addAction:manAction];
    
    [self presentViewController:alertVC animated:YES completion:nil];
}

- (void)changeGender:(NSString *)gender
{
    RNTUserManager *userM = [RNTUserManager sharedManager];
    if (![userM.user.sex isEqualToString:gender]) {
        [RNTMineNetTool changeGenderWithUserId:userM.user.userId sex:gender success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                userM.user.sex = gender;
                [self setData];
            }
        } failure:^(NSError *error) {
            
        }];
    }
}

- (void)reNickname:(NSString *)nickname
{
    WEAK(self);
    UIAlertController *alertVC = [UIAlertController alertControllerWithTitle:@"修改昵称" message:nil preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    
    UIAlertAction *confirmAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        UITextField *textField = [alertVC.textFields objectAtIndex:0];
        NSString *nickName = [NSString stringByReplaceSpaceAndEnterKey:textField.text];
        if (nickName.length > 0) {
            RNTUserManager *userM = [RNTUserManager sharedManager];
            [RNTMineNetTool modifyNickNameWithUserId:userM.user.userId nickName:nickName success:^(NSDictionary *dict) {
                if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                    RNTUserManager *userM = [RNTUserManager sharedManager];
                    userM.user.nickName = nickName;
                    [weakself setData];
                } else {
                    [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
                }
            } failure:^(NSError *error) {
            }];
        }
    }];
    
    [alertVC addAction:cancelAction];
    [alertVC addAction:confirmAction];
    [alertVC addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
        textField.placeholder = nickname;
    }];

    [self presentViewController:alertVC animated:YES completion:nil];
}

- (void)rePhoto
{
    WEAK(self);
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    
    UIAlertAction *photoLibraryAction = [UIAlertAction actionWithTitle:@"从相册选择" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [weakself setIconFromePhotoLibrary];
    }];
    
    UIAlertAction *cameraAction = [UIAlertAction actionWithTitle:@"拍照" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [weakself setIconFromeCamera];
    }];
    
    [alert addAction:cancelAction];
    [alert addAction:photoLibraryAction];
    [alert addAction:cameraAction];
    
    [self presentViewController:alert animated:YES completion:nil];
}

- (void)setIconFromeCamera
{
    // 创建图片选择器
    UIImagePickerController *picker = [self imagePickerControllerWithSourceType:PickerSourceTypeCamera];
    
    //判断有无相机权限
    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    if (authStatus == AVAuthorizationStatusRestricted || authStatus ==AVAuthorizationStatusDenied)
    {
        [self showAlertWithTitle:@"无法启动相机" message:@"是否为Ace开放相机权限"];
    } else {
        // 3.显示图片选择器
        [self presentViewController:picker animated:YES completion:nil];
    }
}

- (void)setIconFromePhotoLibrary
{
    // 1.创建图片选择器
    UIImagePickerController *picker = [self imagePickerControllerWithSourceType:PickerSourceTypePhotoLibrary];
    
    PHAuthorizationStatus author = [PHPhotoLibrary authorizationStatus];
    if (author == PHAuthorizationStatusRestricted || author ==PHAuthorizationStatusDenied)
    {
        [self showAlertWithTitle:@"无法启动照片" message:@"是否为Ace开放照片权限"];
    } else {
        //显示图片选择器
        [self presentViewController:picker animated:YES completion:nil];
    }
}

- (UIImagePickerController *)imagePickerControllerWithSourceType:(PickerSourceType)sourceType
{
    // 1.创建图片选择器
    UIImagePickerController *picker = [[UIImagePickerController alloc] init];
    // 设置图片是否可以编辑
    picker.allowsEditing = YES;
    picker.delegate =  self;
    
    //设置选择器的类型,拍照,调取摄像头
    if (sourceType == PickerSourceTypeCamera) {
        picker.sourceType = UIImagePickerControllerSourceTypeCamera;
    } else if (sourceType == PickerSourceTypePhotoLibrary) {
        picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    }
    return picker;
}

- (void)showAlertWithTitle:(NSString *)title message:(NSString *)message
{
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    
    UIAlertAction *confirmAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        //跳到权限设置页
        if ([[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]]){
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
        }
    }];
    
    [alert addAction:cancelAction];
    [alert addAction:confirmAction];
    
    [self presentViewController:alert animated:YES completion:nil];
}

-(void)textFieldEditChanged:(NSNotification *)obj{
    
    UITextField *textField = (UITextField *)obj.object;
    
    NSString *toBeString = textField.text;
    ;
    NSString *lang = [textField textInputMode].primaryLanguage; // 键盘输入模式
    
    if ([lang isEqualToString:@"zh-Hans"]) { // 简体中文输入，包括简体拼音，健体五笔，简体手写
        UITextRange *selectedRange = [textField markedTextRange];
        //获取高亮部分
        UITextPosition *position = [textField positionFromPosition:selectedRange.start offset:0];
        // 没有高亮选择的字，则对已输入的文字进行字数统计和限制
        if (!position) {
            if (toBeString.length > MaxLength) {
                textField.text = [toBeString substringToIndex:MaxLength];
            }
        }
        // 有高亮选择的字符串，则暂不对文字进行统计和限制
        else{
            
        }
    }
    // 中文输入法以外的直接对其统计限制即可，不考虑其他语种情况
    else{
        if (toBeString.length > MaxLength) {
            textField.text = [toBeString substringToIndex:MaxLength];
        }
    }
}

#pragma mark - UIImagePickerControllerDelegate
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    WEAK(self);
    RNTUserManager *userM = [RNTUserManager sharedManager];
    [self.navigationController dismissViewControllerAnimated:YES completion:^{
        //获取选中图片
        UIImage *image = info[@"UIImagePickerControllerEditedImage"];
        NSData *iconData = UIImageJPEGRepresentation(image, 0.5);
        weakself.iconImage = image;
        
        [RNTMineNetTool modifyIconWithUserId:userM.user.userId iconData:iconData success:^(NSDictionary *dict) {
            if ([dict[@"exeStatus"] isEqualToString:@"1"]) {
                RNTSettingGroup *group = [self.options objectAtIndex:0];
                RNTSettingPhotoItem *photo = [group.items objectAtIndex:0];
                photo.image = image;
                NSUInteger newIndex[] = {0, 0};
                NSIndexPath *indexPath = [[NSIndexPath alloc] initWithIndexes:newIndex length:2];
                [weakself.tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
                [MBProgressHUD showSuccess:@"修改成功！"];
            } else {
                [MBProgressHUD showError:[NSString getErrorMessageWIthErrorCode:dict[@"errCode"]]];
            }
        } failure:^(NSError *error) {
            
        }];

        //获取服务器返回的图片地址
//        photo.imageUrl = 
    }];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    [self.navigationController dismissViewControllerAnimated:YES completion:nil];
}

@end
