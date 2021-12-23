package com.nxsp.community.service;

import com.nxsp.community.dao.LoginTicketMapper;
import com.nxsp.community.dao.UserMapper;
import com.nxsp.community.entity.LoginTicket;
import com.nxsp.community.entity.User;
import com.nxsp.community.util.CommunityUtil;
import com.nxsp.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.nxsp.community.util.CommunityConstant.*;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    private int userId;
    private String oldPassword;
    private String newPassword;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 注册用户，跳转到激活页面
     * @param user
     * @return Map
     */
    public Map<String,Object> register(User user){
        Map<String, Object> map = new HashMap<>();
    //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
        }

        //验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","账号已存在");
            return map;
        }

        //验证邮箱是否存在
        User e = userMapper.selectByEmail(user.getEmail());
        if(e != null){
            map.put("emailMsg","邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = domain + contextPath +"/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        //模板引擎生成邮件内容
        String content = templateEngine.process("/mail/activation",context);
        //发送邮件
        mailClient.sendMail(user.getEmail(),"激活账号", content);
        return map;
    }

    /**
     * Description: 调用此方法给激活状态赋值
     * @param userId: 用户id
     * @param code: 激活码
     * @return int: 激活状态
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        // 如果已激活
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            // 如果未激活且激活码符合，改变status状态使其激活
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        } else {
            // 激活失败
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * Description: 用户登录方法
     * @param username:
     * @param password:
     * @param expiredSeconds: 过期时间
     * @return java.util.Map<java.lang.String,java.lang.Object>:
     */
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        // 验证账号是否存在,是否激活
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        // 验证密码
        // 传入的明文密码加密后再比较
        String passowrd = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(passowrd)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }
        // 登录成功，生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        /**
         *System.currentTimeMillis() + expiredSeconds * 1000
         * 可以这样解读：System.currentTimeMillis()相当于是毫秒为单位
         * 但是，后头乘了1000，就变成了以秒为单位
         */
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * Description:  用户登出方法
     * @param ticket:
     * @return void:
     */
    public void logout(String ticket) {
        // 改为无效状态
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }


    public int updateHeader(int userId , String url){
        return userMapper.updateHeader(userId , url);
    }

    /**
     * Description: 修改密码
     *
     * @param userId:
     * @param oldPassword:
     * @param newPassword:
     * @return java.util.Map<java.lang.String, java.lang.Object>:
     */
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {

        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空!");
            return map;
        }

        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码输入有误!");
            return map;
        }

        // 更新salt
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        userMapper.updateSalt(userId,user.getSalt());
        //更新密码
        userMapper.updatePassword(userId ,CommunityUtil.md5(newPassword + user.getSalt()));
        return map;
    }

}
