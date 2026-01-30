package com.huike.video.modules.user.wallet.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.user.wallet.service.WalletService;
import com.huike.video.modules.user.wallet.vo.WalletMeResponse;
import com.huike.video.modules.user.wallet.vo.WalletTransactionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 钱包控制器
 * 处理用户钱包相关的 HTTP 请求
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    /**
     * 获取当前用户钱包信息
     */
    @GetMapping("/me")
    public Result<WalletMeResponse> getMyWallet() {
        return Result.success(walletService.getMyWallet());
    }

    /**
     * 获取当前用户交易流水
     */
    @GetMapping("/me/transactions")
    public Result<WalletTransactionsResponse> getMyTransactions(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "type", required = false) String type
    ) {
        return Result.success(walletService.getMyTransactions(page, size, type));
    }



}
