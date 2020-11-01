package org.xlp.pdf;

/**
 * <p>
 * 创建时间：2020年9月29日 下午11:24:48
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description pdf模板读写异常类
 */
public class PDFWriterException extends RuntimeException {
	private static final long serialVersionUID = 4058761891732041100L;

	public PDFWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	public PDFWriterException(String message) {
		super(message);
	}

	public PDFWriterException(Throwable cause) {
		super(cause);
	}
}
