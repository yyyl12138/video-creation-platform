package com.huike.video.modules.creation.strategy.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.strategy.ModelStrategy;
import com.huike.video.modules.creation.utils.OpenAiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DeepSeek 模型策略实现
 * 也可以复用于其它兼容 OpenAI 协议的模型
 */
@Slf4j
@Component
public class DeepSeekStrategy implements ModelStrategy {

    private static final String PROVIDER_NAME = "DEEPSEEK";

    @Override
    public String getProvider() {
        return PROVIDER_NAME;
    }

    @Override
    public CreationResponse generate(CreationRequest request, AiModel modelConfig) {
        log.info("Executing DeepSeek Strategy with model: {}", modelConfig.getModelName());

        // 1. 提取配置
        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        if (apiConfig == null) {
            throw new BusinessException("Config missing for model: " + modelConfig.getModelKey());
        }

        String apiKey = (String) apiConfig.get("apiKey");
        String apiUrl = modelConfig.getApiEndpoint();
        // 如果 config 中没有指定 modelName，则使用 key 或默认值
        String callModel = (String) apiConfig.getOrDefault("modelName", "deepseek-chat");

        if (apiKey == null || apiUrl == null) {
            throw new BusinessException("Incomplete config(apiKey/endpoint) for DeepSeek");
        }

        // 2. 准备请求参数
        // 支持从 request.extraMap 覆盖 temperature 等
        Double temperature = 0.7;
        if (request.getExtraMap() != null && request.getExtraMap().containsKey("temperature")) {
            temperature = Double.valueOf(request.getExtraMap().get("temperature").toString());
        }

        // 3. 调用通用 OpenAI 客户端
        String generatedContent = OpenAiClient.chatCompletion(apiUrl, apiKey, callModel, request.getPrompt(), temperature);

        // 4. 封装结果
        return CreationResponse.builder()
                .content(generatedContent)
                .usageTokens(0) // TODO: 可以从完整响应中解析 usage
                .build();
    }
}
