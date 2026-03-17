package com.huike.video.modules.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝配置属性
 * 从 application.yml 中读取 alipay.* 前缀的配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    /**
     * 是否为沙盒环境
     */
    private boolean sandbox = true;

    /**
     * 支付宝网关地址
     */
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户应用私钥 (PKCS8 格式)
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 签名算法，固定 RSA2
     */
    private String signType = "RSA2";

    /**
     * 异步回调地址
     */
    private String notifyUrl;

    /**
     * 同步跳转地址 (前端支付成功页)
     */
    private String returnUrl;
}
