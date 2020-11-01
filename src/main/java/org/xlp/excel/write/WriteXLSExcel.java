package org.xlp.excel.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;
import org.xlp.utils.io.XLPIOUtil;

public class WriteXLSExcel extends AbstractWriteExcel {
	public WriteXLSExcel() {
		this.workbook = new HSSFWorkbook();
	}

	/**
	 * 用excel数据输入文件构造对象
	 * 
	 * @param inputFile
	 *            excel数据输入文件
	 * @throws IOException
	 *             excel数据输入文件读取异常，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如参数为文件夹或文件格式不是以.xls结尾，抛出该异常
	 */
	public WriteXLSExcel(String inputFile) throws IOException { 
		AssertUtils.isNotNull(inputFile, "inputFile param is null or empty!");
		File file = new File(inputFile);
		AssertUtils.assertFile(file);
		if (!file.getName().toLowerCase().endsWith(ExcelType.XLS.getSuffix())) {
			throw new IllegalArgumentException("文件格式必须以" + ExcelType.XLS.getSuffix() + "结尾！");
		}
		POIFSFileSystem fileSystem = new POIFSFileSystem(file);
		this.workbook = new HSSFWorkbook(fileSystem);
	}

	/**
	 * 用excel数据输入文件构造对象
	 * 
	 * @param inputFile
	 *            excel数据输入文件
	 * @throws IOException
	 *             excel数据输入文件读取异常，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如参数为文件夹或文件格式不是以.xls结尾，抛出该异常
	 */
	public WriteXLSExcel(File inputFile) throws IOException { 
		AssertUtils.assertFile(inputFile);
		if (!inputFile.getName().toLowerCase().endsWith(ExcelType.XLS.getSuffix())) {
			throw new IllegalArgumentException("文件格式必须以" + ExcelType.XLS.getSuffix() + "结尾！");
		}
		POIFSFileSystem fileSystem = new POIFSFileSystem(inputFile);
		this.workbook = new HSSFWorkbook(fileSystem);
	}

	/**
	 * 用excel数据输入流构造对象
	 * 
	 * @param inputStream
	 *            excel数据输入流
	 * @throws IOException
	 *             excel数据输入流读取异常，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 */
	public WriteXLSExcel(InputStream inputStream) throws IOException {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		this.workbook = new HSSFWorkbook(inputStream);
	}

	/**
	 * 写入指定名称的文件里
	 * 
	 * @param excelFile
	 * @throws IOException
	 * @throws NullPointerException
	 *             假如参数为空，抛出该异常
	 * @throws IllegalArgumentException
	 *             假如参数为文件夹或文件格式不是以.xls结尾，抛出该异常
	 */
	@Override
	public void write(File excelFile) throws IOException {
		if (excelFile == null) {
			throw new NullPointerException("excelFile param is null!");
		}

		if (excelFile.exists() && excelFile.isDirectory()) {
			throw new IllegalArgumentException("excelFile param is Illegal!");
		}

		if (!excelFile.getName().toLowerCase().endsWith(ExcelType.XLS.getSuffix())) {
			throw new IllegalArgumentException("文件格式必须以" + ExcelType.XLS.getSuffix() + "结尾！");
		}

		if (!excelFile.exists()) {
			excelFile.getParentFile().mkdirs();
		}
		OutputStream outputStream = new FileOutputStream(excelFile);
		try {
			write(outputStream);
		} finally {
			XLPIOUtil.closeOutputStream(outputStream);
		}
	}
}
