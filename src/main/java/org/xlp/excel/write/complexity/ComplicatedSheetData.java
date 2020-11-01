package org.xlp.excel.write.complexity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xlp.utils.XLPStringUtil;

/**
 * <p>
 * 创建时间：2020年6月29日 下午11:17:37
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description excel sheet页数据对象
 */
public class ComplicatedSheetData {
	/**
	 * sheet名称
	 */
	private String sheetName;

	/**
	 * sheet每行的高度
	 */
	private int rowHeight;

	/**
	 * sheet中cell填充的数据对象集合
	 */
	private List<ExcelCellData> excelCellDatas = new LinkedList<ExcelCellData>();

	/**
	 * 标记sheet是否受保护，不可编辑，值为true时不可编辑，false时可以编辑
	 */
	private boolean protectSheet = false;
	
	/**
	 * sheet受保护时，需设置的密码
	 */
	private String password = XLPStringUtil.EMPTY;
	
	/**
	 * 标记sheet是否强制受保护，不可编辑，值为true时强制受保护，不可编辑，false时可以编辑
	 * <br/>
	 * 当值为true时，无论sheet cell 设置setLocked值为true还是false，sheet都不可以编辑
	 */
	private boolean forceProtectSheet = false;
	
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
	
	/**
	 * 构造函数
	 */
	public ComplicatedSheetData() {
	}

	/**
	 * 构造函数
	 * 
	 * @param excelCellDatas
	 */
	public ComplicatedSheetData(List<ExcelCellData> excelCellDatas) {
		setExcelCellDatas(excelCellDatas);
	}

	/**
	 * 构造函数
	 * 
	 * @param sheetName
	 */
	public ComplicatedSheetData(String sheetName) {
		setSheetName(sheetName);
	}

	/**
	 * 构造函数
	 * 
	 * @param sheetName
	 * @param rowHeight
	 * @param excelCellDatas
	 */
	public ComplicatedSheetData(String sheetName, int rowHeight, 
			List<ExcelCellData> excelCellDatas) {
		setSheetName(sheetName);
		this.rowHeight = rowHeight;
		setExcelCellDatas(excelCellDatas);
	}

	public String getSheetName() {
		return sheetName;
	}

	/**
	 * 设置sheet名称
	 * 
	 * @param sheetName
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = XLPStringUtil.emptyTrim(sheetName);
	}

	public int getRowHeight() {
		return rowHeight;
	}

	/**
	 * 设置行高
	 * 
	 * @param rowHeight
	 */
	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public List<ExcelCellData> getExcelCellDatas() {
		return excelCellDatas;
	}

	/**
	 * 设置sheet中cell填充的数据对象集合
	 * 
	 * @param excelCellDatas
	 */
	public void setExcelCellDatas(List<ExcelCellData> excelCellDatas) {
		if (excelCellDatas != null) {
			this.excelCellDatas = excelCellDatas;
		}
	}
	
	/**
	 * 为sheet添加cell填充的数据对象
	 * 
	 * @param excelCellData
	 */
	public void addExcelCellData(ExcelCellData excelCellData){
		if (excelCellData != null) {
			excelCellDatas.add(excelCellData);
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

	public boolean isForceProtectSheet() {
		return forceProtectSheet;
	}

	/**
	 * 标记sheet是否强制受保护，不可编辑，值为true时强制受保护，不可编辑，false时可以编辑
	 * <br/>
	 * 当值为true时，无论sheet cell 设置setLocked值为true还是false，sheet都不可以编辑
	 * 
	 * @param forceProtectSheet
	 */
	public void setForceProtectSheet(boolean forceProtectSheet) {
		this.forceProtectSheet = forceProtectSheet;
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
