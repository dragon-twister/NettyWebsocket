package com.coocaa.websocket.common.config;


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

  @Bean
    public  PaginationInterceptor paginationInterceptor() {
      PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
      return paginationInterceptor;
  }
      @Bean
       public MapperScannerConfigurer mapperScannerConfigurer() {
          MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
          scannerConfigurer.setBasePackage("com.coocaa.common.dao");
          return scannerConfigurer;
      }
  }

