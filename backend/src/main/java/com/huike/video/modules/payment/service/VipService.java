package com.huike.video.modules.payment.service;

/**
 * VIP 会员服务接口
 * 负责会员开通/续费的"发货"逻辑
 */
public interface VipService {

    /**
     * 开通或续费 VIP 会员
     * @param userId 用户ID
     * @param planId 套餐ID (1=月卡, 2=年卡)
     * @return 是否成功
     */
    boolean activateVip(String userId, Integer planId);
}
