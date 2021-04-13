package com.broheim.websocket.core.context;

import com.broheim.websocket.core.endpoint.WebSocketServerEndpoint;
import com.broheim.websocket.core.message.Message;

/**
 * 发送响应数据的统一对外接口
 */
public interface ChannelContext {

    WebSocketServerEndpoint getEndpoint();

    boolean sendSyncMessage(String message);

    void sendAsyncMessage(String message);

    void sendMessage(Message message);
}
