package com.coocaa.websocket.api.controller;

import com.coocaa.websocket.api.websocket.MessageDto;
import com.coocaa.websocket.api.websocket.UserSseUtil;
import com.coocaa.websocket.api.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 消息发送
 *
 * @author liangshizhu
 */
@Slf4j
@RestController
@Api(tags = "消息发送")
public class SseController {

    @PostMapping("/sendToClient")
    @ApiOperation("发往客户端")
    public MessageDto sendToUser(@RequestBody MessageDto json) {
        log.info("websocket send：{}", json);
        boolean result = UserSseUtil.sendMessage(json);
        json.setTargetId(json.getUid());
        json.setUid("server");
        if (result) {
            json.setEvent("send_result_ok");
        } else {
            json.setEvent("send_result_fail");
        }
        return json;
    }
}