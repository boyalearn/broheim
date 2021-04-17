# broheim （布罗海姆）

布罗海姆是一个基于tomcat-websocket-api的二次封装。再原有websocket之上封装了基于心跳检查、断线重连特性。解决了原有websocket网络状况感知不明显的问题。同时也结合SpringBoot的特性封装了spring-boot-starter模块，方便快速集成SpringCloud项目。


该项目是一个websocket项目。用于长连接，实现了心跳、短线重连、同步发送消息等特性
