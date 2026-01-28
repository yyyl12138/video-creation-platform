package com.huike.video.modules.user.vo;

import lombok.Data;

@Data
public class UserMeResponse {

    private String userId;

    private String username;

    private String email;

    private String phone;

    private String avatarUrl;

    private Profile profile;

    private String vipStatus;

    private String vipExpireDate;

    private String creatorStatus;

    private String status;

    @Data
    public static class Profile {

        private String realName;

        private String gender;

        private String birthday;

        private String country;

        private String city;

        private String bio;
    }
}
