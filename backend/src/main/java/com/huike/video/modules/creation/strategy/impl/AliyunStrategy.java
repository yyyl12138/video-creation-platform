package com.huike.video.modules.creation.strategy.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.domain.entity.AiTask;
import com.huike.video.modules.creation.strategy.ModelStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * 阿里云策略 (DashScope)
 * 支持 TEXT (qwen-flash), IMAGE (wanx), VIDEO (wan2.1) 三种 model_type
 */
@Slf4j
@Component
public class AliyunStrategy implements ModelStrategy {

    private static final String PROVIDER_NAME = "ALIYUN";

    @Override
    public String getProvider() {
        return PROVIDER_NAME;
    }

    @Override
    public CreationResponse generate(CreationRequest request, AiModel modelConfig) {
        String modelType = modelConfig.getModelType();
        log.info("AliyunStrategy executing, modelType={}, modelKey={}", modelType, modelConfig.getModelKey());

        switch (modelType) {
            case "TEXT":
                return generateText(request, modelConfig);
            case "VIDEO":
                return generateVideo(request, modelConfig);
            case "IMAGE":
                return generateImage(request, modelConfig);
            default:
                throw new BusinessException(500, "Unsupported model_type for Aliyun: " + modelType);
        }
    }

    /**
     * 文本生成 (Qwen)
     */
    private CreationResponse generateText(CreationRequest request, AiModel modelConfig) {
        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");

        try {
            Generation gen = new Generation();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(request.getPrompt())
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model(modelConfig.getModelKey()) // e.g. qwen-flash
                    .messages(Collections.singletonList(userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            String content = result.getOutput().getChoices().get(0).getMessage().getContent();
            Integer totalTokens = result.getUsage().getTotalTokens();

            return CreationResponse.builder()
                    .content(content)
                    .usageTokens(totalTokens)
                    .build();

        } catch (Exception e) {
            log.error("Aliyun TEXT generation failed", e);
            throw new BusinessException(500, "Qwen API Error: " + e.getMessage());
        }
    }

    /**
     * 视频生成 (Wan2.1 / Wanx) - 异步任务
     * 使用 DashScope SDK VideoSynthesis
     */
    private CreationResponse generateVideo(CreationRequest request, AiModel modelConfig) {
        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");

        try {
            VideoSynthesis videoSynthesis = new VideoSynthesis();
            
            // 构建参数
            VideoSynthesisParam.VideoSynthesisParamBuilder<?, ?> paramBuilder = VideoSynthesisParam.builder()
                    .apiKey(apiKey)
                    .model(modelConfig.getModelKey()) // e.g. wanx-v1, wan2.1-t2v-plus
                    .prompt(request.getPrompt());
            
            // 可选参数: 负面提示词
            if (request.getNegativePrompt() != null) {
                paramBuilder.negativePrompt(request.getNegativePrompt());
            }
            
            // 可选参数: 尺寸 (从 inputConfig 获取)
            Map<String, Object> extraMap = request.getExtraMap();
            if (extraMap != null) {
                if (extraMap.containsKey("size")) {
                    paramBuilder.size((String) extraMap.get("size"));
                }
            }

            VideoSynthesisParam param = paramBuilder.build();
            
            // 使用 asyncCall 提交异步任务
            VideoSynthesisResult result = videoSynthesis.asyncCall(param);
            
            // 获取任务 ID
            String taskId = result.getOutput().getTaskId();
            log.info("Aliyun video task submitted, taskId={}", taskId);

            return CreationResponse.builder()
                    .taskId(taskId)
                    .status("PROCESSING")
                    .build();

        } catch (Exception e) {
            log.error("Aliyun VIDEO generation failed", e);
            throw new BusinessException(500, "Wanx API Error: " + e.getMessage());
        }
    }

    /**
     * 图片生成 (Wanx) - 异步任务
     * 使用 DashScope SDK ImageSynthesis
     */
    private CreationResponse generateImage(CreationRequest request, AiModel modelConfig) {
        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");

        try {
            ImageSynthesis imageSynthesis = new ImageSynthesis();
            
            // 构建参数
            ImageSynthesisParam.ImageSynthesisParamBuilder<?, ?> paramBuilder = ImageSynthesisParam.builder()
                    .apiKey(apiKey)
                    .model(modelConfig.getModelKey()) // e.g. wanx-v1, wanx2.1-t2i-turbo
                    .prompt(request.getPrompt())
                    .n(1); // 生成1张图片
            
            // 可选参数: 负面提示词
            if (request.getNegativePrompt() != null) {
                paramBuilder.negativePrompt(request.getNegativePrompt());
            }
            
            // 可选参数: 尺寸 (从 inputConfig 获取)
            Map<String, Object> extraMap = request.getExtraMap();
            if (extraMap != null) {
                if (extraMap.containsKey("size")) {
                    paramBuilder.size((String) extraMap.get("size"));
                }
            }

            ImageSynthesisParam param = paramBuilder.build();
            
            // 使用 asyncCall 提交异步任务
            ImageSynthesisResult result = imageSynthesis.asyncCall(param);
            
            // 获取任务 ID
            String taskId = result.getOutput().getTaskId();
            log.info("Aliyun image task submitted, taskId={}", taskId);

            return CreationResponse.builder()
                    .taskId(taskId)
                    .status("PROCESSING")
                    .build();

        } catch (Exception e) {
            log.error("Aliyun IMAGE generation failed", e);
            throw new BusinessException(500, "Wanx API Error: " + e.getMessage());
        }
    }

    @Override
    public CreationResponse checkStatus(AiTask task, AiModel modelConfig) {
        String taskId = task.getExternalTaskId();
        if (taskId == null) return null;

        Map<String, Object> apiConfig = modelConfig.getApiConfig();
        String apiKey = (String) apiConfig.get("apiKey");
        
        // 2: 文生图
        if (task.getTaskType() != null && task.getTaskType() == 2) {
            try {
                ImageSynthesis imageSynthesis = new ImageSynthesis();
                // 图片任务需注意：DashScope ImageSynthesis asyncCall 返回的 taskId 也是通过 fetch 查询
                ImageSynthesisResult result = imageSynthesis.fetch(taskId, apiKey);
                
                if (result == null || result.getOutput() == null) return null;
                
                String statusStr = result.getOutput().getTaskStatus();
                log.info("Aliyun IMAGE task status: taskId={}, status={}", taskId, statusStr);
                
                String status = "PROCESSING";
                String imageUrl = null;

                if ("SUCCEEDED".equalsIgnoreCase(statusStr)) {
                    status = "SUCCESS";
                    // 结果列表
                     if (result.getOutput().getResults() != null && !result.getOutput().getResults().isEmpty()) {
                        // 通常 Map 中包含 'url'
                        imageUrl = result.getOutput().getResults().get(0).get("url");
                    }
                } else if ("FAILED".equalsIgnoreCase(statusStr) || "CANCELED".equalsIgnoreCase(statusStr)) {
                    status = "FAILED";
                    log.warn("Aliyun IMAGE task failed: {}", result.getOutput().getMessage());
                }

                return CreationResponse.builder()
                        .taskId(taskId)
                        .status(status)
                        .mediaUrl(imageUrl) // 图片放入 mediaUrl
                        .build();

            } catch (Exception e) {
                log.error("Aliyun IMAGE checkStatus error for taskId={}", taskId, e);
                return null;
            }
        } else {
            // 视频任务 (3, 4)
            try {
                VideoSynthesis videoSynthesis = new VideoSynthesis();
                
                // 使用 fetch 方法查询任务状态
                VideoSynthesisResult result = videoSynthesis.fetch(taskId, apiKey);
                
                if (result == null || result.getOutput() == null) {
                    log.warn("Aliyun checkStatus returned null for taskId={}", taskId);
                    return null;
                }
    
                String statusStr = result.getOutput().getTaskStatus();
                log.info("Aliyun VIDEO task status: taskId={}, status={}", taskId, statusStr);
                
                String status = "PROCESSING";
                String videoUrl = null;
                String coverUrl = null;
                Integer usageTokens = null;
    
                // 状态映射: PENDING, RUNNING, SUCCEEDED, FAILED, CANCELED
                if ("SUCCEEDED".equalsIgnoreCase(statusStr)) {
                    status = "SUCCESS";
                    videoUrl = result.getOutput().getVideoUrl();
                    // DashScope SDK 可能不返回封面 URL，设为 null
                    coverUrl = null;
                    // 尝试获取 usage
                    if (result.getUsage() != null) {
                        // DashScope VideoSynthesis 可能不返回 token 数，设为 1 表示计次
                        usageTokens = 1;
                    }
                } else if ("FAILED".equalsIgnoreCase(statusStr) || "CANCELED".equalsIgnoreCase(statusStr)) {
                    status = "FAILED";
                    log.warn("Aliyun VIDEO task failed: taskId={}", taskId);
                }
    
                return CreationResponse.builder()
                        .taskId(taskId)
                        .status(status)
                        .mediaUrl(videoUrl)
                        .coverUrl(coverUrl)
                        .usageTokens(usageTokens)
                        .build();
    
            } catch (Exception e) {
                log.error("Aliyun VIDEO checkStatus error for taskId={}", taskId, e);
                return null;
            }
        }
    }
}
