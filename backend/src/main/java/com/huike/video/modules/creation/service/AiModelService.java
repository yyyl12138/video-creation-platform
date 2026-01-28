package com.huike.video.modules.creation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huike.video.modules.creation.domain.entity.AiModel;

/**
 * AI 模型服务接口
 */
public interface AiModelService extends IService<AiModel> {

    /**
     * 根据 modelKey 获取启用状态的模型配置
     * @param modelKey 模型标识
     * @return 模型实体
     */
    AiModel getActiveModelByKey(String modelKey);

    /**
     * 根据 modelName 获取启用状态的模型配置
     * @param modelName 模型名称 (易记名称)
     * @return 模型实体
     */
    AiModel getActiveModelByName(String modelName);
}
