package com.rednovo.ace.net.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/9.
 */
public class GiftListResult extends BaseResult {

    /**
     * createTime : 2016-03-04 22:54:05 468
     * id : 2
     * isCombined : 0
     * name : 水晶鞋
     * pic : http://172.16.150.2/images/11.jpg
     * schemaId : 1457103245468
     * sendPrice : 100
     * status : 1
     * transformPrice : 100
     * type: 1,超级礼物，不可连送
     */
    private List<GiftListEntity> giftList = new ArrayList<GiftListEntity>();

    public void setGiftList(List<GiftListEntity> giftList) {
        this.giftList = giftList;
    }

    public List<GiftListEntity> getGiftList() {
        return giftList;
    }

    public static class GiftListEntity implements Serializable {
        private String createTime;
        private String id;
        private String isCombined;
        private String name;
        private String pic;
        private String schemaId;
        private int sendPrice;
        private String status;
        private int transformPrice;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setIsCombined(String isCombined) {
            this.isCombined = isCombined;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public void setSchemaId(String schemaId) {
            this.schemaId = schemaId;
        }

        public void setSendPrice(int sendPrice) {
            this.sendPrice = sendPrice;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setTransformPrice(int transformPrice) {
            this.transformPrice = transformPrice;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getId() {
            return id;
        }

        public String getIsCombined() {
            return isCombined;
        }

        public String getName() {
            return name;
        }

        public String getPic() {
            return pic;
        }

        public String getSchemaId() {
            return schemaId;
        }

        public int getSendPrice() {
            return sendPrice;
        }

        public String getStatus() {
            return status;
        }

        public int getTransformPrice() {
            return transformPrice;
        }
    }
}
