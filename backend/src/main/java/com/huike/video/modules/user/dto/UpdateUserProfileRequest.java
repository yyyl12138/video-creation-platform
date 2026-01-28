package com.huike.video.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {

    @Size(max = 50, message = "realName长度不能超过50")
    private String realName;

    @Size(max = 10, message = "gender长度不能超过10")
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Size(max = 50, message = "country长度不能超过50")
    private String country;

    @Size(max = 50, message = "city长度不能超过50")
    private String city;

    @Size(max = 500, message = "bio长度不能超过500")
    private String bio;
}
