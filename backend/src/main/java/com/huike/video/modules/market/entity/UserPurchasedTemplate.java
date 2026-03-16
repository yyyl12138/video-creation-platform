package com.huike.video.modules.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户已购模版记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_purchased_templates")
public class UserPurchasedTemplate extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private String userId;

    /** 模版ID */
    private String templateId;

    /** 实际支付价格 */
    private BigDecimal pricePaid;
}
