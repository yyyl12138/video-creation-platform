package com.huike.video.modules.creation.strategy.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.domain.entity.AiTask;
import com.huike.video.modules.creation.strategy.ModelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 快手策略 (Kling)
 * 视频生成，异步任务
 */
@Slf4j
@Component
public class KuaishouStrategy implements ModelStrategy {

    private static final String PROVIDER_NAME = "KUAISHOU";
    private static final int TIMEOUT_MS = 30000;

    @Override
    public String getProvider() {
        return PROVIDER_NAME;
    }

    @Override
    public CreationResponse generate(CreationRequest request, AiModel modelConfig) {
        log.info("KuaishouStrategy executing, modelKey={}", modelConfig.getModelKey());

        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String accessKey = (String) apiConfig.get("accessKey");
        String secretKey = (String) apiConfig.get("secretKey");
        String apiUrl = modelConfig.getApiEndpoint();

        if (accessKey == null || secretKey == null) {
            throw new BusinessException(500, "Incomplete config for Kuaishou Kling");
        }

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model_name", modelConfig.getModelKey()); // e.g. kling 1.6
        body.put("prompt", request.getPrompt());
        if (request.getNegativePrompt() != null) {
            body.put("negative_prompt", request.getNegativePrompt());
        }
        // 可从 extraMap 获取更多参数 (duration, aspect_ratio 等)
        if (request.getExtraMap() != null) {
            body.putAll(request.getExtraMap());
        }

        String jsonBody = JSONUtil.toJsonStr(body);
        String authorization = generateJwtToken(accessKey, secretKey);

        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + authorization)
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .timeout(TIMEOUT_MS)
                .execute()) {

            String result = response.body();
            log.info("Kling API response: {}", result);

            if (!response.isOk()) {
                throw new BusinessException(500, "Kling API error: " + response.getStatus() + " | " + result);
            }

            JSONObject json = JSONUtil.parseObj(result);
            // Kling 返回 { "data": { "task_id": "xxx" } }
            String taskId = json.getJSONObject("data").getStr("task_id");

            return CreationResponse.builder()
                    .taskId(taskId)
                    .build();

        } catch (Exception e) {
            log.error("Kuaishou Kling API failed", e);
            if (e instanceof BusinessException) throw (BusinessException) e;
            throw new BusinessException(500, "Kling connection error: " + e.getMessage());
        }
    }

    /**
     * 生成快手 API 的 JWT Token (使用 Hutool)
     */
    private String generateJwtToken(String accessKey, String secretKey) {
        try {
            long now = System.currentTimeMillis() / 1000;
            return cn.hutool.jwt.JWT.create()
                    .setHeader("alg", "HS256")
                    .setHeader("typ", "JWT")
                    .setPayload("iss", accessKey)
                    .setPayload("exp", now + 1800) // 30分钟过期
                    .setPayload("nbf", now - 5)
                    .setSigner(cn.hutool.jwt.signers.JWTSignerUtil.hs256(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .sign();
        } catch (Exception e) {
            throw new BusinessException(500, "Failed to generate Kling JWT: " + e.getMessage());
        }
    }

    @Override
    public CreationResponse checkStatus(AiTask task, AiModel modelConfig) {
        String taskId = task.getExternalTaskId();
        if (taskId == null) return null;

        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String accessKey = (String) apiConfig.get("accessKey");
        String secretKey = (String) apiConfig.get("secretKey");
        String baseUrl = modelConfig.getApiEndpoint(); // e.g. https://api.klingai.com/v1/

        // 构造查询 URL: GET {baseUrl}/videos/text2video/{taskId}
        // 注意：不同任务类型路径可能不同，这里简化处理，需根据实际文档调整
        String queryUrl = baseUrl;
        if (!queryUrl.endsWith("/")) queryUrl += "/";
        
        // 简单推断 endpoint 路径
        String path = "videos/text2video/";
        if (task.getTaskType() == 4) path = "videos/image2video/";
        
        // 如果 baseUrl 已经包含了完整路径前缀，则不做重复拼接
        if (!queryUrl.contains("videos/")) {
            queryUrl += path + taskId;
        } else {
            queryUrl += taskId;
        }

        String authorization = generateJwtToken(accessKey, secretKey);

        try (HttpResponse response = HttpRequest.get(queryUrl)
                .header("Authorization", "Bearer " + authorization)
                .timeout(TIMEOUT_MS)
                .execute()) {

            if (!response.isOk()) {
                log.warn("Kling check status failed: {} - {}", response.getStatus(), response.body());
                return null;
            }

            JSONObject json = JSONUtil.parseObj(response.body());
            // Response: { "code": 0, "message": "", "data": { "task_status": "succeed", "task_result": { "videos": [{ "url": "...", "cover_url": "..." }] }, "task_info": { "request_id": "..." } } }
            
            JSONObject data = json.getJSONObject("data");
            if (data == null) return null;

            String statusStr = data.getStr("task_status"); // succeed, failed, processing, submitted
            String status = "PROCESSING";
            String videoUrl = null;
            String coverUrl = null;
            Integer usageTokens = null;

            if ("succeed".equalsIgnoreCase(statusStr)) {
                status = "SUCCESS";
                JSONObject result = data.getJSONObject("task_result");
                if (result != null && result.getJSONArray("videos") != null && !result.getJSONArray("videos").isEmpty()) {
                    JSONObject videoObj = result.getJSONArray("videos").getJSONObject(0);
                    videoUrl = videoObj.getStr("url");
                    // 尝试获取 cover_url，如果字段名不同需调整 (e.g. cover_img_url)
                    coverUrl = videoObj.getStr("cover_url"); 
                }
                // 尝试获取 token 消耗
                // Kling 可能在 data.usage 或类似的字段中
                if (data.containsKey("usage")) {
                     // 假设格式
                }
            } else if ("failed".equalsIgnoreCase(statusStr)) {
                status = "FAILED";
            }

            return CreationResponse.builder()
                    .taskId(taskId)
                    .status(status)
                    .mediaUrl(videoUrl)
                    .coverUrl(coverUrl)
                    .usageTokens(usageTokens)
                    .build();

        } catch (Exception e) {
            log.error("Kling check status error", e);
            return null;
        }
    }
}
