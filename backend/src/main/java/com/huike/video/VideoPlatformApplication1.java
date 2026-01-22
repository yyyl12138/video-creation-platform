package com.huike.video;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan(basePackages = "com.huike.video.modules", annotationClass = Mapper.class)
public class VideoPlatformApplication1 {

    public static void main(String[] args) {
        SpringApplication.run(VideoPlatformApplication1.class, args);
    }

}
