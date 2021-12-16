package com.coocaa.websocket.api.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回工具类
 *
 * @author liangshizhu
 */
@Data
public class HttpResult<T> implements Serializable {

    private String code;
    private String msg;
    private T data;

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public HttpResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public HttpResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static HttpResult ok() {
        HttpResult r = new HttpResult("0", SUCCESS);
        return r;
    }

    public static HttpResult ok(Object object) {
        HttpResult r = new HttpResult("0", SUCCESS, object);
        return r;
    }

    public static HttpResult fail(Object object) {
        return new HttpResult("-1001", FAIL, object);
    }

    public static HttpResult fail(String msg) {
        return new HttpResult("-1001", msg);
    }

    public static HttpResult fail() {
        return new HttpResult("-1001", FAIL);
    }

}
