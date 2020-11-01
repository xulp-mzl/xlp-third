package org.xlp.xlp_third;

import java.io.File;
import java.io.FileNotFoundException;

import org.xlp.ftp.FTPOption;
import org.xlp.ftp.FTPOptionException;

/**
 * <p>创建时间：2020年8月8日 下午11:26:59</p>
 * @author xlp
 * @version 1.0 
 * @Description 类描述
*/
public class TestFtp {
	public static void main(String[] args) throws FileNotFoundException, FTPOptionException {
		System.out.println("1".substring(1) + 1);
		System.out.println("ddk".replace("k", "l"));
		System.out.println("3".endsWith("")+ "4"); 
		FTPOption ftpOption = new FTPOption("192.168.1.4", "test", "test");
		//ftpOption.preFTPOption();
		//ftpOption.makeDirs("/gg");
		ftpOption.uploadDirToFTP(new File("E:\\神软公司填写资料"), "/");
//		System.out.println(ftpOption.deleteFile("/新建文件夹/等等1.jnt"));
//		System.out.println(ftpOption.deleteDir("5他.txt"));
		//System.out.println(ftpOption.deleteDir("/好\\的.txt"));
		System.out.println(ftpOption.downloadFromFtp("/", "f:\\test\\n看.txt"));
		System.out.println(ftpOption.deleteDir("\\"));
		ftpOption.close();
		System.out.println(ftpOption.getErrorMsg()+12);
	}
}
