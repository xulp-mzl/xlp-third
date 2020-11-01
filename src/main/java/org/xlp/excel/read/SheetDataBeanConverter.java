package org.xlp.excel.read;

import org.xlp.excel.annotation.ExcelField;
import org.xlp.javabean.PropertyDescriptor;
import org.xlp.javabean.convert.mapandbean.MapBeanAbstract;
import org.xlp.javabean.processer.ValueProcesser;
import org.xlp.utils.XLPStringUtil;

public class SheetDataBeanConverter<T> extends MapBeanAbstract<T> {
	/**
	 * 是否启用ExcelField注解转换
	 */
	private boolean isUsedAnnotation = true;

	public SheetDataBeanConverter() {
		super();
	}

	/**
	 * @param isUsedAnnotation
	 *            是否启用ExcelField注解转换 true是，false不启用
	 */
	public SheetDataBeanConverter(boolean isUsedAnnotation) {
		super();
		this.isUsedAnnotation = isUsedAnnotation;
	}

	/**
	 * @param format
	 *            字符串日期相互转换格式
	 */
	public SheetDataBeanConverter(String format) {
		super(format);
	}

	/**
	 * @param processer
	 *            值处理器
	 */
	public SheetDataBeanConverter(ValueProcesser processer) {
		super(processer);
	}

	private String virtualFieldName(PropertyDescriptor<T> pd) {
		String virtualFieldName = null;
		if (isUsedAnnotation) {
			ExcelField excelField = pd.getFieldAnnotation(ExcelField.class);
			if (excelField != null) {
				String fieldName = XLPStringUtil.emptyTrim(excelField.name());
				if (!XLPStringUtil.isEmpty(fieldName)) {
					virtualFieldName = fieldName;
				} else {
					virtualFieldName = pd.getFieldName();
				}
			}
		} else {
			virtualFieldName = pd.getFieldName();
		}
		return virtualFieldName;
	}

	@Override
	protected String virtualReadFieldName(PropertyDescriptor<T> pd) {
		return virtualFieldName(pd);
	}

	@Override
	protected String virtualWriteFieldName(PropertyDescriptor<T> pd) {
		return virtualFieldName(pd);
	}

	public boolean isUsedAnnotation() {
		return isUsedAnnotation;
	}

	public void setUsedAnnotation(boolean isUsedAnnotation) {
		this.isUsedAnnotation = isUsedAnnotation;
	}

}
