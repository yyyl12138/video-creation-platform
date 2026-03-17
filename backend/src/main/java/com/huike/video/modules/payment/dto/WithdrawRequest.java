package com.huike.video.modules.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发起提现请求 DTO
 * 对应接口: POST /api/v1/wallets/me/withdraw
 */
@Data
public class WithdrawRequest {

    /**
     * 提现金额
     */
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "1.00", message = "最低提现金额为1元")
    private BigDecimal amount;

    /**
     * 收款账号信息 (JSON 字符串, 如支付宝账号)
     */
    @NotBlank(message = "收款账号信息不能为空")
    private String accountInfo;
}
