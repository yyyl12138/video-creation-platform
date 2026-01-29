package com.huike.video.modules.material.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 素材文件上传工具类
 */
@Slf4j
@Component
public class MaterialFileUtils {

    @Value("${file.upload-path}")
    private String uploadBasePath;

    /**
     * 保存上传的文件
     * @param file 上传的文件
     * @param materialType 素材类型 (image/video/audio)
     * @return 文件相对路径（用于访问）
     */
    public String saveFile(MultipartFile file, String materialType) throws IOException {
        // 生成文件存储路径：upload/materials/{type}/yyyyMMdd/{uuid}.{ext}
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        
        String relativePath = "materials" + File.separator + materialType + File.separator + dateDir;
        String fullPath = uploadBasePath + relativePath;
        
        // 创建目录
        Path dirPath = Paths.get(fullPath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        
        // 保存文件
        Path filePath = dirPath.resolve(fileName);
        file.transferTo(filePath.toFile());
        
        log.info("文件保存成功: {}", filePath.toAbsolutePath());
        
        // 返回相对路径，用于前端访问
        return "/profile/upload/" + relativePath.replace("\\", "/") + "/" + fileName;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot + 1).toLowerCase();
    }

    /**
     * 根据文件类型判断素材类型
     */
    public String detectMaterialType(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        
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

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        try {
            // filePath 是相对路径，需要转换为绝对路径
            // filePath格式: /profile/upload/materials/image/20260128/uuid.jpg
            String relativePath = filePath.replace("/profile/upload/", "").replace("/", File.separator);
            String absolutePath = uploadBasePath + relativePath;
            Path path = Paths.get(absolutePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("文件删除成功: {}", absolutePath);
                return true;
            } else {
                log.warn("文件不存在: {}", absolutePath);
            }
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
        }
        return false;
    }
}
