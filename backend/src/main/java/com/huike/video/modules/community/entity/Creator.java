package com.huike.video.modules.community.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创作者信息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("creators")
public class Creator extends BaseEntity {

    @TableId
    private String id;

    /** 用户ID */
    private String userId;

    /** 平台账号 */
    private String platformAccount;

    /** 平台类型 */
    private String platformType;

    /** 粉丝数 */
    private Integer followerCount;

    /** 认证状态: 1-未认证, 2-已认证, 3-认证中 */
    private Integer verificationStatus;

    /** 简介 */
    private String bio;

    /** 联系信息 (JSON) */
    private String contactInfo;

    @TableLogic
    private Integer isDeleted;
}
