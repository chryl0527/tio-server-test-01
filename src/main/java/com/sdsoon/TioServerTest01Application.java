package com.sdsoon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;

@Slf4j
@SpringBootApplication
public class TioServerTest01Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {

        //ServerStarter.start();



        SpringApplication.run(TioServerTest01Application.class, args);


        log.debug("================================");
        log.info("===================");
        log.error("--------------");
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}
