package com.huike.video.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.sys.dto.AiModelRequest;
import com.huike.video.modules.sys.entity.AiModel;
import com.huike.video.modules.sys.mapper.AiModelMapper;
import com.huike.video.modules.sys.service.AiModelService;
import com.huike.video.modules.sys.vo.AiModelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelServiceImpl implements AiModelService {

    private final AiModelMapper aiModelMapper;

    @Override
    public Page<AiModelResponse> getModelPage(int page, int size, String type, String provider) {
        Page<AiModel> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(type), AiModel::getModelType, type);
        wrapper.eq(StringUtils.hasText(provider), AiModel::getProvider, provider);
        wrapper.eq(AiModel::getIsDeleted, false);
        wrapper.orderByDesc(AiModel::getCreatedAt);

        Page<AiModel> modelPage = aiModelMapper.selectPage(pageParam, wrapper);

        List<AiModelResponse> responseList = modelPage.getRecords().stream()
                .map(this::convertToResponseWithMask)
                .collect(Collectors.toList());

        Page<AiModelResponse> resultPage = new Page<>(modelPage.getCurrent(), modelPage.getSize(), modelPage.getTotal());
        resultPage.setRecords(responseList);
        return resultPage;
    }

    @Override
    public AiModelResponse getModelById(Long modelId) {
        AiModel model = aiModelMapper.selectById(modelId);
        if (model == null || model.getIsDeleted()) {
            throw new BusinessException(10012, "模型不存在或已删除");
        }
        return convertToResponseWithMask(model);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateModel(AiModelRequest request) {
        AiModel model = new AiModel();
        BeanUtils.copyProperties(request, model);
        
        if (request.getModelId() != null) {
            model.setId(request.getModelId());
            AiModel exist = aiModelMapper.selectById(request.getModelId());
            if (exist == null || exist.getIsDeleted()) {
                throw new BusinessException(10012, "模型不存在");
            }
            aiModelMapper.updateById(model);
        } else {
            // 新增时处理可选字段默认值
            if (model.getIsActive() == null) {
                model.setIsActive(true);
            }
            aiModelMapper.insert(model);
        }
        return model.getId();
    }

    @Override
    public Boolean updateModelStatus(Long modelId, Boolean isActive) {
        int updated = aiModelMapper.update(null, new LambdaUpdateWrapper<AiModel>()
                .eq(AiModel::getId, modelId)
                .set(AiModel::getIsActive, isActive));
        return updated > 0;
    }

    /**
     * 将实体转换为脱敏响应 VO
     */
    private AiModelResponse convertToResponseWithMask(AiModel model) {
        AiModelResponse resp = new AiModelResponse();
        BeanUtils.copyProperties(model, resp);
        resp.setModelId(model.getId());

        if (model.getApiConfig() != null && !model.getApiConfig().isEmpty()) {
            Map<String, Object> maskedConfig = new HashMap<>(model.getApiConfig());
            // 脱敏 apiKey
            if (maskedConfig.containsKey("apiKey")) {
                Object apiKeyObj = maskedConfig.get("apiKey");
                if (apiKeyObj instanceof String) {
                    maskedConfig.put("apiKey", maskKey((String) apiKeyObj));
                }
            }
            // 脱敏 secretKey (可能有)
            if (maskedConfig.containsKey("secretKey")) {
                Object secretObj = maskedConfig.get("secretKey");
                if (secretObj instanceof String) {
                    maskedConfig.put("secretKey", maskKey((String) secretObj));
                }
            }
            resp.setApiConfig(maskedConfig);
        }
        return resp;
    }

    /**
     * API Key 脱敏逻辑: 保留前4后4，中间用 * 代替
     */
    private String maskKey(String key) {
        if (!StringUtils.hasText(key)) return key;
        int len = key.length();
        if (len <= 8) {
            return "********";
        }
        return key.substring(0, 4) + "******" + key.substring(len - 4);
    }
}
