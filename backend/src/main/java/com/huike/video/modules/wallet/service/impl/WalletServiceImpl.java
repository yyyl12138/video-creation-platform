package com.huike.video.modules.wallet.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huike.video.modules.wallet.entity.UserWallet;
import com.huike.video.modules.wallet.mapper.UserWalletMapper;
import com.huike.video.modules.wallet.service.WalletService;
import com.huike.video.modules.wallet.vo.WalletMeResponse;
import com.huike.video.modules.wallet.vo.WalletTransactionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        return WalletTransactionsResponse.empty();
    }
}
