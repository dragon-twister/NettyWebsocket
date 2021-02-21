package com.coocaa.manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.manage.feign.sse.SseFeignClientService;
import com.coocaa.manage.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 小程序推送消息给sse平台
 *
 * @author liangshizhu
 */
@Slf4j
@Service
public class SseServiceImpl implements SseService {

    @Autowired
    SseFeignClientService sseFeignClientService;

    @Value("${miniProgram.sse.appkey}")
    private String appkey;
    @Value("${miniProgram.sse.secret}")
    private String secret;
    private String source_id = "lsid-cloud";

    /**
     * {
     * "uid":"1",
     * "messageId":"5e60ca9a005b3a290021ccb9",
     * "event":"voice_to_tv",
     * "fileUrl":""
     * }
     *
     * @param json
     * @return
     */
    @Override
    public String sendTvMessage(JSONObject json) {

        String uid = json.getString("uid");
        String messageId = json.getString("messageId");
        String event = json.getString("event");
        String targetId = json.getString("targetId");

        //组装sse body报文
        JSONObject sseReqJson = new JSONObject();
        sseReqJson.put("id", messageId);
        sseReqJson.put("event", event);
        sseReqJson.put("data", json.toJSONString());
        String time = System.currentTimeMillis() / 1000 + "";

        JSONObject resutEventData = new JSONObject();

        try {
            log.info("发送消息给SSE平台：{}", sseReqJson.toJSONString());
            String res = sseFeignClientService.sendDeviceMessage(appkey, uid, targetId, source_id, time, getSign(uid, targetId, time), sseReqJson);
            //String res = "{\"code\":0,\"msg\":\"成功\",\"target_online\":1}";
            JSONObject resJSON = JSONObject.parseObject(res);
            Integer code = resJSON.getInteger("code");
            Integer target_online = resJSON.getInteger("target_online");

            //返回码为0 且在线  则返回成功
            if (0 == code && 1 == target_online) {
                resutEventData.put("code", 0);
            } else if (0 == target_online) {
                resutEventData.put("code", -1002);
            }
        } catch (Exception e) {
            resutEventData.put("code", -1001);
            log.info("请求sse异常：", e);
        }
        json.put("event", "send_result");
        json.put("data", resutEventData);
        log.info(uid + "通知小程序消息发送结果：{}", json.toJSONString());
        return json.toJSONString();
    }

    private String getSign(String uid, String target_id, String time) {

        Map<String, String> sortMap = new TreeMap();

        sortMap.put("appkey", appkey);
        sortMap.put("uid", uid);
        sortMap.put("target_id", target_id);
        sortMap.put("source_id", source_id);
        sortMap.put("time", time);

        // 以k1=v1&k2=v2...方式拼接参数
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> s : sortMap.entrySet()) {
            String k = s.getKey();
            String v = s.getValue();
            if (StringUtils.isBlank(v)) {
                continue;
            }
            builder.append(k).append(v);
        }
        builder = builder.append(secret);

        return md5(builder.toString());
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is unsupported", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MessageDigest不支持MD5Util", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toLowerCase();
    }
}
