package com.broheim.websocket.core.config;

import com.broheim.websocket.core.acceptor.Acceptor;
import com.broheim.websocket.core.acceptor.ClientDefaultAcceptor;
import com.broheim.websocket.core.event.DefaultEventPublisher;
import com.broheim.websocket.core.event.EventPublisher;
import com.broheim.websocket.core.listener.ClientHeartbeatListener;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.listener.MessageReceiveListener;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.reactor.ClientDefaultReactor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ClientConfig {

    private EventPublisher publisher;

    private Protocol protocol = new SimpleProtocol();

    private Acceptor acceptor = new ClientDefaultAcceptor<>();

    private List<EventListener> eventListeners = new ArrayList<>();

    public EventPublisher buildClientPublisher() {
        EventPublisher returnPublisher = new DefaultEventPublisher();
        if (null != this.publisher) {
            returnPublisher = publisher;
        }
        this.acceptor.setProtocol(this.protocol);
        this.acceptor.setReactor(new ClientDefaultReactor());
        if (eventListeners.isEmpty()) {
            eventListeners.add(new MessageReceiveListener(this.acceptor));
            eventListeners.add(new ClientHeartbeatListener());
        }
        returnPublisher.addListeners(eventListeners);
        return returnPublisher;
    }
}
