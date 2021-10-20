package com.coocaa.websocket.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.feign.sse.SseFeignClientService;
import com.coocaa.websocket.api.netty.MessageDto;
import com.coocaa.websocket.api.service.SseService;
import com.coocaa.websocket.common.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public MessageDto sendMessage(MessageDto messageDto) {
        R r = R.ok();
        try {
            log.debug("http send:", messageDto.toString());
            String res = sseFeignClientService.sendToClient(messageDto);
            JSONObject resJSON = JSONObject.parseObject(res);
            Integer code = resJSON.getInteger("code");
            if (0 != code) {
                r = R.fail("消息发送失败");
            }
        } catch (Exception e) {
            r = R.fail("消息发送失败");
        }
        //返回发送消息结果
        messageDto.setMessageId(messageDto.getMessageId() + "res");
        messageDto.setTargetId(messageDto.getUid());
        messageDto.setUid("server");
        messageDto.setEvent("send_result");
        messageDto.setData(r);
        log.info("server send:", messageDto.toString());
        return messageDto;
    }

}
