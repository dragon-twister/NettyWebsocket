package com.coocaa.util;


import java.io.Serializable;
import java.util.HashMap;

/**
 * 逐渐废弃
 */
public class R extends HashMap<String, Object> implements Serializable {

    String code;
    boolean success;
    String msg;


    public R() {
        put("code", "0");
        put("success", true);
        put("msg", "success");
        code = "0";
        success = true;
        msg = "success";
    }

    public R data(Object data) {
        this.put("data", data);
        return this;
    }

    public static R ok() {
        return new R();
    }


    public static R fail(String msg) {
        return error("-1001", msg);
    }

    public static R fail(String msg, String detailMsg) {
        R result = error("-1001", msg);
        result.put("detailMsg", detailMsg);
        return result;
    }




    public static R error(String code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        r.put("success", false);
        return r;
    }


}
