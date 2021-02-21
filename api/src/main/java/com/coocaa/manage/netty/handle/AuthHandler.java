package com.coocaa.manage.netty.handle;

import com.coocaa.manage.netty.UserSseUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
/**
 *  1 鉴权 2 用户上线
 * @author  liangshizhu
 */
@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        handleHttpRequest(ctx, fullHttpRequest);

    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();

        if (-1 != uri.indexOf("/ws") || request.method() != GET) {

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            Map<String, List<String>> parameters = queryStringDecoder.parameters();

            if (parameters.size() == 0 || !parameters.containsKey("uid")) {
                log.error("参数不正确");
                return;
            }
            String uid = parameters.get("uid").get(0);
            UserSseUtil.online(uid, ctx.channel());
            // 传递到下一个handler：升级握手
            //重新设置url  不然不会握手
            request.setUri("/ws");
            ctx.fireChannelRead(request.retain());
           // ctx.fireUserEventTriggered();

        } else {
            log.error("not socket");
            ctx.close();
        }
    }

}