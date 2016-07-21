/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.parser;

import java.util.ArrayList;

/**
 * 热门列表
 *
 * @author Zhen.Li
 * @fileNmae HotResult
 * @since 2016-03-05
 */
public class HotResult extends BaseResult {

    private ArrayList<Data> showList=new ArrayList<Data>();

    private ArrayList<User> userList=new ArrayList<User>();

    public ArrayList<Data> getShowList() {
        return showList;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setShowList(ArrayList<Data> datas) {
        this.showList = datas;
    }

    public void setUserList(ArrayList<User> users) {
        this.userList = users;
    }

    public static class User{

        private int basicScore;
        private String channel;
        private String createTime;
        private String nickName;
        private String passWord;
        private String profile;
        private String rank;
        private String schemaId;
        private String sex;
        private String signature;
        private String status;
        private String updateTime;
        private String userId;
        private String uuid;
        private String showImg;

        public String getShowImg() {
            return showImg;
        }

        public void setShowImg(String showImg) {
            this.showImg = showImg;
        }

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
    public static class Data {
        private String coinCnt;
        private String showId;
        private String length;
        private String memberCnt = "0";
        private String position;
        private String shareCnt;
        private String startTime;
        private String supportCnt;
        private String title;
        private String userId;
        private String downStreamUrl;

        public String getDownStreamUrl() {
            return downStreamUrl;
        }

        public void setDownStreamUrl(String downStreanUrl) {
            this.downStreamUrl = downStreanUrl;
        }

        public String getCoinCnt() {
            return coinCnt;
        }

        public void setCoinCnt(String coinCnt) {
            this.coinCnt = coinCnt;
        }

        public String getShowId() {
            return showId;
        }

        public void setShowId(String id) {
            this.showId = id;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getMemberCnt() {
            return memberCnt;
        }

        public void setMemberCnt(String memberCnt) {
            this.memberCnt = memberCnt;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getShareCnt() {
            return shareCnt;
        }

        public void setShareCnt(String shareCnt) {
            this.shareCnt = shareCnt;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getSupportCnt() {
            return supportCnt;
        }

        public void setSupportCnt(String supportCnt) {
            this.supportCnt = supportCnt;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }
}
