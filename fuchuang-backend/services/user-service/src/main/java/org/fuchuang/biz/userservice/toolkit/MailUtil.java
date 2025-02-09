package org.fuchuang.biz.userservice.toolkit;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.fuchuang.framework.starter.convention.exception.ServiceException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Date;

/**
 * 邮箱工具类
 */
public class MailUtil {

    /**
     * 外网邮件发送
     *
     * @param javaMailSender 邮件发送器
     * @param form 发件人邮箱
     * @param to 收件人邮箱地址 收件人@xx.com
     * @param process 邮件内容
     * @param isHTML 是否是HTML格式
     */
    public static void sendMail(JavaMailSender javaMailSender, String form, String to, String process, boolean isHTML) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("【POLAR】验证码"); // 邮件的标题
            helper.setFrom(form); // 发送者
            helper.setTo(to); // 接收者
            helper.setSentDate(new Date()); // 时间
            helper.setText(process, isHTML); // 第二个参数true表示这是一个html文本
        } catch (MessagingException e) {
            throw new ServiceException("邮件发送异常");
        }
        javaMailSender.send(mimeMessage);
    }
}
