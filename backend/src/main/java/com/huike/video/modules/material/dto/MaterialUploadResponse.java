package com.huike.video.modules.material.dto;

import lombok.Data;

@Data
public class MaterialUploadResponse {
    private String materialId;
    private String url;
    private String type;
    private Long fileSize;
    private String name;
}
