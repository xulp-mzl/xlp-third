package org.xlp.docx;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.dml.diagram.CTDataModel;
import org.docx4j.mce.AlternateContent;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.CTObject;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.Pict;
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
	@SuppressWarnings("all")
	public static <T> List<T> findElements(List<Object> nodes, Class<T> cs) {
		List<T> list = new ArrayList<T>();
		if (XLPCollectionUtil.isEmpty(nodes) || cs == null) {
			return list;
		}
		for (Object o : nodes) {
			if (o instanceof JAXBElement) {
				o = ((JAXBElement) o).getValue();
			}
			if (cs.isAssignableFrom(o.getClass()))
				list.add((T) o);
			if (o instanceof org.docx4j.wml.Document) {
				List<Object> artificialList = new ArrayList<Object>();
				artificialList.add(((org.docx4j.wml.Document) o).getBody());
				list.addAll(findElements(artificialList, cs));
			} else if (o instanceof org.docx4j.wml.ContentAccessor) {
				list.addAll(findElements(((org.docx4j.wml.ContentAccessor) o).getContent(), cs));
			} else if (o instanceof org.docx4j.wml.SdtElement) {
				if (((org.docx4j.wml.SdtElement) o).getSdtContent() != null) {
					list.addAll(findElements(((org.docx4j.wml.SdtElement) o).getSdtContent().getContent(), cs));
				}
			} else if (o instanceof Pict) {
				list.addAll(findElements(((Pict) o).getAnyAndAny(), cs)); 
			} else if (o instanceof org.docx4j.dml.picture.Pic) { 
				// Post 2.7.1; untested
				org.docx4j.dml.picture.Pic dmlPic = ((org.docx4j.dml.picture.Pic) o);
				if (dmlPic.getBlipFill() != null && dmlPic.getBlipFill().getBlip() != null) {
					List<Object> artificialList = new ArrayList<Object>();
					artificialList.add(dmlPic.getBlipFill().getBlip());
					list.addAll(findElements(artificialList, cs));
				}
			} else if (o instanceof org.docx4j.dml.CTGvmlPicture) { 
				// Post 2.7.1
				org.docx4j.dml.CTGvmlPicture dmlPic = ((org.docx4j.dml.CTGvmlPicture) o);
				if (dmlPic.getBlipFill() != null && dmlPic.getBlipFill().getBlip() != null) {
					List<Object> artificialList = new ArrayList<Object>();
					artificialList.add(dmlPic.getBlipFill().getBlip());
					list.addAll(findElements(artificialList, cs));
				}
			} else if (o instanceof org.docx4j.vml.CTShape) {
				List<Object> artificialList = new ArrayList<Object>();
				for (JAXBElement<?> j : ((org.docx4j.vml.CTShape) o).getPathOrFormulasOrHandles()) {
					artificialList.add(j);
				}
				list.addAll(findElements(artificialList, cs));
			} else if (o instanceof CTDataModel) {
				CTDataModel dataModel = (CTDataModel) o;
				List<Object> artificialList = new ArrayList<Object>();
				// We're going to create a list merging two children ..
				artificialList.addAll(dataModel.getPtLst().getPt());
				artificialList.addAll(dataModel.getCxnLst().getCxn());
				list.addAll(findElements(artificialList, cs));
			} else if (o instanceof org.docx4j.dml.diagram2008.CTDrawing) {
				list.addAll(findElements(((org.docx4j.dml.diagram2008.CTDrawing) o).getSpTree().getSpOrGrpSp(), cs));
			} else if (o instanceof org.docx4j.vml.CTTextbox) {
				org.docx4j.vml.CTTextbox textBox = ((org.docx4j.vml.CTTextbox) o);
				if (textBox.getTxbxContent() != null) {
					list.addAll(findElements(textBox.getTxbxContent().getEGBlockLevelElts(), cs));
				}
			} else if (o instanceof CTObject) {
				CTObject ctObject = (CTObject) o;
				List<Object> artificialList = new ArrayList<Object>();
				artificialList.addAll(ctObject.getAnyAndAny());
				if (ctObject.getControl() != null) {
					artificialList.add(ctObject.getControl()); // CTControl
				}
				list.addAll(findElements(artificialList, cs));
			} else if (o instanceof org.docx4j.dml.CTGvmlGroupShape) {
				list.addAll(findElements(((org.docx4j.dml.CTGvmlGroupShape) o).getTxSpOrSpOrCxnSp(), cs));
			} else if (o instanceof org.docx4j.dml.CTGvmlShape) {
				org.docx4j.dml.CTGvmlShape sp = (org.docx4j.dml.CTGvmlShape) o;
				if (sp != null && sp.getTxSp() != null && sp.getTxSp().getTxBody() != null) {
					List<Object> artificialList = new ArrayList<Object>();
					artificialList.addAll(sp.getTxSp().getTxBody().getP());
					list.addAll(findElements(artificialList, cs));
				}
			} else if (o instanceof FldChar) {
				FldChar fldChar = ((FldChar) o);
				List<Object> artificialList = new ArrayList<Object>();
				artificialList.add(fldChar.getFldCharType());
				if (fldChar.getFfData() != null) {
					artificialList.add(fldChar.getFfData());
				}
				if (fldChar.getFldData() != null) {
					artificialList.add(fldChar.getFldData());
				}
				if (fldChar.getNumberingChange() != null) {
					artificialList.add(fldChar.getNumberingChange());
				}
				list.addAll(findElements(artificialList, cs));
			} else if (o instanceof org.docx4j.mce.AlternateContent) {
				// we also want to traverse the fallback
				AlternateContent ac = (AlternateContent) o;
				List<Object> artificialList = new ArrayList<Object>();
				artificialList.addAll(ac.getChoice());
				artificialList.add(ac.getFallback());
				list.addAll(findElements(artificialList, cs));
			} else if (o instanceof org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape) {
				org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape sp = 
						(org.docx4j.com.microsoft.schemas.office.word.x2010.wordprocessingShape.CTWordprocessingShape) o;
				if (sp != null && sp.getTxbx() != null && sp.getTxbx().getTxbxContent() != null) {
					list.addAll(findElements(sp.getTxbx().getTxbxContent().getContent(), cs));
				}
			}
		}
		return list;
	}
}
