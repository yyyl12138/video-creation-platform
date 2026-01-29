package com.huike.video.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.material.vo.MaterialBatchDeleteResponse;
import com.huike.video.modules.material.vo.MaterialVO;

import java.util.List;

/**
 * 素材服务接口 - 符合 API 文档规范
 * 统一管理图片、视频、音频素材
 */
public interface MaterialService {

    /**
     * 获取素材详情
     * @param materialId 素材ID
     * @param type 素材类型 (IMAGE/VIDEO/AUDIO)
     */
    MaterialVO getMaterialById(String materialId, String type);

    /**
     * 分页查询素材列表
     * @param page 页码
     * @param size 每页条数
     * @param type 素材类型 (IMAGE/VIDEO/AUDIO, 可选)
     * @param isSystem 是否系统素材 (可选)
     */
    Page<MaterialVO> listMaterials(Integer page, Integer size, String type, Boolean isSystem);

    /**
     * 批量删除素材
     * @param materialIds 素材ID列表
     * @param type 素材类型
     */
    MaterialBatchDeleteResponse batchDelete(List<String> materialIds, String type);

    /**
     * 删除单个素材
     * @param materialId 素材ID
     * @param type 素材类型
     */
    boolean deleteMaterial(String materialId, String type);

    /**
     * 上传素材
     * @param file MultipartFile
     * @param name 素材名称
     * @param tags 标签，逗号分隔，可为空
     * @param isPublic 是否公开
     * @param category 分类，可为空
     */
    com.huike.video.modules.material.vo.MaterialUploadResponse uploadMaterial(org.springframework.web.multipart.MultipartFile file,
                                                        String name,
                                                        String tags,
                                                        Boolean isPublic,
                                                        String category);

}
