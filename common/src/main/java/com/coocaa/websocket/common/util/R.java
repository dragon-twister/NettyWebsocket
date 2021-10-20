package com.coocaa.websocket.common.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 取代奇葩的R，修复springboot cache无法取到属性的问题
 * @author liangshizhu
 */
@Data
public class R implements Serializable {

    String code;
    String msg;
    Object data;

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public R(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static R ok() {
        R r = new R("0",SUCCESS);
        return r;
    }

    public static R ok(Object object) {
        R r = new R("0",SUCCESS,object);
        return r;
    }

    public static R fail(String msg) {
        return new R("-1001",msg);
    }

    public static R fail() {
        return new R("-1001",FAIL);
    }

}
