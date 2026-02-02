package com.huike.video.modules.auth.service.impl;

import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.auth.service.VerificationCodeService;
import com.huike.video.modules.auth.sms.SmsSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 * 使用阿里云号码认证服务发送和校验验证码
 * Redis 用于存储验证码ID和频率限制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final StringRedisTemplate redisTemplate;
    private final SmsSender smsSender;

    /** 发送频率限制（秒） */
    private static final long RATE_LIMIT_SECONDS = 60;

    /** Redis Key 前缀：验证码ID (用于阿里云服务端校验) */
    private static final String CODE_ID_KEY_PREFIX = "sms:id:";
    
    /** Redis Key 前缀：频率限制 */
    private static final String LIMIT_KEY_PREFIX = "sms:limit:";

    @Override
    public boolean sendCode(String target, String type) {
        // 1. 参数校验
        if (!StringUtils.hasText(target) || !StringUtils.hasText(type)) {
            throw new BusinessException(10003, "参数不完整");
        }

        // 2. 校验目标格式（仅支持手机号）
        if (!isValidPhone(target)) {
            throw new BusinessException(10004, "手机号格式不正确");
        }

        // 3. 频率限制检查
        String limitKey = LIMIT_KEY_PREFIX + target;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            Long ttl = redisTemplate.getExpire(limitKey, TimeUnit.SECONDS);
            throw new BusinessException(10005, "请在 " + (ttl != null ? ttl : RATE_LIMIT_SECONDS) + " 秒后重试");
        }

        // 4. 发送验证码
        String verifyCodeId = smsSender.sendVerifyCode(target, type);
        if (verifyCodeId == null) {
            throw new BusinessException(50001, "验证码发送失败，请稍后重试");
        }

        // 5. 存储验证码ID到 Redis (用于后续校验, 5分钟过期)
        String codeIdKey = CODE_ID_KEY_PREFIX + type + ":" + target;
        redisTemplate.opsForValue().set(codeIdKey, verifyCodeId, 5, TimeUnit.MINUTES);
        log.info("验证码ID已存储: key={}, verifyCodeId={}", codeIdKey, verifyCodeId);

        // 6. 设置频率限制
        redisTemplate.opsForValue().set(limitKey, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public boolean verifyCode(String target, String type, String code) {
        if (!StringUtils.hasText(target) || !StringUtils.hasText(type) || !StringUtils.hasText(code)) {
            return false;
        }

        // 获取存储的验证码ID
        String codeIdKey = CODE_ID_KEY_PREFIX + type + ":" + target;
        String verifyCodeId = redisTemplate.opsForValue().get(codeIdKey);

        // 调用阿里云服务端校验
        boolean verified = smsSender.checkVerifyCode(target, code, verifyCodeId);

        if (verified) {
            // 验证成功后删除验证码ID（一次性使用）
            redisTemplate.delete(codeIdKey);
            log.info("验证码验证成功: target={}, type={}", target, type);
        }

        return verified;
    }

    /**
     * 校验手机号格式
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }
        return phone.matches("^1[3-9]\\d{9}$");
    }
}
