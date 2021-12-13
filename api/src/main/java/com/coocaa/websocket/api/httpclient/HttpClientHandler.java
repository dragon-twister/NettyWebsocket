package com.coocaa.websocket.api.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.websocket.MessageDto;
import com.coocaa.websocket.api.util.UserSseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            System.err.println("STATUS: " + response.status());

            if (!response.headers().isEmpty()) {
                for (CharSequence name : response.headers().names()) {
                    for (CharSequence value : response.headers().getAll(name)) {
                        System.err.println("HEADER: " + name + " = " + value);
                    }
                }
                System.err.println();
            }

            if (HttpUtil.isTransferEncodingChunked(response)) {
                System.err.println("CHUNKED CONTENT {");
            } else {
                System.err.println("CONTENT {");
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            //接收发送消息结果，并告诉客户端
            String jsonString = content.content().toString(CharsetUtil.UTF_8);
            MessageDto messageDto = JSONObject.parseObject(jsonString, MessageDto.class);
            if (messageDto != null) {
                String uid = messageDto.getUid();
                if (uid != null) {
                    UserSseUtil.sendMessage(messageDto);
                }
            }
            System.err.print(content.content().toString(CharsetUtil.UTF_8));
            System.err.flush();
            if (content instanceof LastHttpContent) {
                System.err.println("} END OF CONTENT");
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
