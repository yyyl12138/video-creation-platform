package com.huike.video.modules.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.modules.payment.service.VipService;
import com.huike.video.modules.user.entity.User;
import com.huike.video.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * VIP 会员服务实现
 * 负责根据 planId 设置用户的 VIP 等级和过期时间
 *
 * 套餐规则:
 *   planId=1 → 月卡, vipLevel=1, 有效期 30 天
 *   planId=2 → 年卡, vipLevel=2, 有效期 365 天
 *
 * 续费逻辑:
 *   如果用户当前 VIP 未过期，过期时间在现有基础上累加；
 *   如果已过期或从未开通，从当前时间开始计算。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VipServiceImpl implements VipService {

    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateVip(String userId, Integer planId) {
        // 1. 查询当前用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.error("VIP 开通失败: 用户不存在, userId={}", userId);
            return false;
        }

        // 2. 根据 planId 确定 VIP 等级和有效天数
        int vipLevel;
        int days;
        switch (planId) {
            case 1 -> { vipLevel = 1; days = 30; }   // 月卡
            case 2 -> { vipLevel = 2; days = 365; }   // 年卡
            default -> {
                log.warn("未知的 planId={}, 默认按月卡处理", planId);
                vipLevel = 1;
                days = 30;
            }
        }

        // 3. 计算新的过期时间 (续费逻辑)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentExpire = user.getVipExpireTime();
        LocalDateTime baseTime;

        if (currentExpire != null && currentExpire.isAfter(now)) {
            // 未过期 → 在现有过期时间上累加
            baseTime = currentExpire;
        } else {
            // 已过期或从未开通 → 从现在开始
            baseTime = now;
        }
        LocalDateTime newExpireTime = baseTime.plusDays(days);

        // 4. 更新用户 VIP 信息
        // 如果新套餐等级更高，则升级；否则保持当前等级
        int finalLevel = (user.getVipLevel() != null && user.getVipLevel() > vipLevel)
                ? user.getVipLevel() : vipLevel;

        int rows = userMapper.update(null,
                new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getVipLevel, finalLevel)
                        .set(User::getVipExpireTime, newExpireTime)
        );

        if (rows > 0) {
            log.info("VIP 开通成功, userId={}, planId={}, vipLevel={}, expireTime={}",
                    userId, planId, finalLevel, newExpireTime);
            return true;
        }

        log.error("VIP 开通失败: 数据库更新异常, userId={}", userId);
        return false;
    }
}
