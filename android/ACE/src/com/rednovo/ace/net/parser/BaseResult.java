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

/**
 * @author Zhen.Li
 * @fileNmae BaseResult
 * @since 2016-03-05
 */
public class BaseResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 客户端本地定义的错误
     */
    public final static int ERROR = -1;

    private int exeStatus;

    private int errCode;

    private String errMsg;


    public boolean isSuccess() {

        if (getExeStatus() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public BaseResult() {

    }

    public int getExeStatus() {
        return exeStatus;
    }

    public void setExeStatus(int exeStatus) {
        this.exeStatus = exeStatus;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
