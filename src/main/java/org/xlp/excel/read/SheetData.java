package org.xlp.excel.read;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.xlp.utils.XLPObjectUtil;
import org.xlp.utils.XLPStringUtil;

public class SheetData {
	/**
	 * 存储Excel的表头
	 */
	private String[] sheetTitles = new String[0];
	/**
	 * 存储每行的数据每行数据已数组存储
	 */
	private List<Object[]> sheetData = new ArrayList<Object[]>();
	/**
	 * 存储每行数据每行数据已key-value存储
	 */
	private List<Map<String, ?>> sheetDataMap = new ArrayList<Map<String, ?>>();

	/**
	 * sheet索引值
	 */
	private int sheetIndex = 0;

	/**
	 * sheet页的名称
	 */
	private String sheetName = "";
	
	/**
	 * 存储行号
	 */
	private List<Integer> rowNoList = new ArrayList<Integer>();
	
	/**
	 * excel中sheet对象
	 */
	private Sheet sheet;
	
	/**
	 * sheet页判断某行是否为空验证器
	 */
	private ValidateRowData validateRowData = new DefaultValidateRowData();
	
	/**
	 * 读取excel空行时，处理类型，默认中断，不继续往下读
	 */
	private BlankRowOptionType blankRowOptionType = BlankRowOptionType.BREAK;
	
	/**
	 * 标记sheet是否受保护，不可编辑，值为true时不可编辑，false时可以编辑
	 */
	private boolean protectSheet = false;
	
	/**
	 * sheet受保护时，需设置的密码
	 */
	private String password = XLPStringUtil.EMPTY;
	
	/**
	 * 保存要隐藏的列索引值，从0开始
	 */
	private List<Integer> hideColIndexs = new ArrayList<Integer>();
	
	/**
	 * 保存要固定的列数
	 */
	private int fixedCols = 0;
	
	/**
	 * 保存要固定的行数
	 */
	private int fixedRows = 0;
	
	public SheetData() {
	}

	/**
	 * @param sheet
	 */
	public SheetData(Sheet sheet) {
		this.sheet = sheet;
	}
	
	/**
	 * 读取sheet页的内容
	 * 
	 * @param formulaEvaluator
	 *            读取公式中的cell值
	 * @param hasTitle
	 *            表示sheet的第一行是否是表头栏， true表示是
	 */
	public void readSheet() {
		readSheet(null, true); 
	}
	
	/**
	 * 读取sheet页的内容
	 * 
	 * @param formulaEvaluator
	 *            读取公式中的cell值
	 */
	public void readSheet(FormulaEvaluator formulaEvaluator) {
		readSheet(formulaEvaluator, true);
	}
	
	/**
	 * 读取sheet页的内容
	 * 
	 * @param hasTitle
	 *            表示sheet的第一行是否是表头栏， true表示是
	 */
	public void readSheet(boolean hasTitle) {
		readSheet(null, hasTitle); 
	}
	
	/**
	 * 读取sheet页的内容
	 * 
	 * @param formulaEvaluator
	 *            读取公式中的cell值
	 * @param hasTitle
	 *            表示sheet的第一行是否是表头栏， true表示是
	 */
	public void readSheet(FormulaEvaluator formulaEvaluator, boolean hasTitle) {
		if (sheet != null) {
			sheetName = sheet.getSheetName();

			int lastRowNo = sheet.getLastRowNum();
			Object[] rowData;
			Row row;
			Cell cell;
			// 循环获取sheet页每行数据，改行为空跳过
			for (int i = 0; i <= lastRowNo; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					int cellnum = row.getLastCellNum();
					rowData = new Object[cellnum];
					for (int j = 0; j < cellnum; j++) {
						cell = row.getCell(j);
						if (cell != null) {
							// 判断单元格的数据类型
							switch (cell.getCellType()) {
								case NUMERIC: // 数字
									if (DateUtil.isCellDateFormatted(cell)) {
										rowData[j] = cell.getDateCellValue();
									}else {
										rowData[j] = cell.getNumericCellValue();
									}
									break;
								case STRING: // 字符串
									rowData[j] = cell.getStringCellValue();
									break;
								case BOOLEAN: // Boolean
									rowData[j] = cell.getBooleanCellValue();
									break;
								case FORMULA: // 公式
									Cell temp = cell;
									if (formulaEvaluator != null) {
										cell = formulaEvaluator.evaluateInCell(cell);
										if (cell != null) {
											rowData[j] = cell.getNumericCellValue();
										}else {
											rowData[j] = temp.getNumericCellValue();
										}
									}else {
										rowData[j] = cell.getCellFormula();
									}
									break;
								case BLANK: // 空值
									rowData[j] = "";
									break;
								case ERROR: // 故障
									rowData[j] = cell.getErrorCellValue();
									break;
								case _NONE: 
									rowData[j] = "";
									break;
								default:
									rowData[j] = "";
									break;
							}
						}else {
							rowData[j] = "";
						}
					}
					// 判断该行是否为空
					if (validateRowData != null && validateRowData.rowDataIsEmpty(rowData)) {
						if(blankRowOptionType == BlankRowOptionType.BREAK){
							break;
						}
						if (blankRowOptionType == BlankRowOptionType.SKIP) {
							continue;
						}
					}
					sheetData.add(rowData);
					rowNoList.add(Integer.valueOf(i + 1));
				}else {
					if(blankRowOptionType == BlankRowOptionType.BREAK){
						break;
					}
					if (blankRowOptionType == BlankRowOptionType.SKIP) {
						continue;
					}
					sheetData.add(new Object[0]);
					rowNoList.add(Integer.valueOf(i + 1));
				}
			}
			if (!sheetData.isEmpty() && hasTitle) {
				Object[] titles = sheetData.remove(0);
				rowNoList.remove(0);
				int len = titles.length;
				sheetTitles = new String[len];
				for (int i = 0; i < len; i++) {
					sheetTitles[i] = XLPObjectUtil.object2Str(titles[i]); 
				}
			}else if (!sheetData.isEmpty()) {
				Object[] titles = sheetData.get(0);
				int len = titles.length;
				sheetTitles = new String[len];
				for (int i = 0; i < len; i++) {
					sheetTitles[i] = String.valueOf(i);  
				}
			}
			
			int index = 0;
			for(Object[] data : sheetData){
				int len = Math.min(data.length, sheetTitles.length);
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < len; i++) {
					map.put(sheetTitles[i], data[i]);
				}
				map.put("sheetRowNo", rowNoList.get(index++));
				sheetDataMap.add(map);
			}
		}
	}

	public String[] getSheetTitles() {
		return sheetTitles;
	}

	public void setSheetTitles(String[] sheetTitles) {
		if (sheetTitles != null) {
			this.sheetTitles = sheetTitles;
		}
	}

	public List<Object[]> getSheetData() {
		return sheetData;
	}

	public void setSheetData(List<Object[]> sheetData) {
		if (sheetData != null) {
			this.sheetData = sheetData;
		}
	}

	public List<Map<String, ?>> getSheetDataMap() {
		return sheetDataMap;
	}

	public void setSheetDataMap(List<Map<String, ?>> sheetDataMap) {
		if (sheetDataMap != null) {
			this.sheetDataMap = sheetDataMap;
		}
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex < 0 ? 0 : sheetIndex;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = XLPStringUtil.emptyTrim(sheetName);
	}

	public List<Integer> getRowNoList() {
		return rowNoList;
	}

	public void setRowNoList(List<Integer> rowNoList) {
		if (rowNoList != null) {
			this.rowNoList = rowNoList;
		}
	}
	
	/**
	 * 把sheet页数据转换Javabean对象,默认启用注解进行转换
	 * 
	 * @param beanClass
	 * @return
	 */
	public <T> List<T> toBeanList(Class<T> beanClass){
		return toBeanList(beanClass, true);
	}
	
	/**
	 * 把sheet页数据转换Javabean对象
	 * 
	 * @param beanClass
	 * @param isUsedAnnotation 是否启用注解进行转换，true是，false不启用
	 * @return
	 */
	public <T> List<T> toBeanList(Class<T> beanClass, boolean isUsedAnnotation){
		SheetDataBeanConverter<T> sheetDataToBean = new SheetDataBeanConverter<T>(isUsedAnnotation);
		return sheetDataToBean.mapListToBeanList(sheetDataMap, beanClass);
	}

	public ValidateRowData getValidateRowData() {
		return validateRowData;
	}

	public void setValidateRowData(ValidateRowData validateRowData) {
		this.validateRowData = validateRowData;
	}

	/**
	 * 释放所占Sheet资源
	 */
	public void releaseSheet(){
		sheet = null;
	}
	
	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
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

	public boolean isProtectSheet() {
		return protectSheet;
	}

	/**
	 * 标记sheet是否受保护，不可编辑，值为true时不可编辑，false时可以编辑
	 * 
	 * @param protectSheet
	 */
	public void setProtectSheet(boolean protectSheet) {
		this.protectSheet = protectSheet;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * sheet受保护时，需设置的密码
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = XLPStringUtil.emptyTrim(password); 
	}
	
	public List<Integer> getHideColIndexs() {
		return hideColIndexs;
	}

	/**
	 * 设置要隐藏的列索引值，从0开始
	 * 
	 * @param hideColIndexs
	 */
	public void setHideColIndexs(List<Integer> hideColIndexs) {
		if (hideColIndexs != null) {
			this.hideColIndexs = hideColIndexs;
		}
	}
	
	/**
	 * 添加要隐藏的列索引值，从0开始
	 * 
	 * @param hideColIndex
	 */
	public void addHideColIndexs(int hideColIndex) {
		if (hideColIndex >= 0) {
			hideColIndexs.add(Integer.valueOf(hideColIndex));
		}
	}

	public int getFixedCols() {
		return fixedCols;
	}

	/**
	 * 设置要固定的列数
	 * 
	 * @param fixedCols 假如参数小于0，则自动设置为0
	 */
	public void setFixedCols(int fixedCols) {
		this.fixedCols = fixedCols < 0 ? 0 : fixedCols;
	}

	public int getFixedRows() {
		return fixedRows;
	}

	/**
	 * 设置要固定的行数
	 * 
	 * @param fixedRows 假如参数小于0，则自动设置为0
	 */
	public void setFixedRows(int fixedRows) {
		this.fixedRows = fixedRows < 0 ? 0 : fixedRows;
	}
}
