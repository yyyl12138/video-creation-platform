package com.huike.video.modules.user.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.user.dto.ApplyCreatorRequest;
import com.huike.video.modules.user.dto.ChangePasswordRequest;
import com.huike.video.modules.user.dto.UpdateUserProfileRequest;
import com.huike.video.modules.user.service.UserService;
import com.huike.video.modules.user.vo.UserMeResponse;
import com.huike.video.modules.user.vo.UploadAvatarResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserMeResponse> me() {
        return Result.success(userService.getMe());
    }

    @PutMapping("/me/profile")
    public Result<Boolean> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        return Result.success(userService.updateMyProfile(request));
    }

    @PutMapping("/me/password")
    public Result<Boolean> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        return Result.success(userService.changeMyPassword(request));
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadAvatarResponse> uploadMyAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return Result.success(userService.uploadMyAvatar(file, getBaseUrl(request)));
    }

    @PostMapping("/me/apply-creator")
    public Result<Boolean> applyCreator(@Valid @RequestBody ApplyCreatorRequest request) {
        return Result.success(userService.applyCreator(request));
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        boolean isDefaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        if (isDefaultPort) {
            return scheme + "://" + serverName;
        }
        return scheme + "://" + serverName + ":" + port;
    }
}
