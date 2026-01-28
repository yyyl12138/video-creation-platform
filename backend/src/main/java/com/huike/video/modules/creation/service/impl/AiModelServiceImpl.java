package com.huike.video.modules.creation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.mapper.AiModelMapper;
import com.huike.video.modules.creation.service.AiModelService;
import org.springframework.stereotype.Service;

/**
 * AI 模型服务实现类
 */
@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModel> implements AiModelService {

    @Override
    public AiModel getActiveModelByKey(String modelKey) {
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiModel::getModelKey, modelKey)
               .eq(AiModel::getIsActive, true);
        return this.getOne(wrapper);
    }

    @Override
    public AiModel getActiveModelByName(String modelName) {
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiModel::getModelName, modelName)
               .eq(AiModel::getIsActive, true);
        return this.getOne(wrapper);
    }
}
