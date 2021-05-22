package com.broheim.websocket.core.endpoint.context;

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
     * 同步发送消息
     *
     * @param message
     * @return
     */
    boolean sendMessageSync(String message) throws Exception;

    /**
     * 同步发送消息
     *
     * @param message
     * @param timeOut
     * @return
     */
    boolean sendMessageSync(String message, Long timeOut) throws Exception;


    /**
     * 发送同步消息。请求响应模型
     *
     * @param message
     */
    Object sendMessage(String message) throws Exception;


    /**
     * 发送同步消息。请求响应模型
     *
     * @param message
     * @param timeOut
     * @return
     * @throws Exception
     */
    Object sendMessage(String message, Long timeOut) throws Exception;

    /**
     * 发送最原始的消息
     *
     * @param text
     */
    void sendText(String text) throws Exception;
}
