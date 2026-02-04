package com.huike.video.modules.material.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.material.dto.MaterialCopyrightUpdateRequest;
import com.huike.video.modules.material.dto.MaterialStatusUpdateRequest;
import com.huike.video.modules.material.service.MaterialService;
import com.huike.video.modules.material.vo.AdminMaterialUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员素材管理控制器 - 符合 API 文档规范 1.1-1.3
 * 路径: /api/v1/admin/materials
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/materials")
public class AdminMaterialController {

    private final MaterialService materialService;

    /**
     * 1.1 系统素材上传 (管理员)
     * POST /api/v1/admin/materials
     */
    @PostMapping(consumes = "multipart/form-data")
    public Result<AdminMaterialUploadResponse> uploadSystemMaterial(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("copyrightStatus") String copyrightStatus,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) String tags) {
        AdminMaterialUploadResponse resp = materialService.uploadSystemMaterial(file, copyrightStatus, category, tags);
        return Result.success(resp);
    }

    /**
     * 1.2 素材状态/审核管理
     * PUT /api/v1/admin/materials/{materialId}/status
     */
    @PutMapping("/{materialId}/status")
    public Result<Boolean> updateStatus(
            @PathVariable String materialId,
            @RequestBody MaterialStatusUpdateRequest request) {
        boolean success = materialService.updateMaterialStatus(
                materialId, 
                request.getType(), 
                request.getStatus(), 
                request.getReason());
        return success ? Result.success(true) : Result.error(20003, "素材状态更新失败");
    }

    /**
     * 1.3 标记素材版权
     * PUT /api/v1/admin/materials/{materialId}/copyright
     */
    @PutMapping("/{materialId}/copyright")
    public Result<Boolean> updateCopyright(
            @PathVariable String materialId,
            @RequestBody MaterialCopyrightUpdateRequest request) {
        boolean success = materialService.updateMaterialCopyright(
                materialId, 
                request.getType(), 
                request.getCopyrightStatus());
        return success ? Result.success(true) : Result.error(20004, "素材版权更新失败");
    }
}
