package com.huike.video.modules.user.wallet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 钱包交易流水实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wallet_transactions")
public class WalletTransaction extends BaseEntity {

    @TableId
    private String id;

    /**
     * 钱包ID
     */
    private String walletId;

    /**
     * 类型: 1-充值, 2-消费, 3-退款
     */
    private Integer type;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 关联任务ID
     */
    private String relatedTaskId;

    /**
     * 描述
     */
    private String description;
}
