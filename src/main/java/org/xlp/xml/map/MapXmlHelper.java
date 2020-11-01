package org.xlp.xml.map;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.collection.XLPCollectionUtil;
import org.xlp.utils.io.XLPIOUtil;
import org.xlp.xml.XMLTagType;
import org.xlp.xml.XmlConverter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>创建时间：2020年7月19日 下午6:49:32</p>
 * @author xlp
 * @version 1.0 
 * @Description xml与map相互转换工具类
*/
public class MapXmlHelper {
	/**
	 * 把map对象装换成xml字符串格式
	 * 
	 * @param map 
	 * @return xml格式的字符串
	 */
	public static String toXMLString(Map<String, Object> map){
		return toXMLString(map, null, null, null);
	}
	
	/**
	 * 把map对象装换成xml字符串格式
	 * 
	 * @param map 
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(Map<String, Object> map, XMLTagType xmlTagType){
		return toXMLString(map, null, xmlTagType);
	}
	
	/**
	 * 把map对象装换成xml字符串格式
	 * 
	 * @param map 
	 * @return xml格式的字符串
	 * @param rootElemetName 跟标签名称
	 */
	public static String toXMLString(Map<String, Object> map, String rootElemetName){
		return toXMLString(map, rootElemetName, (XMLTagType)null);
	}
	
	/**
	 * 把map对象装换成xml字符串格式
	 * 
	 * @param map 
	 * @param rootElemetName 跟标签名称
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(Map<String, Object> map, String rootElemetName,
			XMLTagType xmlTagType){
		return toXMLString(map, rootElemetName, null, xmlTagType);
	}

	/**
	 * 把map对象装换成xml字符串格式
	 * 
	 * @param map 
	 * @param rootElemetName 跟标签名称
	 * @param charsetName xml的encoding=[charsetName]
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(Map<String, Object> map,
			String rootElemetName, String charsetName, XMLTagType xmlTagType){
		XmlConverter xmlConverter = new MapXmlConverter(map, rootElemetName, charsetName);
		xmlConverter.setXmlTagType(xmlTagType);
		return xmlConverter.toXmlString();
	}
	
	/**
	 * 把map对象装换成xml字符串格式
	 * 
	 * @param map 
	 * @param rootElemetName 跟标签名称
	 * @param charsetName xml的encoding=[charsetName]
	 * @return xml格式的字符串
	 */
	public static String toXMLString(Map<String, Object> map,
			String rootElemetName, String charsetName){
		return toXMLString(map, rootElemetName, charsetName, null);
	}
	
	/**
	 * 把List Map集合装换成xml字符串格式
	 * 
	 * @param maps 
	 * @param rootElemetName 跟标签名称
	 * @param charsetName xml的encoding=[charsetName]
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(List<Map<String, Object>> maps,
			String rootElemetName, String charsetName, XMLTagType xmlTagType){
		XmlConverter xmlConverter = new MapXmlConverter(maps, rootElemetName, charsetName);
		xmlConverter.setXmlTagType(xmlTagType);
		return xmlConverter.toXmlString();
	}
	
	/**
	 * 把List Map集合装换成xml字符串格式
	 * 
	 * @param maps 
	 * @param rootElemetName 跟标签名称
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(List<Map<String, Object>> maps,
			String rootElemetName, XMLTagType xmlTagType){
		return toXMLString(maps, rootElemetName, null, xmlTagType);
	}
	
	/**
	 * 把List Map集合装换成xml字符串格式
	 * 
	 * @param maps 
	 * @param rootElemetName 跟标签名称
	 * @return xml格式的字符串
	 */
	public static String toXMLString(List<Map<String, Object>> maps, String rootElemetName){
		return toXMLString(maps, rootElemetName, null, null);
	}
	
	/**
	 * 把适合的xml的Document对象转换成Map对象
	 * 
	 * @param document
	 * @return 假如document为空，返回null
	 */
	public static Map<String, Object> fromDocument(Document document){
		if(document == null)
			return null;
		//把字符串解析成Document对象
		document.setEntityResolver(new EntityResolver() {//不检查约束文件
			@Override
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				return new InputSource(new ByteArrayInputStream("".getBytes()));
			}
			
		});
		Element element = document.getRootElement();//获取根元素
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		dealXmlElement(map, element);
		return map;
	}
	
	/**
	 * 处理每一个元素节点
	 * 
	 * @param jsonObject
	 * @param jsonConfig
	 * @param element
	 */
	@SuppressWarnings("all")
	private static void dealXmlElement(Map<String, Object> map, Element element){
		if (map == null || element == null) {
			return;
		}
		List<Attribute> attributes = element.attributes();
		List<Element> elementList = element.elements();
		
		Map<String, Object> attrMap = new LinkedHashMap<String, Object>();
		//读取xml属性值
		for (Attribute attribute : attributes) {
			attrMap.put(attribute.getName(), 
					XLPStringUtil.emptyTrim(attribute.getValue()));
		}
		if (XLPCollectionUtil.isEmpty(elementList)) { 
			String text = element.getTextTrim();
			if (!attrMap.isEmpty() && !XLPStringUtil.isEmpty(text)) { 
				String key = element.getName() + "Text";
				String attrValue = (String) attrMap.get(key);
				if (XLPStringUtil.isEmpty(attrValue)) {
					attrMap.put(key, text);
				}else {
					List<Object> list = new ArrayList<Object>(2);
					list.add(attrValue);
					list.add(text);
					attrMap.put(key, list);
				}
			}
		}
		
		//假如没有属性也没有子元素，去该标签文本
		if (XLPCollectionUtil.isEmpty(elementList) && attrMap.isEmpty()) { 
			map.put(element.getName(), element.getTextTrim());
		}
		
		Map<String, Object> temp;
		for (Element ele : elementList) {
			temp = new LinkedHashMap<String, Object>();
			dealXmlElement(temp, ele);
			for (Entry<String, Object> entry : temp.entrySet()) {
				Object value = entry.getValue();
				String key = entry.getKey();
				
				Object attrValue = attrMap.get(key);
				//判断属性中是否存在该值
				if(attrValue == null){
					attrMap.put(key, value);
				}else if (attrValue instanceof List) {
					((List<Object>)attrValue).add(value);
				}else {
					List<Object> list = new ArrayList<Object>(2);
					list.add(attrValue);
					list.add(value);
					attrMap.put(key, list);
				}
			}
		}
		
		if (!attrMap.isEmpty()) {
			map.put(element.getName(), attrMap);
		}
	}
	
	/**
	 * 把适合的xml字符串格式转换为Map对象
	 * 
	 * @param xmlString
	 * @return 假如字符串为空，返回null
	 * @throws RuntimeException 假如xml解析错误，则抛出该异常
	 */
	public static Map<String, Object> fromXmlString(String xmlString){
		if(XLPStringUtil.isEmpty(xmlString))
			return null;
		//把字符串解析成Document对象
		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlString);
		} catch (DocumentException e) {
			throw new RuntimeException("xml解析错误！" + e.getMessage(), e);
		}
		return fromDocument(document);
	}
	
	/**
	 * 把适合的xml文件转换成Map对象
	 * 
	 * @param xmlFile
	 * @return 假如参数为空，返回null
	 * @throws RuntimeException 假如xml解析错误，则抛出该异常
	 */
	public static Map<String, Object> fromXmlFile(File xmlFile){
		if (xmlFile == null) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("解析错误！" + e.getMessage(), e);
		}
		return _fromXmlInputStream(inputStream, true); 
	}
	
	/**
	 * 把适合的xml文件转换成Map对象
	 * 
	 * @param inputStream
	 * @return 假如参数为空，返回null
	 */
	public static Map<String, Object> fromXmlInputStream(InputStream inputStream){
		return _fromXmlInputStream(inputStream, false);
	}
	
	/**
	 * 把适合的xml文件转换成Map对象
	 * 
	 * @param inputStream
	 * @param close 是否关闭输入流，true是，false否
	 * @return 假如参数为空，返回null
	 */
	private static Map<String, Object> _fromXmlInputStream(InputStream inputStream, boolean close){
		if (inputStream == null) {
			return null;
		}
		
		//创建dom4j解析器
		SAXReader reader = new SAXReader();
		//加载document对象
		Document document = null;
		try {
			document = reader.read(inputStream);
		} catch (DocumentException e) {
			throw new RuntimeException("解析错误！" + e.getMessage(), e);
		}finally {
			if (close) {
				XLPIOUtil.closeInputStream(inputStream);
			}
		}
		return fromDocument(document);
	}
	
	/**
	 * 把适合的xml文件转换成Map对象
	 * 
	 * @param xmlFilePath
	 * @return 假如参数为空，返回null
	 * @throws RuntimeException 假如xml解析错误，则抛出该异常
	 */
	public static Map<String, Object> fromXmlFile(String xmlFilePath){
		if (XLPStringUtil.isEmpty(xmlFilePath)) {
			return null;
		}
		return fromXmlFile(new File(xmlFilePath));
	}
}
