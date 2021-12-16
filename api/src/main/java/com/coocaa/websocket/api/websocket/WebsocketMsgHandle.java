package com.coocaa.websocket.api.websocket;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.httpclient.HttpClient;
import com.coocaa.websocket.api.websocket.MessageDto;
import com.coocaa.websocket.api.util.UserSseUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

/**
 * 自定义的handler类，只适合sendToClient接口
 */
@Slf4j
@Configuration
public class WebsocketMsgHandle extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws URISyntaxException, InterruptedException {
        if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
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
        log.info("客户端关闭");
        UserSseUtil.offline(ctx.channel());
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
        MessageDto messageDto = new MessageDto("server", null, "", "cause.getMessage()", "exception");
        log.error("WEBSOCKET ERROR :", cause);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(Unpooled.buffer().writeBytes(JSONObject.toJSONString(messageDto).getBytes()));
        ctx.channel().writeAndFlush(textWebSocketFrame);
        ctx.close();
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws URISyntaxException, InterruptedException {
        if (frame instanceof PingWebSocketFrame) {
            //ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            String jsonStr = ((TextWebSocketFrame) frame).text();
            MessageDto req = JSONObject.parseObject(jsonStr, MessageDto.class);
            //判断事件
            String event = req.getEvent();
            switch (event) {
                case "ping":
                    return;
                default:
                    HttpClient.postJson(req);
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

}