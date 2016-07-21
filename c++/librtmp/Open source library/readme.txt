android编译
1.将rtmpdump-2.4.zip上传到ubuntu 14上解压(需要配置好ndk环境变量)
2.将build_android.sh上传到rtmpdump-2.4文件目录下
3.执行chmod 777 build_android.sh
4.执行./build_android.sh

ios编译
1.将rtmpdump-2.4.zip上传到mac上解压
2.将rtmpdump-2.4文件夹名称修改为rtmpdump
3.上传build_ios.sh,与rtmpdump文件目录同一级
4.执行chmod 777 build_ios.sh
5.执行./build_ios.sh
6.会生成include文件夹和lib文件夹,编译后的头文件和静态库都在这两个文件夹里