package com.huike.video.modules.creation.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.common.enums.TaskTypeEnum;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.common.util.FileStorageUtils;
import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.domain.entity.AiTask;
import com.huike.video.modules.creation.mapper.AiTaskMapper;
import com.huike.video.modules.creation.service.AiModelService;
import com.huike.video.modules.creation.service.CreationService;
import com.huike.video.modules.creation.strategy.StrategyManager;
import com.huike.video.modules.wallet.entity.UserWallet;
import com.huike.video.modules.wallet.mapper.UserWalletMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 创作服务实现 (AiTask)
 */
@Slf4j
@Service
public class CreationServiceImpl implements CreationService {

    private final AiModelService aiModelService;
    private final StrategyManager strategyManager;
    private final AiTaskMapper aiTaskMapper;
    private final UserWalletMapper userWalletMapper;
    private final Executor creationTaskExecutor;

    public CreationServiceImpl(
            AiModelService aiModelService,
            StrategyManager strategyManager,
            AiTaskMapper aiTaskMapper,
            UserWalletMapper userWalletMapper,
            @Qualifier("creationTaskExecutor") Executor creationTaskExecutor) {
        this.aiModelService = aiModelService;
        this.strategyManager = strategyManager;
        this.aiTaskMapper = aiTaskMapper;
        this.userWalletMapper = userWalletMapper;
        this.creationTaskExecutor = creationTaskExecutor;
    }

    // 任务状态常量
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_PROCESSING = 2;
    private static final int STATUS_SUCCESS = 3;
    private static final int STATUS_FAILED = 4;
    private static final int STATUS_CANCELLED = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitTask(String userId, CreationRequest request) {
        log.info("Submitting async task for user={}, modelName={}, modelKey={}", 
                userId, request.getModelName(), request.getModelKey());

        // 1. 获取模型配置 (支持 modelKey 或 modelName)
        AiModel model = resolveModel(request);
        if (model == null) {
            String identifier = request.getModelKey() != null ? request.getModelKey() : request.getModelName();
            throw new BusinessException(30002, "模型未找到: " + identifier);
        }

        // 2. 计算费用并校验余额
        // 注意：这里使用 user prompt 来估算
        BigDecimal cost = calculateCost(model, request.getPrompt());
        UserWallet wallet = getWalletByUserId(userId);
        if (wallet.getBalance().compareTo(cost) < 0) {
            throw new BusinessException(30001, "余额不足，当前余额: " + wallet.getBalance() + ", 需要: " + cost);
        }

        // 3. 预扣费
        deductBalance(userId, cost);

        // 4. 创建任务记录
        String taskId = IdUtil.simpleUUID();
        AiTask task = new AiTask();
        task.setId(taskId);
        task.setUserId(userId);
        
        // 映射任务类型
        task.setTaskType(TaskTypeEnum.fromName(request.getTaskType()).getCode());
        
        // 设置 Prompt
        task.setPrompt(request.getPrompt());
        task.setNegativePrompt(request.getNegativePrompt());
        
        task.setModelId(model.getId());
        task.setStatus(STATUS_PENDING);

        task.setCostToken(cost);
        
        task.setTemplateId(request.getTemplateId());
        task.setPriority(request.getPriority() != null ? request.getPriority() : 5);

        // Input Config
        Map<String, Object> inputConfig = request.getInputConfig();
        if (inputConfig == null) {
            inputConfig = new HashMap<>();
        }
        if (!inputConfig.containsKey("modelKey")) {
            inputConfig.put("modelKey", request.getModelKey());
        }
        task.setInputConfig(inputConfig);
        
        // Output Config (初始化空)
        task.setOutputConfig(new HashMap<>());

        aiTaskMapper.insert(task);

        // 5. 调用策略执行
        try {
            CreationResponse response = strategyManager.executeGeneration(request);

            // 6. 更新任务状态
            if (response.getTaskId() != null) {
                // 第三方异步任务 ID
                task.setExternalTaskId(response.getTaskId());
                task.setStatus(STATUS_PROCESSING);
            } else if (response.getMediaUrl() != null) {
                // 同步返回了结果 (极少见，除非是快速模型)
                task.setResultFilePath(response.getMediaUrl());
                task.setStatus(STATUS_SUCCESS);

                task.setEndTime(LocalDateTime.now());
            }
            
            aiTaskMapper.updateById(task);

        } catch (Exception e) {
            log.error("Task execution failed", e);
            task.setStatus(STATUS_FAILED);
            task.setErrorMessage(e.getMessage());
            aiTaskMapper.updateById(task);
            // 退款
            refundBalance(userId, cost);
            throw new BusinessException(500, "任务执行失败: " + e.getMessage());
        }

        return taskId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreationResponse generateText(String userId, CreationRequest request) {
        log.info("Sync text generation for user={}, modelName={}, modelKey={}", 
                userId, request.getModelName(), request.getModelKey());

        // 1. 获取模型配置 (支持 modelKey 或 modelName)
        AiModel model = resolveModel(request);
        if (model == null) {
            String identifier = request.getModelKey() != null ? request.getModelKey() : request.getModelName();
            throw new BusinessException(500, "Model not found: " + identifier);
        }

        // 2. 校验余额 (预估)
        BigDecimal estimatedCost = calculateCost(model, request.getPrompt());
        UserWallet wallet = getWalletByUserId(userId);
        if (wallet.getBalance().compareTo(estimatedCost) < 0) {
            throw new BusinessException(30001, "余额不足");
        }

        // 3. 调用策略
        CreationResponse response = strategyManager.executeGeneration(request);

        // 4. 按实际消耗扣费
        BigDecimal actualCost = calculateActualCost(model, response.getUsageTokens());
        deductBalance(userId, actualCost);

        // 5. 异步记录到 AiTask 表 (Log for Audit)
        // 虽然是同步请求，但也最好留底，特别是为了流水查询
        recordSyncTask(userId, request, model, response, actualCost);

        return response;
    }

    @Override
    public AiTask getTaskStatus(String taskId) {
        AiTask task = aiTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(500, "Task not found: " + taskId);
        }

        // Lazy Sync: 如果任务还在处理中，查询厂商状态
        if (task.getStatus() == STATUS_PENDING || task.getStatus() == STATUS_PROCESSING) {
            try {
                CreationResponse syncResponse = strategyManager.checkStatus(task);
                
                if (syncResponse != null && syncResponse.getStatus() != null) {
                    String remoteStatus = syncResponse.getStatus();
                    
                    if ("SUCCESS".equals(remoteStatus)) {
                         // 厂商已完成 -> 异步下载并更新 DB
                         handleDownloadAndComplete(task, syncResponse);
                    } else if ("FAILED".equals(remoteStatus)) {
                        task.setStatus(STATUS_FAILED);
                        task.setErrorMessage(syncResponse.getErrorMessage() != null ? 
                                syncResponse.getErrorMessage() : "Remote provider reported failure.");
                        task.setEndTime(LocalDateTime.now());
                        aiTaskMapper.updateById(task);
                        // TODO: 触发退款
                    }
                }
            } catch (Exception e) {
                log.warn("Lazy sync failed for task {}: {}", taskId, e.getMessage());
            }
        }

        return task;
    }

    /**
     * 异步处理下载和完成
     */
    private void handleDownloadAndComplete(AiTask task, CreationResponse response) {
        creationTaskExecutor.execute(() -> {
            try {
                String mediaUrl = response.getMediaUrl();
                String coverUrl = response.getCoverUrl();
                
                // determine subDir and default extension based on task type
                String subDir = "generations";
                String defaultExt = ".mp4";
                
                Integer taskType = task.getTaskType();
                if (taskType != null) {
                    if (taskType == 2) { // Image
                        subDir = "images";
                        defaultExt = ".png";
                    } else if (taskType == 3 || taskType == 4) { // Video
                        subDir = "videos";
                        defaultExt = ".mp4";
                    }
                }
                
                // 1. 下载文件 (视频或图片)
                if (mediaUrl != null && mediaUrl.startsWith("http")) {
                    String extension = extractExtension(mediaUrl, defaultExt);
                    
                    String fileName = task.getId() + extension;
                    String localPath = FileStorageUtils.downloadFile(mediaUrl, fileName, subDir);
                    task.setResultFilePath(localPath);
                    
                    // 尝试获取文件大小 (FileStorageUtils downloadFile 暂未返回大小，这里简略)
                }
                
                // 2. 下载封面
                if (coverUrl != null && coverUrl.startsWith("http")) {
                    String extension = extractExtension(coverUrl, ".jpg");
                    String fileName = task.getId() + "_cover" + extension;
                    String localCoverPath = FileStorageUtils.downloadFile(coverUrl, fileName, "covers");
                    task.setResultCoverPath(localCoverPath);
                } else {
                    task.setResultCoverPath(coverUrl);
                }

                // 3. 更新为成功
                task.setStatus(STATUS_SUCCESS);
                // task.setProgress(100); // 字段已移除
                task.setEndTime(LocalDateTime.now());
                
                aiTaskMapper.updateById(task);
                log.info("Task {} completed and downloaded to {}/{}", task.getId(), subDir, task.getResultFilePath());
                
            } catch (Exception e) {
                log.error("Failed to download result for task {}", task.getId(), e);
                task.setStatus(STATUS_FAILED);
                task.setErrorMessage("下载失败: " + e.getMessage());
                task.setEndTime(LocalDateTime.now());
                aiTaskMapper.updateById(task);
            }
        });
    }

    private String extractExtension(String url, String defaultExt) {
        try {
            int lastDot = url.lastIndexOf(".");
            int lastSlash = url.lastIndexOf("/");
            if (lastDot > lastSlash && lastDot < url.length() - 1) {
                String ext = url.substring(lastDot);
                int queryStart = ext.indexOf("?");
                if (queryStart > 0) ext = ext.substring(0, queryStart);
                if (ext.length() <= 5) return ext;
            }
        } catch (Exception ignored) {}
        return defaultExt;
    }

    @Override
    public int estimateTokenCost(String prompt) {
        // 简单估算: 1 字符 ≈ 1.5 tokens，加上 1000 输出 tokens
        return (int) (prompt.length() * 1.5) + 1000;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelTask(String taskId, String userId) {
        AiTask task = aiTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(500, "任务不存在: " + taskId);
        }
        if (!task.getUserId().equals(userId)) {
            throw new BusinessException(500, "无权操作该任务");
        }

        if (task.getStatus() != STATUS_PENDING && task.getStatus() != STATUS_PROCESSING) {
            throw new BusinessException(500, "当前状态不可取消");
        }

        // 尝试调用第三方取消接口
        boolean remoteCancel = strategyManager.cancelTask(task);
        log.info("Task {} remote cancel result: {}", taskId, remoteCancel);

        task.setStatus(STATUS_CANCELLED);
        task.setEndTime(LocalDateTime.now());
        aiTaskMapper.updateById(task);
        return true;
    }

    // ========== 私有方法 ==========

    private void recordSyncTask(String userId, CreationRequest request, AiModel model, CreationResponse response, BigDecimal cost) {
        try {
            AiTask task = new AiTask();
            task.setId(IdUtil.simpleUUID());
            task.setUserId(userId);
            task.setTaskType(TaskTypeEnum.fromName(request.getTaskType()).getCode());
            task.setModelId(model.getId());
            task.setPrompt(request.getPrompt());
            task.setStatus(STATUS_SUCCESS); // 同步任务直接成功
            task.setCostToken(cost);
            // task.setStartTime(LocalDateTime.now()); // 使用 createdAt
            task.setEndTime(LocalDateTime.now());
            task.setInputConfig(new HashMap<>()); // 简化
            
            // 结果内容 (Text) 不一定要存库，或者存到 outputConfig
            // 这里暂不存 Text Content 到 DB 以免过大
            
            aiTaskMapper.insert(task);
        } catch (Exception e) {
            log.warn("Failed to record sync task log", e);
        }
    }

    private BigDecimal calculateActualCost(AiModel model, Integer actualTokens) {
        BigDecimal unitPrice = model.getUnitPrice();
        if (unitPrice == null || actualTokens == null) {
            return BigDecimal.ZERO;
        }

        if (model.getBillingMode() == 1) {
            return unitPrice;
        } else {
            return unitPrice.multiply(BigDecimal.valueOf(actualTokens))
                    .divide(BigDecimal.valueOf(1_000_000), 6, BigDecimal.ROUND_HALF_UP);
        }
    }

    private UserWallet getWalletByUserId(String userId) {
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId);
        UserWallet wallet = userWalletMapper.selectOne(wrapper);
        if (wallet == null) {
            throw new BusinessException(30003, "用户钱包未找到: " + userId);
        }
        return wallet;
    }

    private BigDecimal calculateCost(AiModel model, String prompt) {
        BigDecimal unitPrice = model.getUnitPrice();
        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO; 
        }

        if (model.getBillingMode() == 1) {
            // 按次
            return unitPrice;
        } else {
            // 按 Token
            int tokens = estimateTokenCost(prompt);
            return unitPrice.multiply(BigDecimal.valueOf(tokens))
                    .divide(BigDecimal.valueOf(1_000_000), 6, BigDecimal.ROUND_HALF_UP);
        }
    }

    private void deductBalance(String userId, BigDecimal amount) {
        LambdaUpdateWrapper<UserWallet> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId)
               .setSql("balance = balance - " + amount)
               .setSql("total_consumed = total_consumed + " + amount);
        userWalletMapper.update(null, wrapper);
    }

    private void refundBalance(String userId, BigDecimal amount) {
        LambdaUpdateWrapper<UserWallet> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserWallet::getUserId, userId)
               .setSql("balance = balance + " + amount)
               .setSql("total_consumed = total_consumed - " + amount);
        userWalletMapper.update(null, wrapper);
    }

    /**
     * 根据 modelKey 或 modelName 解析模型配置
     * 优先使用 modelKey，如果没有则使用 modelName
     */
    private AiModel resolveModel(CreationRequest request) {
        // 优先使用 modelKey
        if (request.getModelKey() != null && !request.getModelKey().isBlank()) {
            return aiModelService.getActiveModelByKey(request.getModelKey());
        }
        // 其次使用 modelName
        if (request.getModelName() != null && !request.getModelName().isBlank()) {
            AiModel model = aiModelService.getActiveModelByName(request.getModelName());
            if (model != null) {
                // 回填 modelKey 到 request，供后续策略使用
                request.setModelKey(model.getModelKey());
            }
            return model;
        }
        return null;
    }
}
