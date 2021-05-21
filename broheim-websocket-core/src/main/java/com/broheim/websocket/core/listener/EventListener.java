package com.broheim.websocket.core.listener;


/**
 * 监听处理事件
 *
 * @param <Event> 事件类型
 */
public interface EventListener<Event> {
    Object onEvent(Event e) throws Exception;
}
