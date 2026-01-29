package com.huike.video.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyCreatorRequest {

    @NotBlank(message = "introduction不能为空")
    private String introduction;

    @NotBlank(message = "portfolioUrl不能为空")
    private String portfolioUrl;
}
