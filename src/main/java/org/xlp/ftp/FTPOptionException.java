package org.xlp.ftp;
/**
 * <p>创建时间：2020年8月12日 下午11:31:56</p>
 * @author xlp
 * @version 1.0 
 * @Description FTP操作异常描述类
*/
public class FTPOptionException extends Exception{
	private static final long serialVersionUID = -539340671250596784L;

	public FTPOptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public FTPOptionException(String message) {
		super(message);
	}

	public FTPOptionException(Throwable cause) {
		super(cause);
	}
}
