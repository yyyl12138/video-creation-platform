package com.huike.video.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.material.dto.MaterialUploadResponse;
import com.huike.video.modules.material.vo.MaterialVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 素材服务接口
 */
public interface MaterialService {

    /**
     * 上传素材
     * @param file 文件
     * @param userId 用户ID
     * @return 上传结果
     */
    MaterialUploadResponse uploadMaterial(MultipartFile file, String userId);

    /**
     * 获取素材列表
     * @param page 页码
     * @param size 每页大小
     * @param type 素材类型 (image/video/audio)
     * @param keyword 关键词
     * @param userId 用户ID
     * @return 分页结果
     */
    Page<MaterialVO> getMaterialList(Integer page, Integer size, String type, String keyword, String userId);

    /**
     * 删除素材
     * @param materialId 素材ID
     * @param materialType 素材类型
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteMaterial(String materialId, String materialType, String userId);
}
