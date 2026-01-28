package com.huike.video.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_profiles")
public class UserProfile extends BaseEntity {

    @TableId
    private String id;

    private String userId;

    private String realName;

    private Integer gender;

    private LocalDate birthday;

    private String country;

    private String city;

    private String bio;
}
