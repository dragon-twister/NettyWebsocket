package com.coocaa.websocket.api.util;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.websocket.api.websocketServer.WsMessageDto;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. 用户上下线
 * 2. 给用户发送消息
 *
 * @author liangshizhu
 */

@Slf4j
public class WebsocketSessionUtil {

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
    public static boolean hasUser(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("uid");
        //netty移除了这个map的remove方法,这里的判断谨慎一点
        return (channel.hasAttr(key) || channel.attr(key).get() != null);
    }

    /**
     * 上线一个用户
     * 1. 记录用户的channel到channelMap
     * 2. 记录用户所连接服务器到redis
     *
     * @param channel
     * @param userId
     */
    public static void online(String userId, Channel channel) {
        channel.attr(uid).set(userId);
        channelMap.put(userId, channel);
        channelGroup.add(channel);
        RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
        redisTemplate.opsForValue().set("UserServer-" + userId, IpUtil.LOCAL_HTTP_URL);
        log.info("{}：上线，{}", channel.attr(uid).get(), IpUtil.LOCAL_HTTP_URL);
    }

    /**
     * 下线
     * 1. 从redis删除用户的连接信息
     * 2. 从channelMap中删除用户channel
     *
     * @param channel
     */
    public static void offline(Channel channel) {
        Object a = channel.attr(uid).get();
        if (null == a) {
            log.error("下线失败，uid为空");
            return;
        }
        String uid = a.toString();
        RedisTemplate redisTemplate = (RedisTemplate) SpringContextUtil.getBean("redisTemplate");
        redisTemplate.delete("uid");

        channelMap.remove(a.toString());
        channelGroup.remove(channel);
        log.info("{}：下线", uid);
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
    public static WsMessageDto sendMessage(WsMessageDto message) {
        Channel channel = channelMap.get(message.getTargetId());
        if (null == channel) {
            log.error(message.getTargetId() + "该用户连接不存在");
            message.setUid("server");
            message.setData("该用户连接不存在");
            message.setEvent("send_result_fail");
            return message;
        }
        channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message)));
        message.setUid("server");
        message.setEvent("send_result_ok");
        return message;
    }

    /**
     * 群发消息
     */
    public static void sendMessageAll(String meesage) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(meesage));
    }
}
