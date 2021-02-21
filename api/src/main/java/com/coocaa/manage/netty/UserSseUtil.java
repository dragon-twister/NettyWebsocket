package com.coocaa.manage.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. 用户上下线
 * 2. 给用户发送消息
 * @author liangshizhu
 */

@Slf4j
public class UserSseUtil {
    //用户id=>channel
    private final static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static AttributeKey<String> uid = AttributeKey.valueOf("uid");

    /**
     * 判断一个通道是否有用户在使用
     * 可做信息转发时判断该通道是否合法
     *
     * @param channel
     * @return
     */
    public boolean hasUser(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("uid");
        //netty移除了这个map的remove方法,这里的判断谨慎一点
        return (channel.hasAttr(key) || channel.attr(key).get() != null);
    }

    /**
     * 上线一个用户
     *
     * @param channel
     * @param userId
     */
    public static void online(String userId, Channel channel) {
        //todo redis token 认证?
        channel.attr(uid).set(userId);

        channelMap.put(userId, channel);
        channelGroup.add(channel);
        log.info("{}：上线",channel.attr(uid).get());

    }

    public static void offline(Channel channel) {
        Object a = channel.attr(uid).get();
        if (null==a) {
            log.error("下线失败，uid为空");
            return;
        }
        channelMap.remove(a.toString());
        channelGroup.remove(channel);
        log.info("{}：下线",channel.attr(uid).get());

    }

    /**
     * 根据用户id获取该用户的通道
     *
     * @param userId
     * @return
     */
    public static Channel getChannelByUserId(String userId) {
        return channelMap.get(userId);
    }

    /**
     * 判断一个用户是否在线
     *
     * @param userId
     * @return
     */
    public static Boolean idOnline(String userId) {
        return channelMap.containsKey(userId) && channelMap.get(userId) != null;
    }


    /**
     * 给指定用户发内容
     * 后续可以掉这个方法推送消息给客户端
     */
    public static void sendMessage(String address, String message) {
        Channel channel = channelMap.get(address);
        if (null == channel) {
            throw new RuntimeException(address + "该用户连接不存在");
        }
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    /**
     * 群发消息
     */
    public static void sendMessageAll(String meesage) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(meesage));
    }
}
