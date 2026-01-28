package com.huike.video.modules.wallet.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletMeResponse {

    private BigDecimal balance;

    private BigDecimal totalRecharged;

    private BigDecimal totalConsumed;

    private String updatedTime;
}
