package com.huike.video.modules.user.wallet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.user.wallet.entity.WalletTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钱包交易流水 Mapper
 */
@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {
}
