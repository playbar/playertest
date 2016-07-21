package com.rednovo.ace.net.parser;


import java.util.ArrayList;

/**
 * Created by lilong on 16/3/8.
 */
public class SubscribeResult extends BaseResult {

    private ArrayList<Data> subscribeList = new ArrayList<Data>();

    public ArrayList<Data> getSubscribeList() {
        return subscribeList;
    }

    public void setSubscribeList(ArrayList<Data> subscribeList) {
        this.subscribeList = subscribeList;
    }


    public static class Data {
        private int basicScore;
        private String channel;
        private String createTime;
        private String nickName;
        private String profile;
        private String rank;
        private String schemaId;
        private String sex;
        private String signature;
        private String status;
        private String updateTime;
        private String userId;
        private String uuid;

        public void setBasicScore(int basicScore) {
            this.basicScore = basicScore;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public void setSchemaId(String schemaId) {
            this.schemaId = schemaId;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getBasicScore() {
            return basicScore;
        }

        public String getChannel() {
            return channel;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getNickName() {
            return nickName;
        }

        public String getProfile() {
            return profile;
        }

        public String getRank() {
            return rank;
        }

        public String getSchemaId() {
            return schemaId;
        }

        public String getSex() {
            return sex;
        }

        public String getSignature() {
            return signature;
        }

        public String getStatus() {
            return status;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public String getUserId() {
            return userId;
        }

        public String getUuid() {
            return uuid;
        }
    }

}
