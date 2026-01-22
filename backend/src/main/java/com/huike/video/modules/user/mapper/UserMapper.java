package com.huike.video.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Long countByUsername(@Param("username") String username);

    Long countByEmail(@Param("email") String email);

    User selectByUsernameOrEmail(@Param("account") String account);
}
