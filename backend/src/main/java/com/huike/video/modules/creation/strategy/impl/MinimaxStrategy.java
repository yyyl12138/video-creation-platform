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

import java.util.HashMap;
import java.util.Map;

/**
 * Minimax 策略 (Hailuo)
 * 视频生成，异步任务
 * 
 * API 文档参考: https://www.minimax.io/platform/docs/video-generation
 */
@Slf4j
@Component
public class MinimaxStrategy implements ModelStrategy {

    private static final String PROVIDER_NAME = "MINIMAX";
    private static final int TIMEOUT_MS = 30000;
    
    // Minimax API 端点
    // private static final String STATUS_QUERY_URL = "https://api.minimax.io/v1/query/video_generation";
    // private static final String FILE_RETRIEVE_URL = "https://api.minimax.io/v1/files/retrieve";

    @Override
    public String getProvider() {
        return PROVIDER_NAME;
    }

    @Override
    public CreationResponse generate(CreationRequest request, AiModel modelConfig) {
        log.info("MinimaxStrategy executing, modelKey={}", modelConfig.getModelKey());

        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");
        String groupId = (String) apiConfig.getOrDefault("groupId", "");
        String apiUrl = modelConfig.getApiEndpoint();

        if (apiKey == null) {
            throw new BusinessException(500, "Incomplete config for Minimax: apiKey required");
        }

        // 构建请求体
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelConfig.getModelKey()); // MiniMax-Hailuo-2.3-Fast
        body.put("prompt", request.getPrompt());
        
        // 添加可选参数
        Map<String, Object> extraMap = request.getExtraMap();
        if (extraMap != null) {
            if (extraMap.containsKey("aspect_ratio")) {
                body.put("aspect_ratio", extraMap.get("aspect_ratio"));
            }
            if (extraMap.containsKey("duration")) {
                body.put("duration", extraMap.get("duration"));
            }
        }

        String jsonBody = JSONUtil.toJsonStr(body);
        log.debug("Minimax request body: {}", jsonBody);

        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .timeout(TIMEOUT_MS)
                .execute()) {

            String result = response.body();
            log.info("Minimax API response status={}, body={}", response.getStatus(), result);

            if (!response.isOk()) {
                throw new BusinessException(500, "Minimax API error: " + response.getStatus() + " - " + result);
            }

            JSONObject json = JSONUtil.parseObj(result);
            
            // 检查错误
            if (json.containsKey("base_resp")) {
                JSONObject baseResp = json.getJSONObject("base_resp");
                Integer statusCode = baseResp.getInt("status_code");
                if (statusCode != null && statusCode != 0) {
                    String statusMsg = baseResp.getStr("status_msg");
                    throw new BusinessException(500, "Minimax error: " + statusMsg);
                }
            }
            
            // 提取 task_id
            String taskId = json.getStr("task_id");
            if (taskId == null) {
                throw new BusinessException(500, "Minimax response missing task_id: " + result);
            }

            return CreationResponse.builder()
                    .taskId(taskId)
                    .status("PROCESSING")
                    .build();

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Minimax API failed", e);
            throw new BusinessException(500, "Minimax connection error: " + e.getMessage());
        }
    }

    @Override
    public CreationResponse checkStatus(AiTask task, AiModel modelConfig) {
        String taskId = task.getExternalTaskId();
        if (taskId == null) return null;

        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");
        
        // 动态推断 URL
        // Endpoint: https://api.minimax.chat/v1/video_generation
        // Query:    https://api.minimax.chat/v1/query/video_generation?task_id=...
        String endpoint = modelConfig.getApiEndpoint();
        String queryUrl = endpoint.replace("/video_generation", "/query/video_generation");
        
        try {
            // 使用 GET 请求
            try (HttpResponse response = HttpRequest.get(queryUrl + "?task_id=" + taskId)
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(TIMEOUT_MS)
                    .execute()) {

                if (!response.isOk()) {
                    log.warn("Minimax check status failed: {} - {}", response.getStatus(), response.body());
                    return null;
                }

                JSONObject json = JSONUtil.parseObj(response.body());
                // log.debug("Minimax checkStatus response: {}", response.body());
                
                // 检查 base_resp
                if (json.containsKey("base_resp")) {
                    JSONObject baseResp = json.getJSONObject("base_resp");
                    Integer statusCode = baseResp.getInt("status_code");
                    if (statusCode != null && statusCode != 0) {
                        log.warn("Minimax status query error: {}", baseResp.getStr("status_msg"));
                        return null;
                    }
                }

                // 获取任务状态
                String statusStr = json.getStr("status");
                String status = "PROCESSING";
                String videoUrl = null;
                String coverUrl = null;
                Integer usageTokens = null;

                if ("Success".equalsIgnoreCase(statusStr)) {
                    status = "SUCCESS";
                    
                    // 推断文件检索 URL (同 Host)
                    String fileRetrieveUrl = endpoint.replace("/video_generation", "/files/retrieve");
                    
                    
                    // 成功时需要通过 file_id 获取视频 URL
                    String fileId = json.getStr("file_id");
                    if (fileId != null) {
                        videoUrl = retrieveFileUrl(apiKey, fileRetrieveUrl, fileId);
                    }
                    
                    // Fallback: 尝试直接从查询响应获取
                    if (videoUrl == null) {
                         videoUrl = json.getStr("video_url");
                         if (videoUrl == null) videoUrl = json.getStr("download_url");
                    }
                    
                    if (videoUrl == null) {
                        log.warn("Minimax Success but no videoUrl found. Query Resp: {}", json.toString());
                    }
                    
                    // 尝试获取封面
                    String coverFileId = json.getStr("cover_file_id");
                    if (coverFileId != null) {
                        coverUrl = retrieveFileUrl(apiKey, fileRetrieveUrl, coverFileId);
                    }
                    
                    usageTokens = 1; 
                    
                } else if ("Fail".equalsIgnoreCase(statusStr)) {
                    status = "FAILED";
                    log.warn("Minimax task failed: taskId={}", taskId);
                }

                return CreationResponse.builder()
                        .taskId(taskId)
                        .status(status)
                        .mediaUrl(videoUrl)
                        .coverUrl(coverUrl)
                        .usageTokens(usageTokens)
                        .build();
            }

        } catch (Exception e) {
            log.error("Minimax check status error", e);
            return null;
        }
    }

    /**
     * 根据 file_id 获取文件下载 URL
     */
    private String retrieveFileUrl(String apiKey, String retrieveUrl, String fileId) {
        // 定义回退 URL (Legacy Host) - Legacy might use POST, but let's try GET first for everything or logic split.
        // If api.minimax.io requires POST, we handle it. But official doc (minimaxi.com) says GET.
        String fallbackUrl = "https://api.minimax.io/v1/files/retrieve";
        
        String urlToUse = retrieveUrl;
        
        for (int i = 0; i < 2; i++) {
            try {
                // Determine Method based on URL?
                // Official doc (minimaxi.com / likely .chat) -> GET
                // Legacy (.io) -> Often POST.
                
                boolean useGet = !urlToUse.contains("minimax.io"); 
                
                HttpResponse response = null;
                try {
                    if (useGet) {
                        response = HttpRequest.get(urlToUse + "?file_id=" + fileId)
                                .header("Authorization", "Bearer " + apiKey)
                                .timeout(TIMEOUT_MS)
                                .execute();
                    } else {
                        // Legacy .io might accept POST
                        Map<String, Object> body = new HashMap<>();
                        body.put("file_id", fileId);
                        response = HttpRequest.post(urlToUse)
                                .header("Authorization", "Bearer " + apiKey)
                                .header("Content-Type", "application/json")
                                .body(JSONUtil.toJsonStr(body))
                                .timeout(TIMEOUT_MS)
                                .execute();
                    }

                    if (response.getStatus() == 404 && i == 0 && !urlToUse.contains("minimax.io")) {
                        log.warn("Minimax retrieve 404 on {}, trying fallback {}", urlToUse, fallbackUrl);
                        urlToUse = fallbackUrl;
                        continue;
                    }

                    if (!response.isOk()) {
                        log.warn("Minimax file retrieve failed: {} on {}", response.body(), urlToUse);
                        return null; 
                    }

                    JSONObject json = JSONUtil.parseObj(response.body());
                    
                    // 提取文件 URL
                    JSONObject file = json.getJSONObject("file");
                    if (file != null) {
                        return file.getStr("download_url");
                    }
                    
                    if (json.containsKey("download_url")) {
                        return json.getStr("download_url");
                    }
                    
                    log.warn("Minimax retrieve response missing download_url: {}", json);
                    return null;
                } finally {
                    if (response != null) response.close();
                }
            } catch (Exception e) {
                log.error("Failed to retrieve file URL on {}", urlToUse, e);
                if (i == 0 && !urlToUse.contains("minimax.io")) {
                     urlToUse = fallbackUrl;
                     continue;
                }
                return null;
            }
        }
        return null;
    }
}
