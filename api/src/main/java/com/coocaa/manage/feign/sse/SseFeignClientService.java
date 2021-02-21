package com.coocaa.manage.feign.sse;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.manage.feign.GetTrailerActorsRelateVideoFeignConfigure;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author bijiahao
 * @date : 2019/7/26.
 * @description dmp feign客户端
 *
 */
@FeignClient(name="sse-service",url="${miniProgram.sse.url}"
        ,fallback = SseFeignClientServiceHystrix.class,
        configuration ={GetTrailerActorsRelateVideoFeignConfigure.class})
public interface SseFeignClientService {

    @RequestMapping(method = RequestMethod.POST, value ="/sse/send-device-message?appkey={appkey}&uid={uid}&target_id={target_id}&source_id={source_id}&time={time}&sign={sign}", consumes = "application/json")
    String sendDeviceMessage(@PathVariable("appkey") String appkey, @PathVariable("uid") String uid, @PathVariable("target_id") String target_id, @PathVariable("source_id") String source_id , @PathVariable("time") String time, @PathVariable("sign") String sign, @RequestBody JSONObject jsonObject);



}
