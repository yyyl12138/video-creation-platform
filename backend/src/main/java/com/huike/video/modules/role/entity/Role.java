package com.huike.video.modules.role.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("roles")
public class Role extends BaseEntity {

    @TableId
    private Long id;

    private String roleName;

    private String description;

    @TableLogic
    private Integer isDeleted;
}
