package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 存储路径配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("storage_configs")
public class StorageConfig extends BaseEntity {

    @TableId
    private Long id;

    /** 存储类型 */
    private String storageType;

    /** 基础路径 */
    private String basePath;

    /** 最大容量 */
    private Long maxCapacity;

    /** 当前使用量 */
    private Long currentUsage;

    /** 允许文件类型 (JSON) */
    private String fileTypes;

    /** 保留天数 */
    private Integer retentionDays;

    /** 备份策略 (JSON) */
    private String backupPolicy;

    /** 状态 */
    private Integer status;

    @TableLogic
    private Integer isDeleted;
}
