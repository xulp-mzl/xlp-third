package org.xlp.excel.read;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;

/**
 * 读取Excel
 * 
 * @author xlp
 * @date 2020-04-15
 */
public class ReadExcel {
	/**
	 * 存储sheet页数据
	 */
	private List<SheetData> sheetDataList = new ArrayList<SheetData>();
	
	private Workbook workbook;
	
	/**
	 * sheet页是否有标题栏
	 */
	private boolean excelHasTitles = true;
	
	/**
	 * excel中sheet页判断某行是否为空验证器
	 */
	private ValidateRowData validateRowData = new DefaultValidateRowData();
	
	/**
	 * 读取excel空行时，处理类型，默认中断，不继续往下读
	 */
	private BlankRowOptionType blankRowOptionType = BlankRowOptionType.BREAK;
	
	/**
	 * @param inputStream
	 * @throws IOException 
	 * @throws EncryptedDocumentException 假如用错误的密码去解析加密的excle文件，则抛出该异常
	 * @throws NullPointerException 假如参数为空，抛出该异常
	 */
	public ReadExcel(InputStream inputStream) throws EncryptedDocumentException, IOException{
		this(inputStream, null);
	}
	
	/**
	 * @param inputStream
	 * @param password 密码
	 * @throws IOException 
	 * @throws EncryptedDocumentException 假如用错误的密码去解析加密的excle文件，则抛出该异常
	 * @throws NullPointerException 假如参数为空，抛出该异常
	 */
	public ReadExcel(InputStream inputStream, String password) throws EncryptedDocumentException, IOException{
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		workbook = WorkbookFactory.create(inputStream, password);
	}
	
	/**
	 * @param xlsFile
	 * @throws IOException 
	 * @throws EncryptedDocumentException 假如用错误的密码去解析加密的excle文件，则抛出该异常
	 * @throws NullPointerException 假如参数为空，抛出该异常
	 * @throws IllegalArgumentException 假如给定的文件是目录或不存在时，抛出该异常
	 */
	public ReadExcel(File xlsFile) throws EncryptedDocumentException, IOException{
		this(xlsFile, null);
	}
	
	/**
	 * @param xlsFile
	 * @param password 密码
	 * @throws IOException 
	 * @throws EncryptedDocumentException 假如用错误的密码去解析加密的excle文件，则抛出该异常
	 * @throws NullPointerException 假如参数为空，抛出该异常
	 * @throws IllegalArgumentException 假如给定的文件是目录或不存在时，抛出该异常
	 */
	public ReadExcel(File xlsFile, String password) throws EncryptedDocumentException, IOException{
		AssertUtils.isNotNull(xlsFile, "xlsFile param is null!"); 
		AssertUtils.assertFile(xlsFile);
		workbook = WorkbookFactory.create(xlsFile, password);
	}
	
	/**
	 * @param xlsFile
	 * @throws IOException 
	 * @throws EncryptedDocumentException 假如用错误的密码去解析加密的excle文件，则抛出该异常 
	 * @throws NullPointerException 假如参数为空，抛出该异常
	 * @throws IllegalArgumentException 假如给定的文件路径是目录或不存在时，抛出该异常
	 */
	public ReadExcel(String xlsFile) throws EncryptedDocumentException, IOException{
		this(xlsFile, null);
	}

	/**
	 * @param xlsFile
	 * @param password 密码
	 * @throws IOException 
	 * @throws EncryptedDocumentException 假如用错误的密码去解析加密的excle文件，则抛出该异常
	 * @throws NullPointerException 假如参数xlsFile为空，抛出该异常
	 * @throws IllegalObjectException  假如给定的文件是目录或不存在，则抛出该异常
	 */
	public ReadExcel(String xlsFile, String password) throws EncryptedDocumentException, IOException{
		AssertUtils.isNotNull(xlsFile, "xlsFile param is null or empty!"); 
		File file = new File(xlsFile);
		AssertUtils.assertFile(file);
		workbook = WorkbookFactory.create(file, password);
	}
	
	/**
	 * 读取Excel中的内容
	 * 
	 * @throws IOException 
	 */
	public void readExcel() throws IOException{
		try{
			//读取公式cell所用
			FormulaEvaluator fe = workbook.getCreationHelper().createFormulaEvaluator();
			
			//获取sheet页数量
			int sheetCount = workbook.getNumberOfSheets();
			SheetData sheetData;
			for (int i = 0; i < sheetCount; i++) {
				sheetData = new SheetData(workbook.getSheetAt(i));
				sheetData.setSheetIndex(i); 
				sheetData.setValidateRowData(validateRowData);
				sheetData.setBlankRowOptionType(blankRowOptionType);
				sheetData.readSheet(fe, excelHasTitles); 
				sheetData.releaseSheet();  
				sheetDataList.add(sheetData);
			}
		}finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}
	
	/**
	 * 获取第一个sheet页的表头信息
	 * @return
	 */
	public String[] getExcelTitles() {
		return getExcelTitles(0);
	}
	
	/**
	 * 获取给定索引sheet页的表头信息
	 * @param sheetIndex sheet页索引值
	 * @return
	 */
	public String[] getExcelTitles(int sheetIndex) {
		SheetData sheetData = getSheetData(sheetIndex);
		if (sheetData != null) {
			return sheetData.getSheetTitles();
		}
		return new String[0];
	}
	
	/**
	 * 获取给定索引sheet页的表头信息
	 * @param sheetName
	 * @return
	 */
	public String[] getExcelTitles(String sheetName) {
		SheetData sheetData = getSheetData(sheetName);
		if (sheetData != null) {
			return sheetData.getSheetTitles();
		}
		return new String[0];
	}

	/**
	 * 每行数据已数组形式返回
	 * 
	 * @return
	 */
	public List<Object[]> getRowDatas() {
		return getRowDatas(0);
	}
	
	/**
	 * 每行数据已数组形式返回
	 * @param sheetIndex sheet页索引值
	 * @return
	 */
	public List<Object[]> getRowDatas(int sheetIndex) {
		SheetData sheetData = getSheetData(sheetIndex);
		if (sheetData != null) {
			return sheetData.getSheetData();
		}
		return new ArrayList<Object[]>(0);
	}
	
	/**
	 * 每行数据已数组形式返回
	 * @param sheetName sheet页名称
	 * @return
	 */
	public List<Object[]> getRowDatas(String sheetName) {
		SheetData sheetData = getSheetData(sheetName);
		if (sheetData != null) {
			return sheetData.getSheetData();
		}
		return new ArrayList<Object[]>(0);
	}

	/**
	 * 每行数据已key-value形式返回
	 * 
	 * @return
	 */
	public List<Map<String, ?>> getRowDataMaps() {
		return getRowDataMaps(0);
	}
	
	/**
	 * 每行数据已key-value形式返回
	 * 
	 * @param sheetIndex sheet页索引值
	 * @return
	 */
	public List<Map<String, ?>> getRowDataMaps(int sheetIndex) {
		SheetData sheetData = getSheetData(sheetIndex);
		if (sheetData != null) {
			return sheetData.getSheetDataMap();
		}
		return new ArrayList<Map<String, ?>>(0);
	}
	
	/**
	 * 每行数据已key-value形式返回
	 * 
	 * @param sheetName sheet页名称
	 * @return
	 */
	public List<Map<String, ?>> getRowDataMaps(String sheetName) {
		SheetData sheetData = getSheetData(sheetName);
		if (sheetData != null) {
			return sheetData.getSheetDataMap();
		}
		return new ArrayList<Map<String, ?>>(0);
	}

	public List<SheetData> getSheetDataList() {
		return sheetDataList;
	}

	public void setSheetDataList(List<SheetData> sheetDataList) {
		if (sheetDataList != null) {
			this.sheetDataList = sheetDataList;
		}
	}
	
	/**
	 * 获取第一个sheet的数据
	 * 
	 * @return
	 */
	public SheetData getSheetData() {
		return getSheetData(0);
	}
	
	/**
	 * 获取第n-1个sheet的数据 
	 * 
	 * @param sheetIndex 从0开始
	 * @return
	 */
	public SheetData getSheetData(int sheetIndex) {
		if (sheetIndex < 0) {
			sheetIndex = 0;
		}
		if (sheetIndex < sheetDataList.size()) {
			return sheetDataList.get(sheetIndex);
		}
		return null;
	}
	
	/**
	 * 获取指定名称sheet页的数据
	 * 
	 * @param sheetName sheet页名称
	 * @return
	 */
	public SheetData getSheetData(String sheetName) {
		for (SheetData sheetData : sheetDataList) {
			if (sheetData.getSheetName().equalsIgnoreCase(sheetName)) {
				return sheetData;
			}
		}
		return null;
	}

	public boolean isExcelHasTitles() {
		return excelHasTitles;
	}

	public void setExcelHasTitles(boolean excelHasTitles) {
		this.excelHasTitles = excelHasTitles;
	}

	public ValidateRowData getValidateRowData() {
		return validateRowData;
	}

	public void setValidateRowData(ValidateRowData validateRowData) {
		this.validateRowData = validateRowData;
	}
	
	public BlankRowOptionType getBlankRowOptionType() {
		return blankRowOptionType;
	}

	/**
	 * 设置读取空行时，如何操作类型
	 * 
	 * @param blankRowOptionType
	 */
	public void setBlankRowOptionType(BlankRowOptionType blankRowOptionType) {
		if (blankRowOptionType != null) {
			this.blankRowOptionType = blankRowOptionType;
		}
	}
}
