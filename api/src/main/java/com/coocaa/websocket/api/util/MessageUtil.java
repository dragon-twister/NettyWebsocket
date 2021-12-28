package com.coocaa.websocket.api.util;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.httpclient.HttpClient;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URISyntaxException;

/**
 * 给目标用户发送消息
 * 给发生产者返回消息发送结果
 */
public class MessageUtil {

    /**
     *
     * @param ctx
     * @param requestWsMessageDto
     * @param responseUtil
     */
    public static void sendToRemoteClient(ChannelHandlerContext ctx, WsMessageDto requestWsMessageDto, ResponseUtil responseUtil) {
        RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
        Object o = redisTemplate.opsForValue().get("UserServer-" + requestWsMessageDto.getUid());
        if (o == null) {
            throw new RuntimeException("目标用户不在线");
        }

        // 如果连接在本地
        if (IpUtil.LOCAL_HTTP_URL.equals((String) o)) {
            WsMessageDto response = WebsocketSessionUtil.sendMessage(requestWsMessageDto);
            responseUtil.response(ctx, response);
            return;
        }

        //如果连接在其他服务器
        try {
            HttpClient.postJson(o + "/sendToLocalClient", JSONObject.toJSONString(requestWsMessageDto), new GenericFutureListener() {
                @Override
                public void operationComplete(Future future) throws Exception {
                    String remoteResult = (String) future.get();
                    responseUtil.response(ctx, remoteResult);
                    ctx.channel().writeAndFlush(remoteResult.getBytes());
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
