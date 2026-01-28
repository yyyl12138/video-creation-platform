package com.huike.video.modules.creation.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.huike.video.common.result.Result;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.service.CreationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 辅助创作接口
 * 对应接口文档 模块三 - AI 辅助创作服务
 */
@Tag(name = "AI创作", description = "AI 辅助创作接口")
@RestController
@RequestMapping("/api/v1/creation")
@RequiredArgsConstructor
public class CreationController {

    private final CreationService creationService;

    /**
     * AI 通用文本生成 (同步任务)
     */
    @Operation(summary = "同步文本生成", description = "文生文，同步返回结果")
    @PostMapping("/text/generate")
    public Result<Map<String, Object>> generateText(@RequestBody CreationRequest request) {
        String userId = StpUtil.getLoginIdAsString();
        CreationResponse response = creationService.generateText(userId, request);

        Map<String, Object> data = new HashMap<>();
        data.put("content", response.getContent());
        data.put("costToken", response.getUsageTokens());

        return Result.success(data);
    }
}


