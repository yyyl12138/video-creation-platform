package com.huike.video.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("creators")
public class Creator extends BaseEntity {

    @TableId
    private String id;

    private String userId;

    private String platformAccount;

    private String platformType;

    private Integer followerCount;

    private Integer verificationStatus;

    private String bio;
}
