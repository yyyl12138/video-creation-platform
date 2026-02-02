package com.huike.video.modules.auth.service;

/**
 * 验证码服务接口
 * 负责验证码的生成、发送、存储和验证
 */
public interface VerificationCodeService {

    /**
     * 发送验证码
     * @param target 手机号
     * @param type 类型 REGISTER/RESET_PWD
     * @return 是否发送成功
     */
    boolean sendCode(String target, String type);

    /**
     * 验证验证码是否正确
     * @param target 手机号
     * @param type 类型
     * @param code 用户输入的验证码
     * @return 是否匹配
     */
    boolean verifyCode(String target, String type, String code);
}
