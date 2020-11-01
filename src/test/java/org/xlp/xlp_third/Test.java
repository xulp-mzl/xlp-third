package org.xlp.xlp_third;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xlp.json.JsonArray;
import org.xlp.json.JsonObject;
import org.xlp.json.utils.JsonHelper;
import org.xlp.xml.XMLTagType;
import org.xlp.xml.XmlConverter;
import org.xlp.xml.json.JsonXmlConverter;
import org.xlp.xml.json.JsonXmlHelper;
import org.xlp.xml.map.MapXmlConverter;
import org.xlp.xml.map.MapXmlHelper;

/**
 * <p>创建时间：2020年7月12日 下午6:44:55</p>
 * @author xlp
 * @version 1.0 
 * @Description 类描述
*/
public class Test {
	public static void main(String[] args) {
		fun1();
		//System.out.println(XmlConverter.XML_DEFAULT_FIRST_LINE);
	}
	
	public static void fun() {
		List<Integer> row = new ArrayList<Integer>();
		row.add(12355);
		row.add(23);
		System.out.println(row.indexOf(12355));
		System.out.println(row.contains(12355));
	}
	
	public static void fun1() {
		JsonObject jsonObject = JsonXmlHelper.fromXmlFile(new File("E:\\下载文件\\825集成导出xml格式\\BOM更改业务.xml"));
		System.out.println(jsonObject.format());
		System.out.println(JsonObject.fromJsonString(jsonObject.format()));
		System.out.println(JsonXmlHelper.toXMLString(JsonObject.fromJsonString(jsonObject.format()), "", XMLTagType.TO_UPPER));

		System.out.println(MapXmlHelper.fromXmlFile(new File("E:\\下载文件\\825集成导出xml格式\\BOM更改业务.xml")));
		System.out.println(MapXmlHelper.toXMLString(MapXmlHelper.fromXmlFile(new File("E:\\下载文件\\825集成导出xml格式\\BOM更改业务.xml"))));
	}
	
	public static void fun2() {
		JsonObject jsonObject = new JsonObject();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", 23);
		map.put("key2", 23);
		map.put("key3", 23);
		jsonObject.put("map", map);
		List<Object> list = new ArrayList<>();
		list.add(map);
		list.add("12r");
		jsonObject.put("list", list);
		list = new ArrayList<>();
		list.add(jsonObject.getObject("list"));
		list.add("er");
		jsonObject.put("arr", list);
		jsonObject.put("mm", 56);
		System.out.println(jsonObject.format());
		System.out.println(jsonObject);
		JsonArray jsonArray = new JsonArray();
		
		JsonObject jsonObject2 = new JsonObject();
		jsonObject2.put("json", jsonObject);
		jsonArray.add(jsonObject2);
		jsonArray.add(jsonObject2);
		jsonArray.add("哈哈");
		jsonArray.add(map);
		System.out.println(jsonArray);
		//System.out.println(JsonXmlHelper.toXMLString(jsonObject, "xml"));
		JsonXmlConverter converter = new JsonXmlConverter(jsonArray, "xml");
		System.out.println(converter.getJsonObject());
		//converter.setNeedFirstLine(false);
		System.out.println(converter.toXmlString(true));
		System.out.println(jsonObject.getKey(1));
	}

	public static void fun3() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", 23);
		map.put("key2", 23);
		map.put("key3", 23);
		List<Map<String, Object> > list = new ArrayList<>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("map", map);
		list.add(map2);
		list.add(map2);
		list.add(map);
		
		MapXmlConverter xmlConverter = new MapXmlConverter(map);
		xmlConverter.setRootTagName("xml");
		//xmlConverter.setXmlTagType(XMLTagType.TO_UPPER);
		System.out.println(xmlConverter.toXmlString());
		System.out.println(MapXmlHelper.toXMLString(map, "xml"));
	}
}
