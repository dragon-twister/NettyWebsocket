package com.coocaa.websocket.api.feign.sse;

import com.coocaa.websocket.api.feign.GetTrailerActorsRelateVideoFeignConfigure;
import com.coocaa.websocket.api.netty.MessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author bijiahao
 * @date : 2019/7/26.
 * @description dmp feign客户端
 */
@FeignClient(name = "sse-service", url = "${miniProgram.sse.url}"
        , fallback = SseFeignClientServiceHystrix.class,
        configuration = {GetTrailerActorsRelateVideoFeignConfigure.class})
public interface SseFeignClientService {

    @RequestMapping(method = RequestMethod.POST, value = "/sendToClient", consumes = "application/json")
    String sendToClient(@RequestBody MessageDto messageDto);

}
