package com.broheim.client.controller;

import com.broheim.client.bean.CommandMessage;
import com.broheim.client.service.WebSocketService;
import com.broheim.client.service.WebSocketTestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping("/echo")
    public String echoServer(@RequestParam("echo") String context) throws Exception {
        CommandMessage commandMessage = new CommandMessage();
        commandMessage.setBody(context);
        commandMessage.setCmd("hello");
        return webSocketService.echo(objectMapper.writeValueAsString(commandMessage));
    }

    @GetMapping("/info")
    public Object getThreadPoolInfo() {
        int size = WebSocketTestService.EXECUTOR.getQueue().size();
        int corePoolSize = WebSocketTestService.EXECUTOR.getCorePoolSize();
        long taskCount = WebSocketTestService.EXECUTOR.getTaskCount();

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("size", size);
        result.put("corePoolSize", corePoolSize);
        result.put("taskCount", taskCount);
        return result;

    }
}
