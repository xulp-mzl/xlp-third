package org.xlp.excel.read;

/**
 * 验证excel中是否为空验证接口
 * 
 * @author xlp
 * @date 2020-04-19
 */
public interface ValidateRowData {
	/**
	 * 判断某行是否为空
	 * 
	 * @param rowData
	 * @return 假如是返回true，否则返回false
	 */
	public boolean rowDataIsEmpty(Object[] rowData);
}
