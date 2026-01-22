package com.huike.video.modules.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.role.entity.Role;
import com.huike.video.modules.role.mapper.RoleMapper;
import com.huike.video.modules.role.service.RoleService;
import com.huike.video.modules.role.vo.RoleOptionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<RoleOptionVo> listSelectableRoles() {
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .orderByAsc(Role::getId));

        return roles.stream()
                .filter(this::isSelectable)
                .map(this::toOption)
                .toList();
    }

    @Override
    public Role getSelectableRoleById(Long roleId) {
        if (roleId == null) {
            throw new BusinessException(99999, "参数错误");
        }

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(10006, "角色不存在");
        }

        if (!isSelectable(role)) {
            throw new BusinessException(10007, "不允许选择该角色");
        }

        return role;
    }

    private boolean isSelectable(Role role) {
        if (role == null || role.getRoleName() == null) {
            return false;
        }

        String name = role.getRoleName().trim().toLowerCase(Locale.ROOT);
        return !name.equals("super_admin")
                && !name.equals("superadmin")
                && !name.equals("admin_root")
                && !name.equals("root")
                && !name.contains("超级管理员");
    }

    private RoleOptionVo toOption(Role role) {
        RoleOptionVo vo = new RoleOptionVo();
        vo.setRoleId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        return vo;
    }
}
