package com.nxsp.community.controller.interceptor;

import com.nxsp.community.entity.LoginTicket;
import com.nxsp.community.entity.User;
import com.nxsp.community.service.UserService;
import com.nxsp.community.util.CookieUtil;
import com.nxsp.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    // 目标方法之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            // 如果cookie中有ticket（即有用户登录后留下的信息）
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            // 能查询到loginTicket，且状态为有效，且超时时间在当前时间之后
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }

        return true;
    }

    // 目标方法之后执行(返回modelAndView、模板引擎之前)
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 获取user信息并存入modelAndView以便前端使用
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }
    // 返回ModelAndView之后（模板引擎之后）执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal中的user信息
        hostHolder.clear();
    }
}
