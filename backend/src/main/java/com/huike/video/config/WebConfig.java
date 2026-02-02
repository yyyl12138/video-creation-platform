package com.huike.video.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import lombok.extern.slf4j.Slf4j;

/**
 * Web MVC 配置
 * 配置静态资源映射，用于访问上传/下载的文件
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-path:./storage/}")
    private String uploadPath;

    /*
    // STATIC RESOURCE HANDLER REMOVED - REPLACED BY FileController
    // Due to persistent FtpProtocolException caused by environmental issues with file: protocol
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      ...
    }
    */

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
