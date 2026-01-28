package com.huike.video.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 * 禁用 CSRF，配置接口权限
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF (前后端分离项目)
            .csrf(csrf -> csrf.disable())
            
            // 禁用 Session (使用 Token 认证)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置接口权限
            .authorizeHttpRequests(auth -> auth
                // 允许所有请求 (认证由 Sa-Token 处理)
                .requestMatchers("/**").permitAll()
            );
        
        return http.build();
    }
}
