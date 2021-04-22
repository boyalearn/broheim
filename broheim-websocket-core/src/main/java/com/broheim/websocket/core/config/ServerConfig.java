package com.broheim.websocket.core.config;


import com.broheim.websocket.core.acceptor.Acceptor;
import com.broheim.websocket.core.acceptor.DefaultAcceptor;
import com.broheim.websocket.core.annonation.SocketEndpointPath;
import com.broheim.websocket.core.event.DefaultEventPublisher;
import com.broheim.websocket.core.event.EventPublisher;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.listener.*;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.reactor.DefaultReactor;
import com.broheim.websocket.core.reactor.Reactor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Slf4j
public class ServerConfig {

    private List<Protocol> protocols = new ArrayList<>();

    private List<Acceptor> acceptors = new ArrayList<>();

    private List<Reactor> reactors = new ArrayList<>();

    private List<Handler> handlers = new ArrayList<>();

    private List<EventListener> eventListeners = new ArrayList<>();

    private List<EventPublisher> eventPublishers = new ArrayList<>();

    public void addReactor(Reactor reactor) {
        this.reactors.add(reactor);
    }

    public void addHandlers(List<Handler> handlers) {
        this.handlers.addAll(handlers);
    }

    public EventPublisher buildPublisherForEndpoint(Class<?> endpointClass) {
        ServerEndpoint serverEndpoint = endpointClass.getAnnotation(ServerEndpoint.class);
        String endpointPath = serverEndpoint.value();
        List<Handler> handlerList = findHandlers(endpointPath);
        Protocol protocol = findProtocol(endpointPath);
        Reactor reactor = findReactor(endpointPath);
        reactor.addHandlers(handlerList);
        Acceptor acceptor = findAcceptor(endpointPath);
        acceptor.setProtocol(protocol);
        acceptor.setReactor(reactor);
        List<EventListener> eventListenerList = findEventListener(endpointPath, acceptor);
        EventPublisher publisher = findPublisher(endpointPath);
        publisher.addListeners(eventListenerList);
        return publisher;
    }

    private EventPublisher findPublisher(String endpointPath) {
        List<EventPublisher> eventPublisherList = new ArrayList<>();

        for (EventPublisher eventPublisher : this.eventPublishers) {
            SocketEndpointPath annotation = eventPublisher.getClass().getAnnotation(SocketEndpointPath.class);
            if (annotation != null && annotation.value().equals(endpointPath)) {
                eventPublisherList.add(eventPublisher);
            }
        }
        if (1 == eventPublisherList.size()) {
            return eventPublisherList.get(0);
        }

        if (eventPublisherList.size() > 1) {
            throw new RuntimeException("EventPublisher it's not unique!");
        }

        for (EventPublisher eventPublisher : this.eventPublishers) {
            SocketEndpointPath annotation = eventPublisher.getClass().getAnnotation(SocketEndpointPath.class);
            if (null == annotation) {
                eventPublisherList.add(eventPublisher);
            }
        }
        if (1 == eventPublisherList.size()) {
            return eventPublisherList.get(0);
        }
        if (eventPublisherList.size() > 1) {
            throw new RuntimeException("EventPublisher it's not unique!");
        }
        return new DefaultEventPublisher();
    }

    private List<EventListener> findEventListener(String endpointPath, Acceptor acceptor) {
        List<EventListener> eventListenerList = new ArrayList<>();
        this.eventListeners.forEach(h -> {
            SocketEndpointPath annotation = h.getClass().getAnnotation(SocketEndpointPath.class);
            if (annotation == null || annotation.value().equals(endpointPath)) {
                eventListenerList.add(h);
            }
        });
        eventListenerList.add(new ServerHeartbeatListener());
        eventListenerList.add(new MessageReceiveListener(acceptor));
        eventListenerList.add(new SyncMessageSendListener());
        eventListenerList.add(new AsyncMessageSendListener());
        return eventListenerList;
    }

    private Acceptor findAcceptor(String endpointPath) {

        List<Acceptor> acceptorList = new ArrayList<>();

        for (Acceptor acceptor : this.acceptors) {
            SocketEndpointPath annotation = acceptor.getClass().getAnnotation(SocketEndpointPath.class);
            if (annotation != null && annotation.value().equals(endpointPath)) {
                acceptorList.add(acceptor);
            }
        }
        if (1 == acceptorList.size()) {
            return acceptorList.get(0);
        }

        if (acceptorList.size() > 1) {
            throw new RuntimeException("Acceptor it's not unique!");
        }

        for (Acceptor acceptor : this.acceptors) {
            SocketEndpointPath annotation = acceptor.getClass().getAnnotation(SocketEndpointPath.class);
            if (null == annotation) {
                acceptorList.add(acceptor);
            }
        }
        if (1 == acceptorList.size()) {
            return acceptorList.get(0);
        }
        if (acceptorList.size() > 1) {
            throw new RuntimeException("Acceptor it's not unique!");
        }
        log.debug("use default DefaultAcceptor..");
        return new DefaultAcceptor();
    }

    private Reactor findReactor(String endpointPath) {

        List<Reactor> reactorList = new ArrayList<>();

        for (Reactor reactor : this.reactors) {
            SocketEndpointPath annotation = reactor.getClass().getAnnotation(SocketEndpointPath.class);
            if (annotation != null && annotation.value().equals(endpointPath)) {
                reactorList.add(reactor);
            }
        }
        if (1 == reactorList.size()) {
            return reactorList.get(0);
        }

        if (reactorList.size() > 1) {
            throw new RuntimeException("Reactor it's not unique!");
        }

        for (Reactor reactor : this.reactors) {
            SocketEndpointPath annotation = reactor.getClass().getAnnotation(SocketEndpointPath.class);
            if (null == annotation) {
                reactorList.add(reactor);
            }
        }
        if (1 == reactorList.size()) {
            return reactorList.get(0);
        }
        if (reactorList.size() > 1) {
            throw new RuntimeException("Reactor it's not unique!");
        }
        log.debug("use default Reactor..");
        return new DefaultReactor();
    }

    private List<Handler> findHandlers(String endpointPath) {
        List<Handler> handlerList = new ArrayList<>();
        this.handlers.forEach(h -> {
            SocketEndpointPath annotation = h.getClass().getAnnotation(SocketEndpointPath.class);
            if (annotation == null || annotation.value().equals(endpointPath)) {
                handlerList.add(h);
            }
        });
        return handlerList;
    }

    private Protocol findProtocol(String endpointPath) {
        List<Protocol> protocolList = new ArrayList<>();
        for (Protocol protocol : this.protocols) {
            SocketEndpointPath annotation = protocol.getClass().getAnnotation(SocketEndpointPath.class);
            if (null != annotation && annotation.value().equals(endpointPath)) {
                protocolList.add(protocol);
            }
        }
        if (1 == protocolList.size()) {
            return protocolList.get(0);
        }

        if (protocolList.size() > 1) {
            throw new RuntimeException("Protocol it's not unique!");
        }

        for (Protocol protocol : this.protocols) {
            SocketEndpointPath annotation = protocol.getClass().getAnnotation(SocketEndpointPath.class);
            if (null == annotation) {
                protocolList.add(protocol);
            }
        }
        if (1 == protocolList.size()) {
            return protocolList.get(0);
        }
        if (protocolList.size() > 1) {
            throw new RuntimeException("Protocol it's not unique!");
        }
        log.debug("use default simple protocol..");
        return new SimpleProtocol();
    }
}
