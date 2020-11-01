package org.xlp.xlp_third;

import java.io.File;

import org.xlp.qrcode.XLPQRCodeReader;
import org.xlp.zip.UnZip;

public class QRCoeTest {

	public QRCoeTest() {
	}
	
	public static void main(String[] args) {
		XLPQRCodeReader qCodeReader = new XLPQRCodeReader();
		try {
			//qCodeReader.read(new File("C:\\Users\\xlp\\Desktop\\doc\\c.tld"));
			//System.out.println(qCodeReader.getResult());
			UnZip unZip = new UnZip("F:\\apk\\xmlbeans-2.6.0.jar");
			unZip.unZip("F:\\apk\\");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
