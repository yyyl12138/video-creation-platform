package com.huike.video.modules.wallet.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.wallet.service.WalletService;
import com.huike.video.modules.wallet.vo.WalletMeResponse;
import com.huike.video.modules.wallet.vo.WalletTransactionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public Result<WalletMeResponse> getMyWallet() {
        return Result.success(walletService.getMyWallet());
    }

    @GetMapping("/me/transactions")
    public Result<WalletTransactionsResponse> getMyTransactions(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "type", required = false) String type
    ) {
        return Result.success(walletService.getMyTransactions(page, size, type));
    }
}
