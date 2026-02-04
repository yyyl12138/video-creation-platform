package com.huike.video.modules.material.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.material.dto.AiTagPredictRequest;
import com.huike.video.modules.material.service.TagService;
import com.huike.video.modules.material.vo.AiTagPredictVO;
import com.huike.video.modules.material.vo.HotTagsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 标签与分类控制器 - 符合 API 文档规范 2.1-2.2
 * 路径: /api/v1/materials/tags
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/tags")
public class TagController {

    private final TagService tagService;

    /**
     * 2.1 获取热门标签/分类
     * GET /api/v1/materials/tags/hot
     */
    @GetMapping("/hot")
    public Result<HotTagsVO> getHotTags(
            @RequestParam(value = "type", required = false) String type) {
        return Result.success(tagService.getHotTags(type));
    }

    /**
     * 2.2 AI 自动识别标签
     * POST /api/v1/materials/tags/ai-predict
     */
    @PostMapping("/ai-predict")
    public Result<AiTagPredictVO> predictTags(@RequestBody AiTagPredictRequest request) {
        return Result.success(tagService.predictTags(request.getMaterialUrl(), request.getType()));
    }
}
