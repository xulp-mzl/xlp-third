package org.xlp.excel.write.complexity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.xlp.assertion.IllegalObjectException;
import org.xlp.excel.write.ExcelType;
import org.xlp.javabean.processer.ValueProcesser;
import org.xlp.utils.XLPDateUtil;
import org.xlp.utils.XLPStringUtil;

/**
 * <p>
 * 创建时间：2020年7月2日 下午10:32:42
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description 导出excel操作类
 */
public class WriteExcelExt extends AbstractWriteExcelExt {

	/**
	 * 构造器
	 */
	public WriteExcelExt() {
		super();
	}


	/**
	 * 构造器
	 * 
	 * @param sheetData
	 * @param excelType
	 */
	public WriteExcelExt(ComplicatedSheetData sheetData, ExcelType excelType) {
		super(sheetData, excelType);
	}

	/**
	 * 构造器
	 * 
	 * @param sheetData
	 */
	public WriteExcelExt(ComplicatedSheetData sheetData) {
		super(sheetData);
	}

	/**
	 * 构造器
	 * 
	 * @param excelType
	 */
	public WriteExcelExt(ExcelType excelType) {
		super(excelType);
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
	public WriteExcelExt(File inputFile, String password) throws IOException {
		super(inputFile, password);
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
	public WriteExcelExt(File inputFile) throws IOException {
		super(inputFile);
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
	public WriteExcelExt(InputStream inputStream, String password) throws IOException {
		super(inputStream, password);
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
	public WriteExcelExt(InputStream inputStream) throws IOException {
		super(inputStream);
	}

	/**
	 * 构造器
	 * 
	 * @param sheetDataList
	 * @param excelType
	 */
	public WriteExcelExt(List<ComplicatedSheetData> sheetDataList, ExcelType excelType) {
		super(sheetDataList, excelType);
	}

	/**
	 * 构造器
	 * 
	 * @param sheetDataList
	 */
	public WriteExcelExt(List<ComplicatedSheetData> sheetDataList) {
		super(sheetDataList);
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
	public WriteExcelExt(String inputFile, String password) throws IOException {
		super(inputFile, password);
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
	public WriteExcelExt(String inputFile) throws IOException {
		super(inputFile);
	}

	/**
	 * 构造器
	 * 
	 * @param valueProcesser
	 * @param sheetDataList
	 * @param excelType
	 */
	public WriteExcelExt(ValueProcesser valueProcesser, List<ComplicatedSheetData> sheetDataList, ExcelType excelType) {
		super(valueProcesser, sheetDataList, excelType);
	}

	/**
	 * 构造器
	 * 
	 * @param valueProcesser
	 * @param sheetDataList
	 */
	public WriteExcelExt(ValueProcesser valueProcesser, List<ComplicatedSheetData> sheetDataList) {
		super(valueProcesser, sheetDataList);
	}


	@Override
	protected void dealSelectOptionDataValidationCompatibility(DataValidation dataValidation) {
		// 处理Excel兼容性问题
		if (dataValidation instanceof XSSFDataValidation) {
			dataValidation.setSuppressDropDownArrow(true);
			dataValidation.setShowErrorBox(true);
		} else {
			dataValidation.setSuppressDropDownArrow(false);
		}
	}

	@Override
	protected DataValidationConstraint createDateConstraint(DataValidationHelper helper, ExcelCellData excelCellData) {
		//获取取值操作类型 between
		Object max = excelCellData.getMaxValue();
		Object min = excelCellData.getMinValue();
		
		//最大值为null时，设置9999-12-31
		if (max == null) {
			max = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
		}
		//最小值为null时，设置1970-01-01
		if (min == null) {
			min = new Date(0);
		}
		String dateFormat = excelCellData.getFormatter();
		if (XLPDateUtil.isDate(max)) {
			max = XLPDateUtil.dateToString(max, dateFormat);
		}
		if (XLPDateUtil.isDate(min)) {
			min = XLPDateUtil.dateToString(min, dateFormat);
		}
		
		String minStr = min.toString();
		String maxStr = max.toString();
		
		if (helper instanceof XSSFDataValidationHelper) {
			if (XLPStringUtil.startsWith(minStr, "[=]{0,1}(?i)Date\\(")
					&& XLPStringUtil.startsWith(maxStr, "[=]{0,1}(?i)Date\\(")) {
				return helper.createDateConstraint(OperatorType.BETWEEN, String.valueOf(min), 
						String.valueOf(max), dateFormat);
			}else {
				max = XLPDateUtil.stringToDate((String) max, dateFormat);
				min = XLPDateUtil.stringToDate((String) min, dateFormat);
				return helper.createDateConstraint(OperatorType.BETWEEN, String.valueOf(DateUtil.getExcelDate((Date)min)), 
						String.valueOf(DateUtil.getExcelDate((Date)max)), dateFormat);
			}
		}else if (helper instanceof HSSFDataValidationHelper) {
			if (XLPStringUtil.startsWith(minStr, "[=]{0,1}(?i)Date\\(")
					&& XLPStringUtil.startsWith(maxStr, "[=]{0,1}(?i)Date\\(")) {
				minStr = minStr.startsWith("=") ? minStr : "=" + minStr;
				maxStr = maxStr.startsWith("=") ? maxStr : "=" + maxStr;
				return helper.createDateConstraint(OperatorType.BETWEEN, minStr, maxStr, dateFormat);
			}else {
				return helper.createDateConstraint(OperatorType.BETWEEN, String.valueOf(min), 
						String.valueOf(max), dateFormat);
			}
		}
		return null;
	}
}
