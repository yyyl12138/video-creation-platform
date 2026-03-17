package com.huike.video.modules.user.service;

import com.huike.video.modules.user.dto.UpdateUserProfileRequest;
import com.huike.video.modules.user.dto.ApplyCreatorRequest;
import com.huike.video.modules.user.dto.ChangePasswordRequest;
import com.huike.video.modules.user.vo.UserMeResponse;
import com.huike.video.modules.user.vo.UploadAvatarResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserMeResponse getMe();

    Boolean updateMyProfile(UpdateUserProfileRequest request);

    Boolean changeMyPassword(ChangePasswordRequest request);

    UploadAvatarResponse uploadMyAvatar(MultipartFile file, String baseUrl);

    Boolean applyCreator(ApplyCreatorRequest request);

    Boolean deleteMyAccount();

    com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.huike.video.modules.user.vo.AdminUserPageVO> getAdminUserPage(Integer page, Integer size, String keyword, String status);

    Boolean updateUserStatus(String userId, Integer status, String reason);

    UserMeResponse getUserDetailForAdmin(String userId);
}
