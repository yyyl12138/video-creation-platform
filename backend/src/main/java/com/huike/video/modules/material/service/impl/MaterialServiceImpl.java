package com.huike.video.modules.material.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

        switch (type.toUpperCase()) {
            case "IMAGE":
                return imageMaterialMapper.deleteById(materialId) > 0;
            case "VIDEO":
                return videoMaterialMapper.deleteById(materialId) > 0;
            case "AUDIO":
                return audioMaterialMapper.deleteById(materialId) > 0;
            default:
                return false;
        }
    }

    // ========== 转换方法 ==========

    private MaterialVO convertToVO(ImageMaterial entity) {
        MaterialVO vo = new MaterialVO();
        vo.setMaterialId(entity.getId());
        vo.setName(entity.getImageName());
        vo.setType("IMAGE");
        vo.setUrl(entity.getFilePath());
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
        vo.setUrl(entity.getFilePath());
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
        vo.setUrl(entity.getFilePath());
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
}
