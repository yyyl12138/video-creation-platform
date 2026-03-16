package com.huike.video.modules.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.market.service.MarketService;
import com.huike.video.modules.market.vo.TemplateMarketVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "模版市场接口")
@RestController
@RequestMapping("/api/v1/market/templates")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @Operation(summary = "模版列表/搜索")
    @GetMapping
    public Result<Page<TemplateMarketVO>> getTemplates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        return Result.success(marketService.getMarketTemplates(page, size, type, keyword));
    }

    @Operation(summary = "模版详情")
    @GetMapping("/{templateId}")
    public Result<TemplateMarketVO> getTemplateDetail(@PathVariable String templateId) {
        return Result.success(marketService.getTemplateDetail(templateId));
    }

    @Operation(summary = "购买/获取模版")
    @SaCheckLogin
    @PostMapping("/{templateId}/purchase")
    public Result<Map<String, Object>> purchaseTemplate(@PathVariable String templateId) {
        return Result.success(marketService.purchaseTemplate(templateId));
    }
}
