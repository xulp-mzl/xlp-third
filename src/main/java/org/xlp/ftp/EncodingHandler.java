package org.xlp.ftp;

import java.io.UnsupportedEncodingException;

/**
 * <p>创建时间：2020年8月10日 下午11:31:54</p>
 * @author xlp
 * @version 1.0 
 * @Description 字符串不同编码中转换处理器接口
*/
public interface EncodingHandler {
	/**
	 * 把原有编码的字符串转换成另一种编码的字符串
	 * 
	 * @param source 源字符串
	 * @param charsetName 源字符串字符编码
	 * @return 转换成另一种编码的字符串
	 * @throws UnsupportedEncodingException 假如给定的编码不存在，则抛出该异常
	 */
	public String convert(String source, String charsetName) throws UnsupportedEncodingException;
}
