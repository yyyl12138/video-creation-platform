package com.huike.video.modules.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.market.dto.CommentDTO;
import com.huike.video.modules.market.service.CommentService;
import com.huike.video.modules.market.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论接口")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "获取模版评论")
    @GetMapping
    public Result<Page<CommentVO>> getComments(
            @RequestParam String targetId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(commentService.getComments(targetId, page, size));
    }

    @Operation(summary = "发表评论")
    @SaCheckLogin
    @PostMapping
    public Result<Long> addComment(@Validated @RequestBody CommentDTO dto) {
        return Result.success(commentService.addComment(dto));
    }

    @Operation(summary = "删除评论")
    @SaCheckLogin
    @DeleteMapping("/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId) {
        return Result.success(commentService.deleteComment(commentId));
    }
}
