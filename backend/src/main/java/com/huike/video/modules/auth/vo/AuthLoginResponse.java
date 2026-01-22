package com.huike.video.modules.auth.vo;

import lombok.Data;

import java.util.List;

@Data
public class AuthLoginResponse {

    private String token;

    private String refreshToken;

    private Long expireIn;

    private UserInfo userInfo;

    @Data
    public static class UserInfo {

        private String userId;

        private String username;

        private String avatarUrl;

        private List<String> roles;
    }
}
