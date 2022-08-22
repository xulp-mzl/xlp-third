package org.xlp.excel.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.xlp.utils.XLPArrayUtil;
import org.xlp.utils.XLPStringUtil;

/**
 * excel操作工具类
 * 
 * @author xlp
 * @date 2020-04-18
 */
public class ExcelUtils {
	/**
	 * 判断row是否为空行
	 * 
	 * @param datas
	 * @return 假如是返回true，否则返回false
	 */
	public static boolean sheetRowIsEmpty(Object[] datas) {
		if (XLPArrayUtil.isEmpty(datas)) {
			return true;
		}
		for (Object s : datas) {
			if (s != null && !XLPStringUtil.isEmpty(s.toString())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取excel表格值
	 * 
	 * @param cell 表格单元
	 * @param formulaEvaluator 读取公式中的cell值
	 * @return 假如第一个参数为null, 则返回""
	 */
	public static Object getCellValue(Cell cell, FormulaEvaluator formulaEvaluator){
		if (cell == null) return XLPStringUtil.EMPTY;
		Object cellValue;
		// 判断单元格的数据类型
		switch (cell.getCellType()) {
			case NUMERIC: // 数字
				if (DateUtil.isCellDateFormatted(cell)) {
					cellValue = cell.getDateCellValue();
				}else {
					cellValue = cell.getNumericCellValue();
				}
				break;
			case STRING: // 字符串
				cellValue = cell.getStringCellValue();
				break;
			case BOOLEAN: // Boolean
				cellValue = cell.getBooleanCellValue();
				break;
			case FORMULA: // 公式
				if (formulaEvaluator != null) {
					cell = formulaEvaluator.evaluateInCell(cell);
					cellValue = getCellValue(cell, null);
				}else {
					cellValue = cell.getCellFormula();
				}
				break;
			case ERROR: // 故障
				cellValue = cell.getErrorCellValue();
				break;
			case BLANK: // 空值
			case _NONE: 
			default:
				cellValue = XLPStringUtil.EMPTY;
		}
		return cellValue;
	}
}
