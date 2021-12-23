package com.coocaa.websocket.api;

import com.coocaa.websocket.api.websocketServer.WebsocketServer;
import com.coocaa.websocket.api.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication(scanBasePackages ="com.coocaa.websocket")
public class ApiApplication implements CommandLineRunner {

    @Autowired
    WebsocketServer websocketServer;
    @Autowired
    HttpServer httpsServer;

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        websocketServer.startServer();
        httpsServer.startServer();
    }

}
