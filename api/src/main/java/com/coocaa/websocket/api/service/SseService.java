package com.coocaa.websocket.api.service;

import com.coocaa.websocket.api.netty.MessageDto;

public interface SseService {

    MessageDto sendMessage(MessageDto messageDto);

}
