package com.huike.video.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截器配置
 * 配置登录认证拦截和白名单
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // ⚠ 临时关闭全局登录拦截，便于本地无账号调试接口
        // TODO 恢复登录校验时，取消下方代码的注释
//        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
//                .addPathPatterns("/**")
//                // 排除不需要登录的路径
//                .excludePathPatterns(
//                        // Swagger/Knife4j
//                        "/doc.html",
//                        "/swagger-resources/**",
//                        "/webjars/**",
//                        "/v3/api-docs/**",
//
//                        // 认证接口 (匿名访问)
//                        "/api/v1/auth/**",
//
//                        // 静态资源
//                        "/profile/upload/**",
//                        "/favicon.ico"
//                );
    }
}
