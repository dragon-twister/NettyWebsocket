package com.coocaa.websocket.api.websocket;

import lombok.ToString;

/**
 * 消息体结构
 */
@ToString
public class MessageDto {

    private String uid;
    private String targetId;
    private String messageId;
    private Object data;
    private String event;

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