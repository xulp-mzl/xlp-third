package org.xlp.ftp;

import java.io.UnsupportedEncodingException;

import org.xlp.consts.CharsetNameConsts;
import org.xlp.utils.XLPStringUtil;

/**
 * <p>创建时间：2020年8月10日 下午11:39:42</p>
 * @author xlp
 * @version 1.0 
 * @Description 文件名称字符编码转换
*/
public class FileNameEncodingHandler implements EncodingHandler{
	@Override
	public String convert(String source, String charsetName) 
			throws UnsupportedEncodingException {
		if (source == null || XLPStringUtil.isEmpty(charsetName)) {
			return source;
		}
		return new String(source.getBytes(charsetName), CharsetNameConsts.ISO_8859_1);
	}

}
