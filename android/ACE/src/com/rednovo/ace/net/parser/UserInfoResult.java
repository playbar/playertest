package com.rednovo.ace.net.parser;

import java.io.Serializable;

/**
 * Created by Dk on 16/3/7.
 */
public class UserInfoResult extends BaseResult {

    /**
     * basicScore : 0
     * channel : 3
     * createTime : 1457846015720
     * extendData : {"fansCnt":"0","postion":" ","relatoin":"0"}
     * nickName : ACE用户3361
     * passWord : c88e8ae13e25993d3aed39a8c12ff02f
     * profile :
     * rank : 0
     * schemaId : 1457866347759
     * sex : 0
     * showImg : http://172.16.150.21/images/3/20160313065227720573205804-show.png
     * signature :
     * status : 1
     * subscribeCnt : 0
     * tokenId : 18813161693
     * updateTime : 1457846015720
     * userId : 3361
     * uuid : 206
     */

    private UserEntity user;

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    public static class UserEntity implements Serializable{
        private int basicScore;
        private String channel;
        private String createTime;
        /**
         * fansCnt : 0
         * postion :
         * relatoin : 0
         */

        private ExtendDataEntity extendData;
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

        public void setExtendData(ExtendDataEntity extendData) {
            this.extendData = extendData;
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

        public ExtendDataEntity getExtendData() {
            return extendData;
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

        public static class ExtendDataEntity implements Serializable{
            private String fansCnt;
            private String postion;
            private String relatoin;
            private int isShow;
            private String subscribeCnt;
            private String downStreanUrl;
            private String showId;
            private String pushDevNo;//账户对应的极光设备号

            public void setShowId(String showId) {
                this.showId = showId;
            }

            public String getShowId() {
                return showId;
            }

            public void setDownStreanUrl(String downStreanUrl) {
                this.downStreanUrl = downStreanUrl;
            }

            public String getDownStreanUrl() {
                return downStreanUrl;
            }

            public void setSubscribeCnt(String subscribeCnt) {
                this.subscribeCnt = subscribeCnt;
            }

            public String getSubscribeCnt() {
                return subscribeCnt;
            }

            public void setIsShow(int isShow) {
                this.isShow = isShow;
            }

            public int getIsShow() {
                return isShow;
            }

            public void setFansCnt(String fansCnt) {
                this.fansCnt = fansCnt;
            }

            public void setPostion(String postion) {
                this.postion = postion;
            }

            public void setRelatoin(String relatoin) {
                this.relatoin = relatoin;
            }

            public String getFansCnt() {
                return fansCnt;
            }

            public String getPostion() {
                return postion;
            }

            public String getRelatoin() {
                return relatoin;
            }

            public String getPushDevNo() {
                return pushDevNo;
            }

            public void setPushDevNo(String pushDevNo) {
                this.pushDevNo = pushDevNo;
            }
        }
    }
}
