package com.rednovo.ace.net.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilong on 16/3/27.
 */
public class HallSubscribeResult extends BaseResult{


    private List<ShowListEntity> recommandList = new ArrayList<ShowListEntity>(2);

    private List<ShowListEntity> showList = new ArrayList<ShowListEntity>(2);

    private List<UserListEntity> userList = new ArrayList<UserListEntity>(2);

    private String ifSubscribe = "-1";       // 没有点阅返回此字段

    public void setIfSubscribe(String ifSubscribe) {
        this.ifSubscribe = ifSubscribe;
    }

    public String getIfSubscribe() {
        return ifSubscribe;
    }

    public void setShowList(List<ShowListEntity> showList) {
        this.showList = showList;
    }

    public List<ShowListEntity> getShowList() {
        return this.showList;
    }

    public void setRecommandList(List<ShowListEntity> recommandList) {
        this.recommandList = recommandList;
    }

    public void setUserList(List<UserListEntity> userList) {
        this.userList = userList;
    }

    public List<ShowListEntity> getRecommandList() {
        return recommandList;
    }

    public List<UserListEntity> getUserList() {
        return userList;
    }

    public static class ShowListEntity {
        private String coinCnt;
        private String createTime;
        private String downStreamUrl;
        private String length;
        private String memberCnt;
        private String position;
        private String schemaId;
        private String shareCnt;
        private String showId;
        private long sortCnt;
        private String startTime;
        private String supportCnt;
        private String title;
        private String userId;

        public void setCoinCnt(String coinCnt) {
            this.coinCnt = coinCnt;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setDownStreamUrl(String downStreamUrl) {
            this.downStreamUrl = downStreamUrl;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public void setMemberCnt(String memberCnt) {
            this.memberCnt = memberCnt;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public void setSchemaId(String schemaId) {
            this.schemaId = schemaId;
        }

        public void setShareCnt(String shareCnt) {
            this.shareCnt = shareCnt;
        }

        public void setShowId(String showId) {
            this.showId = showId;
        }

        public void setSortCnt(long sortCnt) {
            this.sortCnt = sortCnt;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public void setSupportCnt(String supportCnt) {
            this.supportCnt = supportCnt;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCoinCnt() {
            return coinCnt;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getDownStreamUrl() {
            return downStreamUrl;
        }

        public String getLength() {
            return length;
        }

        public String getMemberCnt() {
            return memberCnt;
        }

        public String getPosition() {
            return position;
        }

        public String getSchemaId() {
            return schemaId;
        }

        public String getShareCnt() {
            return shareCnt;
        }

        public String getShowId() {
            return showId;
        }

        public long getSortCnt() {
            return sortCnt;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getSupportCnt() {
            return supportCnt;
        }

        public String getTitle() {
            return title;
        }

        public String getUserId() {
            return userId;
        }
    }

    public static class UserListEntity {
        private int basicScore;
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
        private int subscribeCnt;
        private String tokenId;
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

        public void setSubscribeCnt(int subscribeCnt) {
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

        public String getShowImg() {
            return showImg;
        }

        public String getSignature() {
            return signature;
        }

        public String getStatus() {
            return status;
        }

        public int getSubscribeCnt() {
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
