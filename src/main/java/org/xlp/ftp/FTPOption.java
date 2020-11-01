package org.xlp.ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;
import org.xlp.consts.CharsetNameConsts;
import org.xlp.utils.XLPStackTraceUtil;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.XLPSystemParamUtil;
import org.xlp.utils.io.XLPIOUtil;

/**
 * <p>
 * 创建时间：2020年8月1日 下午6:52:10
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description 简化操作，提供相对简便的上传文件到ftp服务器的操作，和从ftp服务器下载文件操作
 */
public class FTPOption {
	/**
	 * 默认字符编码GBK
	 */
	private static final String DEFAULT_CAHARSET_NAME = CharsetNameConsts.GBK;

	/**
	 * ftp服务更目录
	 */
	private static final String FTP_ROOT_PATH = "/";

	/**
	 * ftp默认端口号
	 */
	private static final int DEFAULT_FTP_PORT = 21;

	/**
	 * ftp服务器IP地址
	 */
	private String ftpIp;

	/**
	 * ftp服务器端口号
	 */
	private int ftpPort = DEFAULT_FTP_PORT;

	/**
	 * 登入ftp服务器的用户名
	 */
	private String username;

	/**
	 * 登入ftp服务器的密码
	 */
	private String password;

	/**
	 * ftp服务器文件的根目录
	 */
	private String ftpBasePath;

	/**
	 * 传送文件时所用字符编码
	 */
	private String charsetName = DEFAULT_CAHARSET_NAME;

	/**
	 * 连接超时时间，默认3秒
	 */
	private int timeout = 3000;

	/**
	 * 文件操作是产生的错误信息
	 */
	private String errorMsg;

	/**
	 * 文件名称或目录字符编码处理器
	 */
	private EncodingHandler encodingHandler = new FileNameEncodingHandler();

	/**
	 * FTPClient
	 */
	private volatile FTPClient ftpClient;

	/**
	 * 标记是否调用过preFTPOption函数，避免重复调用该函数去重复初始化FTPClient
	 */
	private volatile boolean isCalledPreFTPOptionFun = false;

	/**
	 * 构造函数
	 * 
	 * @param ftpIp
	 * @param username
	 * @param password
	 */
	public FTPOption(String ftpIp, String username, String password) {
		this.ftpIp = ftpIp;
		this.username = username;
		this.password = password;
	}

	/**
	 * 构造函数
	 * 
	 * @param ftpIp
	 * @param ftpPort
	 * @param username
	 * @param password
	 */
	public FTPOption(String ftpIp, int ftpPort, String username, String password) {
		this.ftpIp = ftpIp;
		this.ftpPort = ftpPort;
		this.username = username;
		this.password = password;
	}

	/**
	 * 构造函数
	 * 
	 * @param ftpIp
	 * @param ftpPort
	 * @param username
	 * @param password
	 * @param ftpBasePath
	 */
	public FTPOption(String ftpIp, int ftpPort, String username, String password, String ftpBasePath) {
		this.ftpIp = ftpIp;
		this.ftpPort = ftpPort;
		this.username = username;
		this.password = password;
		this.ftpBasePath = ftpBasePath;
	}

	/**
	 * 完成初始化FTPClient，连接到ftp服务器功能，在调用上传文件，删除文件，下载文件等功能前，需调用该函数
	 * 
	 * @return 假如初始化FTPClient，连接到ftp服务器返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如初始化FTPClient时出现错误，则抛出该异常
	 */
	public void preFTPOption() throws FTPOptionException {
		isCalledPreFTPOptionFun = true;

		ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(timeout);
		try {
			// 连接FTP服务器
			ftpClient.connect(ftpIp, ftpPort);
			// 是否成功登录FTP服务器
			if (!ftpClient.login(username, password)) {
				throw new FTPOptionException("登入ftp服务器失败！" + formatTips());
			}
		} catch (IOException e) {
			throw new FTPOptionException(e);
		}
		// 是否成功登录连接到FTP服务器
		int replyCode = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			throw new FTPOptionException("ftp服务器连接失败！" + formatTips());
		}

		ftpBasePath = XLPStringUtil.emptyTrim(ftpBasePath).replace("\\", "/");
		ftpBasePath = ftpBasePath.startsWith("/") ? ftpBasePath : ("/" + ftpBasePath);

		try {
			// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（charsetName变量值）.
			if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
				charsetName = CharsetNameConsts.UTF_8;
			}
			// 设置字符编码格式
			ftpClient.setCharset(Charset.forName(charsetName));
			ftpClient.setControlEncoding(charsetName);
			// 设置被动模式
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		
			String tempFtpBasePath = encodingHandler.convert(ftpBasePath, charsetName);
			// 切换到ftp根目录
			boolean success = ftpClient.changeWorkingDirectory(tempFtpBasePath);
			// 判断ftp根目录是否存在
			if (!success) {
				throw new FTPOptionException("ftp服务器【" + ftpBasePath + "】目录不存在！");
			}
		} catch (IOException e) {
			throw new FTPOptionException(e);
		}
	}

	/**
	 * 切换到ftp服务root目录
	 * 
	 * @throws FTPOptionException
	 *             假如切换到ftp服务root目录失败，则抛出该异常
	 */
	public void changeRootDir() throws FTPOptionException {
		changeTargetDir(FTP_ROOT_PATH);
	}

	/**
	 * 切换到ftp服务基础目录即变量ftpBasePath值所指的目录
	 * 
	 * @throws FTPOptionException
	 *             假如ftp服务基础目录即变量ftpBasePath值所指的目录失败，则抛出该异常
	 */
	public void changeBaseDir() throws FTPOptionException {
		changeTargetDir(ftpBasePath);
	}

	/**
	 * 切换到ftp服务目标目录
	 * 
	 * @param remotePath
	 *            ftp服务目标目录
	 * @throws FTPOptionException
	 *             假如切换到ftp服务目标目录失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如给定的参数为空抛出该异常
	 */
	public void changeTargetDir(String remotePath) throws FTPOptionException {
		AssertUtils.isNotNull(remotePath, "remotePath param is null or empty！");

		remotePath = XLPStringUtil.emptyTrim(remotePath).replace("\\", "/");

		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}
		try {
			String tempRemotePath = encodingHandler.convert(remotePath, charsetName);
			if (!ftpClient.changeWorkingDirectory(tempRemotePath)) {
				throw new FTPOptionException("切换到ftp服务目标目录【" + remotePath + "】失败！");
			}
		} catch (IOException e) {
			throw new FTPOptionException(e);
		}
	}

	/**
	 * 创建目录
	 * <p>
	 * 假如与'/'开头，从root目录开始往下一直创建目标目录，否则从当前目录开始往下一直创建目标目录
	 * </p>
	 * 
	 * @param dir
	 *            需要创建的目录，假如参数为空，则不创建
	 * @throws FTPOptionException
	 *             假如目录创建失败，则抛出该异常
	 */
	public void makeDirs(String dir) throws FTPOptionException {
		dir = XLPStringUtil.emptyTrim(dir);
		// 创建目录
		if (!dir.isEmpty()) {
			dir = dir.replace("\\", "/");
			// 初始化FTP客户端
			if (!isCalledPreFTPOptionFun) {
				preFTPOption();
			}

			if (dir.startsWith("/")) {
				changeRootDir();
			}
			String[] dirs = dir.split("/");
			String tempDir;
			try {
				for (String dir1 : dirs) {
					if (!XLPStringUtil.isEmpty(dir1)) {
						tempDir = encodingHandler.convert(dir1, charsetName);
						// 跳到相应的文件夹
						boolean success = ftpClient.changeWorkingDirectory(tempDir);
						// 假如该文件夹不存在，则创建
						if (!success) {
							// 创建文件夹
							if (!ftpClient.makeDirectory(tempDir)) {
								throw new FTPOptionException("ftp服务器文件目录【" + dir + "】创建失败！");
							}
							ftpClient.changeWorkingDirectory(tempDir);
						}
					}
				}
			} catch (IOException e) {
				throw new FTPOptionException(e);
			}
		}
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @param fileName
	 *            文件名称
	 * @param inputStream
	 *            要上传的文件流
	 * @return 假如上传成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如上传文件到ftp服务器时出现错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如第二个或第三个参数为空，则抛出该异常
	 */
	public boolean uploadToFTP(String filePath, String fileName, InputStream inputStream) throws FTPOptionException {
		AssertUtils.isNotNull(fileName, "fileName param is empty or null!");
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");

		// 初始化FTP客户端
		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		fileName = fileName.trim();
		String currentDir = null;
		try {
			// 获取当前目录
			currentDir = ftpClient.printWorkingDirectory();
			// 创建目录
			makeDirs(filePath);
			// 在ftp服务器上保存文件
			String tempFileName = encodingHandler.convert(fileName, charsetName);
			if (!ftpClient.storeFile(tempFileName, inputStream)) {
				throw new FTPOptionException("ftp服务器文件【" + fileName + "】保存失败！");
			}
		} catch (IOException e) {
			throw new FTPOptionException(e);
		} finally {
			changeSourceDir(currentDir);
		}
		return true;
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param fileName
	 *            文件名称
	 * @param inputStream
	 *            要上传的文件流
	 * @return 假如上传成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如上传文件到ftp服务器时出现错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean uploadToFTP(String fileName, InputStream inputStream) throws FTPOptionException {
		return uploadToFTP(XLPStringUtil.EMPTY, fileName, inputStream);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @param file
	 *            要上传的文件
	 * @return 假如上传成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如上传文件到ftp服务器时出现错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如第二个参数为null，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public boolean uploadToFTP(String filePath, File file) throws FTPOptionException {
		AssertUtils.isNotNull(file, "file param is null!");
		AssertUtils.assertFile(file);

		InputStream inputStream = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			inputStream = new BufferedInputStream(fileInputStream);
			return uploadToFTP(filePath, file.getName(), inputStream);
		} catch (FileNotFoundException e) {
		} finally {
			XLPIOUtil.closeInputStream(fileInputStream);
			XLPIOUtil.closeInputStream(inputStream);
		}
		return true;
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param file
	 *            要上传的文件
	 * @return 假如上传成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如上传文件到ftp服务器时出现错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public boolean uploadToFTP(File file) throws FTPOptionException {
		return uploadToFTP(XLPStringUtil.EMPTY, file);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @param files
	 *            要上传的文件集合
	 * @param ignoreError
	 *            是否忽略文件上传过程中的错误，true忽略，false不忽略
	 * @return 查看错误信息可以调用{@link getErrorMsg()}
	 * @throws FTPOptionException
	 *             假如初始化ftp客户端失败或不忽略文件上传时产生的错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如 要上传的文件集合为null，则抛出该异常
	 */
	public void uploadToFTP(String filePath, List<File> files, boolean ignoreError) throws FTPOptionException {
		AssertUtils.isNotNull(files, "files param is null!");
		StringBuilder builder = new StringBuilder();
		// 初始化FTP客户端
		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		String currentDir = null;
		try {
			// 获取当前目录
			currentDir = ftpClient.printWorkingDirectory();
		} catch (IOException e1) {
			throw new FTPOptionException(e1);
		}
		// 创建目录
		makeDirs(filePath);

		InputStream inputStream = null;
		FileInputStream fileInputStream = null;
		try {
			for (File file : files) {
				// 在ftp服务器上保存文件
				if (file != null && file.exists() && file.isFile()) {
					String fileName = file.getName();
					try {
						String tempFileName = encodingHandler.convert(fileName, charsetName);
						fileInputStream = new FileInputStream(file);
						inputStream = new BufferedInputStream(fileInputStream);
						if (!ftpClient.storeFile(tempFileName, inputStream) && !ignoreError) {
							throw new FTPOptionException("ftp服务器文件【" + fileName + "】保存失败！");
						}
					} catch (IOException e) {
						if (ignoreError) {
							builder.append(XLPStackTraceUtil.getStackTrace(e))
									.append(XLPSystemParamUtil.getSystemNewline());
						} else {
							throw new FTPOptionException(e);
						}
					} finally {
						XLPIOUtil.closeInputStream(fileInputStream);
						XLPIOUtil.closeInputStream(inputStream);
					}

				}
			}
		} finally {
			changeSourceDir(currentDir);
		}
		if (ignoreError) {
			errorMsg = builder.toString();
		}
	}

	/**
	 * 跳转原始目录
	 * 
	 * @param currentDir
	 */
	private void changeSourceDir(String currentDir) {
		if (currentDir != null) {
			try {
				ftpClient.changeWorkingDirectory(currentDir);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @param files
	 *            要上传的文件集合
	 * @return
	 * @throws FTPOptionException
	 *             假如上传文件时出错，则抛出该异常
	 * @throws NullPointerException
	 *             假如 要上传的文件集合为null，则抛出该异常
	 */
	public void uploadToFTP(String filePath, List<File> files) throws FTPOptionException {
		uploadToFTP(filePath, files, false);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param files
	 *            要上传的文件
	 * @throws FTPOptionException
	 *             假如上传文件时出错，则抛出该异常
	 */
	public void uploadToFTP(List<File> files) throws FTPOptionException {
		uploadToFTP(XLPStringUtil.EMPTY, files);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param files
	 *            要上传的文件集合
	 * @param ignoreError
	 *            是否忽略文件上传过程中的错误，true忽略，false不忽略
	 * @return 查看错误信息可以调用{@link getErrorMsg()}
	 * @throws FTPOptionException
	 *             假如初始化ftp客户端失败或不忽略文件上传时产生的错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如 要上传的文件集合为null，则抛出该异常
	 */
	public void uploadToFTP(List<File> files, boolean ignoreError) throws FTPOptionException {
		uploadToFTP(XLPStringUtil.EMPTY, files, ignoreError);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @param fileName
	 *            要上传的文件
	 * @return 假如上传成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如上传文件失败，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如第二个参数为空，则抛出该异常
	 */
	public boolean uploadToFTP(String filePath, String fileName) throws FTPOptionException {
		AssertUtils.isNotNull(fileName, "fileName param is null or empty!");
		return uploadToFTP(filePath, new File(fileName));
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param fileName
	 *            要上传的文件
	 * @return 假如上传成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如上传文件失败，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean uploadToFTP(String fileName) throws FTPOptionException {
		return uploadToFTP(XLPStringUtil.EMPTY, fileName);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param files
	 *            要上传的文件
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @param ignoreError
	 *            是否忽略文件上传过程中的错误，true忽略，false不忽略
	 * @return 查看错误信息可以调用{@link getErrorMsg()}
	 * @throws FTPOptionException
	 *             假如初始化ftp客户端失败或不忽略文件上传时产生的错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如 要上传的文件集合为null，则抛出该异常
	 */
	public void uploadToFTP(List<String> files, String filePath, boolean ignoreError) throws FTPOptionException {
		AssertUtils.isNotNull(files, "files params is null!");
		List<File> fileList = new ArrayList<File>(files.size());
		for (String fileName : files) {
			if (!XLPStringUtil.isEmpty(fileName)) {
				File file = new File(fileName);
				if (file.exists() && file.isFile()) {
					fileList.add(file);
				}
			}
		}
		uploadToFTP(filePath, fileList, ignoreError);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param files
	 *            要上传的文件
	 * @param filePath
	 *            文件在ftp服务器的路径，是ftp根目录后部分文件路径（格式：22/33/ee/或/22/33/ee/） <br/>
	 *            假如改参数为空，则上传在ftp服务器的根目录
	 *            <p>
	 *            假如与'/'开头，从root目录开始往下一直转到所指的目录，否则从当前目录开始往下一直转到所指的目录
	 *            </p>
	 * @return
	 * @throws FTPOptionException
	 *             假如上传文件时出错，则抛出该异常
	 * @throws NullPointerException
	 *             假如 要上传的文件集合为null，则抛出该异常
	 */
	public void uploadToFTP(List<String> files, String filePath) throws FTPOptionException {
		uploadToFTP(files, filePath, false);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param dirFile
	 *            要上传的文件目录和其下面的文件
	 * @param ftpDir
	 *            ftp服务器上的目录
	 * @param ignoreError
	 *            是否忽略文件上传过程中的错误，true忽略，false不忽略
	 * @return 查看错误信息可以调用{@link getErrorMsg()}
	 * @throws FTPOptionException
	 *             假如初始化ftp客户端失败或不忽略文件上传时产生的错误，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件目录不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如第一个参数为空，则抛出该异常
	 */
	public void uploadDirToFTP(File dirFile, String ftpDir, boolean ignoreError) throws FTPOptionException {
		AssertUtils.isNotNull(dirFile, "dirFile param is null!");
		AssertUtils.fileIsExits(dirFile, "【" + dirFile.getPath() + "】该要上传的目录不存在！");

		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		ftpDir = XLPStringUtil.emptyTrim(ftpDir).replace("\\", "/");
		String tempDir = ftpDir;
		if (!ftpDir.isEmpty() && !ftpDir.endsWith("/")) {
			tempDir = ftpDir + "/";
		}

		StringBuilder sb = new StringBuilder();
		File[] files = dirFile.listFiles();
		for (File file : files) {
			_uploadToFTP(file, tempDir, sb, ignoreError);
		}
		if (ignoreError) {
			errorMsg = sb.toString();
		}
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param dirFile
	 *            要上传的文件目录和其下面的文件
	 * @param ftpDir
	 *            ftp服务器上的目录
	 * @throws FTPOptionException
	 *             假如文件上传时产生的错误，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件目录不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如第一个参数为空，则抛出该异常
	 */
	public void uploadDirToFTP(File dirFile, String ftpDir) throws FTPOptionException {
		uploadDirToFTP(dirFile, ftpDir, false);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param dirFile
	 *            要上传的文件目录和其下面的文件
	 * @throws FTPOptionException
	 *             假如文件上传时产生的错误，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件目录不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public void uploadDirToFTP(File dirFile) throws FTPOptionException {
		uploadDirToFTP(dirFile, XLPStringUtil.EMPTY);
	}

	/**
	 * 上传到ftp服务器
	 * 
	 * @param dirFile
	 *            要上传的文件目录和其下面的文件
	 * @param ignoreError
	 *            是否忽略文件上传过程中的错误，true忽略，false不忽略
	 * @return 查看错误信息可以调用{@link getErrorMsg()}
	 * @throws FTPOptionException
	 *             假如初始化ftp客户端失败或不忽略文件上传时产生的错误，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件目录不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如第一个参数为空，则抛出该异常
	 */
	public void uploadDirToFTP(File dirFile, boolean ignoreError) throws FTPOptionException {
		uploadDirToFTP(dirFile, XLPStringUtil.EMPTY, ignoreError);
	}

	/**
	 * 递归上传文件
	 * 
	 * @param dirFile
	 *            要上传的文件目录和其下面的文件
	 * @param dir
	 * @param sb
	 *            存储错误信息
	 * @param ignoreError
	 * @throws FTPOptionException
	 */
	private void _uploadToFTP(File dirFile, String dir, StringBuilder sb, boolean ignoreError)
			throws FTPOptionException {
		if (dirFile.isFile()) {
			try {
				uploadToFTP(dir, dirFile);
			} catch (FTPOptionException e) {
				if (ignoreError) {
					sb.append(XLPStackTraceUtil.getStackTrace(e)).append(XLPSystemParamUtil.getSystemNewline());
				} else {
					throw e;
				}
			}
		} else {
			dir += dirFile.getName() + "/";
			File[] files = dirFile.listFiles();
			for (File file : files) {
				_uploadToFTP(file, dir, sb, ignoreError);
			}
		}
	}

	/**
	 * 关闭资源
	 */
	public void close() {
		if (ftpClient != null) {
			try {
				// FTP退出
				ftpClient.logout();
			} catch (IOException e) {
			} finally {
				// 断开FTP连接
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
					} catch (IOException e) {
					}
				}
			}
			ftpClient = null;
		}
	}

	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String ftpIp) {
		this.ftpIp = ftpIp;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
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

	public String getFtpBasePath() {
		return ftpBasePath;
	}

	public void setFtpBasePath(String ftpBasePath) {
		this.ftpBasePath = ftpBasePath;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		if (XLPStringUtil.isEmpty(charsetName)) {
			charsetName = DEFAULT_CAHARSET_NAME;
		}
		this.charsetName = charsetName;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FTPOption [ftpIp=").append(ftpIp).append(", ftpPort=").append(ftpPort).append(", username=")
				.append(username).append(", password=").append(password).append(", ftpBasePath=").append(ftpBasePath)
				.append(", charsetName=").append(charsetName).append(", errorMsg=").append(errorMsg).append("]");
		return builder.toString();
	}

	/**
	 * 格式化提示信息
	 * 
	 * @return
	 */
	private String formatTips() {
		StringBuilder builder = new StringBuilder();
		builder.append("[ftpIp=").append(ftpIp).append(", ftpPort=").append(ftpPort).append(", username=")
				.append(username).append(", password=").append(password).append("]");
		return builder.toString();
	}

	public int getTimeout() {
		return timeout;
	}

	/**
	 * 设置超时时间，单位毫秒
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 获取文件名称或目录字符编码处理器
	 */
	public EncodingHandler getEncodingHandler() {
		return encodingHandler;
	}

	/**
	 * 设置文件名称或目录字符编码处理器
	 */
	public void setEncodingHandler(EncodingHandler encodingHandler) {
		if (encodingHandler != null) {
			this.encodingHandler = encodingHandler;
		}
	}

	/**
	 * 删除ftp上的指定文件
	 * 
	 * @param remoteFilePath
	 * @return 假如删除成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如删除过程中有错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean deleteFile(String remoteFilePath) throws FTPOptionException {
		AssertUtils.isNotNull(remoteFilePath, "remoteFilePath param is null or empty!");
		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		remoteFilePath = XLPStringUtil.emptyTrim(remoteFilePath).replace("\\", "/");

		try {
			String temp = encodingHandler.convert(remoteFilePath, charsetName);
			return ftpClient.deleteFile(temp);
		} catch (IOException e) {
			throw new FTPOptionException("删除文件【" + remoteFilePath + "】失败！", e);
		}
	}

	/**
	 * 删除ftp上的指定文件夹其下的子文件夹和子文件
	 * 
	 * @param remoteDirPath
	 * @return 假如删除成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如删除过程中有错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean deleteDir(String remoteDirPath) throws FTPOptionException {
		AssertUtils.isNotNull(remoteDirPath, "remoteDirPath param is null or empty!");
		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		remoteDirPath = XLPStringUtil.emptyTrim(remoteDirPath).replace("\\", "/");

		String currentDir = null;
		try {
			currentDir = ftpClient.printWorkingDirectory();
			String temp = encodingHandler.convert(remoteDirPath, charsetName);
			FTPFile[] files = ftpClient.listFiles(temp);
			if (!temp.startsWith("/")) {
				temp = currentDir + "/" + temp;
			}
			for (FTPFile ftpFile : files) {
				_deleteDir(ftpFile, temp, remoteDirPath);
			}
		} catch (IOException e) {
			throw new FTPOptionException("删除文件或文件夹【" + remoteDirPath + "】失败！", e);
		} finally {
			changeSourceDir(currentDir);
		}
		return true;
	}

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param ftpFile
	 * @param dir
	 * @param remoteDirPath
	 * @return
	 * @throws IOException
	 * @throws FTPOptionException
	 */
	private boolean _deleteDir(FTPFile ftpFile, String dir, String remoteDirPath)
			throws IOException, FTPOptionException {
		String name = ftpFile.getName();
		String path = dir;
		//判断当前路径是否是目录
		if (ftpClient.changeWorkingDirectory(path)) { 
			if (!dir.endsWith("/")) {
				dir = dir + "/";
			}
			path = dir + encodingHandler.convert(name, charsetName);
			
			if (!remoteDirPath.endsWith("/")) {
				remoteDirPath += "/" + name;
			} else {
				remoteDirPath += name;
			}
		}

		if (ftpFile.isFile()) {
			if (!ftpClient.deleteFile(path)) {
				throw new FTPOptionException("删除文件【" + remoteDirPath + "】失败！");
			}
		} else {
			FTPFile[] files = ftpClient.listFiles(path);
			for (FTPFile ftpFile2 : files) {
				_deleteDir(ftpFile2, path, remoteDirPath);
			}
			if (!ftpClient.removeDirectory(path)) {
				throw new FTPOptionException("删除文件夹【" + remoteDirPath + "】失败！");
			}
		}
		return true;
	}

	/**
	 * 文件或文件夹重命名
	 * 
	 * @param src
	 *            要重命名的文件或文件夹名称
	 * @param target
	 *            新的文件或文件夹名称
	 * @return 假如文件或文件夹重命名成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如文件或文件夹名称重命名失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean rename(String src, String target) throws FTPOptionException {
		AssertUtils.isNotNull(src, "src param is null or empty!");
		AssertUtils.isNotNull(target, "target param is null or empty!");

		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		src = XLPStringUtil.emptyTrim(src).replace("\\", "/");
		target = XLPStringUtil.emptyTrim(target).replace("\\", "/");
		try {
			String tempSrc = encodingHandler.convert(src, charsetName);
			String tempTarget = encodingHandler.convert(target, charsetName);
			if (!ftpClient.rename(tempSrc, tempTarget)) {
				throw new FTPOptionException("文件或文件夹名称重命名失败！");
			}
		} catch (IOException e) {
			throw new FTPOptionException(e);
		}
		return true;
	}

	/**
	 * 下载文件或文件夹
	 * 
	 * @param remotePath
	 *            ftp服务器文件或文件夹路径
	 * @param localFile
	 *            下载到本地的文件对象
	 * @return 假如下载成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如下载失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean downloadFromFtp(String remotePath, File localFile) throws FTPOptionException {
		AssertUtils.isNotNull(remotePath, "remotePath param is null or empty!");
		AssertUtils.isNotNull(localFile, "localFile param is null!");

		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		remotePath = XLPStringUtil.emptyTrim(remotePath).replace("\\", "/");

		boolean localFileIsFile = localFile.exists() && localFile.isFile();

		File dir = localFile.getParentFile();
		// 创建文件夹
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String currentDir = null;
		OutputStream outputStream = null;
		try {
			currentDir = ftpClient.printWorkingDirectory();
			
			String temp = encodingHandler.convert(remotePath, charsetName);
			FTPFile[] files = ftpClient.listFiles(temp);
			if (files.length == 1 && files[0].isFile()) {
				String name = files[0].getName();
				int index = name.lastIndexOf(".");
				String ext = "";
				if (index != -1) {
					ext = name.substring(index);
				}
				
				if (!localFile.getPath().endsWith(ext) && !localFileIsFile) {
					if (!localFile.exists()) {
						localFile.mkdirs();
					}
					localFile = new File(localFile, name);
				}
				
				//判断当前路径是否是目录
				if (ftpClient.changeWorkingDirectory(temp)) { 
					if (!remotePath.endsWith("/")) {
						remotePath += "/";
					}
					remotePath += name;
					temp = encodingHandler.convert(remotePath, charsetName);
				}
				
				try {
					outputStream = new FileOutputStream(localFile);
					if (!ftpClient.retrieveFile(temp, outputStream)) {
						throw new FTPOptionException("下载文件【" + remotePath + "】失败！");
					}
				} finally {
					XLPIOUtil.closeOutputStream(outputStream);
				}
			} else {
				File tempFile = localFileIsFile ? dir : localFile;
				for (FTPFile ftpFile : files) {
					_downloadFromFtp(ftpFile, remotePath, tempFile);
				}
			}
		} catch (IOException e) {
			throw new FTPOptionException("下载文件或文件夹【" + remotePath + "】失败！", e);
		} finally {
			changeSourceDir(currentDir);
		}

		return true;
	}

	/**
	 * 递归下载文件
	 * 
	 * @param ftpFile
	 * @param remotePath
	 * @param localFile
	 * @throws IOException 
	 * @throws FTPOptionException 
	 */
	private void _downloadFromFtp(FTPFile ftpFile, String remotePath, File localFile) 
			throws IOException, FTPOptionException {
		String name = ftpFile.getName();
		
		if (!remotePath.endsWith("/")) {
			remotePath = remotePath + "/";
		}
		remotePath += name;
		
		String tempPath = encodingHandler.convert(remotePath, charsetName);
		if (ftpFile.isFile()) {
			//下载文件
			if (!localFile.exists()) {
				//创建文件夹
				localFile.mkdirs();
			}
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(new File(localFile, name)); 
				if (!ftpClient.retrieveFile(tempPath, outputStream)) {
					throw new FTPOptionException("下载文件【" + remotePath + "】失败！");
				}
			} finally {
				XLPIOUtil.closeOutputStream(outputStream); 
			}
		}else {
			//递归查找文件
			FTPFile[] files = ftpClient.listFiles(tempPath);
			for (FTPFile ftpFile2 : files) {
				_downloadFromFtp(ftpFile2, remotePath, new File(localFile, name));
			}
		}
	}
	
	/**
	 * 下载文件或文件夹
	 * 
	 * @param remotePath
	 *            ftp服务器文件或文件夹路径
	 * @param localFilePath
	 *            下载到本地的文件对象
	 * @return 假如下载成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如下载失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean downloadFromFtp(String remotePath, String localFilePath) throws FTPOptionException {
		AssertUtils.isNotNull(remotePath, "remotePath param is null or empty!");
		AssertUtils.isNotNull(localFilePath, "localFilePath param is null or empty!");
		return downloadFromFtp(remotePath, new File(localFilePath)); 
	}
	
	/**
	 * 把ftp服务器上的指定文件放入指定的输出流中
	 * 
	 * @param remotePath
	 *            ftp服务器文件或文件夹路径
	 * @param outputStream
	 *            文件输出流
	 * @return 假如下载成功返回true, 否则返回false
	 * @throws FTPOptionException
	 *             假如下载失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	public boolean downloadFromFtp(String remotePath, OutputStream outputStream) throws FTPOptionException {
		AssertUtils.isNotNull(remotePath, "remotePath param is null or empty!");
		AssertUtils.isNotNull(outputStream, "outputStream param is null!");
		if (!isCalledPreFTPOptionFun) {
			preFTPOption();
		}

		remotePath = XLPStringUtil.emptyTrim(remotePath).replace("\\", "/");
		try {
			String temp = encodingHandler.convert(remotePath, charsetName);
			if (!ftpClient.retrieveFile(temp, outputStream)) {
				throw new FTPOptionException("下载文件【" + remotePath + "】失败！");
			}
		} catch (IOException e) {
			throw new FTPOptionException("下载文件【" + remotePath + "】失败！", e);
		}
		return true;
	}
}
