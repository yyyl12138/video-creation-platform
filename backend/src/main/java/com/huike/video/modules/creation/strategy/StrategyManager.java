package com.huike.video.modules.creation.strategy;

import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.service.AiModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 策略管理器
 * 负责根据模型配置路由到具体的策略实现
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StrategyManager {

    private final AiModelService aiModelService;
    private final List<ModelStrategy> strategyList;
    private final Map<String, ModelStrategy> strategyMap = new ConcurrentHashMap<>();

    /**
     * 初始化策略映射
     */
    // @PostConstruct // 避免循环依赖，实际使用时懒加载或监听事件刷新
    public void registerStrategies() {
        if (strategyMap.isEmpty()) {
            for (ModelStrategy strategy : strategyList) {
                strategyMap.put(strategy.getProvider(), strategy);
                log.info("Registered AI Strategy: {}", strategy.getProvider());
            }
        }
    }

    /**
     * 执行生成任务
     * @param request 请求参数
     * @return 响应结果
     */
    public CreationResponse executeGeneration(CreationRequest request) {
        // 1. 确保策略已注册
        registerStrategies();

        // 2. 获取模型配置 (优先使用请求中的 modelKey，否则使用默认策略逻辑 - 暂未实现默认逻辑)
        String modelKey = request.getModelKey();
        if (modelKey == null || modelKey.isEmpty()) {
            throw new BusinessException("Model key is required");
        }

        AiModel aiModel = aiModelService.getActiveModelByKey(modelKey);
        if (aiModel == null) {
            throw new BusinessException("AI Model not found or inactive: " + modelKey);
        }

        // 3. 路由到对应策略
        String provider = aiModel.getProvider();
        ModelStrategy strategy = strategyMap.get(provider);
        if (strategy == null) {
            throw new BusinessException("No implementation found for provider: " + provider);
        }

        // 4. 执行
        return strategy.generate(request, aiModel);
    }
}
