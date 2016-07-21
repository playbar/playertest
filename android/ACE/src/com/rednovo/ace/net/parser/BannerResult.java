package com.rednovo.ace.net.parser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilong on 16/3/13.
 */
public class BannerResult extends BaseResult{
    private ArrayList<AdEntity> ad = new ArrayList<AdEntity>();

    public void setAd(ArrayList<AdEntity> ad) {
        this.ad = ad;
    }

    public ArrayList<AdEntity> getAd() {
        return ad;
    }

    public static class AdEntity {
        private String title;
        private String imgUrl;
        private String addres;

        public void setAddres(String addres) {
            this.addres = addres;
        }

        public String getAddres() {
            return addres;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setImgUrl(String url) {
            this.imgUrl = url;
        }

        public String getTitle() {
            return title;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }
}
