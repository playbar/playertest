package com.rednovo.ace.net.parser;

/**
 * Created by lilong on 16/3/6.
 */
public class NewOrderResult extends BaseResult{

    private String orderId;
    private String returnInfo;

    private Order order;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public static class Order{
        private String appid;
        private String prepayid;
        private String partnerid;
        private String sign;
        private String noncestr;
        private long timestamp;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayId) {
            this.prepayid = prepayId;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String parentId) {
            this.partnerid = parentId;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

}
