package com.huike.video.modules.auth.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfig {

    @Bean
    public SmsSender smsSender(SmsProperties properties) {
        log.info("SMS Service: AliyunSmsSender (Enforced)");
        
        // 简单校验配置，如果不完整则抛出异常，防止启动后无法使用
        if (!properties.isEnabled()) {
             // 虽然用户说“保留开关判断”，但我们要删除 Mock。如果 enabled=false，这里必须报错或者仍然返回 AliyunSmsSender 但它可能无法工作？
             // 用户说“如果原本代码支持...无需改动...”，但我们现在是在修改。
             // 如果 enabled=false，我们抛出异常提示用户必须开启。
             throw new IllegalArgumentException("必须启用短信服务 (sms.aliyun.enabled=true) 且配置正确才能启动应用");
        }
        
        if (properties.getAccessKeyId() == null || properties.getAccessKeyId().isBlank()
                || properties.getAccessKeySecret() == null || properties.getAccessKeySecret().isBlank()) {
            throw new IllegalArgumentException("阿里云 AccessKey 未配置，无法启动短信服务");
        }

        return new AliyunSmsSender(properties);
    }
}
