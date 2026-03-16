package com.huike.video.modules.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝客户端 Bean 配置
 * 注册单例 AlipayClient，供 Service 层注入使用
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AlipayConfig {

    private final AlipayProperties alipayProperties;

    @Bean
    public AlipayClient alipayClient() {
        log.info("初始化 AlipayClient, sandbox={}, appId={}", alipayProperties.isSandbox(), alipayProperties.getAppId());
        return new DefaultAlipayClient(
                alipayProperties.getGatewayUrl(),
                alipayProperties.getAppId(),
                alipayProperties.getPrivateKey(),
                "json",
                "UTF-8",
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getSignType()
        );
    }
}
