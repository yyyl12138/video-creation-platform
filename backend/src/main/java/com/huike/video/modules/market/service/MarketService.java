package com.huike.video.modules.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.market.vo.TemplateMarketVO;

import java.util.Map;

public interface MarketService {

    /**
     * 模版市场分页查询
     */
    Page<TemplateMarketVO> getMarketTemplates(int page, int size, String type, String keyword);

    /**
     * 获取模版详情 (含购买/点赞状态)
     */
    TemplateMarketVO getTemplateDetail(String templateId);

    /**
     * 购买/获取模版
     * @return 购买结果信息
     */
    Map<String, Object> purchaseTemplate(String templateId);
}
