package com.broheim.websocket.core.endpoint.context;

import com.broheim.websocket.core.event.SendMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.listener.EventListener;

import java.io.IOException;

/**
 * 发送响应数据的统一对外接口
 */
public interface ChannelContext {

    /**
     * 异步消息发送
     *
     * @param message
     */
    void sendMessageAsync(String message) throws Exception;

    /**
     * 异步消息发送
     *
     * @param message
     * @param timeOut
     * @throws MessageProtocolException
     * @throws IOException
     */
    void sendMessageAsync(String message, Long timeOut) throws Exception;

    /**
     * 同步发送消息
     *
     * @param message
     * @return
     */
    boolean sendMessageSync(String message) throws Exception;

    /**
     * 发送指定监听的消息
     *
     * @param message
     * @param eventListener
     */
    void sendMessage(String message, EventListener<SendMessageEvent> eventListener) throws Exception;


    /**
     * 发送同步消息。请求响应模型
     *
     * @param message
     */
    Object sendMessage(String message) throws Exception;

    /**
     * 发送最原始的消息
     *
     * @param text
     */
    void sendText(String text) throws Exception;
}
