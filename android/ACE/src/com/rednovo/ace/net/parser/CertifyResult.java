package com.rednovo.ace.net.parser;

/**
 * 实名认证
 */
public class CertifyResult extends BaseResult {

    /**
     * certify :
     */

    private String certify = "";
    private String certifyUrl;

    public String getCertify() {
        return certify;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public String getCertifyUrl() {
        return certifyUrl;
    }

    public void setCertifyUrl(String certifyUrl) {
        this.certifyUrl = certifyUrl;
    }
}
