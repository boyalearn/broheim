## WebSocketEndpoint 处理最原始的消息

OnOpen、OnMessage、OnError、OnClose

## EventPublisher 将这些消息事件通过相应的事件发布出去

Future publish(Event e);

## EventListener 事件监听器监听事件

void onEvent(Event e);

## Protocol 消息协议

用于处理协议消息的处理

## ChannelContext 代表一个连接应该保证线程安全性

使用ChannelContext的目的是为了要发送数据给客户端。所以应该包含一些线程安全的发送消息方法

## Handler 处理解析后的消息

