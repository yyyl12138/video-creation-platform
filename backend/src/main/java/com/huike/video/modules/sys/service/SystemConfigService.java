package com.huike.video.modules.sys.service;

import java.util.Map;

public interface SystemConfigService {

    /**
     * 按类别获取配置详情 (从 Redis 读取)
     */
    Map<Object, Object> getConfigByCategory(String category);

    /**
     * 批量更新特定类别的配置 (带策略校验并刷新 Redis)
     */
    Boolean updateConfigCategory(String category, Map<String, String> configs);
    
    /**
     * 高频读取单一配置项的方法
     */
    String getConfigValue(String category, String key);
}
