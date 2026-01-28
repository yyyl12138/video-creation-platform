package com.huike.video.modules.auth.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sms.aliyun")
public class SmsProperties {

    private boolean enabled = false;

    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    private String templateCode;

    private String regionId = "cn-hangzhou";
}
