package com.huike.video.modules.creation.strategy;

import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.domain.entity.AiTask;
import com.huike.video.modules.creation.service.AiModelService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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
     * 初始化策略映射 (应用启动时执行)
     */
    @PostConstruct
    public void registerStrategies() {
        for (ModelStrategy strategy : strategyList) {
            strategyMap.put(strategy.getProvider(), strategy);
            log.info("Registered AI Strategy: {}", strategy.getProvider());
        }
        log.info("策略注册完成，共 {} 个策略", strategyMap.size());
    }

    /**
     * 执行生成任务
     * @param request 请求参数
     * @return 响应结果
     */
    public CreationResponse executeGeneration(CreationRequest request) {
        // 获取模型配置
        String modelKey = request.getModelKey();
        if (modelKey == null || modelKey.isEmpty()) {
            throw new BusinessException(500, "Model key is required");
        }

        AiModel aiModel = aiModelService.getActiveModelByKey(modelKey);
        if (aiModel == null) {
            throw new BusinessException(500, "AI Model not found or inactive: " + modelKey);
        }

        // 路由到对应策略
        String provider = aiModel.getProvider();
        ModelStrategy strategy = strategyMap.get(provider);
        if (strategy == null) {
            throw new BusinessException(500, "No implementation found for provider: " + provider);
        }

        // 执行
        return strategy.generate(request, aiModel);
    }

    /**
     * 查询任务状态 (Lazy Sync)
     */
    public CreationResponse checkStatus(AiTask task) {
        if (task.getModelId() == null) return null;
        
        AiModel aiModel = aiModelService.getById(task.getModelId());
        if (aiModel == null) return null;

        String provider = aiModel.getProvider();
        ModelStrategy strategy = strategyMap.get(provider);
        
        if (strategy != null) {
            return strategy.checkStatus(task, aiModel);
        }
        return null;
    }

    /**
     * 取消任务 (调用第三方取消接口)
     * @param task 任务实体
     * @return 是否成功取消
     */
    public boolean cancelTask(AiTask task) {
        if (task.getModelId() == null) return false;
        
        AiModel aiModel = aiModelService.getById(task.getModelId());
        if (aiModel == null) return false;

        String provider = aiModel.getProvider();
        ModelStrategy strategy = strategyMap.get(provider);
        
        if (strategy != null) {
            return strategy.cancel(task, aiModel);
        }
        return false;
    }
}

