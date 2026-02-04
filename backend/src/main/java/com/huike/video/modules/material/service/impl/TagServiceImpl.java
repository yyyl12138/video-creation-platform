package com.huike.video.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.creation.domain.entity.AiModel;
import com.huike.video.modules.creation.service.AiModelService;
import com.huike.video.modules.creation.utils.OpenAiClient;
import com.huike.video.modules.material.entity.AudioMaterial;
import com.huike.video.modules.material.entity.ImageMaterial;
import com.huike.video.modules.material.entity.VideoMaterial;
import com.huike.video.modules.material.mapper.AudioMaterialMapper;
import com.huike.video.modules.material.mapper.ImageMaterialMapper;
import com.huike.video.modules.material.mapper.VideoMaterialMapper;
import com.huike.video.modules.material.service.TagService;
import com.huike.video.modules.material.vo.AiTagPredictVO;
import com.huike.video.modules.material.vo.HotTagsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务实现 - 符合 API 文档规范 2.1-2.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final ImageMaterialMapper imageMaterialMapper;
    private final VideoMaterialMapper videoMaterialMapper;
    private final AudioMaterialMapper audioMaterialMapper;
    private final AiModelService aiModelService;

    // AI 标签识别使用的模型 Key (GLM-flash 免费)
    private static final String TAG_PREDICT_MODEL_KEY = "glm-4.7-flash";

    /**
     * 获取热门标签/分类
     * 从数据库统计标签使用频率
     */
    @Override
    public HotTagsVO getHotTags(String type) {
        HotTagsVO result = new HotTagsVO();
        
        // 统计分类
        Set<String> categories = new LinkedHashSet<>();
        // 统计标签频率
        Map<String, Integer> tagCountMap = new HashMap<>();

        // 根据类型查询
        if (!StringUtils.hasText(type) || "IMAGE".equalsIgnoreCase(type)) {
            collectFromImages(categories, tagCountMap);
        }
        if (!StringUtils.hasText(type) || "VIDEO".equalsIgnoreCase(type)) {
            collectFromVideos(categories, tagCountMap);
        }
        if (!StringUtils.hasText(type) || "AUDIO".equalsIgnoreCase(type)) {
            collectFromAudios(tagCountMap);
        }

        // 转换为响应结构
        result.setCategories(new ArrayList<>(categories));
        
        // 按频率排序，取前20个
        List<HotTagsVO.HotTagItem> hotTags = tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(20)
                .map(e -> new HotTagsVO.HotTagItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        result.setHotTags(hotTags);

        return result;
    }

    /**
     * AI 自动识别标签 (使用 GLM-flash 模型)
     */
    @Override
    public AiTagPredictVO predictTags(String materialUrl, String type) {
        AiTagPredictVO result = new AiTagPredictVO();
        
        try {
            // 1. 获取 GLM-flash 模型配置
            AiModel model = aiModelService.getActiveModelByKey(TAG_PREDICT_MODEL_KEY);
            if (model == null) {
                log.warn("GLM-flash 模型未配置或未启用，使用默认标签");
                result.setSuggestedTags(getDefaultTags(type));
                return result;
            }

            // 2. 构建提示词
            String prompt = buildTagPredictPrompt(materialUrl, type);

            // 3. 调用 LLM
            Map<String, Object> apiConfig = model.getApiConfig();
            String apiKey = (String) apiConfig.get("apiKey");
            String apiUrl = model.getApiEndpoint();

            OpenAiClient.ChatResult chatResult = OpenAiClient.chatCompletion(
                    apiUrl, apiKey, model.getModelKey(), prompt, 0.5);

            // 4. 解析响应
            List<String> tags = parseTagsFromResponse(chatResult.getContent());
            result.setSuggestedTags(tags);

        } catch (Exception e) {
            log.error("AI 标签识别失败", e);
            // 返回默认标签而非抛出异常
            result.setSuggestedTags(getDefaultTags(type));
        }

        return result;
    }

    // ========== 辅助方法 ==========

    private void collectFromImages(Set<String> categories, Map<String, Integer> tagCountMap) {
        LambdaQueryWrapper<ImageMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImageMaterial::getStatus, 1);
        wrapper.select(ImageMaterial::getCategory, ImageMaterial::getTags);
        
        List<ImageMaterial> list = imageMaterialMapper.selectList(wrapper);
        for (ImageMaterial img : list) {
            if (StringUtils.hasText(img.getCategory())) {
                categories.add(img.getCategory());
            }
            collectTags(img.getTags(), tagCountMap);
        }
    }

    private void collectFromVideos(Set<String> categories, Map<String, Integer> tagCountMap) {
        LambdaQueryWrapper<VideoMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoMaterial::getStatus, 1);
        wrapper.select(VideoMaterial::getTags);
        
        List<VideoMaterial> list = videoMaterialMapper.selectList(wrapper);
        for (VideoMaterial vid : list) {
            collectTags(vid.getTags(), tagCountMap);
        }
    }

    private void collectFromAudios(Map<String, Integer> tagCountMap) {
        LambdaQueryWrapper<AudioMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AudioMaterial::getStatus, 1);
        wrapper.select(AudioMaterial::getTags);
        
        List<AudioMaterial> list = audioMaterialMapper.selectList(wrapper);
        for (AudioMaterial aud : list) {
            collectTags(aud.getTags(), tagCountMap);
        }
    }

    private void collectTags(String tagsStr, Map<String, Integer> tagCountMap) {
        if (!StringUtils.hasText(tagsStr)) return;
        String[] tags = tagsStr.split(",");
        for (String tag : tags) {
            String trimmed = tag.trim();
            if (!trimmed.isEmpty()) {
                tagCountMap.merge(trimmed, 1, Integer::sum);
            }
        }
    }

    private String buildTagPredictPrompt(String materialUrl, String type) {
        String typeDesc = "IMAGE".equalsIgnoreCase(type) ? "图片" : "VIDEO".equalsIgnoreCase(type) ? "视频" : "素材";
        return String.format(
                "请为以下%s素材推荐5个合适的中文标签，用于分类和搜索。\n" +
                "素材URL: %s\n\n" +
                "请直接返回5个标签，用英文逗号分隔，例如：风景,自然,蓝天,户外,治愈\n" +
                "不要返回其他解释性文字，只返回标签列表。", typeDesc, materialUrl);
    }

    private List<String> parseTagsFromResponse(String content) {
        if (!StringUtils.hasText(content)) {
            return Collections.emptyList();
        }
        
        // 尝试解析逗号分隔的标签
        String cleaned = content.trim();
        // 移除可能的引号
        cleaned = cleaned.replaceAll("[\"']", "");
        
        return Arrays.stream(cleaned.split("[,，]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty() && s.length() < 20) // 过滤过长的内容
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<String> getDefaultTags(String type) {
        if ("IMAGE".equalsIgnoreCase(type)) {
            return Arrays.asList("风景", "人物", "科技", "艺术", "日常");
        } else if ("VIDEO".equalsIgnoreCase(type)) {
            return Arrays.asList("Vlog", "教程", "娱乐", "知识", "生活");
        } else {
            return Arrays.asList("背景音乐", "音效", "人声", "纯音乐", "氛围");
        }
    }
}
