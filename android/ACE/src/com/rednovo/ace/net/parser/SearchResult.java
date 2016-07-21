package com.rednovo.ace.net.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilong on 16/3/18.
 */
public class SearchResult extends BaseResult{

    private List<UserListEntity> userList = new ArrayList<UserListEntity>();

    public void setUserList(List<UserListEntity> userList) {
        this.userList = userList;
    }

    public List<UserListEntity> getUserList() {
        return userList;
    }

    public static class UserListEntity {
        private int basicScore;
        private String nickName;
        private String profile;
        private String rank;
        private String sex;
        private String signature;
        private int subscribeCnt;
        private String userId;

        public void setBasicScore(int basicScore) {
            this.basicScore = basicScore;
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

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public void setSubscribeCnt(int subscribeCnt) {
            this.subscribeCnt = subscribeCnt;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getBasicScore() {
            return basicScore;
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

        public String getSex() {
            return sex;
        }

        public String getSignature() {
            return signature;
        }

        public int getSubscribeCnt() {
            return subscribeCnt;
        }

        public String getUserId() {
            return userId;
        }
    }
}
