package com.coocaa.websocket.api.websocket;

import com.coocaa.websocket.api.util.UserSseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.coocaa.websocket.api.websocket.WebsocketServer.WEBSOCKET_PATH;
import static io.netty.handler.codec.http.HttpMethod.GET;

/**
 * 1 鉴权 todo
 * 2 用户上线
 *
 * @author liangshizhu
 */
@Slf4j
public class HttpRequestHandle extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        handleHttpRequest(ctx, fullHttpRequest);
    }

    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        if (-1 != uri.indexOf(WEBSOCKET_PATH) || request.method() != GET) {
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            Map<String, List<String>> parameters = queryStringDecoder.parameters();

            if (parameters.size() == 0 || !parameters.containsKey("uid")) {
                log.error("参数不正确");
                ctx.close();
            }
            String uid = parameters.get("uid").get(0);
            UserSseUtil.online(uid, ctx.channel());

            // 需要重新设置uri，传递到下一个handler，升级握手。
            request.setUri(WEBSOCKET_PATH);
            ctx.fireChannelRead(request.retain());
        } else {
            log.error("not socket");
            ctx.close();
        }
    }

}