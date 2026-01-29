package com.huike.video.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.admin.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
