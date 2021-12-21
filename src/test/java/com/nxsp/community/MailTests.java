package com.nxsp.community;

import com.nxsp.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("niuxu1997@163.com", "TEST", "Welcome.");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        //存入数据，嵌入模板,注意与demo.html文件里面的username的一致
        context.setVariable("usernames", "sunday");
        //利用/mail/demo模板和thymeleaf模板引擎生成html网页（邮件的内容）
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("niuxu1997@163.com", "HTML", content);
    }

}
