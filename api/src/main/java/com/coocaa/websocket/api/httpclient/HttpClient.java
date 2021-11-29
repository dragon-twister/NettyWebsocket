package com.coocaa.websocket.api.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.websocket.MessageDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    public static Bootstrap b = null;
    static final String URL = System.getProperty("url", "http://127.0.0.1:8080/");

    static {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer());
            //todo 添加返回处理器，通知消息是否发送成功
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

    public static void postJson(MessageDto messageDto) throws InterruptedException, URISyntaxException {
        String json = JSONObject.toJSONString(messageDto);
        String url0 = URL + "sendToClient?uid=" + messageDto.getUid();

        URI uri = new URI(url0);
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

        // Send the HTTP request.
        ch.writeAndFlush(request);

        // Wait for the server to close the connection.
        ch.closeFuture().sync();
    }

}
