package org.xlp.xml.json;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xlp.json.JsonArray;
import org.xlp.json.JsonElement;
import org.xlp.json.JsonObject;
import org.xlp.json.config.JsonConfig;
import org.xlp.json.exception.JsonException;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.collection.XLPCollectionUtil;
import org.xlp.utils.io.XLPIOUtil;
import org.xlp.xml.XMLTagType;
import org.xlp.xml.XmlConverter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * xml与json相互转换工具类
 * 
 * @author xlp
 * @date 2020-05-13
 */
public class JsonXmlHelper {
	/**
	 * 把适合的xml字符串格式转换为JsonObject对象
	 * 
	 * @param xmlString
	 * @param jsonConfig
	 * @return 假如字符串为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlString(String xmlString, JsonConfig jsonConfig){
		if(XLPStringUtil.isEmpty(xmlString))
			return null;
		//把字符串解析成Document对象
		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlString);
		} catch (DocumentException e) {
			throw new JsonException("Json解析错误！" + e.getMessage(), e);
		}
		return fromDocument(document, jsonConfig);
	}
	
	/**
	 * 把适合的xml的Document对象转换成JsonObject对象
	 * 
	 * @param document
	 * @param jsonConfig
	 * @return 假如document为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromDocument(Document document, JsonConfig jsonConfig){
		if(document == null)
			return null;
		//把字符串解析成Document对象
		try {
			document.setEntityResolver(new EntityResolver() {//不检查约束文件
				@Override
				public InputSource resolveEntity(String publicId, String systemId)
						throws SAXException, IOException {
					return new InputSource(new ByteArrayInputStream("".getBytes()));
				}
				
			});
			Element element = document.getRootElement();//获取根元素
			JsonObject jsonObject = new JsonObject(jsonConfig, true);
			dealXmlElement(jsonObject, jsonConfig, element);
			return jsonObject;
		} catch (Exception e) {
			throw new JsonException("Json解析错误！" + e.getMessage(), e);
		}
	}
	
	/**
	 * 处理每一个元素节点
	 * 
	 * @param jsonObject
	 * @param jsonConfig
	 * @param element
	 */
	@SuppressWarnings("all")
	private static void dealXmlElement(JsonObject jsonObject,
			JsonConfig jsonConfig, Element element){
		if (jsonObject == null || element == null) {
			return;
		}
		List<Attribute> attributes = element.attributes();
		List<Element> elementList = element.elements();
		
		JsonObject attrJson = new JsonObject(jsonConfig, true);
		for (Attribute attribute : attributes) {
			attrJson.put(attribute.getName(), 
					XLPStringUtil.emptyTrim(attribute.getValue()));
		}
		if (XLPCollectionUtil.isEmpty(elementList)) { 
			String text = element.getTextTrim();
			if (!attrJson.isEmpty() && !XLPStringUtil.isEmpty(text)) { 
				attrJson.accumulate(element.getName() + "Text", text);
			}
		}
		
		//假如没有属性也没有子元素，去该标签文本
		if (XLPCollectionUtil.isEmpty(elementList) && attrJson.isEmpty()) { 
			jsonObject.put(element.getName(), element.getTextTrim());
		}
		
		JsonObject temp;
		for (Element ele : elementList) {
			temp = new JsonObject(jsonConfig, true);
			dealXmlElement(temp, jsonConfig, ele);
			for (Entry<String, Object> entry : temp.entrySet()) {
				Object value = entry.getValue();
				String key = entry.getKey();
				if(value.getClass().isArray()){
					JsonElement[] jes = (JsonElement[]) value;
					for (JsonElement jsonElement : jes) {
						attrJson.accumulateElement(key, jsonElement);
					}
				}else {
					attrJson.accumulateElement(key, (JsonElement) value);
				}
			}
		}
		
		if (!attrJson.isEmpty()) {
			jsonObject.put(element.getName(), attrJson);
		}
	}
	
	/**
	 * 把适合的xml字符串格式转换为JsonObject对象
	 * 
	 * @param xmlString
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlString(String xmlString){
		return fromXmlString(xmlString, null);
	}
	
	/**
	 * 把适合的xml的Document对象转换成JsonObject对象
	 * 
	 * @param document
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromDocument(Document document){
		return fromDocument(document, null);
	}
	
	/**
	 * 把适合的xml文件转换成JsonObject对象
	 * 
	 * @param xmlFile
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlFile(File xmlFile){
		return fromXmlFile(xmlFile, null);
	}
	
	/**
	 * 把适合的xml文件转换成JsonObject对象
	 * 
	 * @param xmlFile
	 * @param jsonConfig
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlFile(File xmlFile, JsonConfig jsonConfig){
		if (xmlFile == null) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(xmlFile));
		} catch (FileNotFoundException e) {
			throw new JsonException("Json解析错误！" + e.getMessage(), e);
		}
		return _fromXmlInputStream(inputStream, jsonConfig, true); 
	}
	
	
	/**
	 * 把适合的xml文件转换成JsonObject对象
	 * 
	 * @param xmlFilePath
	 * @param jsonConfig
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlFile(String xmlFilePath, JsonConfig jsonConfig){
		if (XLPStringUtil.isEmpty(xmlFilePath)) {
			return null;
		}
		return fromXmlFile(new File(xmlFilePath), jsonConfig);
	}
	
	/**
	 * 把适合的xml文件转换成JsonObject对象
	 * 
	 * @param xmlFilePath
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlFile(String xmlFilePath){
		return fromXmlFile(xmlFilePath, null);
	}
	
	/**
	 * 把适合的xml输入流转换成JsonObject对象
	 * 
	 * @param inputStream
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlInputStream(InputStream inputStream){
		return fromXmlInputStream(inputStream, null);
	}
	
	/**
	 * 把适合的xml文件转换成JsonObject对象
	 * 
	 * @param inputStream
	 * @param jsonConfig
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	public static JsonObject fromXmlInputStream(InputStream inputStream, 
			JsonConfig jsonConfig){
		return _fromXmlInputStream(inputStream, jsonConfig, false);
	}
	
	/**
	 * 把适合的xml文件转换成JsonObject对象
	 * 
	 * @param inputStream
	 * @param jsonConfig
	 * @param close 是否关闭输入流，true是，false否 
	 * @return 假如参数为空，返回null
	 * @throws JsonException 假如解析出错，则抛出该异常
	 */
	private static JsonObject _fromXmlInputStream(InputStream inputStream, 
			JsonConfig jsonConfig, boolean close){
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
			throw new JsonException("Json解析错误！" + e.getMessage(), e);
		}finally {
			if (close) {
				XLPIOUtil.closeInputStream(inputStream);
			}
		}
		return fromDocument(document, jsonConfig);
	}
	
	/**
	 * 把JsonObject对象装换成xml字符串格式
	 * 
	 * @param jsonObject 
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonObject jsonObject){
		return toXMLString(jsonObject, null, null, null);
	}
	
	/**
	 * 把JsonObject对象装换成xml字符串格式
	 * 
	 * @param jsonObject 
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonObject jsonObject, XMLTagType xmlTagType){
		return toXMLString(jsonObject, null, xmlTagType);
	}
	
	/**
	 * 把JsonObject对象装换成xml字符串格式
	 * 
	 * @param jsonObject 
	 * @return xml格式的字符串
	 * @param rootElemetName 跟标签名称
	 */
	public static String toXMLString(JsonObject jsonObject, String rootElemetName){
		return toXMLString(jsonObject, rootElemetName, (XMLTagType)null);
	}
	
	/**
	 * 把JsonObject对象装换成xml字符串格式
	 * 
	 * @param jsonObject 
	 * @param rootElemetName 跟标签名称
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonObject jsonObject, String rootElemetName,
			XMLTagType xmlTagType){
		return toXMLString(jsonObject, rootElemetName, null, xmlTagType);
	}

	/**
	 * 把JsonObject对象装换成xml字符串格式
	 * 
	 * @param jsonObject 
	 * @param rootElemetName 跟标签名称
	 * @param charsetName xml的encoding=[charsetName]
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonObject jsonObject,
			String rootElemetName, String charsetName, XMLTagType xmlTagType){
		XmlConverter xmlConverter = new JsonXmlConverter(jsonObject, rootElemetName, charsetName);
		xmlConverter.setXmlTagType(xmlTagType);
		return xmlConverter.toXmlString();
	}
	
	/**
	 * 把JsonObject对象装换成xml字符串格式
	 * 
	 * @param jsonObject 
	 * @param rootElemetName 跟标签名称
	 * @param charsetName xml的encoding=[charsetName]
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonObject jsonObject,
			String rootElemetName, String charsetName){
		return toXMLString(jsonObject, rootElemetName, charsetName, null);
	}
	
	/**
	 * 把jsonArray对象装换成xml字符串格式
	 * 
	 * @param jsonArray 
	 * @param rootElemetName 跟标签名称
	 * @param charsetName xml的encoding=[charsetName]
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonArray jsonArray,
			String rootElemetName, String charsetName, XMLTagType xmlTagType){
		XmlConverter xmlConverter = new JsonXmlConverter(jsonArray, rootElemetName, charsetName);
		xmlConverter.setXmlTagType(xmlTagType);
		return xmlConverter.toXmlString();
	}
	
	/**
	 * 把jsonArray对象装换成xml字符串格式
	 * 
	 * @param jsonArray 
	 * @param rootElemetName 跟标签名称
	 * @param xmlTagType json转换文xml的tag时，标签名称字符的类型
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonArray jsonArray,
			String rootElemetName, XMLTagType xmlTagType){
		return toXMLString(jsonArray, rootElemetName, null, xmlTagType);
	}
	
	/**
	 * 把jsonArray对象装换成xml字符串格式
	 * 
	 * @param jsonArray 
	 * @param rootElemetName 跟标签名称
	 * @return xml格式的字符串
	 */
	public static String toXMLString(JsonArray jsonArray, String rootElemetName){
		return toXMLString(jsonArray, rootElemetName, null, null);
	}
}
