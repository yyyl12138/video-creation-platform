package com.huike.video.modules.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.user.dto.ApplyCreatorRequest;
import com.huike.video.modules.user.dto.ChangePasswordRequest;
import com.huike.video.modules.user.dto.UpdateUserProfileRequest;
import com.huike.video.modules.user.entity.Creator;
import com.huike.video.modules.user.entity.User;
import com.huike.video.modules.user.entity.UserProfile;
import com.huike.video.modules.user.mapper.CreatorMapper;
import com.huike.video.modules.user.mapper.UserMapper;
import com.huike.video.modules.user.mapper.UserProfileMapper;
import com.huike.video.modules.user.service.UserService;
import com.huike.video.modules.user.vo.UserMeResponse;
import com.huike.video.modules.user.vo.UploadAvatarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final CreatorMapper creatorMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public UserMeResponse getMe() {
        String userId = StpUtil.getLoginIdAsString();

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10002, "用户不存在");
        }

        UserProfile profile = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .last("limit 1"));

        Creator creator = creatorMapper.selectOne(new LambdaQueryWrapper<Creator>()
                .eq(Creator::getUserId, userId)
                .last("limit 1"));

        UserMeResponse response = new UserMeResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatarUrl(user.getAvatarUrl());

        UserMeResponse.Profile profileVo = new UserMeResponse.Profile();
        if (profile != null) {
            profileVo.setRealName(profile.getRealName());
            profileVo.setGender(toGenderText(profile.getGender()));
            profileVo.setBirthday(profile.getBirthday() == null ? null : profile.getBirthday().toString());
            profileVo.setCountry(profile.getCountry());
            profileVo.setCity(profile.getCity());
            profileVo.setBio(profile.getBio());
        }
        response.setProfile(profileVo);

        response.setVipStatus("NORMAL");
        response.setVipExpireDate(null);
        response.setCreatorStatus(toCreatorStatus(creator));
        response.setStatus(toUserStatusText(user.getStatus()));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMyProfile(UpdateUserProfileRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        UserProfile profile = userProfileMapper.selectOne(new LambdaQueryWrapper<UserProfile>()
                .eq(UserProfile::getUserId, userId)
                .last("limit 1"));

        if (profile == null) {
            profile = new UserProfile();
            profile.setId(IdUtil.simpleUUID());
            profile.setUserId(userId);

            profile.setRealName(request.getRealName());
            profile.setGender(toGenderCode(request.getGender()));
            profile.setBirthday(request.getBirthday());
            profile.setCountry(request.getCountry());
            profile.setCity(request.getCity());
            profile.setBio(request.getBio());

            int inserted = userProfileMapper.insert(profile);
            if (inserted != 1) {
                throw new BusinessException(50001, "服务繁忙");
            }
            return true;
        }

        profile.setRealName(request.getRealName());
        profile.setGender(toGenderCode(request.getGender()));
        profile.setBirthday(request.getBirthday());
        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());
        profile.setBio(request.getBio());

        int updated = userProfileMapper.updateById(profile);
        if (updated != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeMyPassword(ChangePasswordRequest request) {
        String userId = StpUtil.getLoginIdAsString();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10002, "用户不存在");
        }

        String storedHash = user.getPasswordHash();
        boolean oldOk;
        if (storedHash != null && (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$"))) {
            oldOk = passwordEncoder.matches(request.getOldPassword(), storedHash);
        } else {
            oldOk = Objects.equals(request.getOldPassword(), storedHash);
            if (oldOk) {
                String upgraded = passwordEncoder.encode(request.getOldPassword());
                int upgradedRows = userMapper.update(null, new LambdaUpdateWrapper<User>()
                        .eq(User::getId, userId)
                        .set(User::getPasswordHash, upgraded));
                if (upgradedRows == 1) {
                    storedHash = upgraded;
                    user.setPasswordHash(upgraded);
                }
            }
        }

        if (!oldOk) {
            throw new BusinessException(99999, "旧密码错误");
        }

        String newHash = passwordEncoder.encode(request.getNewPassword());
        int updated = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPasswordHash, newHash));
        if (updated != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadAvatarResponse uploadMyAvatar(MultipartFile file, String baseUrl) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(99999, "file不能为空");
        }
        String userId = StpUtil.getLoginIdAsString();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10002, "用户不存在");
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null) {
            int idx = original.lastIndexOf('.');
            if (idx >= 0 && idx < original.length() - 1) {
                ext = original.substring(idx);
            }
        }
        if (ext.isBlank()) {
            ext = ".jpg";
        }

        String relativePath = "avatar/" + userId + "_" + System.currentTimeMillis() + ext;
        Path target = Paths.get(uploadPath).resolve(relativePath);
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            throw new BusinessException(50001, "头像保存失败");
        }

        String avatarUrl = (baseUrl == null ? "" : baseUrl) + "/profile/upload/" + relativePath.replace("\\", "/");
        int updated = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getAvatarUrl, avatarUrl));
        if (updated != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }

        UploadAvatarResponse resp = new UploadAvatarResponse();
        resp.setAvatarUrl(avatarUrl);
        resp.setFileSize(file.getSize());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean applyCreator(ApplyCreatorRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        Creator creator = creatorMapper.selectOne(new LambdaQueryWrapper<Creator>()
                .eq(Creator::getUserId, userId)
                .last("limit 1"));

        if (creator == null) {
            creator = new Creator();
            creator.setId(IdUtil.simpleUUID());
            creator.setUserId(userId);
            creator.setFollowerCount(0);
            creator.setPlatformType("PORTFOLIO");
            creator.setPlatformAccount(request.getPortfolioUrl());
            creator.setVerificationStatus(3);
            creator.setBio(request.getIntroduction());
            int inserted = creatorMapper.insert(creator);
            if (inserted != 1) {
                throw new BusinessException(50001, "服务繁忙");
            }
            return true;
        }

        creator.setPlatformType("PORTFOLIO");
        creator.setPlatformAccount(request.getPortfolioUrl());
        creator.setVerificationStatus(3);
        creator.setBio(request.getIntroduction());
        int updated = creatorMapper.updateById(creator);
        if (updated != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }
        return true;
    }

    private String toGenderText(Integer gender) {
        if (gender == null) {
            return "保密";
        }
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "保密";
        };
    }

    private Integer toGenderCode(String gender) {
        if (gender == null) {
            return null;
        }
        String v = gender.trim();
        if (v.equals("男")) {
            return 1;
        }
        if (v.equals("女")) {
            return 2;
        }
        return 0;
    }

    private String toCreatorStatus(Creator creator) {
        if (creator == null || creator.getVerificationStatus() == null) {
            return "NONE";
        }
        return switch (creator.getVerificationStatus()) {
            case 3 -> "APPLIED";
            case 2 -> "APPROVED";
            default -> "NONE";
        };
    }

    private String toUserStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "正常";
            case 2 -> "封禁";
            case 3 -> "注销";
            default -> "未知";
        };
    }
}
