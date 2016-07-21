package com.rednovo.ace.data.cell;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class BaseMode implements Serializable {
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
