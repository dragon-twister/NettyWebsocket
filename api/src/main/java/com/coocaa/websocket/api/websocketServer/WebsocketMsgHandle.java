package com.coocaa.websocket.api.websocketServer;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.util.*;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义的handler类，只适合sendToClient接口
 */
@Slf4j
@Configuration
public class WebsocketMsgHandle extends SimpleChannelInboundHandler<Object> implements ResponseUtil {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object frame) {
        if (frame instanceof TextWebSocketFrame) {
            String reqString = ((TextWebSocketFrame) frame).text();
            if ("ping".equals(reqString)) {
                return;
            }
            WsMessageDto requestWsMessageDto = JSONObject.parseObject(reqString, WsMessageDto.class);
            //判断事件
            String event = requestWsMessageDto.getEvent();
            switch (event) {
                case "sendToTarget":
                default:
                    MessageUtil.sendToRemoteClient(ctx, requestWsMessageDto, this);
                    break;
            }
        }
        //关闭消息
        if (frame instanceof CloseWebSocketFrame) {
            log.info("客户端关闭，通道关闭");
            Channel channel = ctx.channel();
            channel.close();
        }
    }

    /**
     * 未注册状态
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("等待连接");
    }

    /**
     * 非活跃状态，没有连接远程主机的时候。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        WebsocketSessionUtil.offline(ctx.channel());
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        WsMessageDto wsMessageDto = new WsMessageDto("server", null, "", "cause.getMessage()", "exception");
        log.error("WEBSOCKET ERROR :", cause);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(Unpooled.buffer().writeBytes(JSONObject.toJSONString(wsMessageDto).getBytes()));
        ctx.channel().writeAndFlush(textWebSocketFrame);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                log.info("{}超时，断开连接", ctx.channel().id());
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void response(ChannelHandlerContext ctx, Object result) {
        if (result instanceof String) {
            ctx.channel().writeAndFlush(result);
        } else {
            ctx.channel().writeAndFlush(JSONObject.toJSONString(result));
        }
    }
}