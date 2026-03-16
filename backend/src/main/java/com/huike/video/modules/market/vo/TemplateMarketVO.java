package com.huike.video.modules.market.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 模版市场列表/详情 VO
 */
@Data
public class TemplateMarketVO {
    private String templateId;
    private String templateName;
    private String type;
    private String aspectRatio;
    private String previewImagePath;
    private String creatorId;
    private String creatorName;
    private Integer usageCount;
    private Integer likeCount;
    private BigDecimal price;
    private String tags;
    private String description;
    /** 当前用户是否已购买 */
    private Boolean purchased;
    /** 当前用户是否已点赞 */
    private Boolean liked;
}
