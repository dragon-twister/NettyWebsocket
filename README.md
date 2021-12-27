# WebSocket项目

## 背景

此项目基于Netty、Websocket实现IM通信，支持集群部署。

![](http://edrawcloudpubliccn.oss-cn-shenzhen.aliyuncs.com/viewer/self/1444767/share/2021-11-17/1637150176/main.svg)

## 项目目录结构

    api: 提供接口
    common: 通用模块
    
## 接口

| **参数名** | **类型** | **必填** | **说明**                                         |
| ---------- | -------- | -------- | ------------------------------------------------ |
| uid        | String   | 是       | 用户id，客户端生成，保证唯一                     |
| targetId   | String   | 是       | 目标用户id，客户端生成，保证唯一                 |
| messageId  | String   | 是       | 消息id，客户端生成，保证唯一                     |
| data       | String   | 否       | 客户端根据业务需要自定义                         |
| event      | String   | 是       | 事件类型，如果要发给目标用户，则定义为sendToTarget |


### HTTP接口

1. 向目标发送消息：

    url：http://localhost:8002/sendToClient?uid=1
    
    报文： 
    
    ```json
    {
        "uid":"1",
        "targetId":"2",
        "messageId":"be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3",
        "data":{"key":"value"},
        "event":"customize"
    }
    ```

### websocket
    
1. 建立websocket连接：
    
    ```java
    ws://localhost:8001?uid=1

    ```

2. 向目标发送消息：

    ```json
    {
        "uid":"1",
        "targetId":"2",
        "messageId":"be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3",
        "data":{"key":"value"},
        "event":"customize"
    }
    ```

3. 间隔30秒向向服务器发送心跳

    
    ```java
    ping
    ```
    
    注意此处非json字符串，避免浪费资源去转为json。项目会删除1分钟后没有请求的连接，避免资源浪费。
    
    
    
## 客户端需要做的工作：

发起心跳，发现断开后重连。

## FAQ

### 为什么发送消息使用HTTP短连接？

服务器为集群的时候，需要转发请求到连接目标用户的服务器。

### Netty如何将Request和Response对应

在 Netty 里，channel 是多路复用的，返回的 Response 不会自动映射到发出的 Request 上。这也是HTTP CLIENT 很少使用Netty搭建的原因。
如果 Client, Server 都由开发者掌控，那么 client 和 server 可以在交互协议上添加 requestId 。client 端每发送一个 request 后，就在本地记录 (requestId, Future[Response]) 这么一个 pair, 当 response 返回后，根据 requestId 找到对应的 future, 填充 future。
在此项目里，我们使用 MessageId 作为 requestId

### 实现websocket集群的几种方式

1 自定义网关，一致性哈希计算出目标用户所在服务器，需要部署网关比较麻烦。
2 注册中心，redis记录用户id和用户连接的服务器的ip:port，项目使用此方法。
3 消息队列广播消息，此方法浪费资源

