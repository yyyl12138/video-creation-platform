package com.huike.video.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
public class FileController {

    @Value("${file.upload-path:./storage/}")
    private String uploadPath;

    @GetMapping("/storage/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {
        try {
            // 获取请求路径中的相对文件路径
            String requestUri = request.getRequestURI();
            String relativePath = requestUri.substring(requestUri.indexOf("/storage/") + 9);
            // 解码 URL (处理中文等特殊字符)
            relativePath = URLDecoder.decode(relativePath, StandardCharsets.UTF_8.toString());

            // 构建绝对路径
            Path rootLocation = Paths.get(uploadPath).toAbsolutePath();
            Path filePath = rootLocation.resolve(relativePath).normalize();

            // 安全检查：防止目录遍历攻击，确保文件在 storage 目录下
            if (!filePath.startsWith(rootLocation)) {
                log.warn("Access denied: Invalid file path {}", filePath);
                return ResponseEntity.status(403).build();
            }

            Resource resource = new FileSystemResource(filePath.toFile());

            if (resource.exists() && resource.isReadable()) {
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException ex) {
                    log.info("Could not determine file type.");
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                log.info("Serving file: {}", filePath);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                log.warn("File not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error serving file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
