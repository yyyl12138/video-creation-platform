package com.huike.video.modules.user.wallet.service;

import com.huike.video.modules.user.wallet.vo.WalletMeResponse;
import com.huike.video.modules.user.wallet.vo.WalletTransactionsResponse;

import java.math.BigDecimal;

/**
 * 钱包服务接口
 * 负责用户钱包的查询、扣费、退款等操作
 */
public interface WalletService {

    /**
     * 获取当前登录用户的钱包信息
     */
    WalletMeResponse getMyWallet();

    /**
     * 获取当前登录用户的交易流水
     */
    WalletTransactionsResponse getMyTransactions(Integer page, Integer size, String type);

    /**
     * 扣除用户余额（原子操作）
     * @param userId 用户ID
     * @param amount 扣除金额
     * @return 是否成功
     */
    boolean deductBalance(String userId, BigDecimal amount);

    /**
     * 退款到用户余额（原子操作）
     * @param userId 用户ID
     * @param amount 退款金额
     * @return 是否成功
     */
    boolean refundBalance(String userId, BigDecimal amount);

    /**
     * 根据用户ID获取钱包（用于内部服务调用）
     * @param userId 用户ID
     * @return 钱包余额，如果不存在返回 BigDecimal.ZERO
     */
    BigDecimal getBalanceByUserId(String userId);
}
