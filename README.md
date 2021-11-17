# WebSocket项目

##背景

此项目基于Netty、Websocket实现双端通信。

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

## todo

1. 使用Netty搭建HTTPClient代替feign
2. 使用Netty搭建HTTPServer代替Tomcat
3. 使用自定义RPC代替服务器之间的HTTP通信 


