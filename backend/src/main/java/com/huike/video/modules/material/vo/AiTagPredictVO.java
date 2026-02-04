package com.huike.video.modules.material.vo;

import lombok.Data;
import java.util.List;

/**
 * AI 标签识别响应 VO
 * 对应接口 2.2
 */
@Data
public class AiTagPredictVO {

    /** 建议标签列表 */
    private List<String> suggestedTags;
}
