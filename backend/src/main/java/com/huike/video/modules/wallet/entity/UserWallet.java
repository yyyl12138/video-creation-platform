package com.huike.video.modules.wallet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_wallets")
public class UserWallet extends BaseEntity {

    @TableId
    private String id;

    private String userId;

    private BigDecimal balance;

    private BigDecimal totalRecharged;

    private BigDecimal totalConsumed;
}
