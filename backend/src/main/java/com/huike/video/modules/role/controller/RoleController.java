package com.huike.video.modules.role.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.role.service.RoleService;
import com.huike.video.modules.role.vo.RoleOptionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/options")
    public Result<List<RoleOptionVo>> listRoleOptions() {
        return Result.success(roleService.listSelectableRoles());
    }
}
