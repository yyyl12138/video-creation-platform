package com.huike.video.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * 文件存储工具类 (Mock for now, replacing OSS)
 */
@Slf4j
public class FileStorageUtils {

    // Base download path: src/main/resources/static/creation_tasks/
    // In production, this should be configurable or point to an external volume.
    private static final String DOWNLOAD_BASE_DIR = "src/main/resources/static/creation_tasks/";

    /**
     * Download file from URL to local storage with subdirectory support.
     * @param url Remote URL
     * @param fileName Target filename
     * @param subDir Subdirectory name (e.g., "covers", "videos"), can be null
     * @return Local relative path (for frontend access)
     */
    public static String downloadFile(String url, String fileName, String subDir) {
        if (!StringUtils.hasText(url)) return null;

        try {
            // Construct target directory: base + subDir
            String targetPath = DOWNLOAD_BASE_DIR;
            if (StringUtils.hasText(subDir)) {
                targetPath += subDir + File.separator;
            }

            File destDir = new File(targetPath);
            if (!destDir.exists()) {
                boolean created = destDir.mkdirs();
                 if (!created) log.warn("Failed to create directory: {}", destDir.getAbsolutePath());
            }

            File destFile = new File(destDir, fileName);
            log.info("Downloading {} to {}", url, destFile.getAbsolutePath());
            
            long size = HttpUtil.downloadFile(url, destFile);
            log.info("Download complete. Size: {}", size);

            // Return path relative to domain root assuming static resource mapping is set up
            // E.g., /creation_tasks/videos/xxx.mp4
            String relativePath = "/creation_tasks/";
            if (StringUtils.hasText(subDir)) {
                relativePath += subDir + "/";
            }
            return relativePath + fileName;

        } catch (Exception e) {
            log.error("Failed to download file: " + url, e);
            throw new RuntimeException("Download failed: " + e.getMessage());
        }
    }

    /**
     * Overload for backward compatibility (defaults to root or 'misc')
     */
    public static String downloadFile(String url, String fileName) {
        return downloadFile(url, fileName, null);
    }
}
