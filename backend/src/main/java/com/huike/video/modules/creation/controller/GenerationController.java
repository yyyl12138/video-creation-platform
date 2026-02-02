package com.huike.video.modules.creation.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.huike.video.common.result.Result;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.entity.AiTask;
import com.huike.video.modules.creation.service.CreationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成任务接口
 * 对应接口文档 模块三 - 核心生成任务服务
 */
@Tag(name = "生成任务", description = "视频/图片生成任务")
@RestController
@RequestMapping("/api/v1/generation")
@RequiredArgsConstructor
public class GenerationController {

    private final CreationService creationService;

    /**
     * 提交生成任务
     *
     */
    @Operation(summary = "提交生成任务", description = "异步任务，返回 taskId")
    @PostMapping("/tasks")
    public Result<Map<String, Object>> submitTask(@Valid @RequestBody CreationRequest request) {
        String userId = StpUtil.getLoginIdAsString();
        String taskId = creationService.submitTask(userId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("status", "PENDING");
        response.put("queuePosition", 0); // TODO: 实际队列位置
        response.put("estimatedTime", 60); // 预估秒数

        return Result.success(response);
    }

    @Operation(summary = "取消任务", description = "从PENDING/PROCESSING状态可取消")
    @PostMapping("/tasks/{taskId}/cancel")
    public Result<Boolean> cancelTask(@PathVariable String taskId) {
        String userId = StpUtil.getLoginIdAsString();
        boolean success = creationService.cancelTask(taskId, userId);
        return Result.success(success);
    }



    /**
     * 查询任务详情/状态
     *
     */
    @Operation(summary = "查询任务状态", description = "支持轮询获取进度")
    @GetMapping("/tasks/{taskId}")
    public Result<Map<String, Object>> getTaskStatus(@PathVariable String taskId) {
        AiTask task = creationService.getTaskStatus(taskId);

        Map<String, Object> response = new HashMap<>();
        response.put("taskId", task.getId());
        response.put("status", mapStatus(task.getStatus()));
        // Mock progress based on status (since external APIs don't always provide granular progress)
        int progress = 0;
        if (task.getStatus() == 2) progress = 50;
        else if (task.getStatus() == 3 || task.getStatus() == 4) progress = 100;
        response.put("progress", progress);
        response.put("taskType", task.getTaskType());
        response.put("prompt", task.getPrompt());

        // 成功时返回结果
        if (task.getStatus() == 3) {
            Map<String, Object> result = new HashMap<>();
            // 根据任务类型，resultFilePath 可能是图片或视频
            result.put("fileUrl", task.getResultFilePath());
            result.put("coverUrl", task.getResultCoverPath());
            result.put("size", task.getResultFileSize());
            
            // 返回文本生成结果
            if (task.getOutputConfig() != null && task.getOutputConfig().containsKey("content")) {
                result.put("content", task.getOutputConfig().get("content"));
            }
            
            response.put("result", result);
        }

        // 失败时返回错误
        if (task.getStatus() == 4) {
            response.put("error", task.getErrorMessage());
        }

        return Result.success(response);
    }

    private String mapStatus(Integer status) {
        switch (status) {
            case 1: return "PENDING";
            case 2: return "PROCESSING";
            case 3: return "SUCCESS";
            case 4: return "FAILED";
            case 5: return "CANCELLED";
            default: return "UNKNOWN";
        }
    }
}
