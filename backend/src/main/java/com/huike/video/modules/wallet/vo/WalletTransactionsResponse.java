package com.huike.video.modules.wallet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class WalletTransactionsResponse {

    private Long total;

    private List<Record> records;

    @Data
    public static class Record {

        private String transactionId;

        private String type;

        private BigDecimal amount;

        private String description;

        private String createdTime;
    }

    public static WalletTransactionsResponse empty() {
        WalletTransactionsResponse resp = new WalletTransactionsResponse();
        resp.setTotal(0L);
        resp.setRecords(new ArrayList<>());
        return resp;
    }
}
