package com.huike.video.modules.sys.strategy;

import java.util.Map;

/**
 * 系统配置校验策略接口
 */
public interface ConfigValidatorStrategy {
    
    /**
     * @return 该策略支持的配置分类 (大写)
     */
    String getSupportedCategory();
    
    /**
     * 校验配置MAP是否符合规范
     * @param configs 待更新的配置数据
     */
    void validate(Map<String, String> configs);
}
