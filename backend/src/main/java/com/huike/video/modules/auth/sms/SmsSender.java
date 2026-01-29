package com.huike.video.modules.auth.sms;

public interface SmsSender {

    void send(String phone, String code, String type);
}
