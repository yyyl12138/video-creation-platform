package com.huike.video.modules.material.service.impl;

import java.math.BigDecimal;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.util.FileStorageUtils;
import com.huike.video.modules.material.entity.AudioMaterial;
import com.huike.video.modules.material.entity.ImageMaterial;
import com.huike.video.modules.material.entity.VideoMaterial;
import com.huike.video.modules.material.mapper.AudioMaterialMapper;
import com.huike.video.modules.material.mapper.ImageMaterialMapper;
import com.huike.video.modules.material.mapper.VideoMaterialMapper;
import com.huike.video.modules.material.service.MaterialService;
import com.huike.video.modules.material.vo.MaterialBatchDeleteResponse;
import com.huike.video.modules.material.vo.MaterialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 素材服务实现 - 符合 API 文档规范
 */
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final ImageMaterialMapper imageMaterialMapper;
    private final VideoMaterialMapper videoMaterialMapper;
    private final AudioMaterialMapper audioMaterialMapper;
    private final com.huike.video.common.service.ResourceService resourceService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public MaterialVO getMaterialById(String materialId, String type) {
        if (!StringUtils.hasText(type)) {
            return null;
        }
        
        switch (type.toUpperCase()) {
            case "IMAGE":
                ImageMaterial image = imageMaterialMapper.selectById(materialId);
                return image != null ? convertToVO(image) : null;
            case "VIDEO":
                VideoMaterial video = videoMaterialMapper.selectById(materialId);
                return video != null ? convertToVO(video) : null;
            case "AUDIO":
                AudioMaterial audio = audioMaterialMapper.selectById(materialId);
                return audio != null ? convertToVO(audio) : null;
            default:
                return null;
        }
    }

    @Override
    public Page<MaterialVO> listMaterials(Integer page, Integer size, String type, Boolean isSystem) {
        int pageNum = page != null ? page : 1;
        int pageSize = Math.min(size != null ? size : 20, 100); // 限制最大100

        Page<MaterialVO> resultPage = new Page<>(pageNum, pageSize);

        // 如果指定了类型，只查询该类型
        if (StringUtils.hasText(type)) {
            return queryByType(type.toUpperCase(), pageNum, pageSize, isSystem);
        }

        // 未指定类型，合并查询所有类型 (简化实现: 先查图片)
        return queryByType("IMAGE", pageNum, pageSize, isSystem);
    }

    private Page<MaterialVO> queryByType(String type, int pageNum, int pageSize, Boolean isSystem) {
        Page<MaterialVO> resultPage = new Page<>(pageNum, pageSize);

        switch (type) {
            case "IMAGE":
                Page<ImageMaterial> imagePage = new Page<>(pageNum, pageSize);
                LambdaQueryWrapper<ImageMaterial> imageWrapper = new LambdaQueryWrapper<>();
                imageWrapper.eq(ImageMaterial::getStatus, 1);
                if (isSystem != null) {
                    imageWrapper.eq(ImageMaterial::getSourceType, isSystem ? 1 : 2);
                }
                imageWrapper.orderByDesc(ImageMaterial::getCreatedAt);
                imageMaterialMapper.selectPage(imagePage, imageWrapper);
                
                resultPage.setRecords(imagePage.getRecords().stream()
                        .map(this::convertToVO).collect(Collectors.toList()));
                resultPage.setTotal(imagePage.getTotal());
                break;

            case "VIDEO":
                Page<VideoMaterial> videoPage = new Page<>(pageNum, pageSize);
                LambdaQueryWrapper<VideoMaterial> videoWrapper = new LambdaQueryWrapper<>();
                videoWrapper.eq(VideoMaterial::getStatus, 1);
                if (isSystem != null) {
                    videoWrapper.eq(VideoMaterial::getSourceType, isSystem ? 1 : 2);
                }
                videoWrapper.orderByDesc(VideoMaterial::getCreatedAt);
                videoMaterialMapper.selectPage(videoPage, videoWrapper);
                
                resultPage.setRecords(videoPage.getRecords().stream()
                        .map(this::convertToVO).collect(Collectors.toList()));
                resultPage.setTotal(videoPage.getTotal());
                break;

            case "AUDIO":
                Page<AudioMaterial> audioPage = new Page<>(pageNum, pageSize);
                LambdaQueryWrapper<AudioMaterial> audioWrapper = new LambdaQueryWrapper<>();
                audioWrapper.eq(AudioMaterial::getStatus, 1);
                audioWrapper.orderByDesc(AudioMaterial::getCreatedAt);
                audioMaterialMapper.selectPage(audioPage, audioWrapper);
                
                resultPage.setRecords(audioPage.getRecords().stream()
                        .map(this::convertToVO).collect(Collectors.toList()));
                resultPage.setTotal(audioPage.getTotal());
                break;
        }

        return resultPage;
    }

    @Override
    public MaterialBatchDeleteResponse batchDelete(List<String> materialIds, String type) {
        MaterialBatchDeleteResponse response = new MaterialBatchDeleteResponse();
        int success = 0;
        int fail = 0;

        for (String id : materialIds) {
            if (deleteMaterial(id, type)) {
                success++;
            } else {
                fail++;
            }
        }

        response.setSuccessCount(success);
        response.setFailCount(fail);
        return response;
    }

    @Override
    public boolean deleteMaterial(String materialId, String type) {
        if (!StringUtils.hasText(type)) {
            return false;
        }

        String upperType = type.toUpperCase();
        String filePath = null;
        int rows = 0;

        switch (upperType) {
            case "IMAGE": {
                ImageMaterial image = imageMaterialMapper.selectById(materialId);
                if (image != null) {
                    filePath = image.getFilePath();
                }
                rows = imageMaterialMapper.deleteById(materialId);
                break;
            }
            case "VIDEO": {
                VideoMaterial video = videoMaterialMapper.selectById(materialId);
                if (video != null) {
                    filePath = video.getFilePath();
                }
                rows = videoMaterialMapper.deleteById(materialId);
                break;
            }
            case "AUDIO": {
                AudioMaterial audio = audioMaterialMapper.selectById(materialId);
                if (audio != null) {
                    filePath = audio.getFilePath();
                }
                rows = audioMaterialMapper.deleteById(materialId);
                break;
            }
            default:
                return false;
        }

        if (rows <= 0) {
            return false;
        }

        if (StringUtils.hasText(filePath)) {
            // Implementation detail: ResourceService.delete simply calls deleteFile(path).
            // If path contains "http", rename logic might be needed.
            // Assuming for now we strip prefix in Utils or Service? 
            // ResourceServiceImpl.delete -> FileStorageUtils.deleteFile -> Paths.get(base, relative).
            // If relative is "/profile/...", it might fail if base is "/storage/".
            // Clean up path logic:
            String relativeKey = filePath;
            if (filePath.contains("/storage/")) {
                relativeKey = filePath.substring(filePath.indexOf("/storage/") + 9);
            } else if (filePath.contains("/profile/upload/")) {
                 // old legacy path, mapped to storage root in WebMvcConfig
                 relativeKey = filePath.replace("/profile/upload/", "");
                 // MaterialFileUtils stored in "materials/type/date/..."
            }
            // For new "ResourceService" usage, store() returns "videos/xxx.mp4".
            // But uploadMaterial below should probably return full URL to frontend?
            // MaterialUploadResponse usually expects full URL?
            // Profile.vue handles /storage/. ResultDisplay handles full.
            // Let's store the result of resourceService.getUrl(resourceKey).
            
            try {
                // Remove URL prefix if present
                if (relativeKey.startsWith("http")) {
                    // Extract path
                    // This is complex. Let's rely on loose deletion or simple string replacement
                    // If we migrated to ResourceService completely, we should look for "/storage/"
                    // For now, if it's a full URL, try to extract the key part
                    int storageIndex = relativeKey.indexOf("/storage/");
                    if (storageIndex != -1) {
                        relativeKey = relativeKey.substring(storageIndex + 9);
                    } else {
                        // If it's a full URL but not containing /storage/, we can't reliably delete it
                        // Log and skip deletion for this case
                        System.err.println("Cannot reliably delete file with full URL: " + filePath);
                        return true; // Still consider DB deletion successful
                    }
                }
                
                // resourceService.delete will append BASE_PATH.
                // Legacy files were in BASE_PATH + relativeKey.
                // So it should work.
                resourceService.delete(relativeKey);
            } catch (Exception ignored) {
                System.err.println("Failed to delete file from storage: " + filePath + ", error: " + ignored.getMessage());
            }
        }

        return true;
    }

    @Override
    public com.huike.video.modules.material.vo.MaterialUploadResponse uploadMaterial(org.springframework.web.multipart.MultipartFile file,
                                                                     String name,
                                                                     String tags,
                                                                     Boolean isPublic,
                                                                     String category) {
        com.huike.video.modules.material.vo.MaterialUploadResponse resp = new com.huike.video.modules.material.vo.MaterialUploadResponse();
        try {
            // 1. Detect Type
            String materialType = FileStorageUtils.detectMaterialType(file.getOriginalFilename());
            if ("unknown".equals(materialType)) {
                throw new IllegalArgumentException("不支持的素材类型");
            }
            
            // 2. 存储文件 (统一存到 videos, images, audios)
            String module = materialType + "s"; // plural
            // store 返回相对Key (如 videos/xxx.mp4)
            String resourceKey = resourceService.store(file, module);
            
            // 3. 获取完整 URL 用于存库
            String url = resourceService.getUrl(resourceKey);

            String id = IdUtil.simpleUUID();
            long size = file.getSize();

            String uploaderId = null;
            try {
                uploaderId = StpUtil.getLoginIdAsString();
            } catch (Exception ignored) {}
            if (!StringUtils.hasText(uploaderId)) uploaderId = "unknown";

            switch (materialType) {
                case "image":
                    ImageMaterial img = new ImageMaterial();
                    img.setId(id);
                    img.setImageName(name != null ? name : file.getOriginalFilename());
                    img.setFilePath(url);
                    img.setFileSize(size);
                    img.setTags(tags);
                    img.setIsPublic(isPublic != null && isPublic);
                    img.setCategory(category);
                    img.setStatus(1);
                    img.setSourceType(2); // 用户上传
                    img.setUploaderId(uploaderId);
                    imageMaterialMapper.insert(img);
                    break;
                case "video":
                    VideoMaterial vid = new VideoMaterial();
                    vid.setId(id);
                    vid.setVideoName(name != null ? name : file.getOriginalFilename());
                    vid.setFilePath(url);
                    vid.setFileSize(size);
                    vid.setTags(tags);
                    vid.setIsPublic(isPublic != null && isPublic);
                    vid.setStatus(1);
                    vid.setSourceType(2);
                    vid.setUploaderId(uploaderId);
                    videoMaterialMapper.insert(vid);
                    break;
                case "audio":
                    AudioMaterial aud = new AudioMaterial();
                    aud.setId(id);
                    aud.setAudioName(name != null ? name : file.getOriginalFilename());
                    aud.setFilePath(url);
                    aud.setFileSize(size);
                    aud.setTags(tags);
                    aud.setStatus(1);
                    aud.setUploaderId(uploaderId);
                    audioMaterialMapper.insert(aud);
                    break;
            }

            resp.setMaterialId(id);
            resp.setUrl(url); // 返回完整URL
            resp.setType(materialType.toUpperCase());
            resp.setFileSize(size);
            return resp;
        } catch (Exception e) {
            throw new com.huike.video.common.exception.BusinessException(50000, "素材上传失败: " + e.getMessage());
        }
    }

    @Override
    public void createFromAiResult(com.huike.video.modules.creation.domain.entity.AiTask task) {
        if (task == null || task.getStatus() != 3) {
            return;
        }

        try {
            // sourceType = 3 (AI生成)
            int sourceType = 3;
            // copyright = 3 (个人使用, 或根据需求定)
            int copyright = 3; 

            if (task.getTaskType() == 2) {
                // 文生图 -> ImageMaterial
                ImageMaterial img = new ImageMaterial();
                img.setId(IdUtil.simpleUUID()); // 或者复用 task ID? 建议新ID
                img.setImageName("AI生成-" + task.getId());
                img.setFilePath(task.getResultFilePath());
                // file size unknown unless we verified it or stored it
                if (task.getResultFileSize() != null) {
                    img.setFileSize(task.getResultFileSize());
                } else {
                    img.setFileSize(0L);
                }
                img.setStatus(1);
                img.setSourceType(sourceType);
                img.setCopyrightStatus(copyright);
                img.setUploaderId(task.getUserId());
                img.setTags("AI Generated,Task-" + task.getId());
                img.setIsPublic(false);
                imageMaterialMapper.insert(img);

            } else if (task.getTaskType() == 3 || task.getTaskType() == 4) {
                // 文生视频/图生视频 -> VideoMaterial
                VideoMaterial vid = new VideoMaterial();
                vid.setId(IdUtil.simpleUUID());
                vid.setVideoName("AI视频-" + task.getId());
                vid.setFilePath(task.getResultFilePath());
                vid.setCoverPath(task.getResultCoverPath()); // New field
                if (task.getResultFileSize() != null) {
                    vid.setFileSize(task.getResultFileSize());
                } else {
                    vid.setFileSize(0L);
                }
                vid.setStatus(1);
                vid.setSourceType(sourceType);
                vid.setCopyrightStatus(copyright);
                vid.setUploaderId(task.getUserId());
                vid.setTags("AI Generated,Task-" + task.getId());
                vid.setIsPublic(false);
                
                // 设置默认分辨率等 (如果 task outputConfig 有则解析，否则 default)
                vid.setResolution("Unknown"); 
                vid.setDurationSeconds(BigDecimal.ZERO); // 需要从元数据或Task Config获取

                videoMaterialMapper.insert(vid);
            }
        } catch (Exception e) {
            // Log error but don't fail the whole transaction if purely async listener
            System.err.println("Failed to sync AI result to material: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== 转换方法 ==========

    private MaterialVO convertToVO(ImageMaterial entity) {
        MaterialVO vo = new MaterialVO();
        vo.setMaterialId(entity.getId());
        vo.setName(entity.getImageName());
        vo.setType("IMAGE");
        vo.setUrl(resourceService.getUrl(entity.getFilePath()));
        vo.setResolution(entity.getResolution());
        vo.setFileSize(entity.getFileSize());
        vo.setTags(parseTags(entity.getTags()));
        vo.setSourceType(mapSourceType(entity.getSourceType()));
        vo.setCopyrightStatus(mapCopyrightStatus(entity.getCopyrightStatus()));
        vo.setIsSystem(entity.getSourceType() != null && entity.getSourceType() == 1);
        if (entity.getCreatedAt() != null) {
            vo.setCreateTime(entity.getCreatedAt().format(DATE_FORMATTER));
        }
        return vo;
    }

    private MaterialVO convertToVO(VideoMaterial entity) {
        MaterialVO vo = new MaterialVO();
        vo.setMaterialId(entity.getId());
        vo.setName(entity.getVideoName());
        vo.setType("VIDEO");
        // Ensure URL is full (if stored as full, getUrl returns it; if relative, it appends)
        vo.setUrl(resourceService.getUrl(entity.getFilePath()));
        // Set Cover URL
        if (StringUtils.hasText(entity.getCoverPath())) {
            vo.setCoverUrl(resourceService.getUrl(entity.getCoverPath()));
        }
        
        vo.setResolution(entity.getResolution());
        vo.setDurationSeconds(entity.getDurationSeconds());
        vo.setFileSize(entity.getFileSize());
        vo.setTags(parseTags(entity.getTags()));
        vo.setSourceType(mapSourceType(entity.getSourceType()));
        vo.setCopyrightStatus(mapCopyrightStatus(entity.getCopyrightStatus()));
        vo.setIsSystem(entity.getSourceType() != null && entity.getSourceType() == 1);
        if (entity.getCreatedAt() != null) {
            vo.setCreateTime(entity.getCreatedAt().format(DATE_FORMATTER));
        }
        return vo;
    }

    private MaterialVO convertToVO(AudioMaterial entity) {
        MaterialVO vo = new MaterialVO();
        vo.setMaterialId(entity.getId());
        vo.setName(entity.getAudioName());
        vo.setType("AUDIO");
        vo.setUrl(resourceService.getUrl(entity.getFilePath()));
        vo.setDurationSeconds(entity.getDurationSeconds());
        vo.setFileSize(entity.getFileSize());
        vo.setTags(parseTags(entity.getTags()));
        vo.setCopyrightStatus(mapCopyrightStatus(entity.getCopyrightStatus()));
        if (entity.getCreatedAt() != null) {
            vo.setCreateTime(entity.getCreatedAt().format(DATE_FORMATTER));
        }
        return vo;
    }

    private List<String> parseTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return List.of();
        }
        return Arrays.asList(tags.split(","));
    }

    private String mapSourceType(Integer sourceType) {
        if (sourceType == null) return "USER_UPLOAD";
        switch (sourceType) {
            case 1: return "SYSTEM";
            case 2: return "USER_UPLOAD";
            case 3: return "AI_GENERATED";
            default: return "USER_UPLOAD";
        }
    }

    private String mapCopyrightStatus(Integer copyrightStatus) {
        if (copyrightStatus == null) return "PERSONAL_USE";
        switch (copyrightStatus) {
            case 1: return "FREE_COMMERCIAL";
            case 2: return "PAID";
            case 3: return "PERSONAL_USE";
            default: return "PERSONAL_USE";
        }
    }

    // ========== 管理员接口实现 (1.1-1.3) ==========

    @Override
    public com.huike.video.modules.material.vo.AdminMaterialUploadResponse uploadSystemMaterial(
            org.springframework.web.multipart.MultipartFile file,
            String copyrightStatus,
            String category,
            String tags) {
        com.huike.video.modules.material.vo.AdminMaterialUploadResponse resp = 
                new com.huike.video.modules.material.vo.AdminMaterialUploadResponse();
        try {
            // 1. 检测素材类型
            String materialType = com.huike.video.common.util.FileStorageUtils.detectMaterialType(file.getOriginalFilename());
            if ("unknown".equals(materialType)) {
                throw new IllegalArgumentException("不支持的素材类型");
            }

            // 2. 存储文件
            String module = materialType + "s";
            String resourceKey = resourceService.store(file, module);
            String url = resourceService.getUrl(resourceKey);

            // 3. 解析版权状态
            int copyrightStatusInt = parseCopyrightStatus(copyrightStatus);

            String id = cn.hutool.core.util.IdUtil.simpleUUID();
            long size = file.getSize();

            // 管理员上传，sourceType=1(官方), isPublic=true
            switch (materialType) {
                case "image":
                    ImageMaterial img = new ImageMaterial();
                    img.setId(id);
                    img.setImageName(file.getOriginalFilename());
                    img.setFilePath(url);
                    img.setFileSize(size);
                    img.setTags(tags);
                    img.setCategory(category);
                    img.setStatus(1);
                    img.setSourceType(1); // 官方
                    img.setCopyrightStatus(copyrightStatusInt);
                    img.setIsPublic(true);
                    img.setUploaderId("SYSTEM");
                    imageMaterialMapper.insert(img);
                    break;
                case "video":
                    VideoMaterial vid = new VideoMaterial();
                    vid.setId(id);
                    vid.setVideoName(file.getOriginalFilename());
                    vid.setFilePath(url);
                    vid.setFileSize(size);
                    vid.setTags(tags);
                    vid.setStatus(1);
                    vid.setSourceType(1); // 官方
                    vid.setCopyrightStatus(copyrightStatusInt);
                    vid.setIsPublic(true);
                    vid.setUploaderId("SYSTEM");
                    vid.setResolution("Unknown");
                    videoMaterialMapper.insert(vid);
                    break;
                case "audio":
                    AudioMaterial aud = new AudioMaterial();
                    aud.setId(id);
                    aud.setAudioName(file.getOriginalFilename());
                    aud.setFilePath(url);
                    aud.setFileSize(size);
                    aud.setTags(tags);
                    aud.setStatus(1);
                    aud.setCopyrightStatus(copyrightStatusInt);
                    aud.setUploaderId("SYSTEM");
                    audioMaterialMapper.insert(aud);
                    break;
            }

            resp.setMaterialId(id);
            resp.setUrl(url);
            resp.setCopyrightStatus(copyrightStatus);
            return resp;
        } catch (Exception e) {
            throw new com.huike.video.common.exception.BusinessException(50000, "系统素材上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean updateMaterialStatus(String materialId, String type, String status, String reason) {
        if (!StringUtils.hasText(type) || !StringUtils.hasText(status)) {
            return false;
        }

        int statusInt = parseStatus(status);
        String upperType = type.toUpperCase();

        switch (upperType) {
            case "IMAGE": {
                ImageMaterial image = imageMaterialMapper.selectById(materialId);
                if (image == null) return false;
                image.setStatus(statusInt);
                return imageMaterialMapper.updateById(image) > 0;
            }
            case "VIDEO": {
                VideoMaterial video = videoMaterialMapper.selectById(materialId);
                if (video == null) return false;
                video.setStatus(statusInt);
                return videoMaterialMapper.updateById(video) > 0;
            }
            case "AUDIO": {
                AudioMaterial audio = audioMaterialMapper.selectById(materialId);
                if (audio == null) return false;
                audio.setStatus(statusInt);
                return audioMaterialMapper.updateById(audio) > 0;
            }
            default:
                return false;
        }
    }

    @Override
    public boolean updateMaterialCopyright(String materialId, String type, String copyrightStatus) {
        if (!StringUtils.hasText(type) || !StringUtils.hasText(copyrightStatus)) {
            return false;
        }

        int copyrightInt = parseCopyrightStatus(copyrightStatus);
        String upperType = type.toUpperCase();

        switch (upperType) {
            case "IMAGE": {
                ImageMaterial image = imageMaterialMapper.selectById(materialId);
                if (image == null) return false;
                image.setCopyrightStatus(copyrightInt);
                return imageMaterialMapper.updateById(image) > 0;
            }
            case "VIDEO": {
                VideoMaterial video = videoMaterialMapper.selectById(materialId);
                if (video == null) return false;
                video.setCopyrightStatus(copyrightInt);
                return videoMaterialMapper.updateById(video) > 0;
            }
            case "AUDIO": {
                AudioMaterial audio = audioMaterialMapper.selectById(materialId);
                if (audio == null) return false;
                audio.setCopyrightStatus(copyrightInt);
                return audioMaterialMapper.updateById(audio) > 0;
            }
            default:
                return false;
        }
    }

    // ========== 辅助解析方法 ==========

    private int parseCopyrightStatus(String copyrightStatus) {
        if (copyrightStatus == null) return 3;
        switch (copyrightStatus.toUpperCase()) {
            case "FREE_COMMERCIAL": return 1;
            case "PAID": return 2;
            case "PERSONAL_USE": return 3;
            default: return 3;
        }
    }

    private int parseStatus(String status) {
        if (status == null) return 1;
        switch (status.toUpperCase()) {
            case "NORMAL": return 1;
            case "BANNED": return 2;
            case "REVIEWING": return 3;
            default: return 1;
        }
    }
}
