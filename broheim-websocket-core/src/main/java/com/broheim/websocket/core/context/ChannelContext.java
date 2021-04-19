package com.broheim.websocket.core.context;

import com.broheim.websocket.core.endpoint.WebSocketEndpoint;
import com.broheim.websocket.core.event.SendMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.protocol.Protocol;

import java.io.IOException;

/**
 * 发送响应数据的统一对外接口
 */
public interface ChannelContext {

    WebSocketEndpoint getEndpoint();

    String getMessage();

    Protocol getProtocol();

    /**
     * 异步消息发送
     *
     * @param message
     */
    void sendMessageAsync(String message) throws MessageProtocolException, IOException;

    /**
     * 异步消息发送
     *
     * @param message
     * @param timeOut
     * @throws MessageProtocolException
     * @throws IOException
     */
    void sendMessageAsync(String message, Long timeOut) throws MessageProtocolException, IOException;

    /**
     * 同步发送消息
     *
     * @param message
     * @return
     */
    boolean sendMessageSync(String message) throws MessageProtocolException;

    /**
     * 发送指定监听的消息
     *
     * @param message
     * @param eventListener
     */
    void sendMessage(String message, EventListener<SendMessageEvent> eventListener) throws MessageProtocolException;

    /**
     * 发送最原始的消息
     *
     * @param text
     */
    void sendText(String text) throws IOException;
}
