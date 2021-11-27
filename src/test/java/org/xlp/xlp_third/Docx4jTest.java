package org.xlp.xlp_third;

import java.io.File;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.xlp.docx.DocxBookmarkTemplate;

/**
 * <p>创建时间：2021年11月27日 下午5:21:14</p>
 * @author xlp
 * @version 1.0 
 * @Description 类描述
*/
public class Docx4jTest {
	public static void main(String[] args) throws Docx4JException {
		DocxBookmarkTemplate docxBookmarkTemplate = new DocxBookmarkTemplate("f:/12.docx");
		docxBookmarkTemplate.beforeInsertText("title", "收拾2hj").afterInsertText("space", "哈哈2")
				.replaceText("id", "yee ").replaceText("textBox", "哈哈").replaceText("content1", "ss是");
		docxBookmarkTemplate.insertImage("img1", "e:\\请假申请表.jpg", 800)
			.insertImage("img", "e:\\请假申请表.jpg", 1000);
		docxBookmarkTemplate.save(new File("f:/13.docx"));
		docxBookmarkTemplate.close();
	}
}
