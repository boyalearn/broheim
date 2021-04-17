# broheim （布罗海姆）

布罗海姆是一个基于tomcat-websocket-api的二次封装。再原有websocket之上封装了基于心跳检查、断线重连特性。解决了原有websocket网络状况感知不明显的问题。同时也结合SpringBoot的特性封装了spring-boot-starter模块，方便快速集成SpringCloud项目。

# 快速开始


## maven 依赖

在maven依赖中加入依赖项。

``` xml
    <dependency>
        <groupId>org.broheim</groupId>
        <artifactId>broheim-websocket-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.broheim</groupId>
        <artifactId>broheim-websocket-spring-boot</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```

为了找到依赖项还需要配置仓库地址

```xml
    <repositories>
        <repository>
            <id>github-broheim-core</id>
            <name>github-broheim-core</name>
            <url>https://raw.github.com/boyalearn/broheim-repository/core</url>
        </repository>
        <repository>
            <id>github-broheim-starter</id>
            <name>github-broheim-starter</name>
            <url>https://raw.github.com/boyalearn/broheim-repository/starter</url>
        </repository>
    </repositories>
```

编写启动类

```java
@SpringBootApplication
@EnableWebSocketServer
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public CommandReactor commandReactor(){
        return new CommandReactor();
    }
}
```