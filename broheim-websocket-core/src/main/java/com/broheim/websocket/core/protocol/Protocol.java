package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.reactor.Reactor;

/**
 * 协议的作用就是编码和解码消息头
 *
 * @param <E>
 */
public interface Protocol<E> {

    /**
     * 同步消息
     */
    String ASYNC = "async";

    /**
     * 异步消息
     */
    String SYNC = "sync";

    /**
     * 请求响应模型
     */
    String REQ_RESP = "req-resp";

    /**
     * 应答消息
     */
    String ACK = "ack";

    /**
     * ping消息
     */
    String PING = "ping";

    /**
     * 编码协议层消息
     *
     * @param message 应用层消息
     * @return 协议层消息
     * @throws MessageProtocolException
     */
    String encode(ChannelContext channelContext, String message) throws MessageProtocolException;


    /**
     * 编码协议层消息
     *
     * @param message 应用层消息
     * @return 协议层消息
     * @throws MessageProtocolException
     */
    String encode(ChannelContext channelContext, String message, Integer serialNo) throws MessageProtocolException;


    /**
     * 解析协议层消息
     *
     * @param message 协议层消息
     * @return 应用层消息
     * @throws MessageProtocolException
     */
    E decode(String message) throws MessageProtocolException;


    /**
     * 协议解析层逻辑处理
     *
     * @param channelContext 连接管道
     * @param message        原始消息
     * @param reactor        业务消息分发器
     * @throws MessageProtocolException
     */
    void service(ChannelContext channelContext, String message, Reactor reactor) throws MessageProtocolException;

}
