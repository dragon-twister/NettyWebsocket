package com.coocaa.websocket.api.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回工具类
 *
 * @author liangshizhu
 */
@Data
public class R<T> implements Serializable {

    private String code;
    private String msg;
    private T data;

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public R(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static R ok() {
        R r = new R("0", SUCCESS);
        return r;
    }

    public static R ok(Object object) {
        R r = new R("0", SUCCESS, object);
        return r;
    }

    public static R fail(Object object) {
        return new R("-1001", FAIL, object);
    }

    public static R fail(String msg) {
        return new R("-1001", msg);
    }

    public static R fail() {
        return new R("-1001", FAIL);
    }

}
