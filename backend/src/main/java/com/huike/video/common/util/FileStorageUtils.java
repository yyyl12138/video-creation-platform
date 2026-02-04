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

    // ==================== 上传/存储 ====================

    /**
     * 上传 MultipartFile 文件
     * @param file 文件
     * @param subDir 子目录
     * @param fileNamePrefix 文件名前缀
     * @return 存储的相对路径 (subDir/filename)
     */
    public static String uploadFile(MultipartFile file, String subDir, String fileNamePrefix) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        String ext = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(ext)) ext = ".bin";

        String fileName = fileNamePrefix + "_" + System.currentTimeMillis() + ext;
        Path targetPath = resolvePath(subDir, fileName);
        
        Files.createDirectories(targetPath.getParent());
        file.transferTo(targetPath);
        log.info("文件写入成功: {} ({} bytes)", targetPath, file.getSize());

        return getRelativeKey(subDir, fileName);
    }

    /**
     * 下载远程文件
     * @param url 远程URL
     * @param fileName 目标文件名
     * @param subDir 子目录
     * @return 存储的相对路径 (subDir/filename)
     */
    public static String downloadFile(String url, String fileName, String subDir) {
        if (!StringUtils.hasText(url)) return null;

        try {
            Path targetPath = resolvePath(subDir, fileName);
            File destFile = targetPath.toFile();

            if (destFile.exists() && destFile.length() > 0) {
                log.info("文件已存在，跳过下载: {}", targetPath);
                return getRelativeKey(subDir, fileName);
            }

            Files.createDirectories(targetPath.getParent());
            log.info("正在下载 {} 到 {}", url, targetPath);
            
            long size = HttpUtil.downloadFile(url, destFile);
            log.info("下载完成: {} bytes", size);

            return getRelativeKey(subDir, fileName);

        } catch (Exception e) {
            log.error("下载文件失败: " + url, e);
            throw new RuntimeException("下载失败: " + e.getMessage());
        }
    }

    // ==================== 文件操作 ====================

    /**
     * 删除文件
     * @param relativePath 相对路径 (subDir/filename)
     */
    public static boolean deleteFile(String relativePath) {
        if (!StringUtils.hasText(relativePath)) return false;
        try {
            Path targetPath = Paths.get(BASE_STORAGE_PATH, relativePath);
            boolean deleted = Files.deleteIfExists(targetPath);
            log.info("删除文件: {} - {}", targetPath, deleted);
            return deleted;
        } catch (IOException e) {
            log.warn("删除文件失败: {}", relativePath, e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     */
    public static boolean exists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) return false;
        return Files.exists(Paths.get(BASE_STORAGE_PATH, relativePath));
    }

    // ==================== 辅助方法 ====================

    public static String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) return "";
        int idx = originalFilename.lastIndexOf('.');
        if (idx >= 0 && idx < originalFilename.length() - 1) {
            return originalFilename.substring(idx);
        }
        return "";
    }

    /**
     * 根据文件类型判断素材类型 (image/video/audio)
     */
    public static String detectMaterialType(String filename) {
        String ext = extractExtension(filename).toLowerCase().replace(".", "");
        
        if (filename == null) return "unknown";
        
        // 图片类型
        if (ext.matches("jpg|jpeg|png|gif|bmp|webp|svg")) {
            return "image";
        }
        // 视频类型
        if (ext.matches("mp4|avi|mov|wmv|flv|mkv|webm|m4v")) {
            return "video";
        }
        // 音频类型
        if (ext.matches("mp3|wav|flac|aac|ogg|m4a|wma")) {
            return "audio";
        }
        
        return "unknown";
    }

    private static Path resolvePath(String subDir, String fileName) {
        String dir = BASE_STORAGE_PATH;
        if (StringUtils.hasText(subDir)) {
            dir += subDir + File.separator;
        }
        return Paths.get(dir, fileName);
    }

    private static String getRelativeKey(String subDir, String fileName) {
        if (StringUtils.hasText(subDir)) {
            return subDir + "/" + fileName;
        }
        return fileName;
    }
}
