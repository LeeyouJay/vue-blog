package com.arslinth.service;


import com.arslinth.exception.MyAccountException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * @author Arslinth
 * @ClassName MailService
 * @Description 发送邮件
 * @Date 2021/4/15
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailService {

    private final JavaMailSender mailSender;

    //邮件发件人
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.mail.nickName}")
    private String nickName;

    private final TemplateEngine templateEngine;

    @Async
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000L, multiplier = 1))
    public void sendMail(String to, String subject,String template, Map<String,String> templateMap) {

        String emailContent = emailTemplate(template,templateMap);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            //邮件发送人
            messageHelper.setFrom(nickName+'<'+from+'>');
            //邮件接收人
            messageHelper.setTo(to);
            //邮件主题
            message.setSubject(subject);
            //邮件内容，html格式
            messageHelper.setText(emailContent, true);
            //发送
            mailSender.send(message);
            log.info("邮箱 "+to+"信息发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyAccountException(e.getMessage());
        }
    }
    private String emailTemplate(String template,Map<String,String> map){
        //创建邮件正文
        Context context = new Context();
        map.forEach(context::setVariable);
        //将模块引擎内容解析成html字符串
        return templateEngine.process(template, context);
    }

    // 重试失败调用的方法
    @Recover
    private void mailRetryFail(MyAccountException e) {
        log.info("邮件发送失败: {} ", e.getMessage());

    }

}
