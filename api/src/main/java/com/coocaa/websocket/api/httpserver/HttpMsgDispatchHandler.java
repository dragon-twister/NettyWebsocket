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
import com.coocaa.websocket.api.httpserver.controller.Controller;
import com.coocaa.websocket.api.httpserver.controller.SendToLocalClientController;
import com.coocaa.websocket.api.util.*;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * 处理http请求
 */
@Slf4j
public class HttpMsgDispatchHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements ResponseUtil {

    public static Map<String, Controller> map = new HashMap();

    static {
        map.put("/sendToLocalClient", new SendToLocalClientController());
        map.put("/sendToClient", new SendToLocalClientController());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String path = this.getPath(request.uri());
        String contentStr = request.content().toString(CharsetUtil.UTF_8);
        WsMessageDto requestWsMessageDto = JSON.parseObject(contentStr, WsMessageDto.class);
        validReqParams(requestWsMessageDto);
        Controller controller = map.get(path);
        if (controller == null) {
            throw new RuntimeException("uri错误");
        }
        controller.excute(ctx, requestWsMessageDto, this);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        response(ctx, WsMessageDto.fail(cause.getMessage()));
        ctx.close();
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
     * @param result
     */
    @Override
    public void response(ChannelHandlerContext ctx, Object result) {
        byte[] bytes = JSONObject.toJSONString(result).getBytes();
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,
                Unpooled.wrappedBuffer(bytes));
        fullHttpResponse.headers()
                .set(CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setInt(CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        ctx.writeAndFlush(fullHttpResponse);
        ctx.close();
    }

    public void validReqParams(WsMessageDto requestWsMessageDto) {
        if (requestWsMessageDto == null || StringUtils.isBlank(requestWsMessageDto.getMessageId())) {
            throw new RuntimeException("报文错误");
        } else if (StringUtils.isBlank(requestWsMessageDto.getUid()) ||
                StringUtils.isBlank(requestWsMessageDto.getEvent()) ||
                StringUtils.isBlank(requestWsMessageDto.getTargetId())) {
            throw new RuntimeException("报文错误,消息id:" + requestWsMessageDto.getMessageId());
        }

    }
}