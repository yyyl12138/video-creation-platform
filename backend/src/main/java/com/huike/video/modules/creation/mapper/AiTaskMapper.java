package com.huike.video.modules.creation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.creation.domain.entity.AiTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 生成任务 Mapper
 */
@Mapper
public interface AiTaskMapper extends BaseMapper<AiTask> {
}
