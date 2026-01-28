package com.huike.video.modules.auth.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliyunSmsSender implements SmsSender {

    private final SmsProperties properties;
    private final IAcsClient client;

    public AliyunSmsSender(SmsProperties properties) {
        this.properties = properties;
        DefaultProfile profile = DefaultProfile.getProfile(
                properties.getRegionId(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret()
        );
        this.client = new DefaultAcsClient(profile);
    }

    @Override
    public void send(String phone, String code, String type) {
        try {
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phone);
            request.setSignName(properties.getSignName());
            request.setTemplateCode(properties.getTemplateCode());
            request.setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.getAcsResponse(request);
            if (response == null || response.getCode() == null || !"OK".equalsIgnoreCase(response.getCode())) {
                String msg = response == null ? "null response" : (response.getCode() + ":" + response.getMessage());
                log.warn("Aliyun SMS send failed: phone={}, type={}, result={}", phone, type, msg);
            }
        } catch (Exception e) {
            log.warn("Aliyun SMS send exception: phone={}, type={}", phone, type, e);
        }
    }
}
