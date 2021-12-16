package com.coocaa.websocket.api;

import com.coocaa.websocket.api.websocket.WebsocketServer;
import com.coocaa.websocket.api.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan("com.coocaa.websocket")
@EnableAsync
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
