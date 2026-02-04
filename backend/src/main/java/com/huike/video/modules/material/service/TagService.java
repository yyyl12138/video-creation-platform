package com.huike.video.modules.material.service;

import com.huike.video.modules.material.vo.AiTagPredictVO;
import com.huike.video.modules.material.vo.HotTagsVO;

/**
 * 标签服务接口 - 符合 API 文档规范 2.1-2.2
 */
public interface TagService {

    /**
     * 获取热门标签/分类
     * @param type 素材类型 (可选)
     */
    HotTagsVO getHotTags(String type);

    /**
     * AI 自动识别标签
     * @param materialUrl 素材地址
     * @param type 素材类型
     */
    AiTagPredictVO predictTags(String materialUrl, String type);
}
