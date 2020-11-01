package org.xlp.xlp_third;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.xlp.excel.read.BlankRowOptionType;
import org.xlp.excel.read.ReadExcel;
import org.xlp.excel.read.SheetData;
import org.xlp.excel.write.WriteXLSExcel;
import org.xlp.excel.write.WriteXLSXExcel;
import org.xlp.json.JsonObject;
import org.xlp.json.utils.JsonHelper;
import org.xlp.utils.collection.XLPCollectionUtil;
import org.xlp.xml.json.JsonXmlHelper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * 
	 * @throws IOException
	 */
	public void testApp() throws IOException {
		assertTrue(true);
		ReadExcel readExcel = null;
		try {
			readExcel = new ReadExcel("f:\\tt\\2.xls", "123456");
			readExcel.setExcelHasTitles(false);
			readExcel.setBlankRowOptionType(BlankRowOptionType.SKIP);
			readExcel.readExcel();
			System.out.println(readExcel.getSheetDataList().size());
			System.out.println(readExcel.getSheetData().getSheetDataMap());
			System.out.println(readExcel.getSheetData().getSheetTitles().length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		Map<String, Object> map = new HashMap<String, Object>();
		// List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			// UnZip unZip = new UnZip(new
			// File("E:\\下载文件\\sqljdbc.jar和sqljdbc4.rar"));
			// unZip.unZip("E:\\tt");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] s = { "ww", "好好", "er", "时间" };

		List<Object[]> list2 = new ArrayList<>();
		Object[] objects = { 1, 333333333333333333l, "好好", new Date() };
		list2.add(s);
		list2.add(objects);
		list2.add(objects);
		WriteXLSExcel writeXLSExcel = new WriteXLSExcel();
		writeXLSExcel.setCellHeight(15);
		writeXLSExcel.setCellWidth(20);
		writeXLSExcel.addExcelData("kk1", true, list2);
		writeXLSExcel.addExcelData("测试", true, list2);
		SheetData sheetData = new SheetData();
		sheetData.setProtectSheet(true);
		sheetData.setPassword("123");
		sheetData.setSheetName("protect");
		writeXLSExcel.addExcelData(sheetData);
		Collection<Map<String, ?>> list3 = new ArrayList<Map<String, ?>>();
		Map<String, Object> map2 = new HashMap<>();
		map2.put("ss", 14444);
		map2.put("是谁", "''");
		map.put("w", "19");
		list3.add(map);
		list3.add(map2);
		writeXLSExcel.addExcelData(list3);
		writeXLSExcel.addExcelData("好好看1", s, list2);
		writeXLSExcel.addExcelData(list2);
		A a = new A();
		a.set_a(12);
		a.setDs_a_Ac(34);
		writeXLSExcel.addExcelDataOfBeans("bean", XLPCollectionUtil.initList(a, a));
		try {
			writeXLSExcel.write("f:\\tt\\2.xls");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JsonObject json = new JsonObject();
		json.put("ww", "{[]]\\\"\\/eee]}");
		json.put("f\"", "\\");
		json.put("\\", "2");
		JsonObject json1 = new JsonObject();
		json1.put("ww", "{[]]\\\"\\/eee]}");
		json1.put("f\"", "\\");
		json1.put("\\", "2");
		json.put("json", json1);
		System.out.println(json.toString());
		System.out.println(JsonXmlHelper.toXMLString(json));
		System.out.println();
		json = JsonObject.fromJsonString(json.toString());
		System.out.println(json.getString("ww"));
		JsonObject jsonObject = JsonObject.fromBean(new A(), null, false);
		// jsonObject.put("_a", 9);
		System.out.println(jsonObject);
		System.out.println(jsonObject.toBean(A.class, true));

		System.out.println(JsonXmlHelper.fromXmlString("<xml id=\"[]&lt;\">ee</xml>"));
		System.out.println(JsonXmlHelper.toXMLString(JsonXmlHelper.fromXmlString("<xml id=\"[]&lt;\">ee</xml>")));
		// JsonHelper.setXmlTagType(XMLTagType.TO_LOWER);
		System.out.println(
				JsonXmlHelper.toXMLString(JsonXmlHelper.fromXmlString("<xml ID=\"[]&lt;\"><b>4</b><b>4</b></xml>")));
		System.out.println(JsonXmlHelper.fromXmlString("<xml ID=\"[]&lt;\"><b>4</b><b>4</b></xml>"));
		CellStyle cellStyle = new XSSFCellStyle(new StylesTable());
		System.out.println(cellStyle);
		try {
			cellStyle = WorkbookFactory.create(false).createCellStyle();
			System.out.println(cellStyle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
