/*
 * *
 *  *
 *  * @version 1.0.0
 *  *
 *  * Copyright (C) 2012-2016 REDNOVO Corporation.
 *
 */

package com.rednovo.ace.net.parser;


import java.io.Serializable;
import java.util.List;

/**
 * @author Zhen.Li
 * @fileNmae AudienceResult
 * @since 2016-03-06
 */
public class AudienceResult extends BaseResult {

    /**
     * memberList : [{"basicScore":0,"nickName":"小震震","profile":"","rank":"0","sex":"0","signature":"","status":"1","subscribeCnt":0,"userId":"4671"},{"basicScore":0,"nickName":"李龙","profile":"http://172.16.150.21/images/4/20160314103223428084463144-profile.png","rank":"0","sex":"0","signature":"","status":"1","subscribeCnt":0,"userId":"4078"}]
     * memberSize : 2
     * supportCnt : 43
     */

    private String memberSize;
    private String supportCnt;
    /**
     * basicScore : 0
     * nickName : 小震震
     * profile :
     * rank : 0
     * sex : 0
     * signature :
     * status : 1
     * subscribeCnt : 0
     * userId : 4671
     */

    private List<MemberListEntity> memberList;

    public void setMemberSize(String memberSize) {
        this.memberSize = memberSize;
    }

    public void setSupportCnt(String supportCnt) {
        this.supportCnt = supportCnt;
    }

    public void setMemberList(List<MemberListEntity> memberList) {
        this.memberList = memberList;
    }

    public String getMemberSize() {
        return memberSize;
    }

    public String getSupportCnt() {
        return supportCnt;
    }

    public List<MemberListEntity> getMemberList() {
        return memberList;
    }

    public static class MemberListEntity implements Serializable{
        private int basicScore;
        private String nickName;
        private String profile;
        private String rank;
        private String sex;
        private String signature;
        private String status;
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

        public void setStatus(String status) {
            this.status = status;
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

        public String getStatus() {
            return status;
        }

        public int getSubscribeCnt() {
            return subscribeCnt;
        }

        public String getUserId() {
            return userId;
        }
    }
}
