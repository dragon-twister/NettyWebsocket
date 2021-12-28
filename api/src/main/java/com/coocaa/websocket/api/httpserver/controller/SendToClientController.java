package com.coocaa.websocket.api.httpserver.controller;

import com.coocaa.websocket.api.util.MessageUtil;
import com.coocaa.websocket.api.util.ResponseUtil;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.channel.ChannelHandlerContext;


public class SendToClientController implements Controller {
    @Override
    public void excute(ChannelHandlerContext ctx, WsMessageDto req, ResponseUtil responseUtil) {
        MessageUtil.sendToRemoteClient(ctx, req, responseUtil);
    }
}
