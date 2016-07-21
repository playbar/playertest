package com.rednovo.ace.net.parser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dk on 16/3/21.
 */
public class GoodListResult extends BaseResult{

    private List<GoodListEntity> goodList;

    public void setGoodList(List<GoodListEntity> goodList) {
        this.goodList = goodList;
    }

    public List<GoodListEntity> getGoodList() {
        return goodList;
    }

    public static class GoodListEntity implements Serializable{
        private int coinPrice;
        private String createTime;
        private String description;
        private String id;
        private String isCombined;
        private String name;
        private float rmbPrice;
        private String schemaId;
        private String status;
        private String type;
        private String updateTime;

        public void setCoinPrice(int coinPrice) {
            this.coinPrice = coinPrice;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public void setRmbPrice(float rmbPrice) {
            this.rmbPrice = rmbPrice;
        }

        public void setSchemaId(String schemaId) {
            this.schemaId = schemaId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getCoinPrice() {
            return coinPrice;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getDescription() {
            return description;
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

        public float getRmbPrice() {
            return rmbPrice;
        }

        public String getSchemaId() {
            return schemaId;
        }

        public String getStatus() {
            return status;
        }

        public String getType() {
            return type;
        }

        public String getUpdateTime() {
            return updateTime;
        }
    }
}
