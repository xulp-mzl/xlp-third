package org.xlp.xlp_third;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xlp.pdf.PDFTemplateWriter;
import org.xlp.utils.io.XLPIOUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.RadioCheckField;

/**
 * <p>创建时间：2020年9月24日 下午11:43:18</p>
 * @author xlp
 * @version 1.0 
 * @Description 类描述
*/
public class TestPdf {
	public static void main(String[] args) throws IOException {
		
        OutputStream out = null;
		try {
			out = new FileOutputStream(new File("C:\\Users\\xlp\\Desktop\\2.pdf"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Document doc = null;
        Map<String,String> text=new HashMap<String,String>();
        
        
        text.put("Text1", "是打飞机的发生1");
        text.put("Text2", "ssss的发生1受持读诵");
        text.put("Text4", "2012年4月2日1");
       // text.put("Check Box1", "Yes");
        text.put("Check", "Yes");
        text.put("Check Box2", "Yes");
        text.put("Choice2", "true");
        text.put("Group2", "Choice2");
       // getdoc(out, doc, text, null, "C:\\Users\\xlp\\Desktop\\1.pdf");
//		InputStream inputStream = new FileInputStream("C:\\Users\\xlp\\Desktop\\1.pdf");
//		OutputStream outputStream = new FileOutputStream("C:\\Users\\xlp\\Desktop\\4.pdf");
//		XLPIOUtil.copy(inputStream, outputStream);
//		inputStream.close();
//		outputStream.close();
		
		PDFTemplateWriter pdfTemplateWriter = new PDFTemplateWriter("C:\\Users\\xlp\\Desktop\\1.pdf");
		pdfTemplateWriter.setDefaultBaseFont();
		pdfTemplateWriter.setField("Text1", "对对eeevw、从对");
		pdfTemplateWriter.setImageField("Image1", "C:\\Users\\xlp\\Desktop\\pic\\徐龙平.jpg");
		pdfTemplateWriter.setWatermark("相对ww 等等");
		pdfTemplateWriter.write("C:\\Users\\xlp\\Desktop\\4.pdf");
		pdfTemplateWriter.close();
	}
	public static void getdoc(OutputStream out,Document doc,Map<String,String> text
            ,Map<String ,String> img,String templatePath){
        PdfReader reader =null;
        ByteArrayOutputStream bos =null;
        PdfStamper stamper =null;
        PdfCopy copy  =null;
        try{
            reader = new PdfReader(new FileInputStream(new File(templatePath))); 
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();
            //设置字体为中文字体 Adobe 宋体 std L
			BaseFont baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			form.addSubstitutionFont(baseFont); 
            //文本
            if(text!=null&&text.size()>0){
                for(String key : text.keySet()){
                   form.setField(key, text.get(key), true);
                }
            }
            System.out.println(form.setFieldRichValue("Text1", "<h1 style='color:red;'>哈哈哈</h1>"));
            form.setGenerateAppearances(false);
            FieldPosition fieldPosition = form.getFieldPositions("Image1").get(0);
            int pageNo1 = fieldPosition.page;
            Rectangle signRect1 = fieldPosition.position;
            float x1 = signRect1.getLeft();
            float y1 = signRect1.getBottom();
            Image image1 = Image.getInstance("C:\\Users\\xlp\\Desktop\\pic\\徐龙平.jpg");
            PdfContentByte under1 = stamper.getOverContent(pageNo1);
            image1.setAbsolutePosition(x1,y1);
            image1.scaleToFit(signRect1);
            under1.addImage(image1);
            
            
            //form.g
            stamper.setFormFlattening(true);
            stamper.close();
            stamper.setFormFlattening(false);
            form.setField("Text1", "1方法", true);
            out.write(bos.toByteArray());
            /*copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();*/
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(stamper!=null){
                try {
                    stamper.close();
                } catch (Exception e) {
                }
            }
            
            if(doc!=null){
                try {
                    doc.close();
                } catch (Exception e) {
                }
                
            }
            if(copy!=null){
                try {
                    copy.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
