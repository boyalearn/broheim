package com.broheim.websocket.core.publisher;


import com.broheim.websocket.core.event.Event;

import java.util.concurrent.Future;

/**
 * 事件发布者
 * <p>
 * 发布各种事件触发监听器的响应处理方法
 */
public interface EventPublisher {

    Future publish(Event e);
}
