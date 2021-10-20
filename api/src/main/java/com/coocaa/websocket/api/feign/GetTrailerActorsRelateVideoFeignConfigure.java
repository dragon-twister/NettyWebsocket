package com.coocaa.websocket.api.feign;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

/**
 * @author zen
 * @date 2019-12-05 16:08
 * @description
 */

public class GetTrailerActorsRelateVideoFeignConfigure {

    private static final int CONNECT_TIMEOUT_MILLIS = 15000;

    private static final int READ_TIMEOUT_MILLIS = 9000;

    @Bean
    public Request.Options options() {
        return new Request.Options(CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default();
    }

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

}
