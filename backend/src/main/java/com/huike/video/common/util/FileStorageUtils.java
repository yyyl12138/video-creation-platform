package com.huike.video.common.util;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * 文件存储工具类
 * 从 application.yml 读取 file.upload-path 配置
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
