/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.coocaa.websocket.api.httpserver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.httpclient.HttpClient;
import com.coocaa.websocket.api.util.WebsocketSessionUtil;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * 处理http请求
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        Object response = new Object();
        String path = this.getPath(request.uri());
        if ("/sendToLocalClient".equals(path)) {
            // 给本地用户发送消息 能同步返回结果
            String s = request.content().toString(CharsetUtil.UTF_8);
            WsMessageDto requestWsMessageDto = JSON.parseObject(s, WsMessageDto.class);
            // 校验报文
            if (requestWsMessageDto == null || StringUtils.isBlank(requestWsMessageDto.getMessageId())) {
                response = WsMessageDto.fail("报文错误");
            } else if (StringUtils.isBlank(requestWsMessageDto.getUid()) ||
                    StringUtils.isBlank(requestWsMessageDto.getEvent()) ||
                    StringUtils.isBlank(requestWsMessageDto.getTargetId())) {
                response = WsMessageDto.fail(requestWsMessageDto.getMessageId(), "报文错误");
            } else {
                // 报文正确
                response = sendToClient(requestWsMessageDto);
            }
            flushHttpResponse(ctx, response, request.protocolVersion());

        } else if ("/sendToClient".equals(path)) {
            //2. 路由至用户所在服务器 使用netty客户端，无法同步获取结果，涉及内部通讯，需要记录session
            String s = request.content().toString(CharsetUtil.UTF_8);
            WsMessageDto requestWsMessageDto = JSON.parseObject(s, WsMessageDto.class);
            HttpClient.postToClient(requestWsMessageDto, new GenericFutureListener() {
                @Override
                public void operationComplete(Future future) throws Exception {
                    String o1 = (String) future.get();
                    flushHttpResponse(ctx, o1, request.protocolVersion());
                }
            });
        } else {
            response = WsMessageDto.fail("uri错误");
            flushHttpResponse(ctx, response, request.protocolVersion());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public WsMessageDto sendToClient(WsMessageDto json) {
        boolean result = WebsocketSessionUtil.sendMessage(json);
        json.setTargetId(json.getUid());
        json.setUid("server");
        if (result) {
            json.setEvent("send_result_ok");
        } else {
            json.setEvent("send_result_fail");
        }
        return json;
    }

    public byte[] getByte(ByteBuf byteBuf) {
        if (byteBuf.hasArray()) {
            return byteBuf.array();
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public String getPath(String uri) {
        int end = uri.indexOf("?");
        if (end > 0) {
            uri = uri.substring(0, end);
        }
        return uri;
    }

    /**
     * 返回报文并关闭
     *
     * @param ctx
     * @param response
     * @param httpVersion
     */
    public void flushHttpResponse(ChannelHandlerContext ctx, Object response, HttpVersion httpVersion) {
        byte[] bytes = JSONObject.toJSONString(response).getBytes();
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(httpVersion, OK,
                Unpooled.wrappedBuffer(bytes));
        fullHttpResponse.headers()
                .set(CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setInt(CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        ctx.writeAndFlush(fullHttpResponse);
        ctx.close();
    }

}
