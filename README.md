# WebSocket项目

##背景

此项目基于Netty、Websocket实现IM通信。

如果是集群部署，需要Nginx对WebSocket和HTTP请求中url中的uid做一致性哈希解决集群问题。

![](http://edrawcloudpubliccn.oss-cn-shenzhen.aliyuncs.com/viewer/self/1444767/share/2021-11-17/1637150176/main.svg)

## 项目目录结构

    api: 提供接口
    common: 通用模块
    
## 接口

    使用http接口向目标发送消息
    ws://localhost:8005?uid=1
    报文： 
    {
        "uid":"aaa",
        "targetId":"bbb",
        "messageId":"be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3",
        "data":{"key":"value"},
        "event":"customize"
    }
    
    使用websocket向目标发送消息：
    http://localhost:8003/sendToClient?uid=1
    报文： 
    {
        "uid":"aaa",
        "targetId":"bbb",
        "messageId":"be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3",
        "data":{"key":"value"},
        "event":"customize"
    }

## FAQ

### 为什么发送消息使用HTTP短连接？

本项目使用Nginx做负载均衡，服务端想要给客户端发送消息就要依赖Nginx。
Websocket属于长连接，只有在建立连接的时候Nginx使用一致性哈希算法做负载均衡。
连接建立后，Nginx无法改变消息发往哪个服务器。
只有使用HTTP短连接，才能把消息路由到存有目标用户连接的服务器上。
在此也发现了长连接的缺点，长连接在使用中间件做负载均衡的时候，一般只能均分连接，而无法做到均分流量。

### Netty如何将Request和Response对应

在 Netty 里，channel 是多路复用的，返回的 Response 不会自动映射到发出的 Request 上。这也是HTTP CLIENT 很少使用Netty搭建的原因。
如果 Client, Server 都由开发者掌控，那么 client 和 server 可以在交互协议上添加 requestId 。client 端每发送一个 request 后，就在本地记录 (requestId, Future[Response]) 这么一个 pair, 当 response 返回后，根据 requestId 找到对应的 future, 填充 future。
在此项目里，我们使用 MessageId 作为 requestId


### 实现websocket集群的几种方式

1 自定义网关，一致性哈希计算出目标用户所在服务器
2 注册中心，redis记录所有用户id和连接服务器的ip
3 消息队列广播消息
