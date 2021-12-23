package com.coocaa.websocket.api.httpclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

import static com.coocaa.websocket.api.httpclient.HttpClient.RESPONSE_PROMISE_KEY;

/**
 * 处理服务器返回报文
 * 有两种情况：
 * 1.返回给http客户端
 * 2.返回给websocket客户端
 *
 *
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpMessage> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpMessage content) {
        String jsonString = content.content().toString(CharsetUtil.UTF_8);
        Promise promise = ctx.channel().attr(RESPONSE_PROMISE_KEY).get();
        promise.setSuccess(jsonString);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
