package com.huike.video.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.sys.cache.ConfigCacheManager;
import com.huike.video.modules.sys.entity.SystemConfig;
import com.huike.video.modules.sys.mapper.SystemConfigMapper;
import com.huike.video.modules.sys.service.SystemConfigService;
import com.huike.video.modules.sys.strategy.ConfigValidatorStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    private final ConfigCacheManager configCacheManager;
    private final List<ConfigValidatorStrategy> validators;

    @Override
    public Map<Object, Object> getConfigByCategory(String category) {
        return configCacheManager.getCategoryConfigs(category.toUpperCase());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConfigCategory(String category, Map<String, String> configs) {
        String upperCategory = category.toUpperCase();

        // 1. 查找对应的校验策略并执行
        ConfigValidatorStrategy validator = validators.stream()
                .filter(v -> v.getSupportedCategory().equals(upperCategory))
                .findFirst()
                .orElse(null);

        if (validator != null) {
            validator.validate(configs);
        } else {
            log.warn("未找到针对分类 [{}] 的特定校验逻辑, 执行直接更新", upperCategory);
        }

        // 2. 遍历更新数据库
        // 注意: 这里假设 DB 初始化了基础数据，如果遇到新的 Key 则插入
        configs.forEach((key, value) -> {
            int updated = systemConfigMapper.update(null, new LambdaUpdateWrapper<SystemConfig>()
                    .eq(SystemConfig::getCategory, upperCategory)
                    .eq(SystemConfig::getConfigKey, key)
                    .set(SystemConfig::getConfigValue, value));

            if (updated == 0) {
                // 如果更新失败说明没这条记录，则新增
                SystemConfig newConfig = new SystemConfig();
                newConfig.setCategory(upperCategory);
                newConfig.setConfigKey(key);
                newConfig.setConfigValue(value);
                newConfig.setConfigType("STRING"); // 默认类型
                newConfig.setIsPublic(false);
                newConfig.setCreatedBy("ADMIN");
                systemConfigMapper.insert(newConfig);
            }
        });

        // 3. 刷新该分类的 Redis 缓存
        configCacheManager.refreshCategory(upperCategory);

        return true;
    }

    @Override
    public String getConfigValue(String category, String key) {
        return configCacheManager.getConfigValue(category.toUpperCase(), key);
    }
}
