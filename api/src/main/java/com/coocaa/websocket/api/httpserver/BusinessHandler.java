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
import com.coocaa.websocket.api.util.UserSseUtil;
import com.coocaa.websocket.api.websocket.MessageDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class BusinessHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        Object response = new Object();
        String path = this.getPath(request.uri());
        if ("/sendToClient".equals(path)) {
            String s = request.content().toString(CharsetUtil.UTF_8);
            MessageDto requestMessageDto = JSON.parseObject(s, MessageDto.class);
            // 校验报文
            if (requestMessageDto == null || StringUtils.isBlank(requestMessageDto.getMessageId())) {
                response = MessageDto.fail("报文错误");
            } else if (StringUtils.isBlank(requestMessageDto.getUid()) ||
                    StringUtils.isBlank(requestMessageDto.getEvent()) ||
                    StringUtils.isBlank(requestMessageDto.getTargetId())) {
                response = MessageDto.fail(requestMessageDto.getMessageId(), "报文错误");
            } else {
                // 报文正确
                response = sendToClient(requestMessageDto);
            }
        } else {
            response = MessageDto.fail("uri错误");
        }
        byte[] bytes = JSONObject.toJSONString(response).getBytes();
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(request.protocolVersion(), OK,
                Unpooled.wrappedBuffer(bytes));
        fullHttpResponse.headers()
                .set(CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setInt(CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        ctx.writeAndFlush(fullHttpResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public MessageDto sendToClient(MessageDto json) {
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

    public byte[] getByte(ByteBuf byteBuf) {
        if (byteBuf.hasArray()) {
            return byteBuf.array();
        }
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    public String getPath(String uri) {
        String result = "";
        int end = uri.indexOf("?");
        if (end > 0) {
            result = uri.substring(0, end);
        }
        System.out.println("/sendToClient".equals(result));
        return result;
    }

}
