#!/bin/bash


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
        mkdir build/android/include/fdk-aac
        mkdir build/android/lib

        cp libAACdec/include/aacdecoder_lib.h build/android/include/fdk-aac/
        cp libAACenc/include/aacenc_lib.h build/android/include/fdk-aac/
        cp libSYS/include/FDK_audio.h build/android/include/fdk-aac/
        cp libSYS/include/genericStds.h build/android/include/fdk-aac/
        cp libSYS/include/machine_type.h build/android/include/fdk-aac/

        cp obj/local/armeabi-v7a/libFraunhoferAAC.a build/android/lib/libfdk-aac.a
    ;;
esac


