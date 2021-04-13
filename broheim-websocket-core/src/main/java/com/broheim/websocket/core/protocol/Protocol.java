package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;

import javax.websocket.Session;

public interface Protocol<E> {

    /**
     * 编码协议层消息
     *
     * @param appMessage 应用层消息
     * @return 协议层消息
     * @throws MessageProtocolException
     */
    E encode(String appMessage) throws MessageProtocolException;

    /**
     * 编码协议层消息
     *
     * @param appMessage 应用层消息
     * @return 协议层消息
     * @throws MessageProtocolException
     */
    String addProtocolHeader(String appMessage, int sendId) throws MessageProtocolException;

    /**
     * 解析协议层消息
     *
     * @param message 协议层消息
     * @return 应用层消息
     * @throws MessageProtocolException
     */
    E decode(String message) throws MessageProtocolException;


    /**
     * 返回应用消息
     * <p>
     * 如果通信协议则返回null;
     *
     * @param channelContext
     * @param message
     * @return
     */
    String doProtocol(ChannelContext channelContext, E message);

    /**
     * 让持有该Session的线程等待
     *
     * @param session
     */
    void wait(int sendId, Session session) throws InterruptedException;


    /**
     * 让持有该Session的线程等待带超时时间
     *
     * @param session
     */
    void wait(int sendId, Session session, long timeOut) throws InterruptedException;


    /**
     * 唤醒持有该Session的线程
     *
     * @param session
     */
    default void notify(Session session) {
        session.notifyAll();
    }
}
