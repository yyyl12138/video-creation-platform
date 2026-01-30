package com.huike.video.common.util;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件存储工具类
 * 从 application.yml 读取 file.upload-path 配置
 * 提供文件上传、下载的统一封装
 */
@Slf4j
@Component
public class FileStorageUtils {

    @Value("${file.upload-path:./storage/}")
    private String uploadPath;

    // 静态变量供静态方法使用
    private static String BASE_STORAGE_PATH;

    @PostConstruct
    public void init() {
        // 确保路径以 / 结尾
        if (!uploadPath.endsWith(File.separator) && !uploadPath.endsWith("/")) {
            uploadPath = uploadPath + File.separator;
        }
        BASE_STORAGE_PATH = uploadPath;
        log.info("文件存储根路径已初始化: {}", BASE_STORAGE_PATH);
        
        // 确保根目录存在
        File baseDir = new File(BASE_STORAGE_PATH);
        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            log.info("创建存储根目录: {} - {}", BASE_STORAGE_PATH, created ? "成功" : "失败");
        }
    }

    /**
     * 获取存储根路径
     */
    public static String getBasePath() {
        return BASE_STORAGE_PATH;
    }

    // ==================== 上传相关方法 ====================

    /**
     * 上传 MultipartFile 文件到指定子目录
     * @param file 上传的文件
     * @param subDir 子目录 (如 "avatar", "images", "videos")
     * @param fileNamePrefix 文件名前缀 (如 userId)
     * @return 可用于访问的相对路径 (如 /storage/avatar/u123_1704067200.jpg)
     * @throws IOException 文件写入失败
     */
    public static String uploadFile(MultipartFile file, String subDir, String fileNamePrefix) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 提取文件扩展名
        String ext = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(ext)) {
            ext = ".bin";
        }

        // 生成唯一文件名
        String fileName = fileNamePrefix + "_" + System.currentTimeMillis() + ext;

        // 构建目标路径
        String targetDir = BASE_STORAGE_PATH;
        if (StringUtils.hasText(subDir)) {
            targetDir += subDir + File.separator;
        }

        Path targetPath = Paths.get(targetDir, fileName);
        
        // 确保目录存在
        Files.createDirectories(targetPath.getParent());
        
        // 写入文件
        file.transferTo(targetPath);
        log.info("文件上传成功: {} ({}字节)", targetPath, file.getSize());

        // 返回相对访问路径
        String relativePath = "/storage/";
        if (StringUtils.hasText(subDir)) {
            relativePath += subDir + "/";
        }
        return relativePath + fileName;
    }

    /**
     * 上传文件并返回完整 URL
     * @param file 上传的文件
     * @param subDir 子目录
     * @param fileNamePrefix 文件名前缀
     * @param baseUrl 服务器基础URL (如 http://localhost:8080)
     * @return 完整访问 URL
     * @throws IOException 文件写入失败
     */
    public static String uploadFileWithFullUrl(MultipartFile file, String subDir, String fileNamePrefix, String baseUrl) throws IOException {
        String relativePath = uploadFile(file, subDir, fileNamePrefix);
        return (baseUrl != null ? baseUrl : "") + relativePath;
    }

    /**
     * 从原始文件名中提取扩展名
     * @param originalFilename 原始文件名
     * @return 扩展名 (带点，如 ".jpg")，无扩展名则返回空字符串
     */
    public static String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        int idx = originalFilename.lastIndexOf('.');
        if (idx >= 0 && idx < originalFilename.length() - 1) {
            return originalFilename.substring(idx);
        }
        return "";
    }

    /**
     * 从 URL 下载文件到本地存储
     * @param url 远程 URL
     * @param fileName 目标文件名
     * @param subDir 子目录名 (如 "images", "videos", "covers")
     * @return 用于前端访问的相对路径
     */
    public static String downloadFile(String url, String fileName, String subDir) {
        if (!StringUtils.hasText(url)) return null;

        try {
            // 构建目标目录: 基础路径 + 子目录
            String targetPath = BASE_STORAGE_PATH;
            if (StringUtils.hasText(subDir)) {
                targetPath += subDir + File.separator;
            }

            File destDir = new File(targetPath);
            if (!destDir.exists()) {
                boolean created = destDir.mkdirs();
                if (!created) {
                    log.warn("创建目录失败: {}", destDir.getAbsolutePath());
                }
            }

            File destFile = new File(destDir, fileName);
            log.info("正在下载 {} 到 {}", url, destFile.getAbsolutePath());
            
            long size = HttpUtil.downloadFile(url, destFile);
            log.info("下载完成，文件大小: {} bytes", size);

            // 返回相对路径，供前端通过静态资源映射访问
            // 例如: /storage/videos/xxx.mp4
            String relativePath = "/storage/";
            if (StringUtils.hasText(subDir)) {
                relativePath += subDir + "/";
            }
            return relativePath + fileName;

        } catch (Exception e) {
            log.error("下载文件失败: " + url, e);
            throw new RuntimeException("下载失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件 (不指定子目录)
     */
    public static String downloadFile(String url, String fileName) {
        return downloadFile(url, fileName, null);
    }

    /**
     * 获取文件的完整本地路径
     * @param subDir 子目录
     * @param fileName 文件名
     * @return 完整本地路径
     */
    public static String getFullPath(String subDir, String fileName) {
        String path = BASE_STORAGE_PATH;
        if (StringUtils.hasText(subDir)) {
            path += subDir + File.separator;
        }
        return path + fileName;
    }

    /**
     * 检查文件是否存在
     */
    public static boolean exists(String subDir, String fileName) {
        return new File(getFullPath(subDir, fileName)).exists();
    }

    /**
     * 删除文件
     * @return true 如果删除成功或文件不存在
     */
    public static boolean deleteFile(String subDir, String fileName) {
        File file = new File(getFullPath(subDir, fileName));
        if (file.exists()) {
            boolean deleted = file.delete();
            log.info("删除文件 {} - {}", file.getAbsolutePath(), deleted ? "成功" : "失败");
            return deleted;
        }
        return true;
    }
}
