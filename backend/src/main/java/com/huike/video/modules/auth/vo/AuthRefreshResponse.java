package com.huike.video.modules.auth.vo;

import lombok.Data;

@Data
public class AuthRefreshResponse {

    private String token;

    private String refreshToken;

    private Long expireIn;
}
