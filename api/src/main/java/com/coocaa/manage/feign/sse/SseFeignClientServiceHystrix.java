package com.coocaa.manage.feign.sse;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SseFeignClientServiceHystrix implements SseFeignClientService {


    @Override
    public String sendDeviceMessage(String appkey, String uid, String target_id, String source_id, String time, String sign, JSONObject jsonObject) {
        log.error("SSEFeignClientService#sendDeviceMessage触发熔断异常：{}");
        return "{\"code\":-1001,\"msg\":\"触发熔断\",\"target_online\":0}";
    }

}
