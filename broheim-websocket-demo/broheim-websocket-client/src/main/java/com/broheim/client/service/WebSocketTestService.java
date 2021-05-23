package com.broheim.client.service;

import com.broheim.client.bean.CommandMessage;
import com.broheim.websocket.core.endpoint.client.WebSocketClient;
import com.broheim.websocket.core.exception.ChannelCloseException;
import com.broheim.websocket.core.publisher.threadpool.NamedThreadFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class WebSocketTestService {

    @Autowired
    private WebSocketService webSocketService;

    public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            100,
            300,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(5000),
            new NamedThreadFactory("websocket-pool-"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @PostConstruct
    public void init() throws IOException, DeploymentException {
        for (int i = 0; i < 50; i++) {
            EXECUTOR.submit(new Worker(webSocketService.webSocketClient));
        }
    }


    @AllArgsConstructor
    public static class Worker implements Runnable {

        private WebSocketClient client;

        @Override
        public void run() {
            ObjectMapper objectMapper = new ObjectMapper();
            int i = 0;
            int error = 0;
            while (true) {
                String context = "" + i;
                CommandMessage commandMessage = new CommandMessage();
                commandMessage.setBody(context);
                commandMessage.setCmd("hello");
                try {
                    Object json = this.client.sendMessage(objectMapper.writeValueAsString(commandMessage));
                    CommandMessage acceptMessage = objectMapper.readValue(String.valueOf(json), CommandMessage.class);
                    if (!context.equals(acceptMessage.getBody())) {
                        error++;
                        System.err.println(error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    error++;
                    System.err.println(error);
                    if(e instanceof ChannelCloseException){
                        return;
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }

}
