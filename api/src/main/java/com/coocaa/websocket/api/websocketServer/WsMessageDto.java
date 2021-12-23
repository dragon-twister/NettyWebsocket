package com.coocaa.websocket.api.websocketServer;


import java.io.Serializable;

/**
 * 消息体结构
 */
public class WsMessageDto implements Serializable {

    private String uid;
    private String targetId;
    private String messageId;
    private Object data;
    private String event;

    public WsMessageDto() {
    }

    public WsMessageDto(String uid, String targetId, String messageId, Object data, String event) {
        this.uid = uid;
        this.targetId = targetId;
        this.messageId = messageId;
        this.data = data;
        this.event = event;
    }

    public static WsMessageDto fail(String data) {
        WsMessageDto response = new WsMessageDto("server", "", "", data, "send_result_fail");
        return response;
    }

    public static WsMessageDto fail(String messageId, String data) {
        WsMessageDto response = new WsMessageDto("server", "", "", data, "send_result_fail");
        return response;
    }

    public static WsMessageDto ok(String messageId) {
        WsMessageDto response = new WsMessageDto("server", "", messageId, "", "send_result_ok");
        return response;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "uid='" + uid + '\'' +
                ", targetId='" + targetId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", data=" + data +
                ", event='" + event + '\'' +
                '}';
    }
}