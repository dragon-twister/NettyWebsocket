package com.coocaa.websocket.api.util;

import io.netty.channel.ChannelHandlerContext;

/**
 * 类不允许多继承，所以这里使用接口而不是抽象类
 */
public interface ResponseUtil {

    void response(ChannelHandlerContext ctx, Object result);

}

