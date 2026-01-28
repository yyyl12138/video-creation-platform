package com.huike.video.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 旧的上传路径映射 (兼容)
        registry.addResourceHandler("/profile/upload/**")
                .addResourceLocations("file:" + uploadPath);
        
        // 新的存储路径映射 (用于下载的视频、图片、封面等)
        // 访问 /storage/videos/xxx.mp4 -> 本地 {upload-path}/videos/xxx.mp4
        registry.addResourceHandler("/storage/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
