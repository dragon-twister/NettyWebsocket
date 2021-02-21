package com.coocaa.manage.controller;

import com.alibaba.fastjson.JSONObject;
import com.coocaa.util.R;
import com.coocaa.manage.feign.sse.SseFeignClientService;
import com.coocaa.manage.netty.UserSseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *  sse平台推送消息给小程序
 * @author  liangshizhu
 */
@Slf4j
@RestController
@RequestMapping("/sse")
@Api(tags = "sse平台")
public class SseController {

    @Autowired
    SseFeignClientService sseFeignClientService;

    @PostMapping("/sendMiniprogramMessage")
    @ApiOperation("向小程序推送消息")
    public R sendMiniprogramMessage(@RequestBody JSONObject json) {
        log.info("SSE向小程序推送消息：{}",json);
        UserSseUtil.sendMessage(json.getString("uid"), json.toJSONString());
        return R.ok().data(null);
    }

}