package com.huike.video.modules.user.wallet.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.user.wallet.entity.UserWallet;
import com.huike.video.modules.user.wallet.entity.WalletTransaction;
import com.huike.video.modules.user.wallet.mapper.UserWalletMapper;
import com.huike.video.modules.user.wallet.mapper.WalletTransactionMapper;
import com.huike.video.modules.user.wallet.service.WalletService;
import com.huike.video.modules.user.wallet.vo.WalletMeResponse;
import com.huike.video.modules.user.wallet.vo.WalletTransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 钱包服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserWalletMapper userWalletMapper;
    private final WalletTransactionMapper walletTransactionMapper;

    /**
     * 交易类型常量
     */
    private static final int TRANSACTION_TYPE_RECHARGE = 1;
    private static final int TRANSACTION_TYPE_CONSUME = 2;
    private static final int TRANSACTION_TYPE_REFUND = 3;

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

        resp.setWalletId(wallet.getId());
        resp.setBalance(wallet.getBalance() == null ? BigDecimal.ZERO : wallet.getBalance());
        resp.setTotalRecharged(wallet.getTotalRecharged() == null ? BigDecimal.ZERO : wallet.getTotalRecharged());
        resp.setTotalConsumed(wallet.getTotalConsumed() == null ? BigDecimal.ZERO : wallet.getTotalConsumed());
        resp.setUpdatedTime(wallet.getUpdatedAt() == null ? "" : wallet.getUpdatedAt().toString());
        return resp;
    }



    @Override
    public WalletTransactionsResponse getMyTransactions(Integer page, Integer size, String type) {
        String userId = StpUtil.getLoginIdAsString();

        // 获取用户钱包
        UserWallet wallet = userWalletMapper.selectOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));

        if (wallet == null) {
            return WalletTransactionsResponse.empty();
        }

        // 分页参数处理
        int pageNum = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : Math.min(size, 100);

        // 构建查询条件
        LambdaQueryWrapper<WalletTransaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WalletTransaction::getWalletId, wallet.getId());

        // 类型筛选
        if (type != null && !type.isBlank()) {
            Integer typeCode = parseTransactionType(type);
            if (typeCode != null) {
                queryWrapper.eq(WalletTransaction::getType, typeCode);
            }
        }

        queryWrapper.orderByDesc(WalletTransaction::getCreatedAt);

        // 分页查询
        Page<WalletTransaction> pageResult = walletTransactionMapper.selectPage(
                new Page<>(pageNum, pageSize), queryWrapper);

        // 构建响应
        WalletTransactionsResponse resp = new WalletTransactionsResponse();
        resp.setTotal(pageResult.getTotal());

        List<WalletTransactionsResponse.Record> records = new ArrayList<>();
        for (WalletTransaction tx : pageResult.getRecords()) {
            WalletTransactionsResponse.Record record = new WalletTransactionsResponse.Record();
            record.setTransactionId(tx.getId());
            record.setType(toTransactionTypeText(tx.getType()));
            record.setAmount(tx.getAmount());
            record.setDescription(tx.getDescription());
            record.setRelatedTaskId(tx.getRelatedTaskId());
            record.setCreatedTime(tx.getCreatedAt() == null ? "" : tx.getCreatedAt().toString());
            records.add(record);
        }
        resp.setRecords(records);

        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBalance(String userId, BigDecimal amount, String relatedTaskId, String description) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid deduct parameters: userId={}, amount={}", userId, amount);
            return false;
        }

        // 1. 获取钱包信息（需要 walletId 来记录流水）
        UserWallet wallet = userWalletMapper.selectOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));
        if (wallet == null) {
            log.warn("Wallet not found for userId={}", userId);
            return false;
        }

        // 2. 原子性扣费：只有余额足够时才扣除
        LambdaUpdateWrapper<UserWallet> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserWallet::getId, wallet.getId())
               .ge(UserWallet::getBalance, amount)
               .setSql("balance = balance - " + amount)
               .setSql("total_consumed = total_consumed + " + amount);

        int rows = userWalletMapper.update(null, wrapper);
        if (rows == 0) {
            log.warn("Deduct balance failed for userId={}, amount={} (insufficient balance or concurrency conflict)", userId, amount);
            return false;
        }

        // 3. 插入交易流水
        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(IdUtil.simpleUUID());
        transaction.setWalletId(wallet.getId());
        transaction.setType(TRANSACTION_TYPE_CONSUME);
        transaction.setAmount(amount.negate()); // 消费为负数
        transaction.setRelatedTaskId(relatedTaskId);
        transaction.setDescription(description);
        walletTransactionMapper.insert(transaction);

        log.info("Deducted {} from user {} wallet, transaction={}", amount, userId, transaction.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean refundBalance(String userId, BigDecimal amount, String relatedTaskId, String description) {
        if (userId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid refund parameters: userId={}, amount={}", userId, amount);
            return false;
        }

        // 1. 获取钱包信息
        UserWallet wallet = userWalletMapper.selectOne(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId, userId)
                .last("limit 1"));
        if (wallet == null) {
            log.warn("Wallet not found for userId={}", userId);
            return false;
        }

        // 2. 原子性退款
        LambdaUpdateWrapper<UserWallet> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserWallet::getId, wallet.getId())
               .setSql("balance = balance + " + amount)
               .setSql("total_consumed = total_consumed - " + amount);

        int rows = userWalletMapper.update(null, wrapper);
        if (rows == 0) {
            log.warn("Refund balance failed for userId={}, amount={}", userId, amount);
            return false;
        }

        // 3. 插入交易流水
        WalletTransaction transaction = new WalletTransaction();
        transaction.setId(IdUtil.simpleUUID());
        transaction.setWalletId(wallet.getId());
        transaction.setType(TRANSACTION_TYPE_REFUND);
        transaction.setAmount(amount); // 退款为正数
        transaction.setRelatedTaskId(relatedTaskId);
        transaction.setDescription(description);
        walletTransactionMapper.insert(transaction);

        log.info("Refunded {} to user {} wallet, transaction={}", amount, userId, transaction.getId());
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

    /**
     * 解析交易类型字符串为整数
     */
    private Integer parseTransactionType(String type) {
        if (type == null) {
            return null;
        }
        return switch (type.toUpperCase()) {
            case "RECHARGE" -> TRANSACTION_TYPE_RECHARGE;
            case "CONSUME" -> TRANSACTION_TYPE_CONSUME;
            case "REFUND" -> TRANSACTION_TYPE_REFUND;
            default -> null;
        };
    }

    /**
     * 将交易类型整数转为文本
     */
    private String toTransactionTypeText(Integer type) {
        if (type == null) {
            return "UNKNOWN";
        }
        return switch (type) {
            case TRANSACTION_TYPE_RECHARGE -> "RECHARGE";
            case TRANSACTION_TYPE_CONSUME -> "CONSUME";
            case TRANSACTION_TYPE_REFUND -> "REFUND";
            default -> "UNKNOWN";
        };
    }
}
