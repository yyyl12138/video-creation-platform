package com.huike.video.modules.material.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.material.dto.MaterialUploadResponse;
import com.huike.video.modules.material.entity.AudioMaterial;
import com.huike.video.modules.material.entity.ImageMaterial;
import com.huike.video.modules.material.entity.VideoMaterial;
import com.huike.video.modules.material.mapper.AudioMaterialMapper;
import com.huike.video.modules.material.mapper.ImageMaterialMapper;
import com.huike.video.modules.material.mapper.VideoMaterialMapper;
import com.huike.video.modules.material.service.MaterialService;
import com.huike.video.modules.material.util.MaterialFileUtils;
import com.huike.video.modules.material.vo.MaterialVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final ImageMaterialMapper imageMaterialMapper;
    private final VideoMaterialMapper videoMaterialMapper;
    private final AudioMaterialMapper audioMaterialMapper;
    private final MaterialFileUtils materialFileUtils;

    @Override
    @Transactional
    public MaterialUploadResponse uploadMaterial(MultipartFile file, String userId) {
        // 1. 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(20001, "文件不能为空");
        }

        // 2. 验证文件大小（500MB限制）
        long maxSize = 500 * 1024 * 1024L;
        if (file.getSize() > maxSize) {
            throw new BusinessException(20001, "文件大小不能超过500MB");
        }

        // 3. 检测素材类型
        String materialType = materialFileUtils.detectMaterialType(file.getOriginalFilename());
        if ("unknown".equals(materialType)) {
            throw new BusinessException(20001, "不支持的文件格式");
        }

        try {
            // 4. 保存文件
            String filePath = materialFileUtils.saveFile(file, materialType);
            String originalFilename = file.getOriginalFilename();
            String materialId = generateMaterialId(materialType);

            // 5. 保存到数据库
            if ("image".equals(materialType)) {
                ImageMaterial imageMaterial = new ImageMaterial();
                imageMaterial.setId(materialId);
                imageMaterial.setImageName(originalFilename);
                imageMaterial.setFilePath(filePath);
                imageMaterial.setFileSize(file.getSize());
                imageMaterial.setFormat(getFileExtension(originalFilename));
                imageMaterial.setUploaderId(userId);
                imageMaterial.setStatus(1); // 正常
                imageMaterial.setCopyrightStatus(3); // 个人使用
                imageMaterial.setSourceType(2); // 用户上传
                imageMaterial.setIsPublic(false);
                imageMaterialMapper.insert(imageMaterial);
            } else if ("video".equals(materialType)) {
                VideoMaterial videoMaterial = new VideoMaterial();
                videoMaterial.setId(materialId);
                videoMaterial.setVideoName(originalFilename);
                videoMaterial.setFilePath(filePath);
                videoMaterial.setFileSize(file.getSize());
                videoMaterial.setFormat(getFileExtension(originalFilename));
                videoMaterial.setUploaderId(userId);
                videoMaterial.setStatus(1);
                videoMaterial.setCopyrightStatus(3);
                videoMaterial.setSourceType(2);
                videoMaterial.setIsPublic(false);
                videoMaterialMapper.insert(videoMaterial);
            } else if ("audio".equals(materialType)) {
                AudioMaterial audioMaterial = new AudioMaterial();
                audioMaterial.setId(materialId);
                audioMaterial.setAudioName(originalFilename);
                audioMaterial.setFilePath(filePath);
                audioMaterial.setFileSize(file.getSize());
                audioMaterial.setUploaderId(userId);
                audioMaterial.setStatus(1);
                audioMaterial.setCopyrightStatus(3);
                audioMaterialMapper.insert(audioMaterial);
            }

            // 6. 构建响应
            MaterialUploadResponse response = new MaterialUploadResponse();
            response.setMaterialId(materialId);
            response.setUrl(filePath);
            response.setType(materialType);
            response.setFileSize(file.getSize());
            response.setName(originalFilename);

            log.info("素材上传成功: materialId={}, type={}, userId={}", materialId, materialType, userId);
            return response;

        } catch (Exception e) {
            log.error("素材上传失败", e);
            throw new BusinessException(20001, "上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialVO> getMaterialList(Integer page, Integer size, String type, String keyword, String userId) {
        Page<MaterialVO> resultPage = new Page<>(page, size);

        // 根据类型查询不同的表
        if (StringUtils.hasText(type)) {
            if ("image".equals(type)) {
                return getImageMaterialList(resultPage, keyword, userId);
            } else if ("video".equals(type)) {
                return getVideoMaterialList(resultPage, keyword, userId);
            } else if ("audio".equals(type)) {
                return getAudioMaterialList(resultPage, keyword, userId);
            } else {
                // 非法类型：返回空页（避免前端误解为“只有图片”）
                resultPage.setRecords(List.of());
                resultPage.setTotal(0);
                return resultPage;
            }
        }

        // 未指定类型：聚合 image/video/audio 三种素材，按 createTime 倒序合并分页
        return getAllMaterialList(resultPage, keyword, userId);
    }

    private Page<MaterialVO> getAllMaterialList(Page<MaterialVO> page, String keyword, String userId) {
        long fetchSize = page.getCurrent() * page.getSize(); // 为了正确切页，先拉取前 N 条再做内存分页

        // 1) 拉取三类素材各自前 fetchSize 条（按 createdAt 倒序）
        Page<ImageMaterial> imagePage = selectImageMaterials(fetchSize, keyword, userId);
        Page<VideoMaterial> videoPage = selectVideoMaterials(fetchSize, keyword, userId);
        Page<AudioMaterial> audioPage = selectAudioMaterials(fetchSize, keyword, userId);

        // 2) 映射为统一 VO
        List<MaterialVO> merged = new ArrayList<>(
                imagePage.getRecords().size() + videoPage.getRecords().size() + audioPage.getRecords().size()
        );

        merged.addAll(imagePage.getRecords().stream().map(material -> {
            MaterialVO vo = new MaterialVO();
            vo.setId(material.getId());
            vo.setName(material.getImageName());
            vo.setType("image");
            vo.setUrl(material.getFilePath());
            vo.setSize(material.getFileSize());
            vo.setCreateTime(material.getCreatedAt());
            vo.setResolution(material.getResolution());
            vo.setFormat(material.getFormat());
            return vo;
        }).collect(Collectors.toList()));

        merged.addAll(videoPage.getRecords().stream().map(material -> {
            MaterialVO vo = new MaterialVO();
            vo.setId(material.getId());
            vo.setName(material.getVideoName());
            vo.setType("video");
            vo.setUrl(material.getFilePath());
            vo.setSize(material.getFileSize());
            vo.setCreateTime(material.getCreatedAt());
            vo.setResolution(material.getResolution());
            vo.setDurationSeconds(material.getDurationSeconds());
            vo.setFormat(material.getFormat());
            return vo;
        }).collect(Collectors.toList()));

        merged.addAll(audioPage.getRecords().stream().map(material -> {
            MaterialVO vo = new MaterialVO();
            vo.setId(material.getId());
            vo.setName(material.getAudioName());
            vo.setType("audio");
            vo.setUrl(material.getFilePath());
            vo.setSize(material.getFileSize());
            vo.setCreateTime(material.getCreatedAt());
            vo.setDurationSeconds(material.getDurationSeconds());
            return vo;
        }).collect(Collectors.toList()));

        // 3) 合并排序（createTime desc），再按 page/current/size 做内存切片
        merged.sort(Comparator.comparing(MaterialVO::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        int toIndex = (int) Math.min(page.getCurrent() * page.getSize(), merged.size());

        List<MaterialVO> pageRecords;
        if (fromIndex >= merged.size()) {
            pageRecords = List.of();
        } else {
            pageRecords = merged.subList(fromIndex, toIndex);
        }

        Page<MaterialVO> result = new Page<>(page.getCurrent(), page.getSize());
        result.setRecords(pageRecords);
        result.setTotal(imagePage.getTotal() + videoPage.getTotal() + audioPage.getTotal());
        return result;
    }

    private Page<ImageMaterial> selectImageMaterials(long fetchSize, String keyword, String userId) {
        LambdaQueryWrapper<ImageMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageMaterial::getUploaderId, userId);
        wrapper.eq(ImageMaterial::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ImageMaterial::getImageName, keyword);
        }
        wrapper.orderByDesc(ImageMaterial::getCreatedAt);
        return imageMaterialMapper.selectPage(new Page<>(1, fetchSize), wrapper);
    }

    private Page<VideoMaterial> selectVideoMaterials(long fetchSize, String keyword, String userId) {
        LambdaQueryWrapper<VideoMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoMaterial::getUploaderId, userId);
        wrapper.eq(VideoMaterial::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(VideoMaterial::getVideoName, keyword);
        }
        wrapper.orderByDesc(VideoMaterial::getCreatedAt);
        return videoMaterialMapper.selectPage(new Page<>(1, fetchSize), wrapper);
    }

    private Page<AudioMaterial> selectAudioMaterials(long fetchSize, String keyword, String userId) {
        LambdaQueryWrapper<AudioMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AudioMaterial::getUploaderId, userId);
        wrapper.eq(AudioMaterial::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AudioMaterial::getAudioName, keyword);
        }
        wrapper.orderByDesc(AudioMaterial::getCreatedAt);
        return audioMaterialMapper.selectPage(new Page<>(1, fetchSize), wrapper);
    }

    private Page<MaterialVO> getImageMaterialList(Page<MaterialVO> page, String keyword, String userId) {
        LambdaQueryWrapper<ImageMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageMaterial::getUploaderId, userId);
        wrapper.eq(ImageMaterial::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(ImageMaterial::getImageName, keyword);
        }
        wrapper.orderByDesc(ImageMaterial::getCreatedAt);

        Page<ImageMaterial> materialPage = imageMaterialMapper.selectPage(
                new Page<>(page.getCurrent(), page.getSize()), wrapper);

        List<MaterialVO> voList = materialPage.getRecords().stream().map(material -> {
            MaterialVO vo = new MaterialVO();
            vo.setId(material.getId());
            vo.setName(material.getImageName());
            vo.setType("image");
            vo.setUrl(material.getFilePath());
            vo.setSize(material.getFileSize());
            vo.setCreateTime(material.getCreatedAt());
            vo.setResolution(material.getResolution());
            vo.setFormat(material.getFormat());
            return vo;
        }).collect(Collectors.toList());

        Page<MaterialVO> result = new Page<>(page.getCurrent(), page.getSize());
        result.setRecords(voList);
        result.setTotal(materialPage.getTotal());
        return result;
    }

    private Page<MaterialVO> getVideoMaterialList(Page<MaterialVO> page, String keyword, String userId) {
        LambdaQueryWrapper<VideoMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoMaterial::getUploaderId, userId);
        wrapper.eq(VideoMaterial::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(VideoMaterial::getVideoName, keyword);
        }
        wrapper.orderByDesc(VideoMaterial::getCreatedAt);

        Page<VideoMaterial> materialPage = videoMaterialMapper.selectPage(
                new Page<>(page.getCurrent(), page.getSize()), wrapper);

        List<MaterialVO> voList = materialPage.getRecords().stream().map(material -> {
            MaterialVO vo = new MaterialVO();
            vo.setId(material.getId());
            vo.setName(material.getVideoName());
            vo.setType("video");
            vo.setUrl(material.getFilePath());
            vo.setSize(material.getFileSize());
            vo.setCreateTime(material.getCreatedAt());
            vo.setResolution(material.getResolution());
            vo.setDurationSeconds(material.getDurationSeconds());
            vo.setFormat(material.getFormat());
            return vo;
        }).collect(Collectors.toList());

        Page<MaterialVO> result = new Page<>(page.getCurrent(), page.getSize());
        result.setRecords(voList);
        result.setTotal(materialPage.getTotal());
        return result;
    }

    private Page<MaterialVO> getAudioMaterialList(Page<MaterialVO> page, String keyword, String userId) {
        LambdaQueryWrapper<AudioMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AudioMaterial::getUploaderId, userId);
        wrapper.eq(AudioMaterial::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(AudioMaterial::getAudioName, keyword);
        }
        wrapper.orderByDesc(AudioMaterial::getCreatedAt);

        Page<AudioMaterial> materialPage = audioMaterialMapper.selectPage(
                new Page<>(page.getCurrent(), page.getSize()), wrapper);

        List<MaterialVO> voList = materialPage.getRecords().stream().map(material -> {
            MaterialVO vo = new MaterialVO();
            vo.setId(material.getId());
            vo.setName(material.getAudioName());
            vo.setType("audio");
            vo.setUrl(material.getFilePath());
            vo.setSize(material.getFileSize());
            vo.setCreateTime(material.getCreatedAt());
            vo.setDurationSeconds(material.getDurationSeconds());
            return vo;
        }).collect(Collectors.toList());

        Page<MaterialVO> result = new Page<>(page.getCurrent(), page.getSize());
        result.setRecords(voList);
        result.setTotal(materialPage.getTotal());
        return result;
    }

    @Override
    @Transactional
    public boolean deleteMaterial(String materialId, String materialType, String userId) {
        try {
            if ("image".equals(materialType)) {
                ImageMaterial material = imageMaterialMapper.selectById(materialId);
                if (material == null || !material.getUploaderId().equals(userId)) {
                    throw new BusinessException(20001, "素材不存在或无权限删除");
                }
                // 逻辑删除
                material.setIsDeleted(1);
                imageMaterialMapper.updateById(material);
                // 删除文件
                materialFileUtils.deleteFile(material.getFilePath());
            } else if ("video".equals(materialType)) {
                VideoMaterial material = videoMaterialMapper.selectById(materialId);
                if (material == null || !material.getUploaderId().equals(userId)) {
                    throw new BusinessException(20001, "素材不存在或无权限删除");
                }
                material.setIsDeleted(1);
                videoMaterialMapper.updateById(material);
                materialFileUtils.deleteFile(material.getFilePath());
            } else if ("audio".equals(materialType)) {
                AudioMaterial material = audioMaterialMapper.selectById(materialId);
                if (material == null || !material.getUploaderId().equals(userId)) {
                    throw new BusinessException(20001, "素材不存在或无权限删除");
                }
                material.setIsDeleted(1);
                audioMaterialMapper.updateById(material);
                materialFileUtils.deleteFile(material.getFilePath());
            }
            return true;
        } catch (Exception e) {
            log.error("删除素材失败", e);
            throw new BusinessException(20001, "删除失败: " + e.getMessage());
        }
    }

    private String generateMaterialId(String type) {
        String prefix = type.substring(0, 1).toUpperCase();
        return prefix + IdUtil.fastSimpleUUID().substring(0, 15);
    }

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
}
