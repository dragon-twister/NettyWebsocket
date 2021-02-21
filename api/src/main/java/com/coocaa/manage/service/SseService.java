package com.coocaa.manage.service;

import com.alibaba.fastjson.JSONObject;


/**
 * @author liangshizhu
 */
public interface SseService {

    /**
     * @param json 发送消息给电视apk
     * @return
     */
    String sendTvMessage(JSONObject json);


}
