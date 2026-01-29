package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统操作日志表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("operation_logs")
public class OperationLog extends BaseEntity {

    @TableId
    private Long id;

    /** 用户ID */
    private String userId;

    /** 操作类型 */
    private String operationType;

    /** 操作对象 */
    private String operationTarget;

    /** 对象ID */
    private String targetId;

    /** 操作时间 */
    private LocalDateTime operationTime;

    /** IP地址 */
    private String ipAddress;

    /** UA信息 */
    private String userAgent;

    /** 请求方法 */
    private String requestMethod;

    /** 请求路径 */
    private String requestPath;

    /** 请求参数 */
    private String requestParams;

    /** 响应码 */
    private Integer responseCode;

    /** 响应耗时 */
    private Integer responseTimeMs;

    /** 错误信息 */
    private String errorMessage;

    /** 额外信息 (JSON) */
    private String additionalInfo;

    @TableLogic
    private Integer isDeleted;
}
