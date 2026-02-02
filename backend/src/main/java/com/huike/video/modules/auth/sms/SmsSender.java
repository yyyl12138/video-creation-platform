package com.huike.video.modules.auth.sms;

/**
 * 短信发送接口
 * 支持发送验证码和服务端校验
 */
public interface SmsSender {

    /**
     * 发送验证码短信
     * @param phone 手机号
     * @param type 业务类型 (REGISTER/RESET_PWD)
     * @return 验证码 ID (用于后续校验)
     */
    String sendVerifyCode(String phone, String type);

    /**
     * 校验验证码
     * @param phone 手机号
     * @param code 用户输入的验证码
     * @param verifyCodeId 发送时返回的验证码ID (如果使用服务端校验)
     * @return 是否校验通过
     */
    boolean checkVerifyCode(String phone, String code, String verifyCodeId);
}
