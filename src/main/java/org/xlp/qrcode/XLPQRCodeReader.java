package org.xlp.qrcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.xlp.assertion.AssertUtils;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.io.XLPIOUtil;

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/**
 * 基于zxing对二维码内容的读取
 * 
 * @author 徐龙平
 *         <p>
 *         2020-05-11
 *         </p>
 * @version 1.0
 * 
 */
public class XLPQRCodeReader {
	// 字符编码格式
	private String charsetName = "utf-8";
	
	/**
	 * 解码结果
	 */
	private Result result;

	public XLPQRCodeReader(String charsetName) {
		setCharsetName(charsetName);
	}

	public XLPQRCodeReader() {
	}

	public String getContent() {
		if (result != null) {
			return result.getText();
		}
		return null;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		if (!XLPStringUtil.isEmpty(charsetName)) {
			this.charsetName = charsetName.trim();
		}
	}

	/**
	 * 读取二维码中的内容
	 * 
	 * @param inputStream 二维码流
	 * @throws Exception 假如读取异常时，抛出该异常
	 */
	public void read(InputStream inputStream) throws Exception {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!"); 
		BufferedImage image = ImageIO.read(inputStream);
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
		Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, charsetName);
		// 对图像进行解码
		result = new MultiFormatReader().decode(binaryBitmap, hints);
	}

	/**
	 * 读取二维码中的内容
	 * 
	 * @param fileName 二维码文件名称
	 * @throws Exception 假如读取异常时，抛出该异常
	 */
	public void read(String fileName) throws Exception {
		AssertUtils.isNotNull(fileName, "fileName param is null");
		read(new File(fileName.trim())); 
	}
	
	/**
	 * 读取二维码中的内容
	 * 
	 * @param file 二维码文件
	 * @throws Exception 假如读取异常时，抛出该异常
	 */
	public void read(File file) throws Exception {
		AssertUtils.assertFile(file);; 
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			read(inputStream); 
		} finally {
			XLPIOUtil.closeInputStream(inputStream);
		}
	}
	
	public Result getResult() {
		return result;
	}
}
