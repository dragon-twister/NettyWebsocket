package com.coocaa.websocket.api;

import com.coocaa.websocket.api.netty.ServerByNetty;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"com.coocaa.websocket.common.dao"})
@ComponentScan("com.coocaa.websocket")
@EnableFeignClients(basePackages = {"com.coocaa.websocket.api.feign"})
@EnableCaching
public class ApiApplication implements CommandLineRunner {

	@Autowired
	ServerByNetty serverByNetty;

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		serverByNetty.startServer();
	}
}
