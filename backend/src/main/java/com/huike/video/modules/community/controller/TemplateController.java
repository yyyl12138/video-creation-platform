package com.huike.video.modules.community.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模版管理接口
 */
@Tag(name = "模版管理", description = "模版上传与管理接口")
@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final CommunityService communityService;

    @Operation(summary = "上传模版", description = "创作者上传模版文件")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Void> uploadTemplate(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "name", required = false) String name) {
        communityService.uploadTemplate(files, name);
        return Result.success();
    }

    @Operation(summary = "获取我的模版", description = "获取当前创作者上传的模版列表")
    @GetMapping("/me")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.huike.video.modules.community.entity.VideoTemplate>> getMyTemplates(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(communityService.getMyTemplates(page, size));
    }

    @Operation(summary = "删除模版", description = "创作者物理/逻辑删除自己的模版")
    @DeleteMapping("/{templateId}")
    public Result<Boolean> deleteTemplate(@PathVariable String templateId) {
        return Result.success(communityService.deleteTemplate(templateId));
    }
}
