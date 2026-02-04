package com.huike.video.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 资源服务接口
 * 负责统一管理资源的存储、下载和 URL 映射
 */
public interface ResourceService {

    /**
     * 将存储的相对路径转换为完整的 Web URL
     * @param resourcePath 相对路径 (如 "videos/uuid.mp4")
     * @return 完整 URL (如 "http://localhost:8080/storage/videos/uuid.mp4")
     */
    String getUrl(String resourcePath);

    /**
     * 上传文件
     * @param file 文件对象
     * @param module 模块名 (如 "avatar", "creation")
     * @return 相对路径 (Resource Key)
     */
    String store(MultipartFile file, String module) throws IOException;

    /**
     * 下载远程文件
     * @param remoteUrl 远程 URL
     * @param module 模块名 (决定存储子目录)
     * @param suggestedFileName 建议文件名 (可选)
     * @return 相对路径 (Resource Key)
     */
    String download(String remoteUrl, String module, String suggestedFileName);

    /**
     * 删除资源
     * @param resourcePath 相对路径
     * @return 是否成功
     */
    boolean delete(String resourcePath);
}
