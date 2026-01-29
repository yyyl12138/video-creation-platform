package com.huike.video.modules.auth.sms;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogSmsSender implements SmsSender {

    @Override
    public void send(String phone, String code, String type) {
        log.warn("[SMS-MOCK] phone={}, type={}, code={}", phone, type, code);
    }
}
