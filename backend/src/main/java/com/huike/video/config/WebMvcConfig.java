package com.huike.video.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

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
        String basePath = uploadPath;
        if (basePath != null && !basePath.endsWith(File.separator) && !basePath.endsWith("/")) {
            basePath = basePath + File.separator;
        }

        // Windows 兼容：使用 file:/ 形式更稳定，且确保目录以 / 结尾
        String location = "file:/" + basePath.replace("\\", "/");

        // 旧的上传路径映射 (兼容历史数据，不建议新业务使用)
        registry.addResourceHandler("/profile/upload/**")
                .addResourceLocations(location);

        // 新的存储路径映射 (用于下载的视频、图片、封面等)
        // 访问 /storage/videos/xxx.mp4 -> 本地 {upload-path}/videos/xxx.mp4
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(location);
    }
}
