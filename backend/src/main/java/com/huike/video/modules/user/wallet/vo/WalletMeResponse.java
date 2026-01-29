package com.huike.video.modules.user.wallet.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 钱包信息响应 VO
 */
@Data
public class WalletMeResponse {

    private BigDecimal balance;

    private BigDecimal totalRecharged;

    private BigDecimal totalConsumed;

    private String updatedTime;
}
