package com.huike.video.modules.creation.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.huike.video.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用 OpenAI 协议客户端
 * 适用于 DeepSeek, Qwen-Turbo (兼容模式), Ollama, LocalLLM 等
 */
@Slf4j
public class OpenAiClient {

    private static final int TIMEOUT_MS = 60000; // 60s 超时

    /**
     * Chat Completion 响应结果
     */
    @Data
    @AllArgsConstructor
    public static class ChatResult {
        private String content;
        private Integer totalTokens; // usage.total_tokens
    }

    /**
     * 调用 Chat Completion 接口
     * @param apiUrl 接口地址 (如 https://api.deepseek.com/chat/completions)
     * @param apiKey API Key
     * @param model 模型名称 (如 deepseek-chat)
     * @param prompt 提示词
     * @param temperature 温度 (0-2)
     * @return ChatResult 包含生成内容和 Token 消耗
     */
    public static ChatResult chatCompletion(String apiUrl, String apiKey, String model, String prompt, Double temperature) {
        // 1. 构建请求体
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", new Object[]{message});
        body.put("temperature", temperature != null ? temperature : 0.7);
        body.put("stream", false);

        String jsonBody = JSONUtil.toJsonStr(body);
        log.info("Calling LLM API: {}, model={}", apiUrl, model);

        // 2. 发送请求
        try (HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .timeout(TIMEOUT_MS)
                .execute()) {

            // 3. 处理响应
            String result = response.body();
            if (!response.isOk()) {
                log.error("LLM API Error: status={}, body={}", response.getStatus(), result);
                throw new BusinessException(500, "LLM Request Failed: " + response.getStatus());
            }

            // 4. 解析结果 (Standard OpenAI Format)
            JSONObject json = JSONUtil.parseObj(result);
            JSONArray choices = json.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new BusinessException(500, "Empty choices from LLM response");
            }

            String content = choices.getJSONObject(0).getJSONObject("message").getStr("content");

            // 5. 解析 usage
            Integer totalTokens = 0;
            JSONObject usage = json.getJSONObject("usage");
            if (usage != null) {
                totalTokens = usage.getInt("total_tokens", 0);
            }

            return new ChatResult(content, totalTokens);

        } catch (Exception e) {
            log.error("LLM Exception", e);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new BusinessException(500, "LLM Connection Error: " + e.getMessage());
        }
    }
}

