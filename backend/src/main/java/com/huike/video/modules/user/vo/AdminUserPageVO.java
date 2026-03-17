package com.huike.video.modules.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserPageVO {
    private String userId;
    private String username;
    private String email;
    private String roleName;
    private Integer videoCount; // 生成视频数量
    private String status;      // 例如 "正常", "封禁", "注销"
    private LocalDateTime registerTime;
}
