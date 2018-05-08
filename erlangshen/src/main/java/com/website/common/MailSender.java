package com.website.common;

import com.fastjavaframework.exception.ThrowException;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 邮件发送
 * @author https://github.com/shuli495/erlangshen
 */
public class MailSender {
    private static ExecutorService executorService;

    public MailSender() {
        if(null == this.executorService) {
            this.executorService = Executors.newCachedThreadPool();
        }
    }

    /**
     * 发送邮件
     * 将邮件发送线程添加至线程池
     * @param from      发件人邮箱
     * @param nikeName  发件人昵称
     * @param smtp      smtp
     * @param subject   标题
     * @param content   内容
     * @param mail      收件人邮箱
     * @param userName  发件人账号
     * @param pwd       发件人密码
     */
    public void send(String from, String nikeName, String smtp, String subject, String content, String mail, String userName, String pwd) {
        try {
            Sender sender = new Sender(from, nikeName, smtp, subject, content, mail, userName, pwd);
            this.executorService.execute(sender);
        } catch (Exception e) {
            throw new ThrowException("邮件发送队列失败：" + e.getMessage(), "901001");
        }
    }

    /**
     * 发送邮件线程，添加至线程池用
     */
    class Sender extends Thread {

        private String from;
        private String nikeName;
        private String smtp;
        private String subject;
        private String content;
        private String mail;
        private String userName;
        private String pwd;

        public Sender(String from, String nikeName, String smtp, String subject, String content, String mail, String userName, String pwd) {
            this.from = from;
            this.nikeName = nikeName;
            this.smtp = smtp;
            this.subject = subject;
            this.content = content;
            this.mail = mail;
            this.userName = userName;
            this.pwd = pwd;
        }

        public void run() {
            try {
                Properties props = new Properties();
                props.setProperty("mail.debug", "true");
                props.setProperty("mail.smtp.auth", "true");
                // 设置ssl安全认证
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.socketFactory", sf);

                // 设置邮件服务器主机名
                try {
                    props.setProperty("mail.host", this.smtp);
                } catch (Exception e) {
                    throw new ThrowException(this.subject+"的邮件格式（"+this.mail+"）错误！", "901002");
                }
                props.setProperty("mail.transport.protocol", "smtp");

                Session session = Session.getInstance(props);

                Message msg = new MimeMessage(session);
                msg.setSubject(this.subject);
                // 设置邮件内容
                msg.setText(this.content);
                // 设置发件人
                String nick="";
                try {
                    nick=javax.mail.internet.MimeUtility.encodeText(this.nikeName);
                } catch (Exception e) {
                }
                msg.setFrom(new InternetAddress(nick+" <"+this.from+">"));

                Transport transport = session.getTransport();
                // 连接邮件服务器
                try {
                    // 解密邮箱密码
                    transport.connect(this.userName, this.pwd);
                } catch (Exception e) {
                    throw new ThrowException("邮箱账号或密码错误！", "901003");
                }
                // 发送邮件
                transport.sendMessage(msg, new Address[] {new InternetAddress(mail)});
                transport.close();
            } catch (Exception e) {
                throw new ThrowException("邮件发送失败：" + e.getMessage(), "901004");
            }
        }
    }
}
