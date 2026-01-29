package com.huike.video.modules.user.role.service;

import com.huike.video.modules.user.role.entity.Role;
import com.huike.video.modules.user.role.vo.RoleOptionVo;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 获取可选择的角色列表
     */
    List<RoleOptionVo> listSelectableRoles();

    /**
     * 根据ID获取可选择的角色
     */
    Role getSelectableRoleById(Long roleId);
}
