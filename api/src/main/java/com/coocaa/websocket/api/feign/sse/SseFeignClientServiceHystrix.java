package com.coocaa.websocket.api.feign.sse;

import com.coocaa.websocket.api.netty.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class SseFeignClientServiceHystrix implements SseFeignClientService {

    @Override
    public String sendToClient(@RequestBody MessageDto messageDto){
        log.error("SSEFeignClientService#sendDeviceMessage触发熔断异常：{}");
        return "{\"code\":-1001,\"msg\":\"触发熔断\"}";
    }

}
