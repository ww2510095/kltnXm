package com.bm.base.util;


import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Emil {
	
	public static void send(String to) throws Exception {
        String from = "1651603612@qq.com";
//        String to = "24994604@qq.com";
        String star=MyDate.stampToDate(System.currentTimeMillis()-(86400L*1000L));
        String end=MyDate.stampToDate(System.currentTimeMillis());
        String subject = star+"--" +end+"种子门店数据";//标题
        String body = "<a href=\"http://123.207.147.134:8091/data/exportgetselectshopdata?star="+
        		star+"&end="+end+"&uname=13333333333&UID=1\" >附件下载点击这里</a>";
        String smtpHost = "smtp.qq.com";//smtp.qq.com/smtp.sohu.com
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", smtpHost); // 发件人的邮箱的 SMTP服务器地址
        props.setProperty("mail.smtp.auth", "true"); // 请求认证，参数名称与具体实现有关
        // 创建Session实例对象
        Session session = Session.getDefaultInstance(props);
        // 创建MimeMessage实例对象
        MimeMessage message = new MimeMessage(session);
        // 设置发件人
        message.setFrom(new InternetAddress(from));
        // 设置收件人
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        // 设置发送日期
        message.setSentDate(new Date());
        // 设置邮件主题
        message.setSubject(subject);
        
        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart mainPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart html = new MimeBodyPart();
		// 设置HTML内容
		html.setContent(body, "text/html; charset=utf-8");
		mainPart.addBodyPart(html);
		// 将MiniMultipart对象设置为邮件内容
		message.setContent(mainPart);
//        // 设置纯文本内容的邮件正文
//        message.setText(body);
        // 保存并生成最终的邮件内容
        message.saveChanges();
        // 设置为debug模式, 可以查看详细的发送 log+
        session.setDebug(false);
        // 获取Transport对象
        Transport transport = session.getTransport("smtp");
        // 第2个参数需要填写的是QQ邮箱的SMTP的授权码，什么是授权码，它又是如何设置？
        transport.connect(from, "ssctvhmkcxlkdhad");
        // 发送，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) throws Exception {
    	send("24994604@qq.com");
    }

}
