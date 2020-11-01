package org.xlp.excel.read;

/**
 * <p>创建时间：2020年7月4日 下午11:16:33</p>
 * @author xlp
 * @version 1.0 
 * @Description 读取excel时，遇到空行处理类型
*/
public enum BlankRowOptionType {
	/**
	 * 中断，不继续读取
	 */
	BREAK,
	
	/**
	 * 跳过空行
	 */
	SKIP,
	
	/**
	 * 正常读取
	 */
	NORMAL
}
