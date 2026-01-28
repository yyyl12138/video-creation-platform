package com.huike.video.modules.creation.strategy;

import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.domain.entity.AiTask;

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

    /**
     * 查询任务状态 (Lazy Sync)
     * @param task 任务实体 (包含 externalTaskId)
     * @param modelConfig 模型配置
     * @return 最新状态结果 (包含 status, mediaUrl, coverUrl)
     */
    default CreationResponse checkStatus(AiTask task, AiModel modelConfig) {
        return null; // 默认不实现
    }

    /**
     * 取消第三方任务 (如果厂商支持)
     * @param task 任务实体
     * @param modelConfig 模型配置
     * @return 是否成功取消
     */
    default boolean cancel(AiTask task, AiModel modelConfig) {
        // 默认不支持取消，仅更新本地状态
        return false;
    }
}
