package com.huike.video.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {

    @TableId
    private String id;

    private String username;

    private String email;

    private String passwordHash;

    private String avatarUrl;

    private String phone;

    private LocalDateTime lastLoginTime;

    private Integer status;

    private Long roleId;

    // 会员相关扩展字段
    private Integer vipLevel; // 0-非会员, 1-月度会员, 2-年度会员 等
    private LocalDateTime vipExpireTime;
}
