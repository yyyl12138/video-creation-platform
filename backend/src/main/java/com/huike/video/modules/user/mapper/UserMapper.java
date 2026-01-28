package com.huike.video.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huike.video.modules.user.entity.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select count(1) from users where username = #{username}")
    Long countByUsername(@Param("username") String username);

    @Select("select count(1) from users where email = #{email}")
    Long countByEmail(@Param("email") String email);

    @Select("select * from users where username = #{account} or email = #{account} or phone = #{account} limit 1")
    User selectByUsernameOrEmail(@Param("account") String account);

    @Select("select * from users where phone = #{phone} limit 1")
    User selectByPhone(@Param("phone") String phone);
}
