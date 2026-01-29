package com.huike.video.modules.material.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.material.dto.MaterialUploadResponse;
import com.huike.video.modules.material.service.MaterialService;
import com.huike.video.modules.material.vo.MaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 素材管理控制器
 */
@Tag(name = "素材管理", description = "素材上传、查询、删除接口")
@RestController
@RequestMapping("/api/v1/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    /**
     * 上传素材
     * 兼容前端路径：/material/upload 和 /api/v1/material/upload
     */
    @Operation(summary = "上传素材", description = "支持图片、视频、音频文件上传")
    @PostMapping("/upload")
    public Result<MaterialUploadResponse> uploadMaterial(@RequestParam("file") MultipartFile file) {
        String userId = getCurrentUserId();
        MaterialUploadResponse response = materialService.uploadMaterial(file, userId);
        return Result.success(response);
    }

    /**
     * 获取素材列表
     */
    @Operation(summary = "获取素材列表", description = "分页查询用户的素材列表")
    @GetMapping("/list")
    public Result<Page<MaterialVO>> getMaterialList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        String userId = getCurrentUserId();
        Page<MaterialVO> result = materialService.getMaterialList(page, size, type, keyword, userId);
        return Result.success(result);
    }

    /**
     * 删除素材
     */
    @Operation(summary = "删除素材", description = "删除指定的素材")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteMaterial(
            @PathVariable String id,
            @RequestParam(required = false) String type) {
        String userId = getCurrentUserId();
        // 如果type为空，尝试从数据库查询获取
        if (type == null || type.isEmpty()) {
            // 这里可以优化：先查询再删除，或者要求前端必须传type
            throw new com.huike.video.common.exception.BusinessException(20001, "素材类型不能为空");
        }
        boolean success = materialService.deleteMaterial(id, type, userId);
        return Result.success(success);
    }

    /**
     * 临时获取当前用户 ID：
     * - 若已登录，则使用 Sa-Token 中的登录用户
     * - 若未登录，则使用固定测试用户 ID，便于前端无账号本地调试
     *
     * TODO 恢复登录校验时，可改为强制要求登录（直接调用 StpUtil.getLoginIdAsString()）
     */
    private String getCurrentUserId() {
        if (StpUtil.isLogin()) {
            return StpUtil.getLoginIdAsString();
        }
        return "test-user";
    }
}
