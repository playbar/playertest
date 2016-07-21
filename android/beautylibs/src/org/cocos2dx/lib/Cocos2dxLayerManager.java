package org.cocos2dx.lib;

import android.util.Log;

public class Cocos2dxLayerManager {

    public enum EnLayerType{
        LAYER_TYPE_SKELETON(1),
        LAYER_TYPE_PARTICLE(2),
        LAYER_TYPE_SPRITE3D(3);

        public int nCode;
        private EnLayerType(int code){
            nCode = code;
        }

        public String toString(){
            return String.valueOf(this.nCode);
        }

    };
    // strName,作为该图层的唯一标示
    public static void addLayer(String strName, int type){
        nativeAddLayer( strName, type );
    }

    public static void delLayer(String strName){
        nativeDelLayer(strName);
    }

    public static void skeletonEnd(String strName){
        Log.e("Cocos2dxLayerManager->skeletonEnd", strName);
        Cocos2dxLayerContorl.skeletonEnd(strName);
    }

    private static native void nativeAddLayer(String strName, int type);
    private static native void nativeDelLayer(String strName);
}
