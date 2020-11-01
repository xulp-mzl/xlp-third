package org.xlp.xlp_third;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xlp.excel.write.ExcelType;
import org.xlp.excel.write.complexity.ComplicatedSheetData;
import org.xlp.excel.write.complexity.ExcelCellData;
import org.xlp.excel.write.complexity.WriteExcelExt;
import org.xlp.excel.write.complexity.enumeration.Color;
import org.xlp.excel.write.complexity.enumeration.DataType;
import org.xlp.utils.XLPDateUtil;
import org.xlp.utils.XLPStringUtil;

/**
 * <p>创建时间：2020年8月28日 下午11:34:46</p>
 * @author xlp
 * @version 1.0 
 * @Description 类描述
*/
public class TestExcel {
	public static void main(String[] args) throws IOException {
		System.out.println(XLPStringUtil.startsWith("=DaTe(x,x)", "[=]{0,1}(?i)Date\\("));
		ComplicatedSheetData complicatedSheetData = new ComplicatedSheetData();
		ExcelCellData excelCellData = new ExcelCellData("hh姐姐", 0, 1, 0, 0);
		excelCellData.setBackgroundColor(Color.BLUE);
		excelCellData.setMark("测试");
		List<String> list = new ArrayList<String>();
		list.add("eee");
		list.add("姐姐");
		excelCellData.setSelectOptions(list);
		excelCellData.setMaxFormatRow(1);
		//excelCellData.setErrorTip("123");
		complicatedSheetData.addExcelCellData(excelCellData);
		excelCellData = new ExcelCellData("hh", 2, 2, 1, 2);
		excelCellData.setMark("测试ee");
		complicatedSheetData.addExcelCellData(excelCellData);
		excelCellData = new ExcelCellData(1, 3, 3, 3, 3);
		excelCellData.setMark("ee");
		excelCellData.setBackgroundColor(Color.BLUE);
		excelCellData.setFormatter("0.00");
		excelCellData.setDataType(DataType.DECIMAL);
		excelCellData.setMaxFormatRow(4);
		
		excelCellData = new ExcelCellData(LocalTime.of(12, 1, 4), 3, 3, 6, 6);
		excelCellData.setMinValue(new Date());
		excelCellData.setFormatter("HH:mm");
		excelCellData.setDataType(DataType.TIME);
		complicatedSheetData.addExcelCellData(excelCellData);
		//excelCellData.setMaxFormatRow(1);
		
		excelCellData = new ExcelCellData(12, 4, 4, 6, 6);
		excelCellData.setMinValue(2);
		excelCellData.setDataType(DataType.INT);
		excelCellData.setErrorTip("输入值必须大于等于2");
		excelCellData.setFormatter("0");
		complicatedSheetData.addExcelCellData(excelCellData);
		
		excelCellData = new ExcelCellData(new Date(), 4, 4, 7, 7);
		excelCellData.setMinValue(new Date());
		excelCellData.setDataType(DataType.DATE);
		excelCellData.setFormatter("yyyy-MM-dd");
		excelCellData.setMaxFormatRow(1);
		complicatedSheetData.addExcelCellData(excelCellData);
		
		excelCellData = new ExcelCellData("44", 4, 4, 8, 8);
		excelCellData.setMinValue(2);
		excelCellData.setMaxValue(3);
		excelCellData.setMaxFormatRow(1);
		complicatedSheetData.addExcelCellData(excelCellData);
		
		excelCellData = new ExcelCellData(44, 4, 4, 9, 9);
		excelCellData.setMinValue(2);
		excelCellData.setMaxValue(33);
		excelCellData.setDataType(DataType.DECIMAL);;
		excelCellData.setFormatter("0.00");
		complicatedSheetData.addExcelCellData(excelCellData);
		
		complicatedSheetData.addHideColIndexs(1);
		complicatedSheetData.setFixedCols(2);
		WriteExcelExt writeExcelExtXLSX = new WriteExcelExt(complicatedSheetData);
		WriteExcelExt writeExcelExtXLS = new WriteExcelExt(complicatedSheetData, ExcelType.XLS);
		writeExcelExtXLS.write(new File("f:\\apk\\1.xls"));
		writeExcelExtXLSX.write(new File("f:\\apk\\1.xlsx"));
	}
}
