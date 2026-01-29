package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_configs")
public class SystemConfig extends BaseEntity {

    @TableId
    private Long id;

    /** 配置键，唯一 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置类型 */
    private String configType;

    /** 分类 */
    private String category;

    /** 描述 */
    private String description;

    /** 是否公开 */
    private Boolean isPublic;

    /** 创建人ID */
    private String createdBy;

    /** 更新人ID */
    private String updatedBy;

    @TableLogic
    private Integer isDeleted;
}
