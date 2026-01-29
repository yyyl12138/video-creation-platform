package com.huike.video.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.material.service.MaterialService;
import com.huike.video.modules.material.vo.MaterialBatchDeleteRequest;
import com.huike.video.modules.material.vo.MaterialBatchDeleteResponse;
import com.huike.video.modules.material.vo.MaterialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 素材管理控制器 - 符合 API 文档规范 3.1-3.4
 * 使用统一路径 + type 参数区分素材类型
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials")
public class MaterialController {

    private final MaterialService materialService;

    /**
     * 3.2 获取素材列表
     * GET /api/v1/materials
     */
    @GetMapping
    public Result<Page<MaterialVO>> listMaterials(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "isSystem", required = false) Boolean isSystem) {
        return Result.success(materialService.listMaterials(page, size, type, isSystem));
    }

    /**
     * 3.1 获取素材详情
     * GET /api/v1/materials/{materialId}?type=IMAGE
     */
    @GetMapping("/{materialId}")
    public Result<MaterialVO> getMaterial(
            @PathVariable String materialId,
            @RequestParam("type") String type) {
        MaterialVO material = materialService.getMaterialById(materialId, type);
        if (material == null) {
            return Result.error(20001, "素材不存在");
        }
        return Result.success(material);
    }

    /**
     * 3.3 批量删除素材
     * DELETE /api/v1/materials/batch
     */
    @DeleteMapping("/batch")
    public Result<MaterialBatchDeleteResponse> batchDelete(@RequestBody MaterialBatchDeleteRequest request) {
        MaterialBatchDeleteResponse response = materialService.batchDelete(
                request.getMaterialIds(), 
                request.getType()
        );
        return Result.success(response);
    }

    /**
     * 删除单个素材 (扩展接口)
     * DELETE /api/v1/materials/{materialId}?type=IMAGE
     */
    @DeleteMapping("/{materialId}")
    public Result<Void> deleteMaterial(
            @PathVariable String materialId,
            @RequestParam("type") String type) {
        boolean success = materialService.deleteMaterial(materialId, type);
        return success ? Result.success(null) : Result.error(20002, "删除失败");
    }

    /**
     * 3.4 用户上传素材
     * POST /api/v1/materials
     */
    @PostMapping(consumes = "multipart/form-data")
    public Result<com.huike.video.modules.material.vo.MaterialUploadResponse> uploadMaterial(
            @org.springframework.web.bind.annotation.RequestPart("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic,
            @RequestParam(value = "category", required = false) String category) {
        com.huike.video.modules.material.vo.MaterialUploadResponse resp = materialService.uploadMaterial(file, name, tags, isPublic, category);
        return Result.success(resp);
    }
}
