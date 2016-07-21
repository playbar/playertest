package com.rednovo.ace.net.parser;

import java.io.Serializable;

/**
 * Created by Dk on 16/3/19.
 */
public class BindWechatResult extends BaseResult{

    private BindEntity bind;

    public void setBind(BindEntity bind) {
        this.bind = bind;
    }

    public BindEntity getBind() {
        return bind;
    }

    public static class BindEntity implements Serializable{
        private String createTime;
        private String mobileId;
        private String updateTime;
        private String userId;
        private String weChatId;

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setMobileId(String mobileId) {
            this.mobileId = mobileId;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setWeChatId(String weChatId) {
            this.weChatId = weChatId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getMobileId() {
            return mobileId;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public String getUserId() {
            return userId;
        }

        public String getWeChatId() {
            return weChatId;
        }
    }
}
