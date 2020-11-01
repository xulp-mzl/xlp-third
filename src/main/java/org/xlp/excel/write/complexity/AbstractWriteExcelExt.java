package org.xlp.excel.write.complexity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xlp.assertion.AssertUtils;
import org.xlp.assertion.IllegalObjectException;
import org.xlp.excel.write.ExcelType;
import org.xlp.excel.write.WriteExcel;
import org.xlp.excel.write.complexity.enumeration.DataType;
import org.xlp.excel.write.complexity.enumeration.HorizontalType;
import org.xlp.excel.write.complexity.enumeration.VerticalType;
import org.xlp.excel.write.complexity.font.CellFont;
import org.xlp.javabean.convert.mapandbean.MapValueProcesser;
import org.xlp.javabean.processer.ValueProcesser;
import org.xlp.utils.XLPDateUtil;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.collection.XLPCollectionUtil;
import org.xlp.utils.io.XLPIOUtil;

/**
 * <p>
 * 创建时间：2020年7月2日 下午10:32:42
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description 导出excel操作抽象类
 */
public abstract class AbstractWriteExcelExt implements WriteExcel {
	/**
	 * 把object值转换成字符串处理器
	 */
	private ValueProcesser valueProcesser = new MapValueProcesser();

	/**
	 * sheet数据
	 */
	private List<ComplicatedSheetData> sheetDataList = new ArrayList<ComplicatedSheetData>();

	/**
	 * 标记是否自动关闭资源，默认自动关闭，即写完一次excel数据，释放workbook资源，如要重新写，则需新建该对象
	 */
	private boolean autoClose = true;

	/**
	 * excel 工作簿
	 */
	private Workbook workbook;

	/**
	 * 构造器
	 */
	public AbstractWriteExcelExt() {
		workbook = new XSSFWorkbook();
	}

	/**
	 * 构造器
	 * 
	 * @param excelType
	 */
	public AbstractWriteExcelExt(ExcelType excelType) {
		if (excelType == null || excelType == ExcelType.XLSX) {
			workbook = new XSSFWorkbook();
		} else {
			workbook = new HSSFWorkbook();
		}
	}

	/**
	 * 构造器
	 * 
	 * @param sheetDataList
	 */
	public AbstractWriteExcelExt(List<ComplicatedSheetData> sheetDataList) {
		this();
		setSheetDataList(sheetDataList);
	}

	/**
	 * 构造器
	 * 
	 * @param sheetData
	 */
	public AbstractWriteExcelExt(ComplicatedSheetData sheetData) {
		this();
		addSheetData(sheetData);
	}

	/**
	 * 构造器
	 * 
	 * @param valueProcesser
	 * @param sheetDataList
	 */
	public AbstractWriteExcelExt(ValueProcesser valueProcesser, List<ComplicatedSheetData> sheetDataList) {
		this();
		setSheetDataList(sheetDataList);
		setValueProcesser(valueProcesser);
	}

	/**
	 * 构造器
	 * 
	 * @param sheetDataList
	 * @param excelType
	 */
	public AbstractWriteExcelExt(List<ComplicatedSheetData> sheetDataList, ExcelType excelType) {
		this(excelType);
		setSheetDataList(sheetDataList);
	}

	/**
	 * 构造器
	 * 
	 * @param sheetData
	 * @param excelType
	 */
	public AbstractWriteExcelExt(ComplicatedSheetData sheetData, ExcelType excelType) {
		this(excelType);
		addSheetData(sheetData);
	}

	/**
	 * 构造器
	 * 
	 * @param valueProcesser
	 * @param sheetDataList
	 * @param excelType
	 */
	public AbstractWriteExcelExt(ValueProcesser valueProcesser, List<ComplicatedSheetData> sheetDataList, ExcelType excelType) {
		this(excelType);
		setSheetDataList(sheetDataList);
		setValueProcesser(valueProcesser);
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
	public AbstractWriteExcelExt(String inputFile) throws IOException {
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
	public AbstractWriteExcelExt(String inputFile, String password) throws IOException {
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
	public AbstractWriteExcelExt(File inputFile) throws IOException {
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
	public AbstractWriteExcelExt(File inputFile, String password) throws IOException {
		AssertUtils.assertFile(inputFile);
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
	public AbstractWriteExcelExt(InputStream inputStream) throws IOException {
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
	public AbstractWriteExcelExt(InputStream inputStream, String password) throws IOException {
		AssertUtils.isNotNull(inputStream, "inputStream param is null!");
		workbook = WorkbookFactory.create(inputStream, password);
	}

	/**
	 * 把数据写入excel文件中
	 * 
	 * @param fileName
	 * @throws IOException
	 *             当写文件时出现错误，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如给定的文件存在且是目录，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数fileName为空，则抛出该异常
	 */
	@Override
	public void write(String fileName) throws IOException {
		AssertUtils.isNotNull(fileName, "fileName param is null or empty!");
		write(new File(fileName));
	}

	/**
	 * 把数据写入excel文件中
	 * 
	 * @param excelFile
	 * @throws IOException
	 *             当写文件时出现错误，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如给定的文件存在且是目录，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 */
	@Override
	public void write(File excelFile) throws IOException {
		AssertUtils.isNotNull(excelFile, "excelFile param is null!");

		if (excelFile.exists() && excelFile.isDirectory()) {
			throw new IllegalArgumentException("excelFile param is Illegal!");
		}

		if (!excelFile.exists()) {
			excelFile.getParentFile().mkdirs();
		}

		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(excelFile));
			write(outputStream);
		} finally {
			XLPIOUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * 把数据写入excel输出流中
	 * 
	 * @param outputStream
	 *            输出流
	 * @throws IOException
	 *             当写文件时出现错误，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数outputStream为空，抛出该异常
	 */
	@Override
	public void write(OutputStream outputStream) throws IOException {
		AssertUtils.isNotNull(outputStream, "outputStream param is null!");

		Sheet sheet;
		try {
			for (ComplicatedSheetData complicatedSheetData : sheetDataList) {
				String sheetName = complicatedSheetData.getSheetName();
				if (XLPStringUtil.isEmpty(sheetName)) {
					sheet = workbook.createSheet();
				} else {
					sheet = workbook.getSheet(sheetName);
					if (sheet == null) {
						sheet = workbook.createSheet(sheetName);
					}
				}
				
				// 获取cell数据
				List<ExcelCellData> excelCellDatas = complicatedSheetData.getExcelCellDatas();
				CellStyle cellStyle;
				Row row;
				Cell cell;
				for (ExcelCellData excelCellData : excelCellDatas) {
					int rowNo = excelCellData.getFromRow();
					row = sheet.getRow(rowNo);
					if (row == null) {
						// 创建sheet row
						row = sheet.createRow(rowNo);
					}
					// 设置行高
					if (complicatedSheetData.getRowHeight() > 0) {
						row.setHeightInPoints(complicatedSheetData.getRowHeight());
					}
					int colNo = excelCellData.getFromColumn();
					cell = row.getCell(colNo);
					if (cell == null) {
						// 创建cell
						cell = row.createCell(colNo, createCellType(excelCellData));
					}
					cellStyle = createCellStyle(cell, complicatedSheetData, excelCellData, workbook);
					// 设置cell样式
					cell.setCellStyle(cellStyle);
					// 设置cell内容
					setCellValue(cell, excelCellData);
					// 合并单元格
					mergeCells(sheet, excelCellData);

					// 设置下拉框数据
					setSelectOptions(sheet, excelCellData);

					// 设置批注
					setComment(sheet, cell, excelCellData);

					//设置导出模板中其他单元格的样式
					setTempleteOtherCellStyle(sheet, excelCellData);
					
					//设置cell值得取值范围
					setCellValueRange(sheet, excelCellData);
					
					if (excelCellData.getCellWidth() > 0) {
						// 单位为字符宽度的1/256
						sheet.setColumnWidth(colNo, excelCellData.getCellWidth() * 256);
					}
				}
				
				//设置隐藏的列
				for(int hideCol : complicatedSheetData.getHideColIndexs()){
					sheet.setColumnHidden(hideCol, true); 
				}
				
				int fixedCols = complicatedSheetData.getFixedCols();
				int fixedRows = complicatedSheetData.getFixedRows();
				if (fixedCols > 0 || fixedRows > 0) {
					//设置固定行或列
					sheet.createFreezePane(fixedCols, fixedRows); 
				}
				
				if (complicatedSheetData.isForceProtectSheet() || complicatedSheetData.isProtectSheet()) {
					sheet.protectSheet(complicatedSheetData.getPassword()); 
				}
			}
			workbook.write(outputStream);
		} finally {
			if (autoClose) {
				close();
			}
		}
	}

	/**
	 * 设置cell值得取值范围
	 * 
	 * @param sheet
	 * @param excelCellData
	 */
	protected void setCellValueRange(Sheet sheet, ExcelCellData excelCellData) {
		//获取取值操作类型 between
		Object max = excelCellData.getMaxValue();
		Object min = excelCellData.getMinValue();
		if (max == null && min == null) {
			return;
		}
		
		DataType dataType = excelCellData.getDataType();
		List<String> options = excelCellData.getSelectOptions();
		int rows = excelCellData.getMaxFormatRow();
		int fr = excelCellData.getFromRow(), tr = excelCellData.getToRow();
		int fc = excelCellData.getFromColumn(), tc = excelCellData.getToColumn();
		
		DataValidationHelper helper = null;
        //设置数据
        DataValidationConstraint constraint = null;
        
        if (XLPCollectionUtil.isEmpty(options)) {
        	helper = sheet.getDataValidationHelper();
			switch (dataType) {
			case STRING:
				if (max == null) {
					max = Long.MAX_VALUE;
				}
				if (min == null) {
					min = 0;
				}
				constraint = helper.createTextLengthConstraint(OperatorType.BETWEEN, 
						String.valueOf(min), String.valueOf(max));
				break;
			case TIME:
				constraint = createTimeConstraint(helper, excelCellData); 
				break;
			case DATE:
				constraint = createDateConstraint(helper, excelCellData); 
				break;
			case INT:
				if (max == null) {
					max = Long.MAX_VALUE;
				}
				if (min == null) {
					min = Long.MIN_VALUE;
				}
				constraint = helper.createIntegerConstraint(OperatorType.BETWEEN, String.valueOf(min), 
						String.valueOf(max));
				break;
			case DECIMAL:
				if (max == null) {
					max = Double.MAX_VALUE;
				}
				if (min == null) {
					min = Double.MIN_VALUE;
				}
				constraint = helper.createDecimalConstraint(OperatorType.BETWEEN, String.valueOf(min), 
						String.valueOf(max));
				break;
			default:
				return;
			}
		}
        
        if (constraint != null) {
        	//设置行列范围
            CellRangeAddressList addressList = new CellRangeAddressList(fr, tr + rows, fc, tc);
        	DataValidation dataValidation = helper.createValidation(constraint, addressList);
 	        //设置错误提示信息
 			String errorTip = XLPStringUtil.emptyTrim(excelCellData.getErrorTip());
 			if (!errorTip.isEmpty()) {
 				 dataValidation.createErrorBox("输入错误提示", errorTip);
 			}
 	        //处理Excel兼容性问题
 	        if(dataValidation instanceof XSSFDataValidation) {
 	            dataValidation.setShowErrorBox(true);
 	        }
 	        sheet.addValidationData(dataValidation);
		}
	}
	
	/**
	 * 创建时间区间有效性验证器
	 * 
	 * @param helper
	 * @param excelCellData
	 * @return
	 */
	protected DataValidationConstraint createTimeConstraint(DataValidationHelper helper, 
			ExcelCellData excelCellData){
		//获取取值操作类型 between
		Object max = excelCellData.getMaxValue();
		Object min = excelCellData.getMinValue();
		
		//最大值为null时，设置23:59:59
		if (max == null) {
			max = LocalTime.of(23, 59, 59);
		}
		//最小值为null时，设置0:0:0
		if (min == null) {
			min = LocalTime.of(0, 0, 0);
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
		
		if (!XLPStringUtil.startsWith(minStr, "[=]{0,1}(?i)Time\\(")) {
			min = XLPDateUtil.stringToDate((String) min, dateFormat);
			LocalDateTime minDateTime = XLPDateUtil.dateToLocalDateTime((Date) min);
			minStr = "Time(" + minDateTime.getHour() + "," + minDateTime.getMinute() + "," + minDateTime.getSecond() + ")";
		}
		if (!XLPStringUtil.startsWith(maxStr, "[=]{0,1}(?i)Time\\(")) {
			max = XLPDateUtil.stringToDate((String) max, dateFormat);
			LocalDateTime maxDateTime = XLPDateUtil.dateToLocalDateTime((Date) max);
			maxStr = "Time(" + maxDateTime.getHour() + "," + maxDateTime.getMinute() + "," + maxDateTime.getSecond() + ")";
		}
		minStr = minStr.startsWith("=") ? minStr : "=" + minStr;
		maxStr = maxStr.startsWith("=") ? maxStr : "=" + maxStr;
		return helper.createTimeConstraint(OperatorType.BETWEEN, minStr, maxStr);
	}

	/**
	 * 创建日期区间有效性验证器
	 * 
	 * @param helper
	 * @param excelCellData
	 * @return
	 */
	protected abstract DataValidationConstraint createDateConstraint(DataValidationHelper helper,
			ExcelCellData excelCellData);

	/**
	 * 设置导出模板中其他单元格的样式
	 * 
	 * @param sheet
	 * @param excelCellData
	 */
	protected void setTempleteOtherCellStyle(Sheet sheet, ExcelCellData excelCellData) {
		int rows = excelCellData.getMaxFormatRow();
		int tr = excelCellData.getToRow();
		int fc = excelCellData.getFromColumn();
		if (rows > 0) { 
			int countRow = tr + rows;
			for(int i = tr + 1; i <= countRow; i++){
				Row row = sheet.getRow(i);
				if (row == null) {
					row = sheet.createRow(i);
				}
				Cell cell2 = row.getCell(fc);
				if (cell2 == null) {
					cell2 = row.createCell(fc, createCellType(excelCellData));
					cell2.setBlank();
				}
				cell2.setCellStyle(createCellStyle(cell2, null, excelCellData, workbook));
			}
		}
	}

	/**
	 * 设置批注
	 * 
	 * @param sheet
	 * @param cell
	 * @param excelCellData
	 */
	protected void setComment(Sheet sheet, Cell cell, ExcelCellData excelCellData) {
		// 创建绘图对象
		@SuppressWarnings("rawtypes")
		Drawing drawing = sheet.createDrawingPatriarch();
		String mark = excelCellData.getMark();
		if (!XLPStringUtil.isEmpty(mark)) {
			CreationHelper factory = workbook.getCreationHelper();
	        if(cell.getCellComment() != null){
	        	//当存在批注，移除已有的批注
	        	cell.removeCellComment();
	        }
			// 获取批注对象
			// (int dx1, int dy1, int dx2, int dy2, short col1, int row1, short
			// col2, int row2)
			// 前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
			int fromCol = excelCellData.getFromColumn();
			int fromRow = excelCellData.getFromRow();
			ClientAnchor clientAnchor = drawing.createAnchor(0, 0, 0, 0, fromCol, fromRow,
					fromCol + excelCellData.getMarkCol(), fromRow + excelCellData.getMarkRow());
			Comment comment = drawing.createCellComment(clientAnchor);
			// 输入批注信息
			comment.setString(factory.createRichTextString(mark)); 
			cell.setCellComment(comment);
		}
	}

	/**
	 * 设置excel cell下拉框中的数据
	 * 
	 * @param sheet
	 * @param cell
	 * @param excelCellData
	 */
	protected void setSelectOptions(Sheet sheet, ExcelCellData excelCellData) {
		List<String> options = excelCellData.getSelectOptions();
		if (!XLPCollectionUtil.isEmpty(options)) {
			int rows = excelCellData.getMaxFormatRow();
			int fr = excelCellData.getFromRow(), tr = excelCellData.getToRow();
			int fc = excelCellData.getFromColumn(), tc = excelCellData.getToColumn();
			// 获取下拉框验证对象
			DataValidationHelper helper = sheet.getDataValidationHelper();
			CellRangeAddressList addressList = new CellRangeAddressList(fr, tr + rows, fc, tc);
			// 设置下拉框数据
			DataValidationConstraint constraint = helper.createExplicitListConstraint(options.toArray(new String[0]));
			DataValidation dataValidation = helper.createValidation(constraint, addressList);
			//设置错误提示信息
			String errorTip = XLPStringUtil.emptyTrim(excelCellData.getErrorTip());
			if (!errorTip.isEmpty()) {
				 dataValidation.createErrorBox("输入错误提示", errorTip);
			}
			// 处理Excel兼容性问题
			dealSelectOptionDataValidationCompatibility(dataValidation); 
			
			sheet.addValidationData(dataValidation);
		}
	}

	/**
	 * 处理Excel兼容性问题
	 * 
	 * @param dataValidation
	 */
	protected abstract void dealSelectOptionDataValidationCompatibility(DataValidation dataValidation);
	
	/**
	 * 合并单元格
	 * 
	 * @param sheet
	 * @param excelCellData
	 */
	protected void mergeCells(Sheet sheet, ExcelCellData excelCellData) {
		int fr = excelCellData.getFromRow(), tr = excelCellData.getToRow();
		int fc = excelCellData.getFromColumn(), tc = excelCellData.getToColumn();
		if (tr - fr > 0 || tc - fc > 0) {
			// 合并单元，创建要合并的单元格CellRangeAddress对象
			CellRangeAddress region = new CellRangeAddress(fr, tr, fc, tc);
			sheet.addMergedRegion(region);
		}
	}

	/**
	 * 设置cell内容
	 * 
	 * @param cell
	 * @param excelCellData
	 */
	protected void setCellValue(Cell cell, ExcelCellData excelCellData) {
		Object cellValue = excelCellData.getCellValue();
		if (cellValue == null) {
			cell.setBlank();
		}else if(XLPDateUtil.isDate(cellValue)) { 
			cell.setCellValue(XLPDateUtil.dateToString(cellValue, excelCellData.getFormatter()));  
		}else if (cellValue instanceof Number) { 
			Number cvNum = (Number) cellValue;
			cell.setCellValue(cvNum.doubleValue());
		}else if (cellValue instanceof Boolean) {
			Boolean cv = (Boolean) cellValue;
			cell.setCellValue(cv.booleanValue());
		}else {
			String value = (String) valueProcesser.processValue(String.class, cellValue);
			value = XLPStringUtil.emptyTrim(value);
			cell.setCellValue(value);
		}
	}

	/**
	 * 获取CellType
	 * 
	 * @param excelCellData
	 * @return {@link CellType}
	 */
	protected CellType createCellType(ExcelCellData excelCellData) {
		switch (excelCellData.getDataType()) {
		case STRING:
			return CellType.STRING;
		case DATE:
		case TIME:
		case DECIMAL:
		case INT:
			return CellType.NUMERIC;
		case BOOLEAN:
			return CellType.BOOLEAN;
		default:
			return CellType.BLANK;
		}
	}

	/**
	 * 创建CellStyle
	 * 
	 * @param cell
	 * @param complicatedSheetData
	 * @param excelCellData
	 * @param workbook
	 * @return {@link CellStyle}
	 */
	protected CellStyle createCellStyle(Cell cell, ComplicatedSheetData complicatedSheetData,
			ExcelCellData excelCellData, Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		VerticalType verticalType = excelCellData.getVerticalType();
		HorizontalType horizontalType = excelCellData.getHorizontalType();
		CellFont cellFont = excelCellData.getCellFont();
		// 设置垂直位置
		switch (verticalType) {
		case TOP:
			cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
			break;
		case BOTTOM:
			cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
			break;
		case MIDDLE:
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			break;
		}

		// 设置水平位置
		switch (horizontalType) {
		case LEFT:
			cellStyle.setAlignment(HorizontalAlignment.LEFT);
			break;
		case CENTER:
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			break;
		case RIGHT:
			cellStyle.setAlignment(HorizontalAlignment.RIGHT);
			break;
		}

		if (excelCellData.getBackgroundColor() != null) {
			// 设置背景色
			cellStyle.setFillForegroundColor(excelCellData.getBackgroundColor().getIndex());
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}

		// 设置字体
		if (cellFont != null) {
			Font font = workbook.createFont();
			font.setBold(cellFont.isBlod());
			font.setFontName(cellFont.getFontName());
			font.setColor((short) cellFont.getFontColor());
			font.setFontHeightInPoints(cellFont.getFontSize());
			cellStyle.setFont(font);
		}
		// 设置数据类型
		DataFormat format = workbook.createDataFormat();
		cellStyle.setDataFormat(format.getFormat(excelCellData.getFormatter()));
		
		boolean canEdit = excelCellData.isCanEdit();
		if (complicatedSheetData != null) {
			canEdit = complicatedSheetData.isForceProtectSheet() ? false : canEdit;
			if (!canEdit) {
				complicatedSheetData.setProtectSheet(!canEdit);
			}
		}
		
		//设置cell是否可编辑
		cellStyle.setLocked(!canEdit); 
		return cellStyle; 
	}

	public ValueProcesser getValueProcesser() {
		return valueProcesser;
	}

	/**
	 * 设置cell值处理器
	 * 
	 * @param valueProcesser
	 */
	public void setValueProcesser(ValueProcesser valueProcesser) {
		if (valueProcesser != null) {
			this.valueProcesser = valueProcesser;
		}
	}

	public List<ComplicatedSheetData> getSheetDataList() {
		return sheetDataList;
	}

	/**
	 * 设置 Word sheet页数据
	 * 
	 * @param sheetDataList
	 */
	public void setSheetDataList(List<ComplicatedSheetData> sheetDataList) {
		if (sheetDataList != null) {
			this.sheetDataList = sheetDataList;
		}
	}

	/**
	 * 添加sheet页数据
	 * 
	 * @param sheetData
	 */
	public void addSheetData(ComplicatedSheetData sheetData) {
		if (sheetData != null) {
			this.sheetDataList.add(sheetData);
		}
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	/**
	 * 设置标记是否自动释放workbook资源
	 * 
	 * @param autoClose
	 *            值为true时自动关闭，即写完一次excel数据，释放workbook资源，如要重新写，则需新建该对象，false时需手动调用close函数释放资源
	 */
	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	/**
	 * 释放workbook资源
	 * 
	 * @throws IOException
	 *             假如释放workbook资源失败，则抛出该异常
	 */
	public void close() throws IOException {
		if (workbook != null) {
			workbook.close();
			workbook = null;
		}
	}
}
