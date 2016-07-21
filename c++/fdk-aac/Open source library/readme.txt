android编译
1.将fdk-aac-0.1.4.zip上传到ubuntu 14上解压(需要配置好ndk环境变量)
2.将build_android.sh上传到fdk-aac-0.1.4文件目录下
3.执行chmod 777 build_android.sh
4.执行./build_android.sh

iOS编译
1.将fdk-aac-0.1.4.zip上传到mac上解压
2.将build_ios.sh上传到fdk-aac-0.1.4文件目录下
3.执行chmod 777 autogen.sh
4.执行chmod 777 build_ios.sh
5.执行./autogen.sh
6.执行./build_ios.sh
7.会生成一个libfdk-aac的文件夹,编译后的头文件和静态库都在这个文件夹里