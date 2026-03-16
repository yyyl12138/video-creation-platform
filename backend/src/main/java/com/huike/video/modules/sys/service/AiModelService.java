package com.huike.video.modules.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.sys.dto.AiModelRequest;
import com.huike.video.modules.sys.vo.AiModelResponse;

public interface AiModelService {
    
    /**
     * 分页查询模型列表 (带API Key 脱敏)
     */
    Page<AiModelResponse> getModelPage(int page, int size, String type, String provider);

    /**
     * 获取单个模型详情 (带脱敏)
     */
    AiModelResponse getModelById(Long modelId);

    /**
     * 新增或更新模型配置
     * @return 模型的ID
     */
    Long saveOrUpdateModel(AiModelRequest request);

    /**
     * 更新启用状态
     */
    Boolean updateModelStatus(Long modelId, Boolean isActive);
}
