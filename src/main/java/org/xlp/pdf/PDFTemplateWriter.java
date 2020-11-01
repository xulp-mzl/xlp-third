package org.xlp.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.io.XLPIOUtil;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * <p>
 * 创建时间：2020年9月28日 下午11:24:48
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description 简化pdf表单填充类
 */
public class PDFTemplateWriter {
	/**
	 * 标记模板填充后的新pdf文件是否可编辑
	 */
	private boolean canEdit = false;

	/**
	 * pdf输入流
	 */
	private PdfReader reader;

	/**
	 * pdf中转输出流
	 */
	private ByteArrayOutputStream bos;

	/**
	 * 获取pdf表单操作对象
	 */
	private PdfStamper stamper;

	/**
	 * pdf表单对象
	 */
	protected AcroFields form;

	/**
	 * 默认字体编码
	 */
	protected final static String DEFAULT_FONT_ENCONDING = "UniGB-UCS2-H";

	/**
	 * 仿宋字体
	 */
	public static BaseFont BASE_FONT;
	static {
		// 设置字体为中文字体 Adobe 宋体 std L
		try {
			BASE_FONT = BaseFont.createFont("STSongStd-Light", DEFAULT_FONT_ENCONDING, BaseFont.NOT_EMBEDDED);
		} catch (DocumentException | IOException e) {
			System.out.println("仿宋字体创建失败：" + e.getMessage());
		}
	}

	/**
	 * 水印文字默认填充色
	 */
	public static BaseColor DEFAULT_FILL_COLOR = BaseColor.GRAY;
	
	/**
	 * 水印文字默认透明度
	 */
	public static float DEFAULT_OPACTY = 0.2f;
	
	/**
	 * 水印文字默认字体大小
	 */
	public static float DEFAULT_FONT_SIZE = 18f;
	
	/**
	 * 水印文字左边边距
	 */
	public static float DEFAULT_X = 100f;
	
	/**
	 * 水印文字默认底部边距
	 */
	public static float DEFAULT_Y = 100f;
	
	/**
	 * 水印文字默认旋转角度
	 */
	public static float DEFAULT_ROTATION = 30f;
	
	/**
	 * 构造函数
	 * 
	 * @param inputStream
	 *            pdf文件模板输入流
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如pdf读取失败，则抛出该异常
	 */
	public PDFTemplateWriter(InputStream inputStream) {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		init(inputStream);
	}

	/**
	 * 初始化数据
	 * 
	 * @param inputStream
	 * @throws PDFWriterException
	 *             假如pdf读取失败，则抛出该异常
	 */
	private void init(InputStream inputStream) {
		try {
			reader = new PdfReader(inputStream);
			bos = new ByteArrayOutputStream();
			stamper = new PdfStamper(reader, bos);
			form = stamper.getAcroFields();
		} catch (IOException e) {
			throw new PDFWriterException(e);
		} catch (DocumentException e) {
			throw new PDFWriterException(e);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param inputFile
	 *            pdf模板文件
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如pdf读取失败，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public PDFTemplateWriter(File inputFile) {
		AssertUtils.assertFile(inputFile);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputFile);
			init(inputStream);
		} catch (FileNotFoundException e) {
		} finally {
			XLPIOUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param inputFile
	 *            pdf模板文件
	 * @throws NullPointerException
	 *             假如参数为null或为空，则抛出该异常
	 * @throws PDFWriterException
	 *             假如pdf读取失败，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public PDFTemplateWriter(String inputFile) {
		AssertUtils.isNotNull(inputFile, "inputFile param is null or empty!");
		File file = new File(inputFile);
		AssertUtils.assertFile(file);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			init(inputStream);
		} catch (FileNotFoundException e) {
		} finally {
			XLPIOUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * 设置pdf表单文本字段值
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param value
	 *            要设置的值
	 *            <p>
	 *            当为文本框时，设置文本框中的内容
	 *            </p>
	 *            <p>
	 *            当为复选框时，值当取表单属性中->选项->导出值时，该复选框被选中
	 *            </p>
	 *            <p>
	 *            当为单选框时，值当取表单属性中->选项->选择项值时，该单选框被选中
	 *            </p>
	 * @throws PDFWriterException
	 *             假如给表单设置值失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setField(String key, String value) {
		return setField(key, value, true);
	}

	/**
	 * 设置pdf表单文本字段值
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param value
	 *            要设置的值
	 *            <p>
	 *            当为文本框时，设置文本框中的内容
	 *            </p>
	 *            <p>
	 *            当为复选框时，值当取表单属性中->选项->导出值时，该复选框被选中
	 *            </p>
	 *            <p>
	 *            当为单选框时，值当取表单属性中->选项->选择项值时，该单选框被选中
	 *            </p>
	 * @param saveAppearance
	 *            是否保留表单字段的当前外观，值为true时，保留，否则不保留
	 * @throws PDFWriterException
	 *             假如给表单设置值失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setField(String key, String value, boolean saveAppearance) {
		if (XLPStringUtil.isEmpty(key)) {
			return false;
		}
		try {
			value = XLPStringUtil.emptyTrim(value);
			return form.setField(key.trim(), value, saveAppearance);
		} catch (IOException | DocumentException e) {
			throw new PDFWriterException(e);
		}
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param imgFile
	 *            图片文件
	 * @param recoverFromImageError
	 *            是否从图片中复现错误
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如给定的参数【imgFile】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, File imgFile, boolean recoverFromImageError) {
		AssertUtils.assertFile(imgFile);
		try {
			Image image = Image.getInstance(imgFile.getAbsolutePath(), recoverFromImageError);
			return setImageField(key, image);
		} catch (BadElementException | IOException e) {
			throw new PDFWriterException("图片设置失败", e);
		}
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param imgFile
	 *            图片文件
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如给定的参数【imgFile】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, File imgFile) {
		return setImageField(key, imgFile, false);
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param url
	 *            图片链接
	 * @param recoverFromImageError
	 *            是否从图片中复现错误
	 * @throws NullPointerException
	 *             假如给定的参数【url】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, URL url, boolean recoverFromImageError) {
		AssertUtils.isNotNull(url, "url param is null!");
		try {
			Image image = Image.getInstance(url, recoverFromImageError);
			return setImageField(key, image);
		} catch (BadElementException | IOException e) {
			throw new PDFWriterException("图片设置失败", e);
		}
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param url
	 *            图片链接
	 * @throws NullPointerException
	 *             假如给定的参数【url】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, URL url) {
		AssertUtils.isNotNull(url, "url param is null!");
		return setImageField(key, url, false);
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param imgFile
	 *            图片名称
	 * @param recoverFromImageError
	 *            是否从图片中复现错误
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如给定的参数【imgFile】为null或空，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, String imgFile, boolean recoverFromImageError) {
		AssertUtils.isNotNull(imgFile, "imgFile param is null!");
		return setImageField(key, new File(imgFile.trim()), recoverFromImageError);
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param imgFile
	 *            图片名称
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws NullPointerException
	 *             假如给定的参数【imgFile】为null或空，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, String imgFile) {
		return setImageField(key, imgFile, false);
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param imgBytes
	 *            图片字节数组
	 * @param recoverFromImageError
	 *            是否从图片中复现错误
	 * @throws NullPointerException
	 *             假如给定的参数【imgBytes】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, byte[] imgBytes, boolean recoverFromImageError) {
		AssertUtils.isNotNull(imgBytes, "imgBytes param is null!");
		try {
			Image image = Image.getInstance(imgBytes, recoverFromImageError);
			return setImageField(key, image);
		} catch (BadElementException | IOException e) {
			throw new PDFWriterException("图片设置失败", e);
		}
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param imgBytes
	 *            图片字节数组
	 * @throws NullPointerException
	 *             假如给定的参数【imgBytes】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, byte[] imgBytes) {
		return setImageField(key, imgBytes, false);
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param inputStream
	 *            图片输入流
	 * @param recoverFromImageError
	 *            是否从图片中复现错误
	 * @throws NullPointerException
	 *             假如给定的参数【inputStream】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, InputStream inputStream, boolean recoverFromImageError) {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		try {
			Image image = Image.getInstance(XLPIOUtil.IOToByteArray(inputStream, false), recoverFromImageError);
			return setImageField(key, image);
		} catch (BadElementException | IOException e) {
			throw new PDFWriterException("图片设置失败", e);
		}
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 *            pdf表单标识
	 * @param inputStream
	 *            图片输入流
	 * @throws NullPointerException
	 *             假如给定的参数【inputStream】为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如图片设置失败，则抛出该异常
	 * @return 假如指定key的表单字段被找到并且值被改变则返回true，否则返回 false
	 */
	public boolean setImageField(String key, InputStream inputStream) {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		return setImageField(key, inputStream, false);
	}

	/**
	 * 给图片表单域设置图片
	 * 
	 * @param key
	 * @param image
	 * @return
	 */
	private boolean setImageField(String key, Image image) {
		if (XLPStringUtil.isEmpty(key)) {
			return false;
		}
		// 获取图片表单域位置
		List<FieldPosition> fieldPositions = form.getFieldPositions(key.trim());
		if (fieldPositions.isEmpty()) {
			return false;
		}
		FieldPosition fieldPosition = fieldPositions.get(0);
		int pageNo = fieldPosition.page;
		Rectangle rectangle = fieldPosition.position;
		float x = rectangle.getLeft();
		float y = rectangle.getBottom();
		// 获取操作的页面
		PdfContentByte under = stamper.getOverContent(pageNo);
		// 设置图片位置
		image.setAbsolutePosition(x, y);
		// 根据域的大小缩放图片
		image.scaleToFit(rectangle);
		try {
			// 添加图片
			under.addImage(image);
		} catch (DocumentException e) {
			throw new PDFWriterException(e);
		}
		return true;
	}

	/**
	 * 标记模板填充后的新pdf文件是否可编辑
	 * 
	 * @return
	 */
	public boolean isCanEdit() {
		return canEdit;
	}

	/**
	 * 标记模板填充后的新pdf文件是否可编辑,
	 * 
	 * @param canEdit
	 *            值为true时，可编辑，否则，不可编辑
	 */
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	/**
	 * 释放所有资源
	 * 
	 * @throws PDFWriterException
	 *             假如资源关闭失败，则抛出该异常
	 */
	public void close() {
		XLPIOUtil.closeOutputStream(bos);
		bos = null;

		if (stamper != null) {
			try {
				stamper.close();
			} catch (DocumentException | IOException e) {
			}
			stamper = null;
		}

		if (reader != null) {
			reader.close();
			reader = null;
		}
	}

	/**
	 * 设置字体（需在构造函数后调用，否则设置的字体可能无效）
	 * 
	 * @param baseFont
	 */
	public void setBaseFont(BaseFont baseFont) {
		if (baseFont != null) {
			form.addSubstitutionFont(baseFont);
		}
	}

	/**
	 * 设置默认字体（需在构造函数后调用，否则设置的字体可能无效）
	 * 
	 * @param baseFont
	 */
	public void setDefaultBaseFont() {
		form.addSubstitutionFont(BASE_FONT);
	}

	/**
	 * 设置字体（需在构造函数后调用，否则设置的字体可能无效）
	 * 
	 * @param fontName
	 *            字体名称或字体文件路径
	 * @throws PDFWriterException
	 *             假如字体设置失败，则抛出该异常
	 */
	public void setBaseFont(String fontName) {
		setBaseFont(fontName, null);
	}

	/**
	 * 设置字体（需在构造函数后调用，否则设置的字体可能无效）
	 * 
	 * @param fontName
	 *            字体名称或字体文件路径
	 * @param fontEncoding
	 *            字体编码
	 * @throws PDFWriterException
	 *             假如字体设置失败，则抛出该异常
	 */
	public void setBaseFont(String fontName, String fontEncoding) {
		if (!XLPStringUtil.isEmpty(fontName)) {
			fontEncoding = XLPStringUtil.emptyTrim(fontEncoding);
			fontEncoding = fontEncoding.isEmpty() ? DEFAULT_FONT_ENCONDING : fontEncoding;
			try {
				BaseFont baseFont = BaseFont.createFont(fontName.trim(), fontEncoding, BaseFont.NOT_EMBEDDED);
				form.addSubstitutionFont(baseFont);
			} catch (DocumentException e) {
				throw new PDFWriterException("字体设置失败", e);
			} catch (IOException e) {
				throw new PDFWriterException("字体设置失败", e);
			}
		}
	}

	/**
	 * 把填充好的模板pdf输出到指定的输出流中
	 * 
	 * @param outputStream
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如pdf输出到指定的输出流中失败时，则抛出该异常
	 */
	public void write(OutputStream outputStream) {
		AssertUtils.isNotNull(outputStream, "outputStream param is null!");
		stamper.setFormFlattening(!canEdit);
		try {
			stamper.close();
			outputStream.write(bos.toByteArray());
			outputStream.flush();
		} catch (DocumentException | IOException e) {
			throw new PDFWriterException("pdf写入数据失败", e);
		}
	}

	/**
	 * 把填充好的模板pdf输出到指定的文件中
	 * 
	 * @param outFile
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如pdf写入文件失败时，则抛出该异常
	 */
	public void write(File outFile) {
		AssertUtils.isNotNull(outFile, "outFile param is null!");
		if (outFile.isDirectory()) {
			throw new PDFWriterException("给定的文件是目录，pdf写入文件失败！");
		}
		if (!outFile.exists()) {
			outFile.getParentFile().mkdirs();
		}
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(outFile);
			write(outputStream);
		} catch (FileNotFoundException e) {
		} finally {
			XLPIOUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * 把填充好的模板pdf输出到指定的文件中
	 * 
	 * @param outFile
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws PDFWriterException
	 *             假如pdf写入文件失败时，则抛出该异常
	 */
	public void write(String outFile) {
		AssertUtils.isNotNull(outFile, "outFile param is null or empty!");
		write(new File(outFile));
	}

	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 * @param fillAllPage
	 *            标记水印文字是否填充满每页，true时，填充满每页，false时只填充一条
	 * @param opacity
	 *            水印文字透明度（0-1）
	 * @param fillColor
	 *            水印文字填充色
	 * @param baseFont
	 *            水印文字字体
	 * @param fontSize
	 *            水印文字字体大小
	 * @param x
	 *            水印文字左边的边距
	 * @param y
	 *            水印文字底部边距
	 * @param rotation
	 *            水印文字旋转角度
	 */
	public void setWatermark(String content, boolean fillAllPage, float opacity, BaseColor fillColor, 
			BaseFont baseFont, float fontSize, float x, float y, float rotation) {
		if (!XLPStringUtil.isEmpty(content)) { 
			//获取pdf的页数
			int total = reader.getNumberOfPages() + 1;
			// 设置透明度
			PdfGState gs = new PdfGState();
			gs.setFillOpacity(opacity);
			PdfContentByte contentByte;
			// 循环对每页插入水印
			for (int i = 1; i < total; i++) {
				// 水印的起始
				contentByte = stamper.getOverContent(i);
				// 设置透明度
				contentByte.setGState(gs);
				// 开始
				contentByte.beginText();
				if (baseFont != null) {
					// 设置字体和字体大小
					contentByte.setFontAndSize(baseFont, fontSize);
				}
				contentByte.setTextMatrix(30, 30);
				if (fillColor != null) {
					// 设置颜色 默认为黑色
					contentByte.setColorFill(fillColor);
				}
				if (fillAllPage) {
					for (int j = 0; j < 10; j++) {
						for (int k = 0; k < 8; k++) {
							// 开始写入水印
							contentByte.showTextAligned(Element.ALIGN_LEFT, content, 5 + x * j, y * k, rotation);
						}
					}
				}else {
					// 开始写入水印
					contentByte.showTextAligned(Element.ALIGN_MIDDLE, content, x, y, rotation);
				}
				
				contentByte.endText();
			}
		}
	}
	
	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 * @param fillAllPage
	 *            标记水印文字是否填充满每页，true时，填充满每页，false时只填充一条
	 * @param opacity
	 *            水印文字透明度（0-1）
	 * @param fillColor
	 *            水印文字填充色
	 * @param baseFont
	 *            水印文字字体
	 * @param fontSize
	 *            水印文字字体大小
	 */
	public void setWatermark(String content, boolean fillAllPage, float opacity, BaseColor fillColor, 
			BaseFont baseFont, float fontSize) {
		setWatermark(content, fillAllPage, opacity, fillColor, baseFont, fontSize, DEFAULT_X, DEFAULT_Y, DEFAULT_ROTATION);
	}
	
	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 * @param fillAllPage
	 *            标记水印文字是否填充满每页，true时，填充满每页，false时只填充一条
	 * @param opacity
	 *            水印文字透明度（0-1）
	 * @param baseFont
	 *            水印文字字体
	 */
	public void setWatermark(String content, boolean fillAllPage, float opacity, BaseFont baseFont) {
		setWatermark(content, fillAllPage, opacity, DEFAULT_FILL_COLOR, baseFont, DEFAULT_FONT_SIZE);
	}
	
	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 * @param opacity
	 *            水印文字透明度（0-1）
	 * @param baseFont
	 *            水印文字字体
	 */
	public void setWatermark(String content, float opacity, BaseFont baseFont) {
		setWatermark(content, true, opacity, baseFont);
	}
	
	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 * @param baseFont
	 *            水印文字字体
	 */
	public void setWatermark(String content, BaseFont baseFont) {
		setWatermark(content, DEFAULT_OPACTY, baseFont);
	}
	
	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 * @param fillAllPage
	 *            标记水印文字是否填充满每页，true时，填充满每页，false时只填充一条
	 */
	public void setWatermark(String content, boolean fillAllPage) {
		setWatermark(content, fillAllPage, DEFAULT_OPACTY, BASE_FONT);
	}
	
	/**
	 * 设置水印文字
	 * 
	 * @param content
	 *            水印文字内容
	 */
	public void setWatermark(String content) {
		setWatermark(content, true);
	}
}
