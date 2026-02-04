package com.huike.video.common.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.huike.video.common.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Value("${app.resource.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.resource.context-path:/storage/}")
    private String urlContextPath;

    @PostConstruct
    public void init() {
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        if (!urlContextPath.startsWith("/")) {
            urlContextPath = "/" + urlContextPath;
        }
        if (!urlContextPath.endsWith("/")) {
            urlContextPath = urlContextPath + "/";
        }
    }

    @Override
    public String getUrl(String resourcePath) {
        if (!StringUtils.hasText(resourcePath)) {
            return null;
        }
        // 如果已经是 http 开头，直接返回
        if (resourcePath.startsWith("http://") || resourcePath.startsWith("https://")) {
            return resourcePath;
        }

        // 规范化: 移除开头的 /storage/ 或 /
        String cleanPath = resourcePath;
        if (cleanPath.startsWith(urlContextPath)) {
            cleanPath = cleanPath.substring(urlContextPath.length());
        } else if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        // 拼接: http://localhost:8080/storage/videos/xxx.mp4
        return baseUrl + urlContextPath.substring(1) + cleanPath;
    }

    @Override
    public String store(MultipartFile file, String module) throws IOException {
        String subDir = StringUtils.hasText(module) ? module : "default";
        // 使用工具类上传，返回 subDir/filename
        return com.huike.video.common.util.FileStorageUtils.uploadFile(file, subDir, IdUtil.simpleUUID());
    }

    @Override
    public String download(String remoteUrl, String module, String suggestedFileName) {
        if (!StringUtils.hasText(remoteUrl)) return null;
        String subDir = StringUtils.hasText(module) ? module : "default";
        
        // 如果建议文件名为空，生成一个
        String fileName = suggestedFileName;
        if (!StringUtils.hasText(fileName)) {
             String ext = com.huike.video.common.util.FileStorageUtils.extractExtension(remoteUrl);
             if (!StringUtils.hasText(ext)) ext = ".dat";
             fileName = IdUtil.simpleUUID() + ext;
        } else if (!fileName.contains(".")) {
             String ext = com.huike.video.common.util.FileStorageUtils.extractExtension(remoteUrl);
             fileName += StringUtils.hasText(ext) ? ext : ".bin";
        }

        // 使用工具类下载
        return com.huike.video.common.util.FileStorageUtils.downloadFile(remoteUrl, fileName, subDir);
    }

    @Override
    public boolean delete(String resourcePath) {
        return com.huike.video.common.util.FileStorageUtils.deleteFile(resourcePath);
    }
}
