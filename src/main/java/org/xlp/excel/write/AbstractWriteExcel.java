package org.xlp.excel.write;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.xlp.excel.read.SheetData;
import org.xlp.excel.read.SheetDataBeanConverter;
import org.xlp.javabean.convert.mapandbean.MapValueProcesser;
import org.xlp.javabean.processer.ValueProcesser;
import org.xlp.utils.XLPArrayUtil;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.collection.XLPCollectionUtil;

public abstract class AbstractWriteExcel implements WriteExcel {
	/**
	 * excel 工作簿
	 */
	protected Workbook workbook;

	/**
	 * sheet数据
	 */
	private List<SheetData> sheetDataList = new ArrayList<SheetData>();

	/**
	 * sheet表头样式
	 */
	private CellStyle headStyle;

	/**
	 * sheet表头数据样式
	 */
	private CellStyle dataStyle;

	/**
	 * sheet 表格的宽度
	 */
	private int cellWidth;

	/**
	 * sheet 表格的高度
	 */
	private int cellHeight;

	/**
	 * 把object值转换成字符串处理器
	 */
	private ValueProcesser valueProcesser = new MapValueProcesser();

	/**
	 * 标记是否自动关闭资源，默认自动关闭，即写完一次excel数据，释放workbook资源，如要重新写，则需新建该对象
	 */
	private boolean autoClose = true;
	
	/**
	 * 把数据写入excel输出流中
	 * 
	 * @param outputStream
	 * @throws IOException
	 *             假如数据写入失败时，抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，抛出该异常
	 */
	@Override
	public void write(OutputStream outputStream) throws IOException {
		if (outputStream == null) {
			throw new NullPointerException("outputStream param is null!");
		}
		Sheet sheet;
		String[] titles;
		List<Object[]> dataList;
		try {
			for (SheetData sheetData : sheetDataList) {
				if (sheetData != null) {
					sheet = sheetData.getSheet();
					String sheetName = sheetData.getSheetName();

					if (sheet != null && sheet != workbook.getSheet(sheet.getSheetName())) {
						sheet = null;
					}

					// 根据sheet的名称获取指定的sheet
					if (sheet == null && !XLPStringUtil.isEmpty(sheetName)) {
						sheet = workbook.getSheet(sheetName);
					}

					// 创建sheet
					if (sheet == null) {
						if (XLPStringUtil.isEmpty(sheetName)) {
							sheet = workbook.createSheet();
						} else {
							sheet = workbook.createSheet(sheetName);
						}
					}
					
					if (sheetData.isProtectSheet()) {
						sheet.protectSheet(sheetData.getPassword()); 
					}
					
					titles = sheetData.getSheetTitles();
					dataList = sheetData.getSheetData();
					int rowNo = sheet.getLastRowNum() + 1;
					if (!XLPArrayUtil.isEmpty(titles)) {
						// 初始化表头样式
						initHeadStyle();
						rowNo = initRowData(sheet, rowNo, titles, headStyle);
					}
					if (!XLPCollectionUtil.isEmpty(dataList)) {
						initDataStyle();
						for (Object[] data : dataList) {
							rowNo = initRowData(sheet, rowNo, data, dataStyle);
						}
					}
					//设置隐藏的列
					for(int hideCol : sheetData.getHideColIndexs()){
						sheet.setColumnHidden(hideCol, true); 
					}
					
					int fixedCols = sheetData.getFixedCols();
					int fixedRows = sheetData.getFixedRows();
					if (fixedCols > 0 || fixedRows > 0) {
						//设置固定行或列
						sheet.createFreezePane(fixedCols, fixedRows); 
					}
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
	 * 初始化row数据
	 * 
	 * @param sheet
	 * @param rowNo
	 * @param data
	 * @param cellStyle
	 * @return
	 */
	private int initRowData(Sheet sheet, int rowNo, Object[] data, CellStyle cellStyle) {
		Row row;
		Cell cell;
		row = sheet.createRow(rowNo++);
		// 设置行高
		if (cellHeight > 0) {
			row.setHeightInPoints(cellHeight);
		}
		int len = data.length;
		for (int i = 0; i < len; i++) {
			if (cellWidth > 0) {
				// 单位为字符宽度的1/256
				sheet.setColumnWidth(i, cellWidth * 256);
			}
			cell = row.createCell(i);
			String value = (String) valueProcesser.processValue(String.class, data[i]);
			value = XLPStringUtil.emptyTrim(value);
			cell.setCellValue(value);
			cell.setCellStyle(cellStyle);
		}
		return rowNo;
	}

	/**
	 * 写入指定名称的文件里
	 * 
	 * @param fileName
	 * @throws IOException
	 * @throws NullPointerException
	 *             假如参数为空，抛出该异常
	 */
	@Override
	public void write(String fileName) throws IOException {
		if (XLPStringUtil.isEmpty(fileName)) {
			throw new NullPointerException("fileName param is null!");
		}
		write(new File(fileName));
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param dataMap
	 */
	public void addExcelData(Collection<Map<String, ?>> dataMap) {
		addExcelData(null, dataMap);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param dataMap
	 */
	public void addExcelData(String sheetName, Collection<Map<String, ?>> dataMap) {
		if (!XLPCollectionUtil.isEmpty(dataMap)) {
			List<String> titles = new ArrayList<String>();
			List<Object[]> dataList = new ArrayList<Object[]>();
			Iterator<Map<String, ?>> it = dataMap.iterator();
			if (it.hasNext()) {
				Map<String, ?> map = it.next();
				Object[] datas = new Object[map.size()];
				int index = 0;
				for (Entry<String, ?> entry : map.entrySet()) {
					titles.add(entry.getKey());
					datas[index++] = entry.getValue();
				}
				dataList.add(datas);
			}
			List<Entry<String, ?>> notContainEntry = new ArrayList<Entry<String, ?>>();
			while (it.hasNext()) {
				Map<String, ?> map = it.next();
				int titlesSize = titles.size();
				Object[] datas = new Object[titlesSize];
				int index = -1;
				for (Entry<String, ?> entry : map.entrySet()) {
					index = titles.indexOf(entry.getKey());
					if (index != -1) {
						datas[index] = entry.getValue();
					} else {
						notContainEntry.add(entry);
					}
				}
				// 计算不存在的值
				int notSize = notContainEntry.size();
				if (notSize > 0) {
					int startIndex = titlesSize;
					datas = Arrays.copyOf(datas, notSize + titlesSize);
					for (Entry<String, ?> entry : notContainEntry) {
						titles.add(entry.getKey());
						datas[startIndex++] = entry.getValue();
					}
				}
				notContainEntry.clear();
				notContainEntry = null;
				dataList.add(datas);
			}

			SheetData sheetData = createSheetData(titles.toArray(new String[0]), dataList, sheetName);
			sheetDataList.add(sheetData);
		}
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param dataList
	 */
	public void addExcelData(List<Object[]> dataList) {
		addExcelData((String[]) null, dataList);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param dataList
	 */
	public void addExcelData(String sheetName, List<Object[]> dataList) {
		addExcelData(sheetName, false, dataList);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param hasContanTitles
	 *            数据第一行是否是表头
	 * @param dataList
	 */
	public void addExcelData(boolean hasContanTitles, List<Object[]> dataList) {
		addExcelData(null, hasContanTitles, dataList);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param hasContanTitles
	 *            数据第一行是否是表头
	 * @param dataList
	 */
	public void addExcelData(String sheetName, boolean hasContanTitles, List<Object[]> dataList) {
		if (!XLPCollectionUtil.isEmpty(dataList)) {
			List<Object[]> tempList = new ArrayList<Object[]>(dataList);
			String[] titles = null;
			if (hasContanTitles) {
				Object[] temp = tempList.remove(0);
				int tLen = temp.length;
				titles = new String[tLen];
				for (int k = 0; k < tLen; k++) {
					titles[k] = temp[k] == null ? "" : temp[k].toString();
				}
			}
			SheetData sheetData = createSheetData(titles, tempList, sheetName);
			sheetDataList.add(sheetData);
		}
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param titles
	 *            sheet的表头
	 * @param dataList
	 *            要写入的数据
	 */
	public void addExcelData(String sheetName, String[] titles, List<Object[]> dataList) {
		SheetData sheetData = createSheetData(titles, dataList, sheetName);
		sheetDataList.add(sheetData);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param titles
	 *            sheet的表头
	 * @param dataList
	 *            要写入的数据
	 */
	public void addExcelData(String[] titles, List<Object[]> dataList) {
		addExcelData(null, titles, dataList);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param dataList
	 *            要写入的数据
	 * @param isUsedAnnotation
	 *            是否启用ExcelField注解转换 true是，false不启用
	 */
	public <T> void addExcelDataOfBeans(String sheetName, List<T> beanList, boolean isUsedAnnotation) {
		if (!XLPCollectionUtil.isEmpty(beanList)) {
			List<Map<String, ?>> dataMaps = new SheetDataBeanConverter<T>(isUsedAnnotation).beanListToMapList(beanList);
			addExcelData(sheetName, dataMaps);
		}
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param sheetName
	 *            数据对应的sheet的名称
	 * @param dataList
	 *            要写入的数据
	 */
	public <T> void addExcelDataOfBeans(String sheetName, List<T> beanList) {
		addExcelDataOfBeans(sheetName, beanList, true);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param dataList
	 *            要写入的数据
	 */
	public <T> void addExcelDataOfBeans(List<T> beanList) {
		addExcelDataOfBeans(null, beanList);
	}

	/**
	 * 要写入的excel的数据
	 * 
	 * @param dataList
	 *            要写入的数据
	 * @param isUsedAnnotation
	 *            是否启用ExcelField注解转换 true是，false不启用
	 */
	public <T> void addExcelDataOfBeans(List<T> beanList, boolean isUsedAnnotation) {
		addExcelDataOfBeans(null, beanList, isUsedAnnotation);
	}

	/**
	 * 创建SheetData 对象
	 * 
	 * @param titles
	 *            表头
	 * @param dataList
	 *            sheet页数据
	 * @param sheetName
	 *            sheet页名称
	 * @return
	 */
	private SheetData createSheetData(String[] titles, List<Object[]> dataList, String sheetName) {
		SheetData sheetData = new SheetData();
		sheetData.setSheetData(dataList);
		sheetData.setSheetTitles(titles);
		sheetData.setSheetName(sheetName);
		return sheetData;
	}

	public List<SheetData> getSheetDataList() {
		return sheetDataList;
	}

	public void setSheetDataList(List<SheetData> sheetDataList) {
		if (sheetDataList != null) {
			this.sheetDataList = sheetDataList;
		}
	}

	public void addExcelData(SheetData sheetData) {
		if (sheetData != null) {
			this.sheetDataList.add(sheetData);
		}
	}

	public CellStyle getHeadStyle() {
		initHeadStyle();
		return headStyle;
	}

	public void setHeadStyle(CellStyle headStyle) {
		this.headStyle = headStyle;
	}

	public ValueProcesser getValueProcesser() {
		return valueProcesser;
	}

	public void setValueProcesser(ValueProcesser valueProcesser) {
		if (valueProcesser != null) {
			this.valueProcesser = valueProcesser;
		}
	}

	protected void initHeadStyle() {
		if (headStyle == null) {
			headStyle = workbook.createCellStyle();
			// 对齐方式设置
			headStyle.setAlignment(HorizontalAlignment.CENTER);
			headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			// 设置背景颜色
			headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			// 粗体字设置
			Font font = workbook.createFont();
			font.setBold(true);
			headStyle.setFont(font);
			DataFormat format = workbook.createDataFormat();
			headStyle.setDataFormat(format.getFormat("@"));
		}
	}

	protected void initDataStyle() {
		if (dataStyle == null) {
			dataStyle = workbook.createCellStyle();
			// 对齐方式设置
			dataStyle.setAlignment(HorizontalAlignment.LEFT);
			dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			DataFormat format = workbook.createDataFormat();
			dataStyle.setDataFormat(format.getFormat("@"));
		}
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	public CellStyle getDataStyle() {
		initDataStyle();
		return dataStyle;
	}

	public void setDataStyle(CellStyle dataStyle) {
		this.dataStyle = dataStyle;
	}

	public Workbook getWorkbook() {
		return workbook;
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
