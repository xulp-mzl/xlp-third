package org.xlp.excel.read;

import org.xlp.excel.util.ExcelUtils;

/**
 * 验证excel中是否为空验证默认实现类
 * 
 * @author xlp
 * @date 2020-04-19
 */
public class DefaultValidateRowData implements ValidateRowData{
	@Override
	public boolean rowDataIsEmpty(Object[] rowData) {
		return ExcelUtils.sheetRowIsEmpty(rowData);
	}

}
