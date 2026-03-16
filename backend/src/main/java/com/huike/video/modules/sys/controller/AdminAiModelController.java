package com.huike.video.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.sys.dto.AiModelRequest;
import com.huike.video.modules.sys.service.AiModelService;
import com.huike.video.modules.sys.vo.AiModelResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理后台 - AI 模型配置接口")
@RestController
@RequestMapping("/api/v1/admin/models")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminAiModelController {

    private final AiModelService aiModelService;

    @Operation(summary = "多条件分页查询模型")
    @GetMapping
    public Result<Page<AiModelResponse>> getModels(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String provider) {
        return Result.success(aiModelService.getModelPage(page, size, type, provider));
    }

    @Operation(summary = "获取单个模型详情")
    @GetMapping("/{modelId}")
    public Result<AiModelResponse> getModelById(@PathVariable Long modelId) {
        return Result.success(aiModelService.getModelById(modelId));
    }

    @Operation(summary = "新增/修改模型配置")
    @PostMapping
    public Result<Long> saveOrUpdateModel(@Validated @RequestBody AiModelRequest request) {
        return Result.success(aiModelService.saveOrUpdateModel(request));
    }

    @Operation(summary = "启用/停用模型状态")
    @PutMapping("/{modelId}/status")
    public Result<Boolean> updateModelStatus(@PathVariable Long modelId, @RequestBody Map<String, Boolean> body) {
        Boolean isActive = body.get("isActive");
        if (isActive == null) {
            return Result.error(99999, "isActive 状态不可为空");
        }
        return Result.success(aiModelService.updateModelStatus(modelId, isActive));
    }
}
