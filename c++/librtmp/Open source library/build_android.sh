#!/bin/bash



setup_pkgconfig()
{
echo "prefix=`pwd`/build/android
exec_prefix=\${prefix}
libdir=\${prefix}/lib
incdir=\${prefix}/include

Name: librtmp
Description: RTMP implementation
Version: v2.4
Requires:
URL: http://rtmpdump.mplayerhq.hu
Libs: -L\${libdir} -lrtmp -lz
Libs.private:
Cflags: -I\${incdir}" > build/android/lib/pkgconfig/librtmp.pc

}



case "$1" in
    'clean')
        ndk-build NDK_PROJECT_PATH=. clean
        rm -rf build
        rm -rf obj
    ;;

    *)
        ndk-build NDK_PROJECT_PATH=.
	mkdir build
	mkdir build/android
	mkdir build/android/include
	mkdir build/android/include/librtmp
	mkdir build/android/lib
	mkdir build/android/lib/pkgconfig
	

	cp librtmp/amf.h build/android/include/librtmp/
	cp librtmp/http.h build/android/include/librtmp/
	cp librtmp/log.h build/android/include/librtmp/
	cp librtmp/rtmp.h build/android/include/librtmp/

	cp obj/local/armeabi-v7a/librtmp.a build/android/lib

	setup_pkgconfig
    ;;
esac



