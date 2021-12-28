package com.coocaa.websocket.api.httpserver.controller;

import com.coocaa.websocket.api.util.ResponseUtil;
import com.coocaa.websocket.api.util.WebsocketSessionUtil;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.channel.ChannelHandlerContext;

public class SendToLocalClientController implements Controller {

    @Override
    public void excute(ChannelHandlerContext ctx, WsMessageDto req, ResponseUtil responseUtil) {
        WsMessageDto resDto = WebsocketSessionUtil.sendMessage(req);
        responseUtil.response(ctx, resDto);
    }
}
