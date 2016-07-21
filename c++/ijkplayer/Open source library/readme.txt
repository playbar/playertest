android编译
1.将ijkplayer-k0.4.5.1.tar.gz上传到ubuntu 14上解压(需要配置好ndk环境变量)
2.执行cd config
3.执行rm module.sh
4.执行cd ..
5.执行./init-config.sh
6.执行./init-android.sh
7.执行cd android/contrib
8.执行./compile-ffmpeg.sh clean
9.执行./compile-ffmpeg.sh armv7a
10.执行cd ..
11.执行./compile-ijk.sh armv7a

ios编译
1.新建ijkplayer文件夹
2.将ijkplayer-k0.4.5.1.tar.gz上传到ijkplayer文件夹下后解压
3.在ijkplayer文件夹下新建3个文件夹x264,fdk-aac,librtmp
4.将编译好的x264,fdk-aac,librtmp的头文件和静态库分别拷贝到对应文件夹下
5.修改librtmp/lib/pkgconfig下的librtmp.pc文件内容,"prefix"字段的内容需要修改为当前librmtp文件夹所在的绝对路径
6.执行cd ijkplayer-k0.4.5.1
7.执行cd config
8.执行rm module.sh
9.执行cd ..
10.执行./init-config.sh
11.执行./init-ios.sh
12.执行cd iOS
13.执行./compile-ffmpeg.sh clean
14.执行./compile-ffmpeg.sh all