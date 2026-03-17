package com.huike.video.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统参数配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_configs")
public class SystemConfig extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置键 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置类型 (JSON, STRING 等) */
    private String configType;

    /** 分类 (STORAGE, SCHEDULING 等) */
    private String category;

    /** 描述 */
    private String description;

    /** 是否公开 */
    private Boolean isPublic;

    /** 创建人ID */
    private String createdBy;

    /** 更新人ID */
    private String updatedBy;

    /** 逻辑删除 */
    private Boolean isDeleted;
}
