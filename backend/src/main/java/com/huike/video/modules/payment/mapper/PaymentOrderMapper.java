package com.huike.video.modules.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.payment.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单 Mapper
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {
}
