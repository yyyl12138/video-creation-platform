package com.huike.video.modules.wallet.service;

import com.huike.video.modules.wallet.vo.WalletMeResponse;
import com.huike.video.modules.wallet.vo.WalletTransactionsResponse;

public interface WalletService {

    WalletMeResponse getMyWallet();

    WalletTransactionsResponse getMyTransactions(Integer page, Integer size, String type);
}
