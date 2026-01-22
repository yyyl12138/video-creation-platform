package com.huike.video.modules.role.service;

import com.huike.video.modules.role.entity.Role;
import com.huike.video.modules.role.vo.RoleOptionVo;

import java.util.List;

public interface RoleService {

    List<RoleOptionVo> listSelectableRoles();

    Role getSelectableRoleById(Long roleId);
}
