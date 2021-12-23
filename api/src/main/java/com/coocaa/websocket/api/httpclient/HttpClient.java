package com.coocaa.websocket.api.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.util.SpringContextUtil;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * netty http客户端实现有两种方法
 * 1. 使用 {@link io.netty.util.concurrent.Promise} ，但线程等待阻塞，消耗资源
 * 2. 消息传递中使用sessionid保存会话，此方法需要通信双方支持
 */
public class HttpClient {

    public static Bootstrap b = new Bootstrap();
    public static final AttributeKey<Promise> RESPONSE_PROMISE_KEY = AttributeKey.valueOf("response_promise");

    static {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer());
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

    /**
     * 通用 http post json方法
     * @param url
     * @param json
     * @param listener
     * @throws InterruptedException
     * @throws URISyntaxException
     */
    public static void postJson(String url, String json, GenericFutureListener listener) throws InterruptedException, URISyntaxException {
        URI uri = new URI("http://" + url);
        Channel ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();

        // Prepare the HTTP request.
        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());

        request.headers().set(HttpHeaderNames.HOST, uri.getPort());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json");
        ByteBuf bbuf = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, bbuf.readableBytes());
        request.content().clear().writeBytes(bbuf);

        //设置promise和监听器
        Promise<Object> promise = b.config().group().next().newPromise();
        ch.attr(RESPONSE_PROMISE_KEY).set(promise.addListener(listener));

        //发送请求
        ch.writeAndFlush(request);
        ch.closeFuture().sync();
    }

    /**
     * 找到用户所连接的服务器，发送消息
     * @param wsMessageDto
     * @param listener
     */
    public static void postToClient(WsMessageDto wsMessageDto, GenericFutureListener listener) {
        String json = JSONObject.toJSONString(wsMessageDto);
        //从redis获取，如果获取不到就返回错误
        RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
        Object o = redisTemplate.opsForValue().get("UserServer-" + wsMessageDto.getUid());
        if (o == null) {
            throw new RuntimeException();
        }
        String url0 = o + "/sendToLocalClient";
        try {
            postJson(url0, json, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
