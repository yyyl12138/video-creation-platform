package com.huike.video.modules.creation.strategy;

import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;

/**
 * AI 模型策略接口
 */
public interface ModelStrategy {

    /**
     * 获取支持的模型提供商 (如 DEEPSEEK, ALIYUN, KUAISHOU)
     */
    String getProvider();

    /**
     * 执行生成 (同步/异步)
     * @param request 请求参数
     * @param modelConfig 模型配置信息
     * @return 响应结果
     */
    CreationResponse generate(CreationRequest request, AiModel modelConfig);
}
