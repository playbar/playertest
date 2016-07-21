package com.rednovo.ace.net.parser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dk on 16/3/12.
 */
public class MyFansListResult extends BaseResult {

    private List<MyFansResult> fansList;

    public List<MyFansResult> getFansList() {
        return fansList;
    }

    public void setFansList(List<MyFansResult> fansList) {
        this.fansList = fansList;
    }

    public static class MyFansResult implements Serializable{

        private String basicScore;
        private String channel;
        private String createTime;
        private String nickName;
        private String passWord;
        private String profile;
        private String rank;
        private String schemaId;
        private String sex;
        private String showImg;
        private String signature;
        private String status;
        private String subscribeCnt;
        private String tokenId;
        private String updateTime;
        private String userId;
        private String uuid;

        public void setBasicScore(String basicScore) {
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

        public void setPassWord(String passWord) {
            this.passWord = passWord;
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

        public void setShowImg(String showImg) {
            this.showImg = showImg;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setSubscribeCnt(String subscribeCnt) {
            this.subscribeCnt = subscribeCnt;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
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

        public String getBasicScore() {
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

        public String getPassWord() {
            return passWord;
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

        public String getShowImg() {
            return showImg;
        }

        public String getSignature() {
            return signature;
        }

        public String getStatus() {
            return status;
        }

        public String getSubscribeCnt() {
            return subscribeCnt;
        }

        public String getTokenId() {
            return tokenId;
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
