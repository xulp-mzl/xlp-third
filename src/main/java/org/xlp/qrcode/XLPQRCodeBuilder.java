package org.xlp.qrcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 基于zxing对二维码内容为字符串操作的简单封装类
 * 
 * @author 徐龙平
 *         <p>
 *         2016-12-28
 *         </p>
 * @version 1.0
 * 
 */
public class XLPQRCodeBuilder {
	// 二维码纠错级别
	public final static int L = 1;
	public final static int M = 2;
	public final static int H = 3;
	public final static int Q = 4;
	// 图标边框宽度
	private final static int FRAME_WIDTH = 1;
	// 二维码图片宽度
	private int width = 300;
	// 二维码图片高度
	private int height = 300;
	// 二维码图片格式
	private String format = "png";
	// 二维码图片中存储的内容
	private String contentString = "";
	// 字符编码格式
	private String charsetCode = "utf-8";
	// 二维码纠错级别
	private int errorCorrectionLevel = M;
	// private ErrorCorrectionLevel level = ErrorCorrectionLevel.M;
	// 二维码外边距
	private int margin = 2;
	// 二维码形成格式
	private BarcodeFormat qrCode = BarcodeFormat.QR_CODE;
	// 二维码中的图标比例不对时是否需要补白：true为补白; false为不补白;
	private boolean hasFiller = true;

	/**
	 * 二维码图片中存储的内容
	 * 
	 * @param content
	 */
	public XLPQRCodeBuilder(String content) {
		if (content != null) {
			this.contentString = content;
		}
	}

	/**
	 * 二维码形成格式和二维码图片中存储的内容
	 * 
	 * @param barcodeFormat
	 * @param content
	 */
	public XLPQRCodeBuilder(String content, BarcodeFormat barcodeFormat) {
		this.qrCode = barcodeFormat;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidthAndHeight(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * 二维码图片格式
	 * 
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	public void setCharsetCode(String charsetCode) {
		this.charsetCode = charsetCode;
	}

	public void setErrorCorrectionLevel(int errorCorrectionLevel) {
		this.errorCorrectionLevel = errorCorrectionLevel;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	/**
	 * 二维码形成格式
	 * 
	 * @param qrCode
	 */
	public void setQrCode(BarcodeFormat qrCode) {
		this.qrCode = qrCode;
	}

	/**
	 * @param hasFiller
	 */
	public void setHasFiller(boolean hasFiller) {
		this.hasFiller = hasFiller;
	}

	/**
	 * 形成二维码 写入到指定文件中
	 * 
	 * @param file
	 *            二维码形成的文件
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(File file) {
		boolean writeSuccess = false;
		try {
			file = createNewFile(file);

			MatrixToImageWriter
					.writeToPath(initQRCode(), format, file.toPath());
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSuccess;
	}

	/**
	 * 用旧文件创建出新文件
	 * 
	 * @param file
	 *            旧文件
	 * @return 新文件对象
	 */
	private File createNewFile(File file) {
		String path = file.toPath().toString();
		// 斜线最后的位置
		int lastIndext = path.lastIndexOf(File.separatorChar + "");
		// 文件根目录
		String root = path.substring(0, lastIndext);
		// 文件名
		String fileName = path.substring(lastIndext);
		if (!fileName.endsWith("." + format)) {
			fileName = fileName.substring(0, fileName.lastIndexOf(".") + 1)
					+ format;
		}
		File rootDes = new File(root);
		rootDes.mkdirs();
		file = new File(rootDes, fileName);
		return file;
	}

	/**
	 * 形成二维码 写入到指定路径的文件中
	 * 
	 * @param path
	 *            文件路径
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(String path) {
		File file = new File(path);
		return write(file);
	}

	/**
	 * 形成二维码 写入到指定输出流中
	 * 
	 * @param out
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(OutputStream out) {
		boolean writeSuccess = false;
		try {
			MatrixToImageWriter.writeToStream(initQRCode(), format, out);
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSuccess;
	}

	/**
	 * 初始化二维码参数
	 * 
	 * @return BitMatrix
	 * @throws WriterException
	 */
	private BitMatrix initQRCode() throws WriterException {
		// 定义二维码参数
		HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		// 二维码字符集
		hints.put(EncodeHintType.CHARACTER_SET, this.charsetCode);
		ErrorCorrectionLevel level;

		switch (errorCorrectionLevel) {
		case L:
			level = ErrorCorrectionLevel.L;
			break;
		case M:
			level = ErrorCorrectionLevel.M;
			break;
		case H:
			level = ErrorCorrectionLevel.H;
			break;
		case Q:
			level = ErrorCorrectionLevel.Q;
			break;
		default:
			level = ErrorCorrectionLevel.M;
			break;
		}

		// 二维码纠错级别
		hints.put(EncodeHintType.ERROR_CORRECTION, level);
		// 二维码外边距
		hints.put(EncodeHintType.MARGIN, this.margin);

		BitMatrix bitMatrix = new MultiFormatWriter().encode(contentString,
				this.qrCode, width, height, hints);

		return bitMatrix;
	}

	/**
	 * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
	 * 
	 * @param srcImageFile
	 *            要添加到二维码中图片的地址
	 * @param height
	 *            目标高度
	 * @param width
	 *            目标宽度
	 * @return
	 * @throws IOException
	 */
	private BufferedImage scale(String srcImageFile, int height, int width)
			throws IOException {
		File file = new File(srcImageFile);
		BufferedImage srcImage = ImageIO.read(file);
		return scale(height, width, srcImage);
	}

	/**
	 * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
	 * 
	 * @param inputStream
	 *            图标输入流
	 * @param height
	 *            目标高度
	 * @param width
	 *            目标宽度
	 * @return
	 * @throws IOException
	 */
	private BufferedImage scale(InputStream inputStream, int height, int width)
			throws IOException {
		BufferedImage srcImage = ImageIO.read(inputStream);
		return scale(height, width, srcImage);
	}

	/**
	 * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
	 * 
	 * @param srcImageFile
	 *            要添加到二维码中图片的地址
	 * @param height
	 *            目标高度
	 * @param width
	 *            目标宽度
	 */
	private BufferedImage scale(int height, int width, BufferedImage srcImage) {
		double ratio = 0.0; // 缩放比例
		Image destImage = srcImage.getScaledInstance(width, height,
				BufferedImage.SCALE_SMOOTH);
		// 计算比例
		if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
			if (srcImage.getHeight() > srcImage.getWidth()) {
				ratio = (new Integer(height)).doubleValue()
						/ srcImage.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue()
						/ srcImage.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(
					AffineTransform.getScaleInstance(ratio, ratio), null);
			destImage = op.filter(srcImage, null);
		}
		if (hasFiller) {// 补白
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphic = image.createGraphics();
			graphic.setColor(Color.white);
			graphic.fillRect(0, 0, width, height);
			if (width == destImage.getWidth(null))
				graphic.drawImage(destImage, 0,
						(height - destImage.getHeight(null)) / 2,
						destImage.getWidth(null), destImage.getHeight(null),
						Color.white, null);
			else
				graphic.drawImage(destImage,
						(width - destImage.getWidth(null)) / 2, 0,
						destImage.getWidth(null), destImage.getHeight(null),
						Color.white, null);
			graphic.dispose();
			destImage = image;
		}
		return (BufferedImage) destImage;
	}

	/**
	 * 形成中央带图标的二维码的缓冲图片 参数srcImagePath和inputStream只要一个不为空，即可以
	 * 
	 * @param srcImagePath
	 *            图标路径
	 * @param inputStream
	 *            图标输入流
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	private BufferedImage genBarcode(String srcImagePath,
			InputStream inputStream) throws WriterException, IOException {
		// 生成二维码
		BitMatrix matrix = initQRCode();
		int matrixWidth = matrix.getWidth();
		int matrixHeight = matrix.getHeight();

		int iconWidth = matrixWidth / 4;
		int iconHeight = matrixHeight / 4;
		// 读取源图像
		BufferedImage scaleImage = null;
		if (srcImagePath != null) {
			scaleImage = scale(srcImagePath, iconHeight, iconWidth);
		} else if (inputStream != null) {
			scaleImage = scale(inputStream, iconHeight, iconWidth);
		}
		int[][] srcPixels = new int[iconWidth][iconHeight];
		for (int i = 0; i < iconWidth; i++) {
			for (int j = 0; j < iconHeight; j++) {
				srcPixels[i][j] = scaleImage.getRGB(i, j);
			}
		}

		// 二维矩阵转为一维像素数组
		int halfW = matrixWidth / 2;
		int halfH = matrixHeight / 2;
		int[] pixels = new int[width * height];
		int imageHalfWidth = iconWidth / 2;
		for (int y = 0; y < matrixHeight; y++) {
			for (int x = 0; x < matrixWidth; x++) {
				// 读取图片
				if (x > halfW - imageHalfWidth && x < halfW + imageHalfWidth
						&& y > halfH - imageHalfWidth
						&& y < halfH + imageHalfWidth) {
					pixels[y * width + x] = srcPixels[x - halfW
							+ imageHalfWidth][y - halfH + imageHalfWidth];
				} else if ((x > halfW - imageHalfWidth - FRAME_WIDTH
						&& x < halfW - imageHalfWidth + FRAME_WIDTH
						&& y > halfH - imageHalfWidth - FRAME_WIDTH && y < halfH
						+ imageHalfWidth + FRAME_WIDTH) // 在图片四周形成边框
						|| (x > halfW + imageHalfWidth - FRAME_WIDTH
								&& x < halfW + imageHalfWidth + FRAME_WIDTH
								&& y > halfH - imageHalfWidth - FRAME_WIDTH && y < halfH
								+ imageHalfWidth + FRAME_WIDTH)
						|| (x > halfW - imageHalfWidth - FRAME_WIDTH
								&& x < halfW + imageHalfWidth + FRAME_WIDTH
								&& y > halfH - imageHalfWidth - FRAME_WIDTH && y < halfH
								- imageHalfWidth + FRAME_WIDTH)
						|| (x > halfW - imageHalfWidth - FRAME_WIDTH
								&& x < halfW + imageHalfWidth + FRAME_WIDTH
								&& y > halfH + imageHalfWidth - FRAME_WIDTH && y < halfH
								+ imageHalfWidth + FRAME_WIDTH)) {
					pixels[y * width + x] = 0xfffffff;
				} else {
					// 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
					pixels[y * width + x] = matrix.get(x, y) ? 0xff000000
							: 0xfffffff;
				}
			}
		}

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		image.getRaster().setDataElements(0, 0, width, height, pixels);

		return image;
	}

	/**
	 * 形成中央带图标的二维码 写入到指定文件中
	 * 
	 * @param file
	 *            二维码形成的文件
	 * @param iconPath
	 *            图标路径
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(File file, String iconPath) {
		boolean writeSuccess = false;
		try {
			file = createNewFile(file);

			ImageIO.write(genBarcode(iconPath, null), format, file);
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSuccess;
	}

	/**
	 * 形成中央带图标的二维码 写入到指定路径的文件中
	 * 
	 * @param path
	 *            文件路径
	 * @param iconPath
	 *            图标路径
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(String path, String iconPath) {
		File file = new File(path);
		return write(file, iconPath);
	}

	/**
	 * 形成中央带图标的二维码 写入到指定输出流中
	 * 
	 * @param out
	 *            输出流
	 * @param iconPath
	 *            图标路径
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(OutputStream out, String iconPath) {
		boolean writeSuccess = false;
		try {
			ImageIO.write(genBarcode(iconPath, null), format, out);
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSuccess;
	}

	/**
	 * 形成中央带图标的二维码 写入到指定文件中
	 * 
	 * @param file
	 *            二维码形成的文件
	 * @param inputStream
	 *            图标输入流
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(File file, InputStream inputStream) {
		boolean writeSuccess = false;
		try {
			file = createNewFile(file);

			ImageIO.write(genBarcode(null, inputStream), format, file);
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSuccess;
	}

	/**
	 * 形成中央带图标的二维码 写入到指定路径的文件中
	 * 
	 * @param path
	 *            文件路径
	 * @param inputStream
	 *            图标输入流
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(String path, InputStream inputStream) {
		File file = new File(path);
		return write(file, inputStream);
	}

	/**
	 * 形成中央带图标的二维码 写入到指定输出流中
	 * 
	 * @param out
	 *            输出流
	 * @param inputStream
	 *            图标输入流
	 * @return 形成二维码是否成功，true 成功，false 失败
	 */
	public boolean write(OutputStream out, InputStream inputStream) {
		boolean writeSuccess = false;
		try {
			ImageIO.write(genBarcode(null, inputStream), format, out);
			writeSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeSuccess;
	}
}
