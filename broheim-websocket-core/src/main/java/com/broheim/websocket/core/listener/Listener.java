package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.event.accept.Event;

/**
 * 监听处理事件
 *
 */
public interface Listener {
    void onEvent(Event event) throws Exception;
}
