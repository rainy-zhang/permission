package com.mmall.util;

import com.mmall.beans.Email;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class EmailUtil {

    public static boolean send(Email mail) {

        String from = "791136444@qq.com";
        int port = 465;
        String host = "smtp.qq.com";
        String username = "791136444";
        String password = "ejqqxuwauljbbfgf";   //授权码

        HtmlEmail email = new HtmlEmail();
        try {
            email.setHostName(host);
            email.setSmtpPort(port);
            email.setCharset("UTF-8");
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(true);

            for (String str : mail.getReceivers()) {
                email.addTo(str);
            }
            email.setFrom(from);
            email.setMsg(mail.getMessage());
            email.setSubject(mail.getSubject());
            email.send();
            log.info("{} 发送邮件到 {}", from, StringUtils.join(mail.getReceivers(), ","));
            return true;
        } catch (EmailException e) {
            log.error(from + "发送邮件到" + StringUtils.join(mail.getReceivers(), ",") + "失败", e);
            return false;
        }
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("791136444@qq.com");
        Email email = Email.builder().subject("使用commons-email发送的邮件").message("测试邮件").receivers(set).build();
        send(email);
    }



}

