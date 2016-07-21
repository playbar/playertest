package com.rednovo.ace.net.parser;

import java.io.Serializable;

/**
 * 登陆成功后存储登录信息的类型
 * Created by Dk on 16/4/25.
 */
public class LoginInfoResult implements Serializable {
    public String userId;
    public String pword;
    public String loginType;
    public String nickName;
    public String profile;
}
