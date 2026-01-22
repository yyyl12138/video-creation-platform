package com.huike.video.modules.auth.vo;

import lombok.Data;

import java.util.List;

@Data
public class AuthSwitchRoleResponse {

    private String userId;

    private Long roleId;

    private List<String> roles;
}
