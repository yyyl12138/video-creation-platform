package com.huike.video.modules.sys.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huike.video.modules.sys.entity.SystemConfig;
import com.huike.video.modules.sys.mapper.SystemConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置的 Redis 缓存管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigCacheManager implements ApplicationRunner {

    private final StringRedisTemplate stringRedisTemplate;
    private final SystemConfigMapper systemConfigMapper;

    private static final String REDIS_KEY_PREFIX = "sys:configs:";

    /**
     * 项目启动时全量加载配置到 Redis
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("--- 开始加载系统配置到 Redis ---");
        List<SystemConfig> allConfigs = systemConfigMapper.selectList(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getIsDeleted, false)
        );
        if (allConfigs == null || allConfigs.isEmpty()) {
            log.info("--- 系统配置表为空，跳过加载 ---");
            return;
        }

        // 按 category 分组
        Map<String, List<SystemConfig>> groupedConfigs = allConfigs.stream()
                .collect(Collectors.groupingBy(SystemConfig::getCategory));

        groupedConfigs.forEach((category, configs) -> {
            String redisKey = REDIS_KEY_PREFIX + category;
            // 先清理旧缓存
            stringRedisTemplate.delete(redisKey);
            // 组装 Hash 数据
            Map<String, String> hashData = configs.stream()
                    .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
            // 写入 Redis
            stringRedisTemplate.opsForHash().putAll(redisKey, hashData);
            log.info("加载配置分类 [{}], 共 {} 项", category, hashData.size());
        });
        log.info("--- 系统配置加载完成 ---");
    }

    /**
     * 刷新特定分类的缓存（用于后台修改配置后调用）
     * @param category 配置分类
     */
    public void refreshCategory(String category) {
        log.info("刷新配置缓存分类: {}", category);
        List<SystemConfig> configs = systemConfigMapper.selectList(
                new LambdaQueryWrapper<SystemConfig>()
                        .eq(SystemConfig::getCategory, category)
                        .eq(SystemConfig::getIsDeleted, false)
        );

        String redisKey = REDIS_KEY_PREFIX + category;
        stringRedisTemplate.delete(redisKey);

        if (configs != null && !configs.isEmpty()) {
            Map<String, String> hashData = configs.stream()
                    .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue));
            stringRedisTemplate.opsForHash().putAll(redisKey, hashData);
        }
    }

    /**
     * 获取指定分类下的所有配置
     */
    public Map<Object, Object> getCategoryConfigs(String category) {
        String redisKey = REDIS_KEY_PREFIX + category;
        return stringRedisTemplate.opsForHash().entries(redisKey);
    }

    /**
     * 获取指定配置项的值
     */
    public String getConfigValue(String category, String key) {
        String redisKey = REDIS_KEY_PREFIX + category;
        Object value = stringRedisTemplate.opsForHash().get(redisKey, key);
        return value != null ? value.toString() : null;
    }
}
