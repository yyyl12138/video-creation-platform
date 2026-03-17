package com.huike.video.modules.sys.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.huike.video.common.result.Result;
import com.huike.video.modules.sys.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理后台 - 动态参数配置接口")
@RestController
@RequestMapping("/api/v1/admin/configs")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminConfigController {

    private final SystemConfigService systemConfigService;

    @Operation(summary = "获取某类系统参数集合")
    @GetMapping("/{category}")
    public Result<Map<String, Object>> getConfigByCategory(@PathVariable String category) {
        Map<Object, Object> rawConfigs = systemConfigService.getConfigByCategory(category);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("category", category.toUpperCase());
        
        // 转换 Redis 取出的 Object/Object 类型为 前端期望的配置 Map
        Map<String, Object> configDetails = new HashMap<>();
        if (rawConfigs != null) {
            rawConfigs.forEach((k, v) -> configDetails.put(k.toString(), v));
        }
        response.put("config", configDetails);

        return Result.success(response);
    }

    @Operation(summary = "全量更新特定分类参数")
    @PutMapping("/{category}")
    public Result<Boolean> updateCategoryConfig(
            @PathVariable String category,
            @RequestBody Map<String, String> configs) {
            
        if (configs == null || configs.isEmpty()) {
            return Result.error(99999, "参数配置对象不能为空");
        }
        return Result.success(systemConfigService.updateConfigCategory(category, configs));
    }
}
