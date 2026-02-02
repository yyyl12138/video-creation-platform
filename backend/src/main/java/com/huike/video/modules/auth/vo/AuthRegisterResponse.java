package com.huike.video.modules.auth.vo;

import lombok.Data;

@Data
public class AuthRegisterResponse {

    private String userId;

    private String username;

    private String registerTime;

    private String token;

    private String refreshToken;

    private long expireIn;

    private com.huike.video.modules.auth.vo.AuthLoginResponse.UserInfo userInfo;
}
