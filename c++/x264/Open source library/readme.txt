android编译
1.将x264.tar.gz上传到ubuntu 14上解压(需要配置好ndk环境变量)
2.将build_android.sh上传到解压后的x264文件目录下
3.执行chmod 777 build_android.sh
4.执行./build_android.sh

ios编译
1.安装yasm,下载地址:http://www.tortall.net/projects/yasm/releases/yasm-1.3.0.tar.gz
2.编译和安装yasm,进入yasm目录,执行./configure && make && make install
3.将x264.tar.gz上传到mac上解压
4.上传build_ios.sh,与解压后的x264文件目录同一级
5.执行chmod 777 build_ios.sh
6.执行./build_ios.sh
7.会生成一个x264-ios的文件夹,编译后的头文件和静态库都在这个文件夹里