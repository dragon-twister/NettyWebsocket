package com.coocaa.websocket.api.websocket;


/**
 * 消息体结构
 */
public class MessageDto {

    private String uid;
    private String targetId;
    private String messageId;
    private Object data;
    private String event;

    public MessageDto() {
    }

    public MessageDto(String uid, String targetId, String messageId, Object data, String event) {
        this.uid = uid;
        this.targetId = targetId;
        this.messageId = messageId;
        this.data = data;
        this.event = event;
    }

    public static MessageDto fail(String data) {
        MessageDto response = new MessageDto("server", "", "", data, "send_result_fail");
        return response;
    }

    public static MessageDto fail(String messageId, String data) {
        MessageDto response = new MessageDto("server", "", "", data, "send_result_fail");
        return response;
    }

    public static MessageDto ok() {
        MessageDto response = new MessageDto("server", "", "", "", "send_result_ok");
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