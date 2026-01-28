package com.huike.video.modules.creation.strategy.impl;

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
 * 智谱策略
 * GLM-4 系列模型，使用 OpenAI 兼容协议
 */
@Slf4j
@Component
public class ZhipuStrategy implements ModelStrategy {

    private static final String PROVIDER_NAME = "ZHIPU";

    @Override
    public String getProvider() {
        return PROVIDER_NAME;
    }

    @Override
    public CreationResponse generate(CreationRequest request, AiModel modelConfig) {
        log.info("ZhipuStrategy executing, modelKey={}", modelConfig.getModelKey());

        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");
        String apiUrl = modelConfig.getApiEndpoint();
        String model = modelConfig.getModelKey(); // e.g. glm-4.7-flash

        if (apiKey == null || apiUrl == null) {
            throw new BusinessException(500, "Incomplete config for Zhipu");
        }

        Double temperature = 0.7;
        if (request.getExtraMap() != null && request.getExtraMap().containsKey("temperature")) {
            temperature = Double.valueOf(request.getExtraMap().get("temperature").toString());
        }

        // GLM 兼容 OpenAI 协议
        OpenAiClient.ChatResult chatResult = OpenAiClient.chatCompletion(apiUrl, apiKey, model, request.getPrompt(), temperature);

        return CreationResponse.builder()
                .content(chatResult.getContent())
                .usageTokens(chatResult.getTotalTokens())
                .build();
    }
}
