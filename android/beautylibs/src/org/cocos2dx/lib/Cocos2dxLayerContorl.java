package org.cocos2dx.lib;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制Cocos2dx图层 添加和删除动画
 * Created by Dk on 16/6/2.
 */
public class Cocos2dxLayerContorl {

    private static List<String> queue = new ArrayList<String>();

    private String path;

    private static boolean needAddLayer;
    private static boolean needDelLayer;
    private static boolean isPlay;


    /**
     * 添加图层
     * strName,作为该图层的唯一标示
     *
     * @param type
     */
    public void addLayer(int type) {
        if (queue.size() > 0 && !isPlay) {
            //System.out.println("setEndListener----addLayer---" + queue.size());
            Cocos2dxLayerManager.addLayer(queue.get(0), type);
            isPlay = true;
            needAddLayer = false;
        }
//        if( needAddLayer ){
//            //Cocos2dxLayerManager.addLayer("/storage/emulated/0", Cocos2dxLayerManager.EnLayerType.LAYER_TYPE_SKELETON.nCode);
//            Cocos2dxLayerManager.addLayer(queue.get(0), type);
//            needAddLayer = false;
//        }
    }

    /**
     * 通过唯一标示<strName>删除图层
     */
    public void delLayer() {
        if (needDelLayer) {
            if (queue.size() > 0) {
                Cocos2dxLayerManager.delLayer(queue.get(0));
                queue.remove(0);
            }
            needDelLayer = false;
            isPlay = false;
        }
//        if(needDelLayer){
//            Cocos2dxLayerManager.delLayer(queue.get(0));
//            needDelLayer = false;
//            if (queue.size() > 0) {
//                queue.remove(0);
//            }
//        }
    }

    /**
     * 添加到队列中
     *
     * @param path
     */
    public void addGift(String path) {
        queue.add(path);
        needAddLayer = true;
        //System.out.println("setEndListener----addGift---" + queue.size());
    }

    public void onActivityDestroy(){
        if(isPlay){
            needDelLayer = true;
            delLayer();
        }
        isPlay = false;
        queue.clear();
        needAddLayer = false;
        needDelLayer = false;
    }

    public static void skeletonEnd(String strName) {
        needDelLayer = true;
//        if (queue.size() > 0) {
//            if (queue.size() == 1) {
//                needAddLayer = false;
//            }
//            queue.remove(0);
        //System.out.println("setEndListener----addGift---" + queue.size());
//        }
    }
}
