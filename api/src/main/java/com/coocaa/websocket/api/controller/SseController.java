package com.coocaa.websocket.api.controller;

import com.coocaa.websocket.api.feign.sse.SseFeignClientService;
import com.coocaa.websocket.api.netty.MessageDto;
import com.coocaa.websocket.api.netty.UserSseUtil;
import com.coocaa.websocket.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 不支持单机，需要
 *
 * @author liangshizhu
 */
@Slf4j
@RestController
@Api(tags = "消息发送")
public class SseController {

    @Autowired
    SseFeignClientService sseFeignClientService;

    @PostMapping("/sendToClient")
    @ApiOperation("发往客户端")
    public R sendToUser(@RequestBody MessageDto json) {
        log.info("websocket send：{}", json);
        UserSseUtil.sendMessage(json);
        return R.ok();
    }

}