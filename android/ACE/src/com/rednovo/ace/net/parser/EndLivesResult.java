package com.rednovo.ace.net.parser;

/**
 * Created by lizhen on 16/3/15.
 */
public class EndLivesResult extends BaseResult {


    /**
     * coins : 239840
     * fans : 121212
     * length : 3445
     * memberCnt : 3445
     * support : 1088
     */

    private String coins;
    private String fans;
    private String length;
    private String memberCnt;
    private String support;

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setMemberCnt(String memberCnt) {
        this.memberCnt = memberCnt;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getCoins() {
        return coins;
    }

    public String getFans() {
        return fans;
    }

    public String getLength() {
        return length;
    }

    public String getMemberCnt() {
        return memberCnt;
    }

    public String getSupport() {
        return support;
    }
}
