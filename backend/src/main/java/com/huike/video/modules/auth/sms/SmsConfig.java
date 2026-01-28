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
        if (properties.isEnabled()
                && properties.getAccessKeyId() != null && !properties.getAccessKeyId().isBlank()
                && properties.getAccessKeySecret() != null && !properties.getAccessKeySecret().isBlank()
                && properties.getSignName() != null && !properties.getSignName().isBlank()
                && properties.getTemplateCode() != null && !properties.getTemplateCode().isBlank()) {
            log.info("SMS sender: AliyunSmsSender enabled=true regionId={}", properties.getRegionId());
            return new AliyunSmsSender(properties);
        }
        log.info("SMS sender: LogSmsSender (mock) enabled={} (set sms.aliyun.enabled=true and fill access-key-id/access-key-secret/sign-name/template-code to enable real SMS)", properties.isEnabled());
        return new LogSmsSender();
    }
}
