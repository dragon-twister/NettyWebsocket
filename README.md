- [小程序项目](#-----)
  * [背景](#----)
  * [项目目录结构](#websocket)
  * [websocket](#websocket)

# 小程序项目

##背景

公司业务要求小程序和电视实现双向通信。
此项目基于netty、websocket实现，已去除业务代码。

## 项目目录结构

    api:为前端提供服务的接口
    common:通用模块
    
## websocket

    地址：
     wss://beta-mgredup.coocaa.com/ws/mp2tv?uid=1
     ws://localhost:8004/ws/mp2tv?uid=1
    报文： 
    {
        "uid":"6adb58d1568811e9ac64525400f3186f",
        "targetId":"72942380",
        "messageId":"tmp_be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3.mp3",
        "data":{"fileUrl":"http://develop-1302688857.cos.ap-shenzhen-fsi.myqcloud.com/tmp_be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3.mp3"},
        "fileUrl":"http://develop-1302688857.cos.ap-shenzhen-fsi.myqcloud.com/tmp_be1544a784aaf33543628f7fadf2febcb1d73ba61746d3f3.mp3",
        "event":"voice_to_tv"
    }

