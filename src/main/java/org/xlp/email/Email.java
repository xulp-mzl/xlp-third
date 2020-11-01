package org.xlp.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.xlp.assertion.AssertUtils;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.io.XLPIOUtil;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 邮件对象
 * 
 * @author 徐龙平
 * @version 1.0
 * 
 */
public class Email {
	public static final int NOT_SET_PORT = -1;
	
	/**
	 * 默认字符编码
	 */
	public static final String DEFAULT_CHARSET_NAME = "utf-8";
	
	/**
	 * 默认发送邮件的服务器的主机名
	 */
	public static final String DEFAULT_SERVER_HOST = "smtp.163.com";
	
	/**
	 * 默认发送邮件使用的协议
	 */
	public static final String DEFAULT_PROTOCOL = "smtp";

	// 发送邮件的服务器的主机名
	private String serverHost = DEFAULT_SERVER_HOST;
	// 发送邮件的服务器的端口号
	private int serverPort = NOT_SET_PORT;
	// 发送邮件时， 是否需要身份验证
	private boolean validate = true;
	// 字符编码
	private String charSetName = DEFAULT_CHARSET_NAME;
	// 是否开启debug模式，默认没开启
	private boolean openDebug = false;
	// 使用的协议（JavaMail规范要求）
	private String protocol = DEFAULT_PROTOCOL;
	// 是否开启ssl连接,默认开启
	private boolean openSSLConnect = true;
	// 登陆邮件发送服务器的用户名和密码
	private String username;
	private String password;
	// 邮件发送者的邮箱地址
	private String from;
	// 邮件发送者的名称
	private String nickname = "";
	// 邮件接收者的邮箱地址 （可为多个）
	private String[] to = new String[0];
	// 邮件抄送者的邮箱地址 （可为多个）
	private String[] cc = new String[0];
	// 邮件匿名抄送接收者的邮箱地址 （可为多个）
	private String[] bcc = new String[0];
	// 邮件主题
	private String subject = "";
	// 邮件的文本内容（可以使用html标签）
	private String content = "";
	// 邮件附件的文件名
	private List<File> attachments;
	// 参数配置
	private Properties properties;

	public Email() {
	}

	public Email(String serverHost, String username, String password,
			String from, String[] to, String subject, String content,
			List<File> attachments) {
		setServerHost(serverHost);
		this.username = username;
		this.password = password;
		this.from = from;
		setTo(to);
		setSubject(subject);
		setContent(content);
		setAttachments(attachments);
	}

	public Email(String serverHost, String username, String password,
			String from, String to, String subject, String content,
			List<File> attachments) {
		setServerHost(serverHost);
		this.username = username;
		this.password = password;
		this.from = from;
		setTo(to);
		setSubject(subject);
		setContent(content);
		setAttachments(attachments);
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		if (!XLPStringUtil.isEmpty(serverHost))
			this.serverHost = serverHost;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getCharSetName() {
		return charSetName;
	}

	public void setCharSetName(String charSetName) {
		if (!XLPStringUtil.isEmpty(charSetName))
			this.charSetName = charSetName;
	}

	public boolean isOpenDebug() {
		return openDebug;
	}

	public void setOpenDebug(boolean openDebug) {
		this.openDebug = openDebug;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		if (!XLPStringUtil.isEmpty(protocol))
			this.protocol = protocol;
	}

	public boolean isOpenSSLConnect() {
		return openSSLConnect;
	}

	public void setOpenSSLConnect(boolean openSSLConnect) {
		this.openSSLConnect = openSSLConnect;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 获取发送者网络地址
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public InternetAddress getFromAddress() throws UnsupportedEncodingException {
		return new InternetAddress(from, nickname, charSetName);
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		if (to != null)
			this.to = to;
	}

	/**
	 * 设置接收者邮箱地址，如果多个，用英文逗号隔开
	 * 
	 * @param to
	 */
	public void setTo(String to) {
		if (!XLPStringUtil.isEmpty(to))
			this.to = to.split(",");
	}

	/**
	 * 获取接收者网络地址
	 * 
	 * @return
	 * @throws AddressException
	 */
	public InternetAddress[] getToAddress() throws AddressException {
		int len = to.length;
		InternetAddress[] internetAddresses = new InternetAddress[len];
		for (int i = 0; i < len; i++) {
			internetAddresses[i] = new InternetAddress(to[i]);
		}
		return internetAddresses;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		if (cc != null)
			this.cc = cc;
	}

	/**
	 * 获取抄送者网络地址
	 * 
	 * @return
	 * @throws AddressException
	 */
	public InternetAddress[] getCCAddress() throws AddressException {
		int len = cc.length;
		InternetAddress[] internetAddresses = new InternetAddress[len];
		for (int i = 0; i < len; i++) {
			internetAddresses[i] = new InternetAddress(cc[i]);
		}
		return internetAddresses;
	}

	/**
	 * 设置抄送者邮箱地址，如果多个，用英文逗号隔开
	 * 
	 * @param to
	 */
	public void setCc(String cc) {
		if (!XLPStringUtil.isEmpty(cc))
			this.cc = cc.split(",");
	}

	public String[] getBcc() {
		return bcc;
	}

	public void setBcc(String[] bcc) {
		if (bcc != null)
			this.bcc = bcc;
	}

	/**
	 * 设置匿名抄送接收者邮箱地址，如果多个，用英文逗号隔开
	 * 
	 * @param to
	 */
	public void setBcc(String bcc) {
		if (!XLPStringUtil.isEmpty(bcc))
			this.bcc = bcc.split(",");
	}

	/**
	 * 获取匿名抄送接收者网络地址
	 * 
	 * @return
	 * @throws AddressException
	 */
	public InternetAddress[] getBCCAddress() throws AddressException {
		int len = bcc.length;
		InternetAddress[] internetAddresses = new InternetAddress[len];
		for (int i = 0; i < len; i++) {
			internetAddresses[i] = new InternetAddress(bcc[i]);
		}
		return internetAddresses;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		if (!XLPStringUtil.isEmpty(subject))
			this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (!XLPStringUtil.isEmpty(content))
			this.content = content;
	}

	public List<File> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<File> attachments) {
		this.attachments = attachments;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		if (!XLPStringUtil.isEmpty(nickname))
			this.nickname = nickname;
	}

	/**
	 * 设置邮件发送配置
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * 获取邮件发送默认配置
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 */
	public Properties getProperties() throws GeneralSecurityException {
		if (properties == null) {
			properties = new Properties();
			if (openSSLConnect) {
				MailSSLSocketFactory sf = new MailSSLSocketFactory();
				sf.setTrustAllHosts(true);
				if (serverPort != NOT_SET_PORT)
					properties.put("mail.smtp.socketFactory.port", serverPort);
				properties.put("mail.smtp.ssl.enable", openSSLConnect);
				properties.put("mail.smtp.ssl.socketFactory", sf);
			}
			// 设置端口号
			if (serverPort != NOT_SET_PORT)
				properties.put("mail.smtp.port", serverPort);
			// 设置发送邮件的邮件服务器的属性
			properties.put("mail.smtp.host", serverHost);
			// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证
			properties.put("mail.smtp.auth", validate);
			// 使用的协议（JavaMail规范要求）
			properties.put("mail.transport.protocol", protocol);
		}
		return properties;
	}

	/**
	 * 
	 * 密码用户名验证
	 * 
	 */
	private class MyAuthenticator extends Authenticator {
		String username;
		String password;

		public MyAuthenticator() {
		}

		public MyAuthenticator(String username, String password) {
			this();
			this.username = username;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}

	/**
	 * 得到密码用户名验证器
	 * 
	 * @return
	 */
	public Authenticator getAuthenticator() {
		return new MyAuthenticator(username, password);
	}

	/**
	 * 设置端口号
	 * 
	 * @param serverPort
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getServerPort() {
		return serverPort;
	}

	/**
	 * 用给定的参数格式化邮件内容 <br/>
	 * 内部采用的是<code>MessageFormat</code>.format(content, arguments);
	 * 
	 * @param arguments
	 */
	public void formatContent(Object... arguments) {
		if (arguments != null && content != null)
			content = MessageFormat.format(content, arguments);
	}

	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesFile 配置文件
	 * @param charsetName 读取配置文件时使用的字符编码
	 * @return
	 * @throws IOException 假如读取文件失败，则抛出该异常
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如给定的参数【propertiesFile】为空，则抛出该异常
	 */
	public static Email loadFromConfig(File propertiesFile, String charsetName) throws IOException {
		AssertUtils.assertFile(propertiesFile);
		Properties emailConfig = new Properties();
		charsetName = XLPStringUtil.emptyTrim(charsetName);
		charsetName = charsetName.isEmpty() ? DEFAULT_CHARSET_NAME : charsetName;
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(propertiesFile), charsetName); 
			emailConfig.load(reader);
		} finally {
			XLPIOUtil.closeReader(reader);
		} 
		
		return loadFromConfig(emailConfig);
	}
	
	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesFilePath 配置文件
	 * @param charsetName 读取配置文件时使用的字符编码
	 * @return
	 * @throws IOException 假如读取文件失败，则抛出该异常
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如给定的参数【propertiesFilePath】为空，则抛出该异常
	 */
	public static Email loadFromConfig(String propertiesFilePath, String charsetName) throws IOException {
		AssertUtils.isNotNull(propertiesFilePath, "propertiesFilePath param is null or empty!");;
		return loadFromConfig(new File(propertiesFilePath), charsetName);
	}
	
	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesFilePath 配置文件
	 * @return
	 * @throws IOException 假如读取文件失败，则抛出该异常
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如给定的参数为空，则抛出该异常
	 */
	public static Email loadFromConfig(String propertiesFilePath) throws IOException {
		return loadFromConfig(propertiesFilePath, XLPStringUtil.EMPTY);
	}
	
	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesReader 配置文件输入字符流
	 * @return
	 * @throws IOException 假如读取文件失败，则抛出该异常
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如给定的参数为null，则抛出该异常
	 */
	public static Email loadFromConfig(Reader propertiesReader) throws IOException {
		AssertUtils.isNotNull(propertiesReader, "propertiesReader param is null!");
		Properties emailConfig = new Properties();
		emailConfig.load(propertiesReader);
		return loadFromConfig(emailConfig);
	}

	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesInputStream 配置文件输入流
	 * @return
	 * @throws IOException 假如读取文件失败，则抛出该异常
	 * @throws NullPointerException 假如给定的参数为null，则抛出该异常
	 */
	public static Email loadFromConfig(InputStream propertiesInputStream) throws IOException {
		AssertUtils.isNotNull(propertiesInputStream, "propertiesInputStream param is null!");
		Properties emailConfig = new Properties();
		emailConfig.load(propertiesInputStream);
		return loadFromConfig(emailConfig);
	}
	
	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesFile 配置文件
	 * @return
	 * @throws IOException 假如读取文件失败，则抛出该异常
	 * @throws IllegalObjectException 假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException 假如给定的参数【propertiesFile】为空，则抛出该异常
	 */
	public static Email loadFromConfig(File propertiesFile) throws IOException {
		return loadFromConfig(propertiesFile, XLPStringUtil.EMPTY);
	}
	
	/**
	 * 用配置文件加载出Email对象
	 * <p>
	 * mail.host=smtp.163.com 发送邮件主机
	 * </br>
	 * mail.username=xxx 发送邮件的邮箱用户名
	 * </br>
	 * mail.password=xx 发送邮件的邮箱用密码
	 * </br>
	 * mail.from=xx 发送邮件的邮箱地址
	 * </br>
	 * mail.subject=xxx 邮件标题
	 * </br>
	 * mail.content=xxx 邮件内容
	 * </br>
	 * mail.validate=true 是有使用用户名和密码进行验证，默认使用（true）
	 * </br>
	 * mail.charSetName=xxx 发送邮件时使用的字符编码，默认utf-8
	 * </br>
	 * mail.openDebug=false 是否开启debug模式  默认false
	 * </br>
	 * mail.protocol=xx 发邮件的协议默认smtp
	 * </br>
	 * mail.openSSLConnect=true 是否开启SSL连接 默认使用
	 * </br>
	 * mail.nickname=xxx 发件人昵称
	 * </p>
	 * 
	 * @param propertiesConfig Properties配置对象
	 * @return
	 * @throws NullPointerException 假如给定的参数为null，则抛出该异常
	 */
	public static Email loadFromConfig(Properties propertiesConfig){
		AssertUtils.isNotNull(propertiesConfig, "propertiesConfig param is null!");
		Properties emailConfig = propertiesConfig;
		Email email = new Email();
		String value = emailConfig.getProperty("mail.host");
		email.setServerHost(XLPStringUtil.isEmpty(value) ? DEFAULT_SERVER_HOST: value);
		email.setUsername(emailConfig.getProperty("mail.username"));
		email.setPassword(emailConfig.getProperty("mail.password"));
		email.setFrom(emailConfig.getProperty("mail.from"));
		email.setSubject(emailConfig.getProperty("mail.subject", ""));
		email.setContent(emailConfig.getProperty("mail.content", ""));
		value = emailConfig.getProperty("mail.port");
		email.setServerPort(XLPStringUtil.isEmpty(value) ? -1 : Integer
				.parseInt(value));
		value = emailConfig.getProperty("mail.validate");
		email.setValidate(XLPStringUtil.isEmpty(value) ? true : Boolean
				.parseBoolean(value));
		value = emailConfig.getProperty("mail.charSetName");
		email.setCharSetName(XLPStringUtil.isEmpty(value) ? DEFAULT_CHARSET_NAME : value);
		value = emailConfig.getProperty("mail.openDebug");
		email.setOpenDebug(XLPStringUtil.isEmpty(value) ? false : Boolean
				.parseBoolean(value));
		value = emailConfig.getProperty("mail.protocol");
		email.setProtocol(XLPStringUtil.isEmpty(value) ? DEFAULT_PROTOCOL : value);
		value = emailConfig.getProperty("mail.openSSLConnect");
		email.setOpenSSLConnect(XLPStringUtil.isEmpty(value) ? true : Boolean
				.parseBoolean(value));
		email.setNickname(emailConfig.getProperty("mail.nickname", ""));
		return email;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Email [bcc=");
		builder.append(Arrays.toString(bcc));
		builder.append(", cc=");
		builder.append(Arrays.toString(cc));
		builder.append(", charSetName=");
		builder.append(charSetName);
		builder.append(", content=");
		builder.append(content);
		builder.append(", from=");
		builder.append(from);
		builder.append(", nickname=");
		builder.append(nickname);
		builder.append(", openDebug=");
		builder.append(openDebug);
		builder.append(", openSSLConnect=");
		builder.append(openSSLConnect);
		builder.append(", password=");
		builder.append(password);
		builder.append(", protocol=");
		builder.append(protocol);
		builder.append(", serverHost=");
		builder.append(serverHost);
		builder.append(", serverPort=");
		builder.append(serverPort);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", to=");
		builder.append(Arrays.toString(to));
		builder.append(", username=");
		builder.append(username);
		builder.append(", validate=");
		builder.append(validate);
		builder.append("]");
		return builder.toString();
	}
}
