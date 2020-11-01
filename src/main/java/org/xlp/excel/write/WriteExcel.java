package org.xlp.excel.write;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface WriteExcel {
	/**
	 * 写excel
	 * 
	 * @param fileName
	 * @throws IOException 
	 */
	public void write(String fileName) throws IOException;
	
	/**
	 * 写excel
	 * 
	 * @param outputStream
	 * @throws IOException 
	 */
	public void write(OutputStream outputStream) throws IOException;
	
	/**
	 * 写excel
	 * 
	 * @param excelFile
	 * @throws IOException 
	 */
	public void write(File excelFile) throws IOException;
}
