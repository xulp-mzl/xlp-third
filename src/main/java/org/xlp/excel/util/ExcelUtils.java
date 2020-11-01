package org.xlp.excel.util;

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
}
