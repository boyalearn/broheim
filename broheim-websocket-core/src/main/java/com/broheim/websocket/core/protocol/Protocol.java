package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.exception.MessageProtocolException;

/**
 * 协议的作用就是编码和解码消息头
 *
 * @param <E>
 */
public interface Protocol<E> {


    /**
     * 编码协议层消息
     *
     * @param message 应用层消息
     * @return 协议层消息
     * @throws MessageProtocolException
     */
    String encode(String message) throws MessageProtocolException;


    /**
     * 解析协议层消息
     *
     * @param message 协议层消息
     * @return 应用层消息
     * @throws MessageProtocolException
     */
    E decode(String message) throws MessageProtocolException;

}
