package com.huike.video.modules.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.auth.dto.AuthLoginRequest;
import com.huike.video.modules.auth.dto.AuthRegisterRequest;
import com.huike.video.modules.auth.dto.AuthSwitchRoleRequest;
import com.huike.video.modules.auth.dto.SendCodeRequest;
import com.huike.video.modules.auth.service.AuthService;
import com.huike.video.modules.auth.service.VerificationCodeService;
import com.huike.video.modules.auth.vo.AuthLoginResponse;
import com.huike.video.modules.auth.vo.AuthRegisterResponse;
import com.huike.video.modules.auth.vo.AuthSwitchRoleResponse;
import com.huike.video.modules.user.role.entity.Role;
import com.huike.video.modules.user.role.mapper.RoleMapper;
import com.huike.video.modules.user.role.service.RoleService;
import com.huike.video.modules.user.entity.User;
import com.huike.video.modules.user.mapper.UserMapper;
import com.huike.video.modules.user.wallet.entity.UserWallet;
import com.huike.video.modules.user.wallet.mapper.UserWalletMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserWalletMapper userWalletMapper;
    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public AuthRegisterResponse register(AuthRegisterRequest request) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getPhone(), "register", request.getCode())) {
            throw new BusinessException(10006, "验证码错误或已过期");
        }

        Long usernameCount = userMapper.countByUsername(request.getUsername());
        if (usernameCount != null && usernameCount > 0) {
            throw new BusinessException(10003, "用户名已存在");
        }

        if (request.getEmail() != null) {
            Long emailCount = userMapper.countByEmail(request.getEmail());
            if (emailCount != null && emailCount > 0) {
                throw new BusinessException(10004, "邮箱已存在");
            }
        }

        String userId = generateUserId();

        User user = new User();
        user.setId(userId);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus(1);
        if (request.getRoleId() != null) {
            Role role = roleService.getSelectableRoleById(request.getRoleId());
            user.setRoleId(role.getId());
        } else {
            user.setRoleId(1L);
        }

        int inserted = userMapper.insert(user);
        if (inserted != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }

        UserWallet wallet = new UserWallet();
        wallet.setId(generateWalletId());
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalRecharged(BigDecimal.ZERO);
        wallet.setTotalConsumed(BigDecimal.ZERO);

        int walletInserted = userWalletMapper.insert(wallet);
        if (walletInserted != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }

        // 自动登录
        StpUtil.login(user.getId());

        AuthRegisterResponse response = new AuthRegisterResponse();
        response.setUserId(userId);
        response.setUsername(user.getUsername());
        response.setRegisterTime(LocalDateTime.now().format(DATETIME_FORMATTER));
        
        response.setToken(StpUtil.getTokenValue());
        response.setRefreshToken(StpUtil.getTokenValue());
        response.setExpireIn(StpUtil.getTokenTimeout());
        
        AuthLoginResponse.UserInfo userInfo = new AuthLoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setRoles(getRoleNames(user.getRoleId()));
        response.setUserInfo(userInfo);

        return response;
    }

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        User user = userMapper.selectByUsernameOrEmail(request.getUsername());

        if (user == null) {
            throw new BusinessException(10002, "用户名或密码错误");
        }

        if (!Objects.equals(user.getStatus(), 1)) {
            throw new BusinessException(10005, "账号不可用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(10002, "用户名或密码错误");
        }

        if (request.getRoleId() != null) {
            Role role = roleService.getSelectableRoleById(request.getRoleId());
            updateUserRole(user.getId(), role.getId());
            user.setRoleId(role.getId());
        }

        StpUtil.login(user.getId());

        AuthLoginResponse response = new AuthLoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setRefreshToken(StpUtil.getTokenValue());
        response.setExpireIn(StpUtil.getTokenTimeout());

        AuthLoginResponse.UserInfo userInfo = new AuthLoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setRoles(getRoleNames(user.getRoleId()));

        response.setUserInfo(userInfo);
        return response;
    }

    @Override
    public AuthSwitchRoleResponse switchRole(AuthSwitchRoleRequest request) {
        String userId = StpUtil.getLoginIdAsString();
        Role role = roleService.getSelectableRoleById(request.getRoleId());
        updateUserRole(userId, role.getId());

        AuthSwitchRoleResponse response = new AuthSwitchRoleResponse();
        response.setUserId(userId);
        response.setRoleId(role.getId());
        response.setRoles(List.of(role.getRoleName()));
        return response;
    }

    private void updateUserRole(String userId, Long roleId) {
        int updated = userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getRoleId, roleId));
        if (updated != 1) {
            throw new BusinessException(50001, "服务繁忙");
        }
    }

    private List<String> getRoleNames(Long roleId) {
        if (roleId == null) {
            return List.of("ROLE_USER");
        }
        Role role = roleMapper.selectById(roleId);
        if (role == null || role.getRoleName() == null) {
            return List.of("ROLE_USER");
        }
        return List.of(role.getRoleName());
    }

    private String generateUserId() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int suffix = new Random().nextInt(90000) + 10000;
        return "U" + date + suffix;
    }

    private String generateWalletId() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int suffix = new Random().nextInt(90000) + 10000;
        return "W" + date + suffix;
    }

    // ========== 新增方法 (TODO: 完善业务逻辑) ==========

    @Override
    public Boolean sendCode(SendCodeRequest request) {
        return verificationCodeService.sendCode(request.getTarget(), request.getType());
    }

    @Override
    @Transactional
    public AuthLoginResponse loginBySms(com.huike.video.modules.auth.dto.LoginBySmsRequest request) {
        // 1. 校验验证码 (作为注册流程，使用 REGISTER 类型)
        if (!verificationCodeService.verifyCode(request.getPhone(), "REGISTER", request.getCode())) {
            throw new BusinessException(10006, "验证码错误或已过期");
        }

        // 2. 查询用户
        User user = userMapper.selectByPhone(request.getPhone());

        // 3. 用户若已存在，则提示直接登录 (注册仅限新用户)
        if (user != null) {
             throw new BusinessException(10003, "手机号已注册，请使用密码登录");
        }

        // 4. 用户不存在则自动注册
        String userId = generateUserId();
        user = new User();
        user.setId(userId);
        // 生成默认用户名: M + 手机号后4位 + 随机4位
        String suffix = request.getPhone().length() >= 4 ? request.getPhone().substring(request.getPhone().length() - 4) : "0000";
        user.setUsername("M" + suffix + (new Random().nextInt(9000) + 1000));
        // 设置随机初始密码 (用户不可知，必须通过验证码重置或设置)
        user.setPasswordHash(passwordEncoder.encode(generateRandomPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(null); // 短信登录不强制邮箱
        user.setStatus(1);
        user.setRoleId(1L); // 默认为普通用户
        
        userMapper.insert(user);
        
        // 创建钱包
        UserWallet wallet = new UserWallet();
        wallet.setId(generateWalletId());
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setTotalRecharged(BigDecimal.ZERO);
        wallet.setTotalConsumed(BigDecimal.ZERO);
        userWalletMapper.insert(wallet);


        // 5. 执行登录 (SotToken)
        StpUtil.login(user.getId());

        // 6. 构建响应
        AuthLoginResponse response = new AuthLoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setRefreshToken(StpUtil.getTokenValue());
        response.setExpireIn(StpUtil.getTokenTimeout());

        AuthLoginResponse.UserInfo userInfo = new AuthLoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setRoles(getRoleNames(user.getRoleId())); 

        response.setUserInfo(userInfo);
        return response;
    }

    @Override
    public Boolean resetPassword(com.huike.video.modules.auth.dto.ResetPasswordRequest request) {
        // 1. 校验验证码
        if (!verificationCodeService.verifyCode(request.getTarget(), "RESET_PWD", request.getCode())) {
            throw new BusinessException(10006, "验证码错误或已过期");
        }

        // 2. 查询用户
        User user = userMapper.selectByPhone(request.getTarget());
        if (user == null) {
            throw new BusinessException(10004, "用户不存在");
        }

        // 3. 更新密码
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);

        return true;
    }

    @Override
    public com.huike.video.modules.auth.vo.AuthRefreshResponse refresh(com.huike.video.modules.auth.dto.RefreshTokenRequest request) {
        // 简单刷新: 验证 token 后重新签发
        com.huike.video.modules.auth.vo.AuthRefreshResponse response = new com.huike.video.modules.auth.vo.AuthRefreshResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setRefreshToken(StpUtil.getTokenValue());
        response.setExpireIn(StpUtil.getTokenTimeout());
        return response;
    }

    @Override
    public Boolean logout() {
        StpUtil.logout();
        return true;
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
