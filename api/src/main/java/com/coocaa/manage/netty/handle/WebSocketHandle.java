package com.coocaa.manage.netty.handle;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.util.R;
import com.coocaa.manage.netty.UserSseUtil;
import com.coocaa.manage.service.SseService;
import com.coocaa.util.SpringContextUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


/**
 * 自定义的handler类
 */
@Slf4j
@Configuration
public class WebSocketHandle extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

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
        cause.printStackTrace();
        String res = R.fail(cause.getMessage()).toString();
        log.info("WEBSOCKET ERROR :{}", res);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(Unpooled.buffer().writeBytes(res.getBytes()));
        ctx.channel().writeAndFlush(textWebSocketFrame);
        ctx.close();
    }


    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        if (frame instanceof PingWebSocketFrame) {
            //ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {

            String jsonStr = ((TextWebSocketFrame) frame).text();
            JSONObject json = JSONObject.parseObject(jsonStr);
            log.info("接收小程序报文 <= {}", json);

            String uid = json.getString("uid");
            String event = json.getString("event");

            switch (event) {
                case "ping":
                    return;
                default:
                    log.info(uid + "给电视端发送消息,{}", json);
                    SseService sseService = (SseService) SpringContextUtil.getBean("sseServiceImpl");
                    String response = sseService.sendTvMessage(json);
                    TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(Unpooled.buffer().writeBytes(response.getBytes()));
                    ctx.channel().writeAndFlush(textWebSocketFrame);
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