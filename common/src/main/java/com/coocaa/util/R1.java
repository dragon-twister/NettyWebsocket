package com.coocaa.util;

import lombok.Data;

import java.io.Serializable;

/**
 * 取代奇葩的R，修复springboot cache无法取到属性的问题
 * @author liangshizhu
 */
@Data
public class R1  implements Serializable {

    String code;
    String msg;
    Object data;

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public R1(String code,String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R1(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static R1 ok() {
        R1 r = new R1("0",SUCCESS);
        return r;
    }

    public static R1 ok(Object object) {
        R1 r = new R1("0",SUCCESS,object);
        return r;
    }

    public static R1 fail(String msg) {
        return new R1("-1001",msg);
    }

    public static R1 fail() {
        return new R1("-1001",FAIL);
    }

}
