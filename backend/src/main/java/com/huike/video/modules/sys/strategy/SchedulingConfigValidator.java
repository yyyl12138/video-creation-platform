package com.huike.video.modules.sys.strategy;

import com.huike.video.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 调度/并发参数配置校验
 */
@Component
public class SchedulingConfigValidator implements ConfigValidatorStrategy {

    @Override
    public String getSupportedCategory() {
        return "SCHEDULING";
    }

    @Override
    public void validate(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) {
            throw new BusinessException(11001, "配置信息不能为空");
        }
        
        // 校验数字类型的字段不能为负数
        validatePositiveNumber(configs, "maxConcurrentGeneration");
        validatePositiveNumber(configs, "maxConcurrentTranscoding");
        validatePositiveNumber(configs, "taskQueueLimit");
    }

    private void validatePositiveNumber(Map<String, String> configs, String key) {
        String val = configs.get(key);
        if (val != null && !val.isBlank()) {
            try {
                int num = Integer.parseInt(val);
                if (num < 0) {
                    throw new BusinessException(11003, key + " 必须大于等于0");
                }
            } catch (NumberFormatException e) {
                throw new BusinessException(11004, key + " 必须是有效的数字");
            }
        }
    }
}
