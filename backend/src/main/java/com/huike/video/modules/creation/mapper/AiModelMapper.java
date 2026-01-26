package com.huike.video.modules.creation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.creation.domain.entity.AiModel;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 模型 Mapper 接口
 */
@Mapper
public interface AiModelMapper extends BaseMapper<AiModel> {
}
