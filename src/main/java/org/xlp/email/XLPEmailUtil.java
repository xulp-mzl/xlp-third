package org.xlp.email;

import java.io.File;
import java.util.Date;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.xlp.utils.collection.XLPCollectionUtil;

/**
 * 邮件发送工具类
 * 
 * @author 徐龙平
 * @version 1.0
 * 
 */
public class XLPEmailUtil {
	/**
	 * 邮件发送
	 * 
	 * @param email
	 *            邮件信息对象
	 * @return 邮件发送是否成功，假如返回true，发送成功，否则发送失败
	 */
	@SuppressWarnings("restriction")
	public static boolean sendEmail(Email email) {
		Transport transport = null;
		try {
			Session session = null;
			// 构建一个邮件发送session
			if(email.isValidate())
				session = Session.getDefaultInstance(email.getProperties(),
						email.getAuthenticator());
			else
				session = Session.getDefaultInstance(email.getProperties());
			// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
			// 用（你可以在控制台（console)上看到发送邮件的过程）
			if (email.isOpenDebug())
				session.setDebug(true);
			// 用session为参数创建邮件发送消息对象
			MimeMessage message = new MimeMessage(session);
			// 加载发件人地址
			message.setFrom(email.getFromAddress());
			// 设置收件人邮箱地址
			message.addRecipients(RecipientType.TO, email.getToAddress());
			// 设置抄送人邮箱地址
			message.addRecipients(RecipientType.CC, email.getCCAddress());
			// 设置匿名抄送人邮箱地址
			message.addRecipients(RecipientType.BCC, email.getBCCAddress());
			// 设置邮件主题
			message.setSubject(email.getSubject(), email.getCharSetName());
			// message.setSubject(new String(email.getSubject().getBytes(),
			// email.getCharSetName()));
			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 设置邮件的文本内容
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(email.getContent(), "text/html;charset="
					+ email.getCharSetName());
			multipart.addBodyPart(contentPart);

			MimeBodyPart attachmentPart;
			// 设置邮件附件
			if (!XLPCollectionUtil.isEmpty(email.getAttachments())) {
				for (File file : email.getAttachments()) {
					attachmentPart = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(
							file.getAbsoluteFile());
					attachmentPart.setDataHandler(new DataHandler(fds));
					// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
					sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
					attachmentPart.setFileName("=?"
							+ email.getCharSetName().replace("-", "")
									.toUpperCase() + "?B?"
							+ enc.encode(fds.getName().getBytes()) + "?=");
					// attachmentPart.setFileName(com.sun.xml.internal.messaging.saaj.packaging.mime.internet.
					// MimeUtility.encodeText(new
					// String(fds.getName().getBytes(),
					// email.getCharSetName())));
					multipart.addBodyPart(attachmentPart);
				}
			}

			// 防止出现错误添加代码
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap
					.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);
			// 将multipart对象放到message中
			message.setContent(multipart);

			// 设置显示的发件时间
			message.setSentDate(new Date());
			// 保存前面的设置
			message.saveChanges();

			// 发送邮件
			transport = session.getTransport(email.getProtocol());
			// 连接服务器的邮箱
			if (email.getServerPort() == Email.NOT_SET_PORT)
				transport.connect(email.getServerHost(), email.getUsername(),
						email.getPassword());
			else
				transport.connect(email.getServerHost(), email.getServerPort(),
						email.getUsername(), email.getPassword());
			// 把邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("--------邮件发送失败，失败原因是：" + e.getMessage());
			return false;
		} finally {
			if (transport != null)
				try {
					transport.close();
				} catch (MessagingException e) {
				}
		}
		return true;
	}
}
