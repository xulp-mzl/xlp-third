package org.xlp.excel.write;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;
import org.xlp.utils.io.XLPIOUtil;

public class WriteXLSAndXLSXExcel extends AbstractWriteExcel {
	public WriteXLSAndXLSXExcel(ExcelType excelType) {
		if (excelType == null || ExcelType.XLSX == excelType) {
			workbook = new XSSFWorkbook();
		}else {
			workbook = new HSSFWorkbook();
		}
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
	 */
	public WriteXLSAndXLSXExcel(String inputFile) throws IOException { 
		this(inputFile, null);
	}
	
	/**
	 * 用excel数据输入文件构造对象
	 * 
	 * @param inputFile
	 *            excel数据输入文件
	 * @param password
	 * 			  打开excel时所需输入的密码
	 * @throws IOException
	 *             excel数据输入文件读取异常，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public WriteXLSAndXLSXExcel(String inputFile, String password) throws IOException { 
		AssertUtils.isNotNull(inputFile, "inputFile param is null or empty!");
		File file = new File(inputFile);
		AssertUtils.assertFile(file);
		workbook = WorkbookFactory.create(file, password); 
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
	 */
	public WriteXLSAndXLSXExcel(File inputFile) throws IOException { 
		this(inputFile, null);
	}
	
	/**
	 * 用excel数据输入文件构造对象
	 * 
	 * @param inputFile
	 *            excel数据输入文件
	 * @param password
	 * 			  打开excel时所需输入的密码            
	 * @throws IOException
	 *             excel数据输入文件读取异常，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws IllegalObjectException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public WriteXLSAndXLSXExcel(File inputFile, String password) throws IOException { 
		AssertUtils.assertFile(inputFile);
		if (!inputFile.getName().toLowerCase().endsWith(ExcelType.XLS.getSuffix())) {
			throw new IllegalArgumentException("文件格式必须以" + ExcelType.XLS.getSuffix() + "结尾！");
		}
		workbook = WorkbookFactory.create(inputFile, password); 
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
	public WriteXLSAndXLSXExcel(InputStream inputStream) throws IOException {
		this(inputStream, null);
	}
	
	/**
	 * 用excel数据输入流构造对象
	 * 
	 * @param inputStream
	 *            excel数据输入流
	 * @param password
	 * 			  打开excel时所需输入的密码            
	 * @throws IOException
	 *             excel数据输入流读取异常，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 */
	public WriteXLSAndXLSXExcel(InputStream inputStream, String password) throws IOException {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		workbook = WorkbookFactory.create(inputStream, password); 
	}

	/**
	 * 写入指定名称的文件里
	 * 
	 * @param excelFile
	 * @throws IOException
	 * @throws NullPointerException
	 *             假如参数为空，抛出该异常
	 */
	@Override
	public void write(File excelFile) throws IOException {
		if (excelFile == null) {
			throw new NullPointerException("excelFile param is null!");
		}

		if (excelFile.exists() && excelFile.isDirectory()) {
			throw new IllegalArgumentException("excelFile param is Illegal!");
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
