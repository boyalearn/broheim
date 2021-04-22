package com.broheim.websocket.core.config;

import com.broheim.websocket.core.acceptor.Acceptor;
import com.broheim.websocket.core.acceptor.ClientDefaultAcceptor;
import com.broheim.websocket.core.acceptor.DefaultAcceptor;
import com.broheim.websocket.core.event.DefaultEventPublisher;
import com.broheim.websocket.core.event.EventPublisher;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.listener.*;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.reactor.ClientDefaultReactor;
import com.broheim.websocket.core.reactor.Reactor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Slf4j
public class ClientConfig {

    private EventPublisher publisher;

    private Protocol protocol;

    private Acceptor acceptor;

    private Reactor reactor;

    private List<EventListener> eventListeners = new ArrayList<>();

    private List<Handler> handlers = new ArrayList<>();

    public EventPublisher buildClientPublisher() {
        EventPublisher returnPublisher;
        if (null == this.publisher) {
            log.debug("use default eventPublisher..");
            returnPublisher = new DefaultEventPublisher();
        } else {
            returnPublisher = this.publisher;
        }
        if (null == this.protocol) {
            log.debug("use default simple protocol..");
            this.protocol = new SimpleProtocol();
        }

        if (null == this.acceptor) {
            log.debug("use default acceptor..");
            this.acceptor = new DefaultAcceptor();
        }
        this.acceptor.setProtocol(this.protocol);

        if (null == this.reactor) {
            this.reactor = new ClientDefaultReactor();
        }

        this.acceptor.setReactor(this.reactor);
        if (eventListeners.isEmpty()) {
            eventListeners.add(new MessageReceiveListener(this.acceptor));
            eventListeners.add(new ClientHeartbeatListener());
            eventListeners.add(new AsyncMessageSendListener());
            eventListeners.add(new SyncMessageSendListener());
            eventListeners.add(new RequestResponseMessageListener());
        }
        returnPublisher.addListeners(eventListeners);
        return returnPublisher;
    }
}
