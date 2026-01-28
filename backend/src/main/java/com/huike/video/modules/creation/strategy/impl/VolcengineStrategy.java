package com.huike.video.modules.creation.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.domain.entity.AiTask;
import com.huike.video.modules.creation.strategy.ModelStrategy;
import com.volcengine.ark.runtime.model.content.generation.CreateContentGenerationTaskRequest;
import com.volcengine.ark.runtime.model.content.generation.CreateContentGenerationTaskResult;
import com.volcengine.ark.runtime.model.content.generation.GetContentGenerationTaskRequest;
import com.volcengine.ark.runtime.model.content.generation.GetContentGenerationTaskResponse;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 火山引擎策略 (Doubao/Ark Refactored)
 * 使用 Ark Runtime SDK 进行视频生成
 * 支持 AK/SK 和 API Key 两种认证方式
 */
@Slf4j
@Component
public class VolcengineStrategy implements ModelStrategy {

    private static final String PROVIDER_NAME = "VOLCENGINE";

    @Override
    public String getProvider() {
        return PROVIDER_NAME;
    }

    @Override
    public CreationResponse generate(CreationRequest request, AiModel modelConfig) {
        log.info("VolcengineStrategy executing, modelKey={}", modelConfig.getModelKey());

        ArkService service = buildService(modelConfig);
        try {
            // 1. 确定 Model ID (优先使用 Endpoint ID，否则使用 Model Key)
            String endpointId = (String) modelConfig.getApiConfig().get("endpointId");
            String modelId = (endpointId != null && !endpointId.isEmpty()) ? endpointId : modelConfig.getModelKey();

            // 2. 构建内容
            List<CreateContentGenerationTaskRequest.Content> contents = new ArrayList<>();
            contents.add(CreateContentGenerationTaskRequest.Content.builder()
                    .type("text")
                    .text(request.getPrompt())
                    .build());

            // 3. 构建请求
            CreateContentGenerationTaskRequest createRequest = CreateContentGenerationTaskRequest.builder()
                    .model(modelId)
                    .content(contents)
                    .build();

            // 4. 发起调用
            CreateContentGenerationTaskResult result = service.createContentGenerationTask(createRequest);
            log.info("Volcengine task created: {}", result);

            return CreationResponse.builder()
                    .taskId(result.getId())
                    .status("PROCESSING")
                    .build();

        } catch (Exception e) {
            log.error("Volcengine API failed", e);
            throw new BusinessException(500, "Volcengine error: " + e.getMessage());
            // if (service != null) service.shutdown();
    }
    }

    @Override
    public CreationResponse checkStatus(AiTask task, AiModel modelConfig) {
        String taskId = task.getExternalTaskId();
        if (taskId == null) return null;

        ArkService service = buildService(modelConfig);
        try {
            GetContentGenerationTaskRequest getRequest = GetContentGenerationTaskRequest.builder()
                    .taskId(taskId)
                    .build();

            GetContentGenerationTaskResponse response = service.getContentGenerationTask(getRequest);
            // log.debug("Volcengine checkStatus response: {}", response);

            String statusStr = response.getStatus(); // SDK: "succeeded", "failed", etc.
            String status = "PROCESSING";
            String videoUrl = null;
            String coverUrl = null;

            if ("succeeded".equalsIgnoreCase(statusStr)) {
                status = "SUCCESS";
                // SDK content 解析策略
                if (response.getContent() != null) {
                    Object contentObj = response.getContent();
                    // 序列化为 JSON 字符串以处理不同类型的对象 (List, Map, Content Object)
                    String jsonStr = JSONUtil.toJsonStr(contentObj);
                    log.info("Volcengine raw content: {}", jsonStr);

                    cn.hutool.json.JSONObject contentJson = null;
                    
                    // 尝试判断是否为数组
                    if (JSONUtil.isTypeJSONArray(jsonStr)) {
                        cn.hutool.json.JSONArray array = JSONUtil.parseArray(jsonStr);
                        if (!array.isEmpty()) {
                            // 视频生成通常是一个结果，取第一个
                            contentJson = array.getJSONObject(0);
                        }
                    } else if (JSONUtil.isTypeJSONObject(jsonStr)) {
                        contentJson = JSONUtil.parseObj(jsonStr);
                    }

                    if (contentJson != null) {
                        videoUrl = contentJson.getStr("videoUrl");
                        if (videoUrl == null) videoUrl = contentJson.getStr("video_url");
                        if (videoUrl == null) videoUrl = contentJson.getStr("url");
                        
                        coverUrl = contentJson.getStr("coverUrl");
                        if (coverUrl == null) coverUrl = contentJson.getStr("cover_image_url");
                        if (coverUrl == null) coverUrl = contentJson.getStr("cover_url");
                    }
                    
                    // DEBUG-DELETE-LATER: 若未提取到 URL，直接返回原始内容
                    if (videoUrl == null) {
                         videoUrl = "DEBUG_RAW: " + jsonStr;
                    }
                } else {
                    // content is null
                    videoUrl = "DEBUG_CONTENT_NULL: " + JSONUtil.toJsonStr(response);
                }
                
                // Double check
                if (videoUrl == null) {
                     videoUrl = "DEBUG_FINAL_NULL"; 
                }
            } else if ("failed".equalsIgnoreCase(statusStr)) {
                status = "FAILED";
                // 错误详情? response.getError() not directly available in standard GetResponse?
                // 仅根据状态判断
                log.warn("Volcengine task failed, status: {}", statusStr);
            }

            return CreationResponse.builder()
                    .taskId(taskId)
                    .status(status)
                    .mediaUrl(videoUrl)
                    .coverUrl(coverUrl)
                    .build();

        } catch (Exception e) {
            log.error("Volcengine check status error", e);
            return null;
        } finally {
            // if (service != null) service.shutdown();
        }
    }

    private ArkService buildService(AiModel modelConfig) {
        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String accessKey = (String) apiConfig.get("accessKey");
        String secretKey = (String) apiConfig.get("secretKey");
        
        ArkService.Builder builder = ArkService.builder();
        
        // 自动识别鉴权方式
        if (secretKey != null && !secretKey.isEmpty()) {
            // 使用 AK/SK (IAM)
            builder.ak(accessKey).sk(secretKey);
        } else {
            // 使用 API Key (Ark)
            builder.apiKey(accessKey);
        }
        
        // 如果需要配置 Base URL (通常 SDK 连接到 ark.cn-beijing.volces.com)
        // String baseUrl = modelConfig.getApiEndpoint();
        // if (baseUrl != null && !baseUrl.isEmpty()) {
        //    builder.baseUrl(baseUrl);
        // }
        // 注意：Volcengine SDK 默认 BaseURL 可能已经是正确的，如果 endpoint 是标准公有云，不需要设置
        // 且 Endpoint ID 模式下主要是连接方舟
        
        return builder.build();
    }
}
