package com.huike.video.common.enums;

import lombok.Getter;

/**
 * 任务类型枚举
 * 统一管理 API 层 (String) 与 数据库层 (Integer) 的映射
 */
@Getter
public enum TaskTypeEnum {

    TEXT_TO_TEXT(1, "TEXT_TO_TEXT"),
    TEXT_TO_IMAGE(2, "TEXT_TO_IMAGE"),
    TEXT_TO_VIDEO(3, "TEXT_TO_VIDEO"),
    IMAGE_TO_VIDEO(4, "IMAGE_TO_VIDEO"),
    VIDEO_RENDER(5, "VIDEO_RENDER");

    private final Integer code;
    private final String name;

    TaskTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据字符串名称获取枚举
     * @param name 任务类型名称 (如 "TEXT_TO_VIDEO")
     * @return 枚举值，未匹配则返回 TEXT_TO_TEXT
     */
    public static TaskTypeEnum fromName(String name) {
        if (name == null) return TEXT_TO_TEXT;
        for (TaskTypeEnum type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return TEXT_TO_TEXT;
    }

    /**
     * 根据数据库代码获取枚举
     * @param code 数据库存储的整数代码
     * @return 枚举值，未匹配则返回 TEXT_TO_TEXT
     */
    public static TaskTypeEnum fromCode(Integer code) {
        if (code == null) return TEXT_TO_TEXT;
        for (TaskTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return TEXT_TO_TEXT;
    }
}
