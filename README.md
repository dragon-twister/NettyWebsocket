# WebSocket项目

##背景

此项目基于Netty、Websocket实现双端通信。如果是集群部署，需要Nginx基于url中的uid做一致性哈希解决集群问题。

## 项目目录结构

    api:为前端提供服务的接口
    common:通用模块
    
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

