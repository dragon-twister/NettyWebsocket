package com.coocaa.manage;

import com.coocaa.manage.netty.ServerByNetty;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"com.coocaa.dao"})
@ComponentScan("com.coocaa")
@EnableFeignClients(basePackages = {"com.coocaa.manage.feign"})
@EnableCaching
public class ManageApplication implements CommandLineRunner {
	/**
	 * netty服务
	 */
	@Autowired
	ServerByNetty serverByNetty;


	public static void main(String[] args) {
		SpringApplication.run(ManageApplication.class, args);


	}
	@Override
	public void run(String... args) throws Exception {
		serverByNetty.startServer();
	}
}
