package org.xlp.docx;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.docx4j.Docx4J;
import org.docx4j.TraversalUtil;
import org.docx4j.finders.RangeFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.relationships.Relationships;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlp.assertion.AssertUtils;
import org.xlp.utils.XLPStringUtil;
import org.xlp.utils.collection.XLPCollectionUtil;
import org.xlp.utils.io.path.XLPFilePathUtil;

/**
 * <p>
 * 创建时间：2021年11月7日 下午10:25:41
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description word（书签）模板操作类，操作word相应的书签
 */
public class DocxBookmarkTemplate implements Closeable {
	/**
	 * 日志对象
	 */
	final static Logger LOGGER = LoggerFactory.getLogger(DocxBookmarkTemplate.class);
	
	/**
	 * word处理器对象
	 */
	private WordprocessingMLPackage wordprocessing;

	/**
	 * word书签集合
	 */
	private List<CTBookmark> bookmarks;

	/**
	 * word书签集合
	 */
	private List<CTMarkupRange> markupRanges;

	/**
	 * 构造函数
	 * 
	 * @param inputStream
	 *            word文档输入流
	 * @param password
	 *            密码
	 * @throws Docx4JException
	 *             假如加载文件输入流失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如第一个参数为null，则抛出该异常
	 */
	public DocxBookmarkTemplate(InputStream inputStream, String password) throws Docx4JException {
		AssertUtils.isNotNull(inputStream, "inputStream paramter is not null!");
		wordprocessing = (WordprocessingMLPackage) WordprocessingMLPackage.load(inputStream, password);
	}

	/**
	 * 构造函数
	 * 
	 * @param inputStream
	 *            word文档输入流
	 * @throws Docx4JException
	 *             假如加载文件输入流失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空则抛出该异常
	 */
	public DocxBookmarkTemplate(InputStream inputStream) throws Docx4JException {
		AssertUtils.isNotNull(inputStream, "inputStream paramter is not null!");
		wordprocessing = (WordprocessingMLPackage) WordprocessingMLPackage.load(inputStream);
	}

	// ----------------------file
	/**
	 * 构造函数
	 * 
	 * @param docxFile
	 *            word文档
	 * @param password
	 *            密码
	 * @throws Docx4JException
	 *             假如加载文件输入流失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如第一个参数为null，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public DocxBookmarkTemplate(File docxFile, String password) throws Docx4JException {
		AssertUtils.assertFile(docxFile);
		wordprocessing = (WordprocessingMLPackage) WordprocessingMLPackage.load(docxFile, password);
	}

	/**
	 * 构造函数
	 * 
	 * @param docxFile
	 *            word文档
	 * @throws Docx4JException
	 *             假如加载文件输入流失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为null，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public DocxBookmarkTemplate(File docxFile) throws Docx4JException {
		this(docxFile, XLPStringUtil.EMPTY);
	}

	// ----------------------file path
	/**
	 * 构造函数
	 * 
	 * @param docxFilePath
	 *            word文档
	 * @param password
	 *            密码
	 * @throws Docx4JException
	 *             假如加载文件输入流失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如第一个参数为空，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public DocxBookmarkTemplate(String docxFilePath, String password) throws Docx4JException {
		AssertUtils.isNotNull(docxFilePath, "docxFilePath paramter is not null or empty!");
		File docxFile = new File(docxFilePath);
		AssertUtils.assertFile(docxFile);
		wordprocessing = (WordprocessingMLPackage) WordprocessingMLPackage.load(docxFile, password);
	}

	/**
	 * 构造函数
	 * 
	 * @param docxFilePath
	 *            word文档
	 * @throws Docx4JException
	 *             假如加载文件输入流失败，则抛出该异常
	 * @throws NullPointerException
	 *             假如参数为空，则抛出该异常
	 * @throws IllegalArgumentException
	 *             假如给定的文件是目录或不存在，则抛出该异常
	 */
	public DocxBookmarkTemplate(String docxFilePath) throws Docx4JException {
		this(docxFilePath, XLPStringUtil.EMPTY);
	}

	/**
	 * 替换指定书签中的内容
	 * 
	 * @param bookmarkName 书签名称
	 * @param text 替换的内容
	 * @return this
	 */
	public DocxBookmarkTemplate replaceText(String bookmarkName, String text){
		Map<String, String> map = new HashMap<String, String>();
		map.put(bookmarkName, text);
		return replaceText(map);
	}
	
	/**
	 * 替换指定书签中的内容
	 * 
	 * @param replaceContent 替换的内容(key:书签名称，value:替换内容)
	 * @return this
	 */
	public DocxBookmarkTemplate replaceText(Map<String, String> replaceContent){
		if (replaceContent != null) {
			optionBookmarks(replaceContent, false, false, true);
		}
		return this;
	}
	
	//-------------在书签前插入数据---------------------
	/**
	 * 插入指定书签中的内容，在书签前插入数据
	 * 
	 * @param bookmarkName 书签名称
	 * @param text 插入的内容
	 * @return this
	 */
	public DocxBookmarkTemplate beforeInsertText(String bookmarkName, String text){
		Map<String, String> map = new HashMap<String, String>();
		map.put(bookmarkName, text);
		return beforeInsertText(map);
	}
	
	/**
	 * 插入指定书签中的内容
	 * 
	 * @param replaceContent 插入的内容(key:书签名称，value:插入的内容)
	 * @return this
	 */
	public DocxBookmarkTemplate beforeInsertText(Map<String, String> replaceContent){
		if (replaceContent != null) {
			optionBookmarks(replaceContent, true, false, false);
		}
		return this;
	}

	//-------------在书签后插入数据---------------------
	/**
	 * 插入指定书签中的内容，在书签后插入数据
	 * 
	 * @param bookmarkName 书签名称
	 * @param text 插入的内容
	 * @return this
	 */
	public DocxBookmarkTemplate afterInsertText(String bookmarkName, String text){
		Map<String, String> map = new HashMap<String, String>();
		map.put(bookmarkName, text);
		return afterInsertText(map);
	}
	
	/**
	 * 插入指定书签中的内容
	 * 
	 * @param replaceContent 插入的内容(key:书签名称，value:插入的内容)
	 * @return this
	 */
	public DocxBookmarkTemplate afterInsertText(Map<String, String> replaceContent){
		if (replaceContent != null) {
			optionBookmarks(replaceContent, false, true, false);
		}
		return this;
	}
	
	/**
	 * 操作书签
	 * 
	 * @param map
	 *            插入书签的内容
	 * @param beforeInsert
	 *            是否在书签内容前插入，值为true时，是，并且afterInsert和replace值无效
	 * @param afterInsert
	 *            是否在书签内容后插入，值为true时，是，并且beforeInsert和replace值无效
	 * @param replace
	 *            是否替换书签里的内容，值为true时，替换，并且beforeInsert和afterInsert值无效
	 */
	private void optionBookmarks(Map<String, String> map, boolean beforeInsert, 
			boolean afterInsert, boolean replace) {
		Set<String> keys = map.keySet();
		CTBookmark bm;
		for (String key : keys) {
			bm = null;
			for (CTBookmark bookmark : getBookmarks()) {
				if(key != null && key.equals(bookmark.getName())){ 
					bm = bookmark;
					break;
				}
			}
			
			if (bm == null) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("名称为【" + key + "】的书签不存在！");  
				}
				continue;
			}
			
			Object parent = bm.getParent();
            if (parent instanceof ContentAccessor) {
                List<Object> content = ((ContentAccessor) parent).getContent();
                int startIndex = -1;
                int endIndex = -1;
                int i = 0;
                for (Object o : content) {
                    if (o instanceof JAXBElement) {
                        o = ((JAXBElement<?>) o).getValue();
                    }
                    //查找CTBookmark对象所在的位置
                    if (bm.equals(o)) {
                        startIndex = i;
                    }else if (!(o instanceof CTBookmark) && (o instanceof CTMarkupRange) 
                    		&& ((CTMarkupRange) o).getId().equals(bm.getId())) {
                    	//查找CTMarkupRange对象所在的位置
                    	endIndex = i;
                    	break;
                    }
                    i++;
                }
                //假如书签可用，则进行相应的操作
                if (endIndex > startIndex) {
                	//截取CTBookmark和CTMarkupRange之间的元素
                    List<Object> betweenElements = XLPCollectionUtil.subList(content, 
                    		startIndex + 1, endIndex);
                    //判断CTBookmark和CTMarkupRange之间的是否有元素
                    //没有插入新的文本元素，有修改已有的文本元素
                    Text text = null;
                    if (!DocxUtils.containsBlockElementAndText(betweenElements)) {
                    	Child[] childs = createChildElements(parent);
                    	text = (Text) childs[0];
                        content.add(startIndex + 1, childs[1]);
                    } else {
                    	//查找文本元素集合
                        List<Text> texts =  DocxUtils.findElements(betweenElements, Text.class);
                        if (replace) {
                        	text = texts.isEmpty() ? null : texts.remove(0);
                            Iterator<Text> iterator = texts.iterator();
                            while (iterator.hasNext()){
                                Text text1 = iterator.next();
                                Object textparent = ((Child)text1).getParent();
                                if (textparent instanceof ContentAccessor){
                                    ((ContentAccessor) textparent).getContent().remove(text1);
                                }
                            }
						} else if (afterInsert) {
							text = texts.isEmpty() ? null : texts.get(texts.size() - 1); 
						} else if (beforeInsert) {
							text = texts.isEmpty() ? null : texts.get(0); 
						}
                        texts.clear();
                        texts = null;
                    }
                    
                    if (text != null) {
                    	String textValue = text.getValue();
                    	textValue = XLPStringUtil.isEmpty(textValue) ? XLPStringUtil.toEmpty(textValue) : textValue;
                    	if (replace) {
							textValue = XLPStringUtil.nullToEmpty(map.get(key));
						} else if (afterInsert) {
 							textValue += XLPStringUtil.nullToEmpty(map.get(key));
 						} else if (beforeInsert) {
 							textValue = XLPStringUtil.nullToEmpty(map.get(key)) + textValue;
 						}
                    	text.setValue(textValue);
					} else if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("名称为【" + key + "】的书签操作失败！");
					}
                } else if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("名称为【" + key + "】的书签操作失败！");
				}
            }
		}
	}
	
	/**
	 * 根据给定的父元素创建新的子元素
	 * 
	 * @param parent
	 * @return Text元素以及他的父元素（P 或 R）
	 */
	private Child[] createChildElements(Object parent){
        Child[] newElements = new Child[2];
        ObjectFactory factory = Context.getWmlObjectFactory();
        R r = factory.createR();
        Text text = factory.createText();
        r.getContent().add(text);
        if (!(parent instanceof P)){
            P p = factory.createP();
            p.getContent().add(r);
            newElements[1] = p;
        }else {
            newElements[1] = r;
        }
        newElements[0] = text;
        return newElements;
    }

	/**
	 * 获取所有的书签信息
	 * 
	 * @throws Docx4JException
	 *             假如获取失败，则抛出该异常
	 */
	public List<CTBookmark> getBookmarks() {
		// 防止重复获取
		if (bookmarks == null) {
			findAllMarkupRanges(); 
		}
		return bookmarks;
	}

	/**
	 * 获取书签
	 */
	public List<CTMarkupRange> getMarkupRanges() {
		// 防止重复获取
		if (markupRanges == null) {
			findAllMarkupRanges(); 
		}
		return markupRanges;
	}

	/**
	 * 查找所有书签
	 */
	private void findAllMarkupRanges() {
		MainDocumentPart mainDocumentPart = wordprocessing.getMainDocumentPart();
		bookmarks = new ArrayList<CTBookmark>();
		markupRanges = new ArrayList<CTMarkupRange>();
		findMainPartMarkupRanges(mainDocumentPart, bookmarks, markupRanges);
		findHeaderAndFooterPartMarkupRanges(mainDocumentPart, bookmarks, markupRanges);
	}

	/**
	 * 查找页眉页脚书签
	 * 
	 * @param mainDocumentPart
	 * @param bookmarks
	 * @param markupRanges
	 */
	private void findHeaderAndFooterPartMarkupRanges(MainDocumentPart mainDocumentPart, List<CTBookmark> bookmarks,
			List<CTMarkupRange> markupRanges) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("开始查找页眉页脚书签。。。");
		}
		RelationshipsPart relationshipsPart = mainDocumentPart.getRelationshipsPart();
		Relationships relationships = relationshipsPart.getRelationships();
		List<Relationship> relationshipList = relationships.getRelationship();
		Part part;
		List<Object> list;
		ContentAccessor contentAccessor;
		for (Relationship relationship : relationshipList) {
			part = relationshipsPart.getPart(relationship);
			if (part instanceof HeaderPart || part instanceof FooterPart) {
				contentAccessor = (ContentAccessor) part;
				list = contentAccessor.getContent();
				// 提取书签
				RangeFinder finder = new RangeFinder("CTBookmark", "CTMarkupRange");
				new TraversalUtil(list, finder);
				bookmarks.addAll(finder.getStarts());
				markupRanges.addAll(finder.getEnds());
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("查找页眉页脚书签结束。。。");
		}
	}

	/**
	 * 查找主文档书签
	 * 
	 * @param mainDocumentPart
	 * @param bookmarks
	 * @param markupRanges
	 */
	private void findMainPartMarkupRanges(MainDocumentPart mainDocumentPart, List<CTBookmark> bookmarks,
			List<CTMarkupRange> markupRanges) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("开始查找主文档书签。。。");
		}
		Document document = mainDocumentPart.getJaxbElement();
		List<Object> objects = document.getContent();
		// 提取书签
		RangeFinder finder = new RangeFinder("CTBookmark", "CTMarkupRange");
		new TraversalUtil(objects, finder);
		bookmarks.addAll(finder.getStarts());
		markupRanges.addAll(finder.getEnds());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("查找主文档书签结束。。。");
		}
	}

	/**
	 * 释放资源
	 */
	@Override
	public void close() {
		wordprocessing = null;
	}
	
	/**
	 * 插入指定书签中的内容
	 * 
	 * @param bookmarkName 书签名称
	 * @param child 插入的元素，可以是图片、表格等
	 * @return this
	 */
	public DocxBookmarkTemplate insertElement(String bookmarkName, Child child){
		Map<String, Child> insetElements = new HashMap<String, Child>();
		insetElements.put(bookmarkName, child);
		return insertElement(insetElements);
	}
	
	/**
	 * 插入指定书签中的内容
	 * 
	 * @param insetElements 插入的内容(key:书签名称，value:插入的元素对象)
	 * @return this
	 */
	public DocxBookmarkTemplate insertElement(Map<String, Child> insetElements){
		if (insetElements == null)  return this;
		
		Set<String> keys = insetElements.keySet();
		CTBookmark bm;
		for (String key : keys) {
			bm = null;
			for (CTBookmark bookmark : getBookmarks()) {
				if(key != null && key.equals(bookmark.getName())){ 
					bm = bookmark;
					break;
				}
			}
			
			if (bm == null) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("名称为【" + key + "】的书签不存在！");  
				}
				continue;
			}
			
			Object parent = bm.getParent();
            if (parent instanceof ContentAccessor) {
                List<Object> content = ((ContentAccessor) parent).getContent();
                int startIndex = -1;
                int endIndex = -1;
                int i = 0;
                for (Object o : content) {
                    if (o instanceof JAXBElement) {
                        o = ((JAXBElement<?>) o).getValue();
                    }
                    //查找CTBookmark对象所在的位置
                    if (bm.equals(o)) {
                        startIndex = i;
                    }else if (!(o instanceof CTBookmark) && (o instanceof CTMarkupRange) 
                    		&& ((CTMarkupRange) o).getId().equals(bm.getId())) {
                    	//查找CTMarkupRange对象所在的位置
                    	endIndex = i;
                    	break;
                    }
                    i++;
                }
                //假如书签可用，则进行相应的操作
                if (endIndex > startIndex) {
                	Child child = insetElements.get(key);
                	if (!(parent instanceof P) && child instanceof R) {
                		ObjectFactory factory = Context.getWmlObjectFactory();
                        P p = factory.createP();
                        p.getContent().add(child);
                        child = p;
					}
                	content.add(startIndex + 1, child);
                } else if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("名称为【" + key + "】的书签操作失败！");
				}
            }
		}
		return this;
	}
	
	/**
	 * 保存修改后的文档
	 * 
	 * @param file 保存的目标文件
	 * @param password 文件打开时需输入的密码
	 * @throws Docx4JException 假如文件保存失败，则抛出该异常  
	 * @throws NullPointerException 假如第一个参数为null，则抛出该异常 
	 */
	public void save(File file, String password) throws Docx4JException{
		AssertUtils.isNotNull(file, "file paramter is null!");
		password = XLPStringUtil.emptyToNull(password);
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (file.getName().endsWith(".xml")) {
			wordprocessing.save(file, Docx4J.FLAG_SAVE_FLAT_XML);			
		} else {
			wordprocessing.save(file, Docx4J.FLAG_SAVE_ZIP_FILE, password);						
		}
	}
	
	/**
	 * 保存修改后的文档
	 * 
	 * @param file 保存的目标文件
	 * @throws Docx4JException 假如文件保存失败，则抛出该异常  
	 * @throws NullPointerException 假如参数为null，则抛出该异常 
	 */
	public void save(File file) throws Docx4JException{
		save(file, null);
	}
	
	/**
	 * 保存修改后的文档
	 * 
	 * @param outFilename 保存的目标文件
	 * @param password 文件打开时需输入的密码
	 * @throws Docx4JException 假如文件保存失败，则抛出该异常  
	 * @throws NullPointerException 假如第一个参数为null或空，则抛出该异常 
	 */
	public void save(String outFilename, String password) throws Docx4JException{
		AssertUtils.isNotNull(outFilename, "outFilename paramter is null or empty!");
		outFilename = XLPFilePathUtil.normalize(outFilename);
		save(new File(outFilename), password);
	}

	/**
	 * 保存修改后的文档
	 * 
	 * @param outFilename 保存的目标文件
	 * @throws Docx4JException 假如文件保存失败，则抛出该异常  
	 * @throws NullPointerException 假如参数为null或空，则抛出该异常 
	 */
	public void save(String outFilename) throws Docx4JException{
		save(outFilename, null);
	}
	
	/**
	 * 保存修改后的文档
	 * 
	 * @param outputStream 保存的文件输出流
	 * @param password 文件打开时需输入的密码
	 * @throws Docx4JException 假如文件保存失败，则抛出该异常  
	 * @throws NullPointerException 假如第一个参数为null，则抛出该异常 
	 */
	public void save(OutputStream outputStream, String password) throws Docx4JException{
		AssertUtils.isNotNull(outputStream, "outputStream paramter is null!");
		password = XLPStringUtil.emptyToNull(password);
		wordprocessing.save(outputStream, Docx4J.FLAG_SAVE_ZIP_FILE, password);
	}
	
	/**
	 * 保存修改后的文档
	 * 
	 * @param outputStream 保存的文件输出流
	 * @throws Docx4JException 假如文件保存失败，则抛出该异常  
	 * @throws NullPointerException 假如参数为null，则抛出该异常 
	 */
	public void save(OutputStream outputStream) throws Docx4JException{
		save(outputStream, null);
	}
	
	public static void main(String[] args) throws Docx4JException {
		DocxBookmarkTemplate docxBookmarkTemplate = new DocxBookmarkTemplate("f:/12.docx");
		docxBookmarkTemplate.beforeInsertText("title", "收拾hj").afterInsertText("space", "哈哈1")
				.replaceText("id", "yee ").replaceText("textBox", "哈哈textBox");
		docxBookmarkTemplate.save(new File("f:/13.docx"));
		docxBookmarkTemplate.close();
	}

}
