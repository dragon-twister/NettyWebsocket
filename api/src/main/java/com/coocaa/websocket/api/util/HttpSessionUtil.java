package com.coocaa.websocket.api.util;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Slf4j
public class HttpSessionUtil {

    public static ConcurrentHashMap<String, NioSocketChannel> channelMap = new ConcurrentHashMap();

    /**
     * 记录一个会话
     *
     * @param channel
     * @param sessionId
     */
    public static void online(String sessionId, NioSocketChannel channel) {
        channelMap.put(sessionId, channel);
    }

    /**
     * 给某个会话发送消息
     *
     */
    public static boolean sendMessage(String sessionId, Object object) {
        NioSocketChannel channel = channelMap.get(sessionId);
        if (null == channel) {
            log.error(sessionId + "Http Session不存在");
            return false;
        }
        flushHttp(channel, object, HttpVersion.HTTP_1_1);
        return true;
    }

    public static boolean sendMessage(WsMessageDto wsMessageDto) {
        NioSocketChannel channel = channelMap.get(wsMessageDto.getMessageId());
        if (null == channel) {
            log.error(wsMessageDto.getMessageId() + "Http Session不存在");
            return false;
        }
        flushHttp(channel, wsMessageDto, HttpVersion.HTTP_1_1);
        return true;
    }


    public static void flushHttp(NioSocketChannel channel, Object response, HttpVersion httpVersion) {
        byte[] bytes = JSONObject.toJSONString(response).getBytes();
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpVersion, OK,
                Unpooled.wrappedBuffer(bytes));
        fullHttpResponse.headers()
                .set(CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setInt(CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        channel.writeAndFlush(fullHttpResponse);
    }

}
