package com.huike.video.modules.material.vo;

import lombok.Data;
import java.util.List;

/**
 * 热门标签响应 VO
 * 对应接口 2.1
 */
@Data
public class HotTagsVO {

    /** 分类列表 */
    private List<String> categories;

    /** 热门标签列表 */
    private List<HotTagItem> hotTags;

    @Data
    public static class HotTagItem {
        /** 标签名 */
        private String name;
        /** 使用次数 */
        private Integer count;

        public HotTagItem() {}
        
        public HotTagItem(String name, Integer count) {
            this.name = name;
            this.count = count;
        }
    }
}
