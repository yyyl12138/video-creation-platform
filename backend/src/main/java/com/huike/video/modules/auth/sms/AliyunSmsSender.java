package com.huike.video.modules.auth.sms;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.sdk.service.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * 阿里云号码认证服务 - 短信验证码发送器
 * 使用 dypnsapi20170525 SDK
 */
@Slf4j
public class AliyunSmsSender implements SmsSender {

    private final SmsProperties properties;
    private final AsyncClient client;

    public AliyunSmsSender(SmsProperties properties) {
        this.properties = properties;
        
        // 使用静态凭证提供者
        StaticCredentialProvider provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(properties.getAccessKeyId())
                        .accessKeySecret(properties.getAccessKeySecret())
                        .build()
        );

        // 构建异步客户端
        this.client = AsyncClient.builder()
                .region(properties.getRegionId())
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build();
        
        log.info("AliyunSmsSender 初始化完成, region={}", properties.getRegionId());
    }

    @Override
    public String sendVerifyCode(String phone, String type) {
        try {
            // 构建请求
            SendSmsVerifyCodeRequest request = SendSmsVerifyCodeRequest.builder()
                    .signName(properties.getSignName())
                    .templateCode(properties.getTemplateCode())
                    .phoneNumber(phone)
                    .templateParam("{\"code\":\"##code##\",\"min\":\"5\"}")
                    .codeLength(6L)
                    .validTime(300L) // 5分钟有效期
                    .build();

            // 发送请求
            CompletableFuture<SendSmsVerifyCodeResponse> future = client.sendSmsVerifyCode(request);
            SendSmsVerifyCodeResponse response = future.get();

            // 详细日志，用于调试
            log.info("阿里云响应: {}", response);

            // 检查响应
            if (response != null && response.getBody() != null) {
                String code = response.getBody().getCode();
                String message = response.getBody().getMessage();
                log.info("响应 code={}, message={}", code, message);
                
                if ("OK".equalsIgnoreCase(code)) {
                    // 尝试获取 verifyCode，如果为空则使用 bizId 或生成占位符
                    String verifyCodeId = null;
                    String bizId = null;
                    
                    if (response.getBody().getModel() != null) {
                        verifyCodeId = response.getBody().getModel().getVerifyCode();
                        bizId = response.getBody().getModel().getBizId();
                        log.info("Model: verifyCode={}, bizId={}", verifyCodeId, bizId);
                    }
                    
                    // 如果 verifyCode 为空，使用 bizId 或生成一个标识
                    if (verifyCodeId == null || verifyCodeId.isEmpty()) {
                        verifyCodeId = bizId != null ? bizId : "SMS_" + System.currentTimeMillis();
                    }
                    
                    log.info("验证码发送成功: phone={}, type={}, verifyCodeId={}", phone, type, verifyCodeId);
                    return verifyCodeId;
                } else {
                    log.warn("验证码发送失败: phone={}, code={}, message={}", phone, code, message);
                    return null;
                }
            }
            
            log.warn("验证码发送响应为空: phone={}", phone);
            return null;

        } catch (Exception e) {
            log.error("验证码发送异常: phone={}, type={}", phone, type, e);
            return null;
        }
    }

    @Override
    public boolean checkVerifyCode(String phone, String code, String verifyCodeId) {
        try {
            // 构建校验请求
            CheckSmsVerifyCodeRequest request = CheckSmsVerifyCodeRequest.builder()
                    .phoneNumber(phone)
                    .verifyCode(code)
                    .build();

            // 发送请求
            CompletableFuture<CheckSmsVerifyCodeResponse> future = client.checkSmsVerifyCode(request);
            CheckSmsVerifyCodeResponse response = future.get();

            // 检查响应
            if (response != null && response.getBody() != null) {
                String resultCode = response.getBody().getCode();
                if ("OK".equalsIgnoreCase(resultCode)) {
                    String verifyResult = response.getBody().getModel().getVerifyResult();
                    boolean verified = "PASS".equalsIgnoreCase(verifyResult);
                    log.info("验证码校验完成: phone={}, verifyResult={}, verified={}", phone, verifyResult, verified);
                    return verified;
                } else {
                    log.warn("验证码校验失败: phone={}, code={}, message={}", 
                            phone, resultCode, response.getBody().getMessage());
                    return false;
                }
            }
            
            return false;

        } catch (Exception e) {
            log.error("验证码校验异常: phone={}", phone, e);
            return false;
        }
    }
}
