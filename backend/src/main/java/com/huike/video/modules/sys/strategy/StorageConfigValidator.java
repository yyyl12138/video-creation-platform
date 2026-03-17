package com.huike.video.modules.sys.strategy;

import com.huike.video.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 存储参数配置校验
 */
@Component
public class StorageConfigValidator implements ConfigValidatorStrategy {

    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("^https?://[\\w.\\-]+.*$");

    @Override
    public String getSupportedCategory() {
        return "STORAGE";
    }

    @Override
    public void validate(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) {
            throw new BusinessException(11001, "配置信息不能为空");
        }
        
        // 校验 endpoint 必须是 http 网址
        String endpoint = configs.get("endpoint");
        if (endpoint != null && !endpoint.isBlank()) {
            if (!HTTP_URL_PATTERN.matcher(endpoint).matches()) {
                throw new BusinessException(11002, "Endpoint 格式不合法");
            }
        }
    }
}
