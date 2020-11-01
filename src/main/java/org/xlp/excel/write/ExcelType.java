package org.xlp.excel.write;

/**
 * 导出excel类型
 * 
 * @author xlp
 * @date 2020-04-20
 */
public enum ExcelType {
	/**
	 * 2003版本
	 */
	XLS(".xls"),
	
	/**
	 * 高于2003版本
	 */
	XLSX(".xlsx");
	
	private String suffix;

	public String getSuffix() {
		return suffix;
	}
	
	private ExcelType(String suffix){
		this.suffix = suffix;
	}
}
