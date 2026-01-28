package com.huike.video.modules.creation.service;

import com.huike.video.modules.creation.domain.dto.CreationRequest;
import com.huike.video.modules.creation.domain.dto.CreationResponse;
import com.huike.video.modules.creation.domain.entity.AiTask;

/**
 * 创作服务接口
 */
public interface CreationService {

    /**
     * 提交异步任务 (视频/图片生成)
     * @param userId 用户ID
     * @param request 请求参数
     * @return 任务ID
     */
    String submitTask(String userId, CreationRequest request);

    /**
     * 同步文本生成
     * @param userId 用户ID
     * @param request 请求参数
     * @return 生成结果
     */
    CreationResponse generateText(String userId, CreationRequest request);

    /**
     * 查询任务状态 (包含 Lazy Sync)
     * @param taskId 任务ID
     * @return 任务信息
     */
    AiTask getTaskStatus(String taskId);

    /**
     * 预估 Token 消耗
     * @param prompt 提示词
     * @return 预估 Token 数
     */
    int estimateTokenCost(String prompt);

    /**
     * 取消任务
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelTask(String taskId, String userId);
}
