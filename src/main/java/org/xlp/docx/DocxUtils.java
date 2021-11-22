package org.xlp.docx;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.TraversalUtil;
import org.docx4j.finders.ClassFinder;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.ProofErr;
import org.docx4j.wml.R;
import org.xlp.utils.collection.XLPCollectionUtil;

/**
 * <p>
 * 创建时间：2021年11月15日 下午11:08:31
 * </p>
 * 
 * @author xlp
 * @version 1.0
 * @Description 提供查找指定元素类型的集合等功能
 */
public class DocxUtils {
	/**
	 * 判断给定的docx文档节点类型是否包括块级元素或文本元素
	 * 
	 * @param nodes
	 *            给定判断的节点集合
	 * @return 假如包含，返回true，否则返回false
	 */
	@SuppressWarnings("rawtypes")
	public static boolean containsBlockElementAndText(List<Object> nodes) {
		if (XLPCollectionUtil.isEmpty(nodes)) {
			return false;
		}
		for (Object o : nodes) {
			if (o instanceof JAXBElement) {
				o = ((JAXBElement) o).getValue();
			}
			if (!(o instanceof Br || o instanceof CTMarkupRange || o instanceof R.Tab
					|| o instanceof R.LastRenderedPageBreak || o instanceof ProofErr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 从给定的节点集合中查找到指定类型节点集合
	 * 
	 * @param nodes 待查找集合
	 * @param cs 查找元素类型
	 * @return 假如参数为null或未查到返回空集合，否则返回查找类型集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> findElements(List<Object> nodes, Class<T> cs) {
		List<T> list = new ArrayList<T>();
		if (XLPCollectionUtil.isEmpty(nodes) || cs == null) {
			return list;
		}
		ClassFinder classFinder = new ClassFinder(cs);
		new TraversalUtil(nodes, classFinder);
		return (List<T>) classFinder.results;
	}
}
