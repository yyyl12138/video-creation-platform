package com.huike.video.modules.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.huike.video.common.result.Result;
import com.huike.video.modules.market.dto.InteractionRequest;
import com.huike.video.modules.market.service.SocialInteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "社交互动接口")
@RestController
@RequestMapping("/api/v1/interactions")
@RequiredArgsConstructor
@SaCheckLogin
public class InteractionController {

    private final SocialInteractionService interactionService;

    @Operation(summary = "点赞/取消点赞模版")
    @PostMapping("/like")
    public Result<Map<String, Object>> toggleLike(@Validated @RequestBody InteractionRequest request) {
        boolean liked = interactionService.toggleLike(request.getTargetId());
        return Result.success(Map.of("liked", liked, "message", liked ? "点赞成功" : "已取消点赞"));
    }

    @Operation(summary = "关注/取关作者")
    @PostMapping("/follow")
    public Result<Map<String, Object>> toggleFollow(@Validated @RequestBody InteractionRequest request) {
        boolean followed = interactionService.toggleFollow(request.getTargetId());
        return Result.success(Map.of("followed", followed, "message", followed ? "关注成功" : "已取消关注"));
    }
}
