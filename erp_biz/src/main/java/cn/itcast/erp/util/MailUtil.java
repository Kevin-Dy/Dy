package cn.itcast.erp.util;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮件工具类
 *
 */
public class MailUtil {

	private JavaMailSender sender;
	private String from;
	
	public void sendMail(String to, String title, String content) throws Exception{
		// 创建邮件
		MimeMessage mail = sender.createMimeMessage();
		// 邮件 工具包装
		MimeMessageHelper helper = new MimeMessageHelper(mail);
		// 发件人
		helper.setFrom(from);
		// 收件人
		helper.setTo(to);
		// 设置邮件标题
		helper.setSubject(title);
		// 设置内容
		helper.setText(content);
		
		// 发送
		sender.send(mail);
	}

	public void setSender(JavaMailSender sender) {
		this.sender = sender;
	}

	public void setFrom(String from) {
		this.from = from;
	}
}
