package com.rednovo.ace.data.cell;

import java.io.Serializable;

/**
 * Created by lilong on 16/3/12.
 */
public class LiveInfo implements Serializable {


    private static final long serialVersionUID = 3738785625528373361L;
    private String showId = "";

    private String starId = "";

    private String nickName = "";

    private String profile = "";

    private String rank = "";

    private String sex = "";

    private String audienceCnt = "";

    private String downStreanUrl = "";

    private String signature = "";

    private String showImg = "";

    public void setShowImg(String mShowImg) {
        this.showImg = mShowImg;
    }

    public String getShowImg() {
        return showImg;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public String getDownStreanUrl() {
        return downStreanUrl;
    }

    public void setDownStreanUrl(String downStreanUrl) {
        this.downStreanUrl = downStreanUrl;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getStarId() {
        return starId;
    }

    public void setStarId(String starId) {
        this.starId = starId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAudienceCnt() {
        return audienceCnt;
    }

    public void setAudienceCnt(String audienceCnt) {
        this.audienceCnt = audienceCnt;
    }

}
