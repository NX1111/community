package com.nxsp.community.config;

import com.nxsp.community.util.CommunityConstant;
import com.nxsp.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
       //所有resources目录下的静态资源都可以访问，不拦截
        web.ignoring().antMatchers("/resources/**");
    }
    // 认证采用自己之前的逻辑，不再重写方法
    // 但是需要构建用户认证的结果，并存入SecurityContext，以便于Security进行授权

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权配置（权限和路径的对应关系）
        // 第一个授权是需要登录才能访问的地址
        // 第二个授权是需要版主身份才能置顶加精，第三个授权是需要管理员身份才能删除
        // 禁用csrf
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**",
                        "/actuator/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();

        // 权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    // 没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                       //获取请求头"x-requested-with"
                        String xRequestedWith = request.getHeader("x-requested-with");
                        // 如果是异步请求，返回403，交由js解析提示错误消息
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            //声明需要返回的数据类型
                            response.setContentType("application/plain;charset=utf-8");
                            //向前台输出字符流
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录哦!"));
                        } else {
                            //如果不是异步，直接重定向到登录界面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    // 权限不足
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限!"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码，随便覆盖一个logout方法，使得程序可以往后执行我们自己的代码
        http.logout().logoutUrl("/securitylogout");
    }

}
