package com.huike.video.modules.user.wallet.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.user.wallet.entity.UserWallet;
import com.huike.video.modules.user.wallet.mapper.UserWalletMapper;
import com.huike.video.modules.user.wallet.service.WalletService;
import com.huike.video.modules.user.wallet.vo.WalletMeResponse;
import com.huike.video.modules.user.wallet.vo.WalletTransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 钱包服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserWalletMapper userWalletMapper;

    @Override
    public WalletMeResponse getMyWallet() {
        String userId = StpUtil.getLoginIdAsString();
        UserWallet wallet = userWalletMapper.selectOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));

        WalletMeResponse resp = new WalletMeResponse();
        if (wallet == null) {
            resp.setBalance(BigDecimal.ZERO);
            resp.setTotalRecharged(BigDecimal.ZERO);
            resp.setTotalConsumed(BigDecimal.ZERO);
            resp.setUpdatedTime("");
            return resp;
        }

        resp.setBalance(wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance());
        resp.setTotalRecharged(wallet.getTotalRecharged() == null ? BigDecimal.ZERO : wallet.getTotalRecharged());
        resp.setTotalConsumed(wallet.getTotalConsumed() == null ? BigDecimal.ZERO : wallet.getTotalConsumed());
        resp.setUpdatedTime(wallet.getUpdatedAt() == null ? "" : wallet.getUpdatedAt().toString());
        return resp;
    }

    @Override
    public WalletTransactionsResponse getMyTransactions(Integer page, Integer size, String type) {
        // TODO: 实现交易流水查询
        return WalletTransactionsResponse.empty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBalance(String userId, BigDecimal amount) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid deduct parameters: userId={}, amount={}", userId, amount);
            return false;
        }

        // 原子性扣费：只有余额足够时才扣除
        LambdaUpdateWrapper<UserWallet> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId)
               .ge(UserWallet::getBalance, amount)  // 余额 >= 扣除金额
               .setSql("balance = balance - " + amount)
               .setSql("total_consumed = total_consumed + " + amount);
        
        int rows = userWalletMapper.update(null, wrapper);
        if (rows == 0) {
            log.warn("Deduct balance failed for userId={}, amount={}", userId, amount);
            return false;
        }
        
        log.info("Deducted {} from user {} wallet", amount, userId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundBalance(String userId, BigDecimal amount) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid refund parameters: userId={}, amount={}", userId, amount);
            return false;
        }

        LambdaUpdateWrapper<UserWallet> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId)
               .setSql("balance = balance + " + amount)
               .setSql("total_consumed = total_consumed - " + amount);
        
        int rows = userWalletMapper.update(null, wrapper);
        if (rows == 0) {
            log.warn("Refund balance failed for userId={}, amount={}", userId, amount);
            return false;
        }
        
        log.info("Refunded {} to user {} wallet", amount, userId);
        return true;
    }

    @Override
    public BigDecimal getBalanceByUserId(String userId) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        
        UserWallet wallet = userWalletMapper.selectOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));
        
        if (wallet == null || wallet.getBalance() == null) {
            return BigDecimal.ZERO;
        }
        
        return wallet.getBalance();
    }
}
