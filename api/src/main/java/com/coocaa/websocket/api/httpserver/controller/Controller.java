package com.coocaa.websocket.api.httpserver.controller;

import com.coocaa.websocket.api.util.ResponseUtil;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.channel.ChannelHandlerContext;

/**
 * 所有HTTP接口的父类
 */
public interface Controller {

    /**
     * 接口执行方法
     */
    void excute(ChannelHandlerContext ctx, WsMessageDto req, ResponseUtil responseUtil);

}
